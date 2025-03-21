package com.sonozaki.bedatastore.datastore

import android.content.Context
import android.util.Log
import androidx.annotation.GuardedBy
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.dataStoreFile
import com.sonozaki.bedatastore.corruptionHandler.encryptedCorruptionHandler
import com.sonozaki.bedatastore.encryption.EncryptedSerializer
import com.sonozaki.bedatastore.encryption.EncryptionManagerFactory
import com.sonozaki.bedatastore.migration.getEncryptedMigrations
import com.sonozaki.bedatastore.serializer.ByteArraySerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


fun <T> encryptedDataStore(
    fileName: String,
    serializer: Serializer<T>,
    corruptionHandler: ReplaceFileCorruptionHandler<T>? = null,
    produceMigrations: (Context) -> List<DataMigration<T>> = { listOf() },
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    alias: String = fileName,
    isDBA: Boolean = false
): ReadOnlyProperty<Context, DataStore<T>> {
    return EncryptedDatastoreSingletonDelegate(
        fileName, serializer, corruptionHandler, produceMigrations, scope, alias, isDBA
    )
}

fun <T> dataStoreDBA(
    fileName: String,
    serializer: Serializer<T>,
    corruptionHandler: ReplaceFileCorruptionHandler<T>? = null,
    produceMigrations: (Context) -> List<DataMigration<T>> = { listOf() },
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
): ReadOnlyProperty<Context, DataStore<T>> {
    return DataStoreSingletonDelegateDBA(
        fileName, OkioSerializerWrapper(serializer), corruptionHandler, produceMigrations, scope
    )
}

internal class DataStoreSingletonDelegateDBA<T> internal constructor(
    private val fileName: String,
    private val serializer: OkioSerializer<T>,
    private val corruptionHandler: ReplaceFileCorruptionHandler<T>?,
    private val produceMigrations: (Context) -> List<DataMigration<T>>,
    private val scope: CoroutineScope
) : ReadOnlyProperty<Context, DataStore<T>> {

    private val lock = Any()

    @GuardedBy("lock")
    @Volatile
    private var INSTANCE: DataStore<T>? = null

    /**
     * Gets the instance of the DataStore.
     *
     * @param thisRef must be an instance of [Context]
     * @param property not used
     */
    override fun getValue(thisRef: Context, property: KProperty<*>): DataStore<T> {
        return INSTANCE ?: synchronized(lock) {
            if (INSTANCE == null) {
                val applicationContext = thisRef.applicationContext.createDeviceProtectedStorageContext()
                INSTANCE = DataStoreFactory.create(
                    storage = OkioStorage(FileSystem.SYSTEM, serializer) {
                        applicationContext.dataStoreFile(fileName, true).absolutePath.toPath()
                    },
                    corruptionHandler = corruptionHandler,
                    migrations = produceMigrations(applicationContext),
                    scope = scope
                )
            }
            INSTANCE!!
        }
    }
}


private class EncryptedDatastoreSingletonDelegate<T>(
    private val fileName: String,
    private val serializer: Serializer<T>,
    private val corruptionHandler: ReplaceFileCorruptionHandler<T>?,
    private val produceMigrations: (Context) -> List<DataMigration<T>>,
    private val scope: CoroutineScope,
    private val alias: String,
    private val isDBA: Boolean
) : ReadOnlyProperty<Context, EncryptedDatastore<T>> {

    private val lock = Any()

    @GuardedBy("lock")
    @Volatile
    private var INSTANCE: EncryptedDatastore<T>? = null

    override fun getValue(thisRef: Context, property: KProperty<*>): EncryptedDatastore<T> {
        return INSTANCE ?: synchronized(lock) {
            if (INSTANCE == null) {
                Log.w("lifecycle","startingDatastore")
                val applicationContext = if (isDBA) {
                    thisRef.applicationContext.createDeviceProtectedStorageContext()
                } else {
                    thisRef.applicationContext
                }
                val encryptionManager = EncryptionManagerFactory.get()
                val encryptedSerializer = EncryptedSerializer(encryptionManager, serializer, alias)
                val encryptedCorruptionHandler = encryptedCorruptionHandler(corruptionHandler, encryptedSerializer, scope.coroutineContext)
                val byteArraySerializer = ByteArraySerializer(serializer.defaultValue, encryptedSerializer, scope.coroutineContext)
                val encryptedMigrations = getEncryptedMigrations(applicationContext, produceMigrations, encryptedSerializer, scope.coroutineContext)
                Log.w("input",applicationContext.dataStoreFile(fileName, isDBA).absolutePath)
                Log.w("input",applicationContext.dataStoreFile(fileName, isDBA).exists().toString())
                val datastore = DataStoreFactory.create(
                    storage = OkioStorage(FileSystem.SYSTEM, OkioSerializerWrapper(byteArraySerializer)) {
                        applicationContext.dataStoreFile(fileName, isDBA).absolutePath.toPath()
                    },
                    corruptionHandler = encryptedCorruptionHandler,
                    migrations = encryptedMigrations,
                    scope = scope
                )
                Log.w("lifecycle","creatingDatastore")
                INSTANCE = EncryptedDatastore(datastore, encryptedSerializer, scope.coroutineContext)
            }
            INSTANCE!!
        }
    }
}

fun Context.dataStoreFile(fileName: String, isDBA: Boolean): File {
    val context = if (isDBA) {
        createDeviceProtectedStorageContext()
    } else {
        applicationContext
    }
    return File(context.filesDir, "datastore/$fileName")
}

internal class OkioSerializerWrapper<T>(private val delegate: Serializer<T>) : OkioSerializer<T> {
    override val defaultValue: T
        get() = delegate.defaultValue

    override suspend fun readFrom(source: BufferedSource): T {
        return delegate.readFrom(source.inputStream())
    }

    override suspend fun writeTo(t: T, sink: BufferedSink) {
        delegate.writeTo(t, sink.outputStream())
    }
}
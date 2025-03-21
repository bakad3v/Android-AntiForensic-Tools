package com.sonozaki.encrypteddatastore.datastoreDBA

import androidx.datastore.dataStoreFile
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
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import java.io.File

/**
 * **This is a modification of android Datastore with some fixes to make it direct boot aware.**
 *
 * Creates a property delegate for a single process DataStore. This should only be called once
 * in a file (at the top level), and all usages of the DataStore should use a reference the same
 * Instance. The receiver type for the property delegate must be an instance of [Context].
 *
 * This should only be used from a single application in a single classloader in a single process.
 *
 * Example usage:
 * ```
 * val Context.myDataStore by dataStore("filename", serializer)
 *
 * class SomeClass(val context: Context) {
 *    suspend fun update() = context.myDataStore.updateData {...}
 * }
 * ```
 *
 * @param fileName the filename relative to Context.applicationContext.filesDir that DataStore
 * acts on. The File is obtained from [dataStoreFile]. It is created in the "/datastore"
 * subdirectory.
 * @param serializer The serializer for `T`.
 * @param corruptionHandler The corruptionHandler is invoked if DataStore encounters a
 * [androidx.datastore.core.CorruptionException] when attempting to read data. CorruptionExceptions
 * are thrown by serializers when data can not be de-serialized.
 * @param produceMigrations produce the migrations. The ApplicationContext is passed in to these
 * callbacks as a parameter. DataMigrations are run before any access to data can occur. Each
 * producer and migration may be run more than once whether or not it already succeeded
 * (potentially because another migration failed or a write to disk failed.)
 * @param scope The scope in which IO operations and transform functions will execute.
 *
 * @return a property delegate that manages a datastore as a singleton.
 */
@Suppress("MissingJvmstatic")
fun <T> dataStoreDirectBootAware(
    fileName: String,
    serializer: Serializer<T>,
    corruptionHandler: ReplaceFileCorruptionHandler<T>? = null,
    produceMigrations: (Context) -> List<DataMigration<T>> = { listOf() },
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
): ReadOnlyProperty<Context, DataStore<T>> {
    return DataStoreSingletonDelegateFixed(
        fileName, OkioSerializerWrapper(serializer), corruptionHandler, produceMigrations, scope
    )
}

/**
 * Delegate class to manage DataStore as a singleton.
 */
internal class DataStoreSingletonDelegateFixed<T> internal constructor(
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
     * Gets the instance of the directboot aware DataStore.
     *
     * @param thisRef must be an instance of [Context]
     * @param property not used
     */
    override fun getValue(thisRef: Context, property: KProperty<*>): DataStore<T> {
        val deviceContext =  thisRef.createDeviceProtectedStorageContext()
        Log.w("path",File(deviceContext.filesDir, "datastore/$fileName").absolutePath)
        Log.w("pathTrue", fileName + File(deviceContext.filesDir, "datastore/$fileName").exists().toString())
        return INSTANCE ?: synchronized(lock) {
            if (INSTANCE == null) {
                INSTANCE = DataStoreFactory.create(
                    storage = OkioStorage(FileSystem.SYSTEM, serializer) {
                        File(deviceContext.filesDir, "datastore/$fileName").absolutePath.toPath()
                    },
                    corruptionHandler = corruptionHandler,
                    migrations = produceMigrations(deviceContext),
                    scope = scope
                )
            }
            INSTANCE!!
        }
    }
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
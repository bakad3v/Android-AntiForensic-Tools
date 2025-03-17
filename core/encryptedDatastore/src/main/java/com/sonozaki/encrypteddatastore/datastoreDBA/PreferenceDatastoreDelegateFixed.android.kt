package com.sonozaki.encrypteddatastore.datastoreDBA

import android.content.Context
import androidx.annotation.GuardedBy
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File

/**
 * Creates a property delegate for a single process DataStore. This should only be called once
 * in a file (at the top level), and all usages of the DataStore should use a reference the same
 * Instance. The receiver type for the property delegate must be an instance of [Context].
 *
 * This should only be used from a single application in a single classloader in a single process.
 *
 * Example usage:
 * ```
 * val Context.myDataStore by preferencesDataStore("filename")
 *
 * class SomeClass(val context: Context) {
 *    suspend fun update() = context.myDataStore.edit {...}
 * }
 * ```
 *
 *
 * @param name The name of the preferences. The preferences will be stored in a file in the
 * "datastore/" subdirectory in the application context's files directory and is generated using
 * [preferencesDataStoreFile].
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
fun preferencesDataStoreDirectBootAware(
    name: String,
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
    produceMigrations: (Context) -> List<DataMigration<Preferences>> = { listOf() },
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
): ReadOnlyProperty<Context, DataStore<Preferences>> {
    return DBAPreferenceDataStoreSingletonDelegate(name, corruptionHandler, produceMigrations, scope)
}

/**
 * Delegate class to manage Preferences DataStore as a singleton.
 */
internal class DBAPreferenceDataStoreSingletonDelegate internal constructor(
    private val name: String,
    private val corruptionHandler: ReplaceFileCorruptionHandler<Preferences>?,
    private val produceMigrations: (Context) -> List<DataMigration<Preferences>>,
    private val scope: CoroutineScope
) : ReadOnlyProperty<Context, DataStore<Preferences>> {

    private val lock = Any()

    @GuardedBy("lock")
    @Volatile
    private var INSTANCE: DataStore<Preferences>? = null

    /**
     * Gets the instance of the DataStore.
     *
     * @param thisRef must be an instance of [Context]
     * @param property not used
     */
    override fun getValue(thisRef: Context, property: KProperty<*>): DataStore<Preferences> {
        val deviceContext = thisRef.createDeviceProtectedStorageContext()
        return INSTANCE ?: synchronized(lock) {
            if (INSTANCE == null) {

                INSTANCE = PreferenceDataStoreFactory.create(
                    corruptionHandler = corruptionHandler,
                    migrations = produceMigrations(deviceContext),
                    scope = scope
                ) {
                    File(deviceContext.filesDir, "datastore/$name.preferences_pb")
                }
            }
            INSTANCE!!
        }
    }
}
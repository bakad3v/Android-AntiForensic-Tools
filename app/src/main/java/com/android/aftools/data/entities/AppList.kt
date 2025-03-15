package com.android.aftools.data.entities

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Class for representing list of apps.
 *
 * Why do I use encrypted DataStore instead of encrypted database for storing lists of items? SQLCypher, the library for database encryption, encrypts and decrypts data using encryption key stored in the RAM. On some devices it's possible to dump RAM without the root rights and extract the encryption key, as demonstrated in this [post](https://cellebrite.com/en/decrypting-databases-using-ram-dump-health-data/) by the Cellebrite. On the other hand, in this app data is encrypted and decrypted in Android KeyStore, isolated environment where cryptographic operations may be executed safely and keys can't be extracted. It's easier to implement such an encryption with Datastore than with database.
 */
@Serializable
data class AppList(
    @Serializable(with = AppListSerializer::class)
    val list: PersistentList<AppDatastore> = persistentListOf()
) {

    fun add(app: AppDatastore): AppList {
        val newList = if (app.packageName !in list.map { it.packageName })
            list.add(app)
        else
            list
        return AppList(newList)
    }

    fun addMultiple(apps: List<AppDatastore>): AppList {
        val packageNames = list.map { it.packageName }
        val toAdd = apps.filter { it.packageName !in packageNames  }
        return AppList(list.addAll(toAdd))
    }

    fun delete(packageName: String): AppList {
        val newList = list.removeAt(list.indexOfFirst { it.packageName == packageName })
        return AppList(newList)
    }

    fun clear() = AppList(persistentListOf())

    fun setDeletionStatus(packageName: String, status: Boolean): AppList {
        val newList = list.mutate { list ->
            val index = list.indexOfFirst { it.packageName == packageName }
            val newElement = list[index].copy(toDelete = status)
            list[index] = newElement
        }
        return AppList(newList)
    }

    fun setHideStatus(packageName: String, status: Boolean): AppList {
        val newList = list.mutate { list ->
            val index = list.indexOfFirst { it.packageName == packageName }
            val newElement = list[index].copy(toHide = status)
            list[index] = newElement
        }
        return AppList(newList)
    }

    fun setClearDataStatus(packageName: String, status: Boolean): AppList {
        val newList = list.mutate { list ->
            val index = list.indexOfFirst { it.packageName == packageName }
            val newElement = list[index].copy(toClearData = status)
            list[index] = newElement
        }
        return AppList(newList)
    }


}

@OptIn(ExperimentalSerializationApi::class)
class AppListSerializer(
    private val serializer: KSerializer<AppDatastore>,
) : KSerializer<PersistentList<AppDatastore>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<AppDatastore>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<AppDatastore>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<AppDatastore> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }

}
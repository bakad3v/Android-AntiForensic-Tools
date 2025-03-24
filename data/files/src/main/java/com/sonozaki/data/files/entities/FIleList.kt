package com.sonozaki.data.files.entities

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
 * Class for representing list of files.
 *
 * Why do I use encrypted DataStore instead of encrypted database for storing lists of items? SQLCypher, the library for database encryption, encrypts and decrypts data using encryption key stored in the RAM. On some devices it's possible to dump RAM without the root rights and extract the encryption key, as demonstrated in this [post](https://cellebrite.com/en/decrypting-databases-using-ram-dump-health-data/) by the Cellebrite. On the other hand, in this app data is encrypted and decrypted in Android KeyStore, isolated environment where cryptographic operations may be executed safely and keys can't be extracted. It's easier to implement such an encryption with Datastore than with database.
 */
@Serializable
data class FilesList(
    @Serializable(with = FileListSerializer::class)
    val list: PersistentList<FileDatastore> = persistentListOf()
) {
    fun getSorted(sortOrder: com.sonozaki.entities.FilesSortOrder): FilesList {
        val newList = when(sortOrder) {
            com.sonozaki.entities.FilesSortOrder.NAME_ASC -> list.mutate { mutableList -> mutableList.sortBy{ it.name } }
            com.sonozaki.entities.FilesSortOrder.NAME_DESC -> list.mutate { mutableList -> mutableList.sortByDescending { it.name } }
            com.sonozaki.entities.FilesSortOrder.PRIORITY_ASC -> list.mutate { mutableList -> mutableList.sortBy { it.priority } }
            com.sonozaki.entities.FilesSortOrder.PRIORITY_DESC -> list.mutate { mutableList -> mutableList.sortByDescending { it.priority } }
            com.sonozaki.entities.FilesSortOrder.SIZE_ASC -> list.mutate { mutableList -> mutableList.sortBy { it.size } }
            com.sonozaki.entities.FilesSortOrder.SIZE_DESC -> list.mutate { mutableList -> mutableList.sortByDescending { it.size } }
        }
        return FilesList(newList)
    }

    fun add(file: FileDatastore): FilesList {
        val newList = if (file.uri !in list.map { it.uri })
            list.add(file)
        else
            list
        return FilesList(newList)
    }

    fun delete(uri: String): FilesList {
        val newList = list.removeAt(list.indexOfFirst { it.uri == uri })
        return FilesList(newList)
    }

    fun clear() = FilesList(persistentListOf())

    fun changePriority(uri: String, priority: Int): FilesList {
        val newList = list.mutate { list ->
            val index = list.indexOfFirst { it.uri == uri }
            val newElement = list[index].copy(priority = priority)
            list[index] = newElement
        }
        return FilesList(newList)
    }
}

@OptIn(ExperimentalSerializationApi::class)
class FileListSerializer(
    private val serializer: KSerializer<FileDatastore>,
) : KSerializer<PersistentList<FileDatastore>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<PersistentList<FileDatastore>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<FileDatastore>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<FileDatastore> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }

}
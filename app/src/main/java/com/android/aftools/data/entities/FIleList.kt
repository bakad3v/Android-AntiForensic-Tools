package com.android.aftools.data.entities

import com.android.aftools.domain.entities.FilesSortOrder
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

@Serializable
data class FilesList(
    @Serializable(with = FileListSerializer::class)
    val list: PersistentList<FileDatastore> = persistentListOf()
) {
    fun getSorted(sortOrder: FilesSortOrder): FilesList {
        val newList = when(sortOrder) {
            FilesSortOrder.NAME_ASC -> list.mutate { it.sortBy{ it.name } }
            FilesSortOrder.NAME_DESC -> list.mutate { it.sortByDescending { it.name } }
            FilesSortOrder.PRIORITY_ASC -> list.mutate { it.sortBy { it.priority } }
            FilesSortOrder.PRIORITY_DESC -> list.mutate { it.sortByDescending { it.priority } }
            FilesSortOrder.SIZE_ASC -> list.mutate { it.sortBy { it.size } }
            FilesSortOrder.SIZE_DESC -> list.mutate { it.sortByDescending { it.size } }
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
package com.android.aftools.data.entities

import kotlinx.collections.immutable.PersistentList
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
data class IntList(
    @Serializable(with = IntListSerializer::class)
    val list: PersistentList<Int> = persistentListOf()
) {
    fun delete(id: Int): IntList {
        val newList = list.remove(id)
        return IntList(newList)
    }

    fun deleteMultiple(ids: Collection<Int>): IntList {
        val newList = list.removeAll(ids)
        return IntList(newList)
    }

    fun add(profile: Int): IntList {
        val newList = list.add(profile)
        return IntList(newList)
    }

    fun addMultiple(profiles: Collection<Int>): IntList {
        val newList = list.addAll(profiles)
        return IntList(newList)
    }
}

@OptIn(ExperimentalSerializationApi::class)
class IntListSerializer(
    private val serializer: KSerializer<Int>,
) : KSerializer<PersistentList<Int>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<Int>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<Int>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<Int> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }

}
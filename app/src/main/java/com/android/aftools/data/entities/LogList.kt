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

@Serializable
data class LogList(
    @Serializable(with = LogListSerializer::class)
    val list: PersistentList<LogDatastore> = persistentListOf()
) {
    fun getAvailableDays(): Set<Long> {
        return list.map { it.day }.toSet()
    }

    fun getLogsForDay(day: Long): LogList {
        return LogList(list.mutate { mutableList -> mutableList.filter { it.day== day } })
    }

    fun deleteLogsForDays(days: List<Long>): LogList {
        return LogList(list.mutate {  mutableList -> mutableList.filter { it.day !in days } })
    }

    fun insertLogEntry(logEntry: LogDatastore): LogList {
        return LogList(list.add(logEntry.copy(id= list.lastOrNull()?.id?.plus(1) ?: 1)))
    }
}

@OptIn(ExperimentalSerializationApi::class)
class LogListSerializer(
    private val serializer: KSerializer<LogDatastore>,
) : KSerializer<PersistentList<LogDatastore>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<LogDatastore>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<LogDatastore>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<LogDatastore> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }

}
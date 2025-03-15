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
 * Class for representing log entries.
 *
 * Why do I use encrypted DataStore instead of encrypted database for storing lists of items? SQLCypher, the library for database encryption, encrypts and decrypts data using encryption key stored in the RAM. On some devices it's possible to dump RAM without the root rights and extract the encryption key, as demonstrated in this [post](https://cellebrite.com/en/decrypting-databases-using-ram-dump-health-data/) by the Cellebrite. On the other hand, in this app data is encrypted and decrypted in Android KeyStore, isolated environment where cryptographic operations may be executed safely and keys can't be extracted. It's easier to implement such an encryption with Datastore than with database.
 */
@Serializable
data class LogList(
    @Serializable(with = LogListSerializer::class)
    val list: PersistentList<LogDatastore> = persistentListOf()
) {
    fun getAvailableDays(): Set<Long> {
        return list.map { it.day }.toSet()
    }

    fun getLogsForDay(day: Long): LogList {
        return LogList(list.mutate { mutableList -> mutableList.retainAll { it.day == day } })
    }

    fun deleteLogsForDays(days: Set<Long>): LogList {
        return LogList(list.mutate { mutableList -> mutableList.retainAll { it.day !in days } })
    }

    fun insertLogEntry(logEntry: LogDatastore): LogList {
        return LogList(list.add(logEntry.copy(id = list.lastOrNull()?.id?.plus(1) ?: 1)))
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
package com.sonozaki.bedatastore.entities

internal sealed class EncryptedData<T> {
    class EmptyResult<T>: EncryptedData<T>()
    data class Result<T>(val data: T): EncryptedData<T>()
}
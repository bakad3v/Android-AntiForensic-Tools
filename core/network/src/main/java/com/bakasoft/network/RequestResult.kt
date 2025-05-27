package com.bakasoft.network

sealed class RequestResult<T> {
    data class Error<T>(val error: NetworkError): RequestResult<T>()
    data class Data<T>(val data: T): RequestResult<T>()
}
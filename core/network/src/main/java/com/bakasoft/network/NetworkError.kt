package com.bakasoft.network

sealed class NetworkError {
    data class ServerError(val code: Int, val description: String): NetworkError()
    data object ConnectionError: NetworkError()
    data object EmptyResponse: NetworkError()
    data class UnknownError(val error: String): NetworkError()
}
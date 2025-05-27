package com.bakasoft.network

import com.bakasoft.network.NetworkError.ConnectionError
import com.bakasoft.network.NetworkError.ServerError
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * Retrieving API request result in a safe way, mapping network exceptions to NetworkError instances
 */
suspend fun <T> safeApiCall(unsafeCall: suspend () -> Response<T>): RequestResult<T> {
    return try {
        val response = unsafeCall()
        if (response.isSuccessful) {
            val body = response.body()
            return if (body == null) {
                RequestResult.Error(NetworkError.EmptyResponse)
            } else {
                RequestResult.Data(body)
            }
        }
        return RequestResult.Error(NetworkError.ServerError(response.code(), response.message()))
    } catch (e: HttpException) {
        return RequestResult.Error(ServerError(e.code(), e.message ?: ""))
    } catch (_: IOException) {
        return RequestResult.Error(ConnectionError)
    } catch (e: Exception) {
        return RequestResult.Error(NetworkError.UnknownError(e.message ?: ""))
    }
}
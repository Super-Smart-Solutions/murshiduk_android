package com.saatco.murshadik.api

import retrofit2.Response
import java.io.IOException

object APIHelper {
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): T {
        val response = call.invoke()
        if (response.isSuccessful) {
            return response.body() ?: throw IOException("Response body is null")
        } else {
            throw IOException("API call failed with error: ${response.code()} ${response.message()}")
        }
    }
}
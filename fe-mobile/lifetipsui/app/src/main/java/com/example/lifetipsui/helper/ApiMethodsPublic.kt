package com.example.lifetipsui.helper

import android.os.Build
import androidx.annotation.RequiresApi
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.*


class ApiMethodsPublic {
    companion object {
        // Khởi tạo Ktor client với logging
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun getRaw(
            url: String,
            params: Map<String, String>? = null
        ): String {
            return client.get(url) {
                if (!params.isNullOrEmpty()) {
                    url {
                        params.forEach { (key, value) ->
                            parameters.append(key, value)
                        }
                    }
                }
            }.bodyAsText()
        }


        suspend inline fun <reified T> post(
            url: String,
            data: Any? = null
        ): T {
            return client.post(url) {
                contentType(ContentType.Application.Json)
                if (data != null) {
                    setBody(data)
                }
            }.body()
        }

        // PUT method với body là một object (data)
        suspend inline fun <reified T> put(
            url: String,
            data: Any? = null
        ): T {
            return client.put(url) {
                contentType(ContentType.Application.Json)
                setBody(data ?: "")
            }.body()
        }

        // DELETE method với optional path params hoặc body
        suspend inline fun <reified T> delete(
            url: String,
            params: Map<String, String>? = null,
            data: Any? = null
        ): T {
            return client.delete(url) {
                params?.forEach { (key, value) ->
                    url {
                        parameters.append(key, value)
                    }
                }
                if (data != null) {
                    contentType(ContentType.Application.Json)
                    setBody(data)
                }
            }.body()
        }
    }
}

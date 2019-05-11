package com.bawer.tasks.revolut.ewallet.test

import com.bawer.tasks.revolut.ewallet.model.response.ApiResponse
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.get
import io.ktor.client.request.port
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions

@Suppress("TestFunctionName")
internal abstract class BaseControllerTests {

    private val gson = Gson()

    protected suspend inline fun <reified T> HttpResponse.getApiResponse(): ApiResponse<*> {
        val responseString = readText()
        Assertions.assertNotNull(responseString)
        Assertions.assertTrue(responseString.isNotEmpty())
        val apiResponse = gson.fromJson(responseString, ApiResponse::class.java)
        Assertions.assertNotNull(apiResponse)
        return if (apiResponse.returnObject != null && (T::class.java != List::class.java)) {
            ApiResponse(
                    returnObject = gson.fromJson(gson.toJson(apiResponse.returnObject), T::class.java),
                    errorDescription = apiResponse.errorDescription,
                    status = apiResponse.status
            )
        } else apiResponse
    }

    protected suspend fun GET(uriString: String) = client.get<HttpResponse>(formUrlString(uriString))

    protected suspend fun POST(
            uriString: String,
            requestBody: Any
    ) = client.call(formUrlString(uriString)) {
        method = HttpMethod.Post
        port = TEST_PORT
        body = TextContent(gson.toJson(requestBody), contentType = ContentType.Application.Json)
    }.response

    private fun formUrlString(uriString: String) = "$BASE_URL$uriString"

    companion object {

        internal const val TEST_PORT = 8081
        private const val BASE_URL = "http://localhost:$TEST_PORT"

        private val client = HttpClient()

        @JvmStatic
        @AfterAll
        fun tearDown() { client.close() }
    }
}
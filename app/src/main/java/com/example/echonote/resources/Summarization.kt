package com.example.echonote.resources

import Message
import OpenAIRequest
import OpenAIResponse
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.UnknownHostException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun sanitizeInput(input: String): String {
    return input
        .replace("\\", "\\\\")  // Escape backslashes
        .replace("\"", "\\\"")  // Escape double quotes
        .replace("\n", "")
}


class Summarization {
    private val url = "https://api.openai.com/v1/chat/completions"
    private val apiKey = "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"
    private val client = OkHttpClient()

    fun getSummary(question: String, callback: (String) -> Unit) {
        val sanitizedQuestion = sanitizeInput(question)
        val requestObject = OpenAIRequest(
            model = "gpt-4o-mini-2024-07-18",
            messages = listOf(
                Message(
                    role = "system",
                    content = "You are the best note taker."
                ),
                Message(
                    role = "user",
                    content = "Summarize the following lecture content into key takeaways. Start with a bold title (do not include the word 'title'), highlight the main points, important terms, and any conclusions or actionable items. Make the summary concise, focusing on the core concepts. Do not include any closing sentences or unnecessary remarks. Here is the lecture content: $sanitizedQuestion"
                )
            ),
            maxTokens = 1000,
            temperature = 0.0
        )
        val jsonRequestBody = Json.encodeToString(requestObject)

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(jsonRequestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val errorMessage = if (e is UnknownHostException) {
                    "Network connection error. Please check your internet connection."
                } else {
                    "Failed to summarize text! Error $e"
                }
                callback(errorMessage)
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback("Failed to summarize text! Error ${it.body?.string()}")
                        return
                    }
                    val responseBody = it.body?.string()
                    if (responseBody == null) {
                        callback("Empty response from server!")
                        return
                    }
                    try {
                        Log.d("API_RESPONSE", "Raw Response: $responseBody")
                        val json = Json { ignoreUnknownKeys = true }
                        val jsonResponse = json.decodeFromString<OpenAIResponse>(responseBody)
                        val summaryText = (jsonResponse.choices.firstOrNull()?.message?.content)
                            ?: "No summary available"
                        callback(summaryText)
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Error parsing response: ${e.message}", e)
                        callback("Error parsing the response!")
                    }
                }
            }
        })
    }
}

package com.example.echonote.resources

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.UnknownHostException

fun sanitizeInput(input: String): String {
    return input
        .replace("\\", "\\\\")  // Escape backslashes
        .replace("\"", "\\\"")  // Escape double quotes
}


class Summarization {
    private val url = "https://api.openai.com/v1/chat/completions"
    private val apiKey = "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"
    private val client = OkHttpClient()

    fun getSummary(question: String, callback: (String) -> Unit) {
        val sanitizedQuestion = sanitizeInput(question)

        val requestBody = """
        {
          "model": "gpt-4o-mini-2024-07-18",
          "messages": [
            {
              "role": "system",
              "content": "You are the best note taker."
            },
            {
              "role": "user",
              "content": "Summarize the following lecture content into key takeaways. Start with a bold title(do not include the word 'title'), highlight the main points, important terms, and any conclusions or actionable items. Make the summary concise, focusing on the core concepts. Here is the lecture content: $sanitizedQuestion"
            }
          ],
          "max_tokens": 1000,
          "temperature": 0
        }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
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
                        val jsonResponse = JSONObject(responseBody)
                        val choicesArray = jsonResponse.getJSONArray("choices")
                        val summaryText = choicesArray.getJSONObject(0).getJSONObject("message").getString("content")
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

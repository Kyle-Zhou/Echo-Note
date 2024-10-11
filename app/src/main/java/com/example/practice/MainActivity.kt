package com.example.practice

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etQuestion = findViewById<EditText>(R.id.etQuestion)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val txtResponse = findViewById<TextView>(R.id.txtResponse)
        txtResponse.movementMethod = ScrollingMovementMethod()

        // Initialize Markwon with LaTeX support
        val markwon = Markwon.builder(this)
            .usePlugin(JLatexMathPlugin.create(txtResponse.textSize, txtResponse.textSize)) // Pass Float values directly
            .build()

        btnSubmit.setOnClickListener {
            val question = etQuestion.text.toString()

            if (question.isNotEmpty()) {
                // Hide the keyboard after clicking the button
                hideKeyboard()

                getResponse(question) { response ->
                    runOnUiThread {
                        Log.d("API_RESPONSE", "Response received: $response")
                        markwon.setMarkdown(txtResponse, response) // Apply markdown + LaTeX rendering
                    }
                }
            } else {
                Toast.makeText(this, "Please input text to summarize!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideKeyboard() {
        // Get the InputMethodManager
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        // Hide the keyboard
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getResponse(question: String, callback: (String) -> Unit) {
        val apiKey = "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"
        val url = "https://api.openai.com/v1/chat/completions"

        val requestBody = """
        {
          "model": "gpt-4o-mini-2024-07-18",
          "messages": [
            {
              "role": "system",
              "content": "You are a best note taker."
            },
            {
              "role": "user",
              "content": "Summarize the following lecture content into key takeaways. Highlight the main points, important terms, and any conclusions or actionable items. Make the summary concise, focusing on the core concepts. $question"
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
                e.printStackTrace()
                Log.e("API_ERROR", "Failed to summarize text!")
                callback("Failed to summarize text!")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("API_ERROR", "Failed response from server")
                        callback("Failed to summarize text!")
                        return
                    }

                    val responseBody = it.body?.string()
                    if (responseBody != null) {
                        try {
                            Log.d("API_RESPONSE", "Raw Response: $responseBody")
                            val jsonResponse = JSONObject(responseBody)
                            val choicesArray = jsonResponse.getJSONArray("choices")
                            val summaryText = choicesArray.getJSONObject(0).getJSONObject("message").getString("content")

                            callback(summaryText) // Return the content to be displayed
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e("API_ERROR", "Error parsing response: ${e.message}")
                            callback("Error parsing the response!")
                        }
                    } else {
                        Log.e("API_ERROR", "Empty response from server")
                        callback("Empty response from server!")
                    }
                }
            }
        })
    }
}

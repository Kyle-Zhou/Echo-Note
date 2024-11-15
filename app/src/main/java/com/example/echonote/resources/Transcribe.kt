package com.example.echonote.resources

import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.concurrent.thread

class Transcribe {

    private val client = OkHttpClient()

    fun transcribeAudio(audioUrl: String) {
        val jsonBody = JSONObject().apply {
            put("audio_url", audioUrl)
            put("punctuate", true)
            put("speaker_labels", true)
            put("language_code", "en_us")
        }

        val request = Request.Builder()
            .url("https://api.assemblyai.com/v2/transcript")
            .addHeader("Authorization", "542b1d72e6f04f908f8b03455351f921")
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        println("Request failed: ${response.code}")
                    } else {
                        val responseBody = response.body?.string()
                        println("Response Body: $responseBody")

                        val jsonResponse = JSONObject(responseBody ?: "{}")
                        val jobId = jsonResponse.optString("id")

                        if (jobId.isNotEmpty()) {
                            // poll the status of the transcription job
                            pollForTranscriptionResult(jobId)
                        } else {
                            println("Job ID not found.")
                        }
                    }
                }
            }
        })
    }

    fun pollForTranscriptionResult(jobId: String) {
        val url = "https://api.assemblyai.com/v2/transcript/$jobId"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "542b1d72e6f04f908f8b03455351f921")
            .build()

        // polling logic inside a separate thread to avoid blocking the UI/main thread
        thread {
            var isCompleted = false
            while (!isCompleted) {
                try {
                    val response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        println("Response Body: $responseBody")

                        val jsonResponse = JSONObject(responseBody ?: "{}")

                        // check the transcription job status
                        val status = jsonResponse.optString("status")
                        if (status == "completed") {
                            // transcription is complete -> get the transcribed text
                            val audioFileUrl = jsonResponse.optString("audio_url")
                            val text = jsonResponse.optString("text")

                            println("Transcription Text: $audioFileUrl")
                            println("Transcription Text: $text")
                            isCompleted = true
                        } else {
                            // transcription still in progress -> print the status and wait before retrying
                            println("Transcription is still in progress. Status: $status")
                            Thread.sleep(5000)
                        }
                    } else {
                        println("Polling request failed: ${response.code}")
                        break
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    break
                }
            }
        }
    }
}

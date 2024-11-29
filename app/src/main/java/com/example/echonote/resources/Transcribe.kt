import com.example.echonote.utils.BaseEchoNoteException
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class Transcribe {

    private val client = OkHttpClient()

    suspend fun transcribeAudio(audioUrl: String): String {
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

        return suspendCancellableCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWith(Result.failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            continuation.resumeWith(Result.failure(IOException("Request failed: ${response.code}")))
                        } else {
                            val responseBody = response.body?.string()
                            val jsonResponse = JSONObject(responseBody ?: "{}")
                            val jobId = jsonResponse.optString("id")

                            if (jobId.isNotEmpty()) {
                                // Start polling for transcription result
                                GlobalScope.launch {
                                    try {
                                        val result = pollForTranscriptionResult(jobId)
                                        continuation.resumeWith(Result.success(result))
                                    } catch (e: Exception) {
                                        continuation.resumeWith(Result.failure(e))
                                    }
                                }
                            } else {
                                continuation.resumeWith(Result.failure(Exception("Job ID not found.")))
                            }
                        }
                    }
                }
            })
        }
    }

    private suspend fun pollForTranscriptionResult(jobId: String): String {
        val url = "https://api.assemblyai.com/v2/transcript/$jobId"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "542b1d72e6f04f908f8b03455351f921")
            .build()

        while (true) {
            try {
                val response = client.newCall(request).execute()
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "{}")
                        val status = jsonResponse.optString("status")

                        if (status == "completed") {
                            return jsonResponse.optString("text")
                        } else if (status == "failed") {
                            throw BaseEchoNoteException("Transcription failed: ${jsonResponse.optString("error")}")
                        }
                    } else {
                        throw BaseEchoNoteException("Polling request failed: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                throw e
            }
            delay(5000) // wait before retrying
        }
    }
}

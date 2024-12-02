package com.example.echonote.ui.pages

import Transcribe
import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.SnackbarHost
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.graphics.Color
import com.example.echonote.R
import com.example.echonote.data.models.FolderModel
import com.example.echonote.data.models.ItemModel
import com.example.echonote.data.persistence.SupabaseClient
import com.example.echonote.resources.Summarization
import com.example.echonote.ui.components.BottomSheetFragment
import com.example.echonote.ui.components.LeftRoundedRadioButton
import com.example.echonote.ui.components.RightRoundedRadioButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

enum class AudioState {IDLE, RECORDING,  TRANSCRIBING, LOADING}

@Composable
fun AddPageScreen(onCancel: () -> Unit) {
    val summarization = remember { Summarization() }

    var selectedOption by remember { mutableStateOf("Default") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var folderId by remember { mutableLongStateOf(-1L) }
    var folderModel by remember { mutableStateOf<FolderModel>(FolderModel(SupabaseClient, ::currentMoment)) }

    LaunchedEffect(Unit) {
        folderModel.init()
        if (folderModel.folders.isEmpty()) {
            delay(500)
        }
        folderModel.folders.firstOrNull()?.let { firstFolder ->
            folderId = firstFolder.id
            selectedOption = firstFolder.title
        }
    }

    var itemModel by remember(folderId) { mutableStateOf(ItemModel(SupabaseClient, ::currentMoment, folderId)) }
    LaunchedEffect(folderId) { itemModel.init() } // Re-init the model each time the folderId changes

    var selectedMode by remember { mutableStateOf("Record") }
    var textInput by remember { mutableStateOf("") }
    var recordedText by remember { mutableStateOf("Recorded Text will appear here") }
    val isRecordMode = selectedMode == "Record"
    var summaryText by remember { mutableStateOf("") }
    var isSheetVisible by remember { mutableStateOf(false) }

    var audioState by remember { mutableStateOf(AudioState.IDLE) }

    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val mediaRecorder = remember { MediaRecorder() }
    val transcribe = Transcribe()
    val tempFile = remember { File.createTempFile("temp_audio", ".mp4", context.cacheDir) }

    fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow((context as android.app.Activity).currentFocus?.windowToken, 0)
    }

    fun startRecording() {
        try {
            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(tempFile)
                prepare()
                start()
            }
            audioState = AudioState.RECORDING
        } catch (e: Exception) {
            println("Failed to start recording: ${e.message}")
        }
    }

    fun getInputStreamFromUri(uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            println("Error reading input stream: ${e.message}")
            null
        }
    }

    suspend fun uploadAndTranscribeAudio(filePath: String? = null, uri: Uri? = null) {
        audioState = AudioState.TRANSCRIBING
        try {
            val audioData = filePath?.let { File(it).readBytes() } ?: uri?.let { getInputStreamFromUri(it) }
            if (audioData == null || audioData.isEmpty()) throw Exception("Audio data is empty or unavailable")
            val fileName = "audio_${System.currentTimeMillis()}.mp4"
            val audioFileUrl = SupabaseClient.uploadAudioFileAndGetUrl(fileName, audioData)
            if (audioFileUrl.isNullOrEmpty()) throw Exception("Failed to upload audio file")
            val transcriptionResult = transcribe.transcribeAudio(audioFileUrl)
            if (transcriptionResult.isNullOrEmpty()) throw Exception("Transcription failed")
            recordedText = transcriptionResult
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar("Audio uploaded and transcribed successfully!")
            }
        } catch (e: Exception) {
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar("Upload failed: ${e.message}")
            }
        } finally {
            audioState = AudioState.IDLE
        }
    }

    suspend fun stopRecording() {
        try {
            mediaRecorder.apply {
                stop()
                reset()
            }
            uploadAndTranscribeAudio(tempFile.absolutePath)
        } catch (e: Exception) {
            println("Recording Failed: ${e.message}")
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar("Recording failed: ${e.message}")
            }
            audioState = AudioState.IDLE
        }
    }

    val audioPermission = Manifest.permission.RECORD_AUDIO
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) startRecording() else coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar("Permission denied")
        }
    }

    val pickAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch { uploadAndTranscribeAudio(uri = uri) }
        } else {
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar("No audio file selected")
            }
        }
    }

    fun openFilePicker() {
        pickAudioLauncher.launch("audio/*")
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(scaffoldState.snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.blue))
                .padding(padding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 5.dp)
            ) {
                Button(
                    onClick = {
                        textInput = ""
                        recordedText = ""
                        onCancel()
                    },
                    modifier = Modifier
                        .height(35.dp)
                        .width(110.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = Color.Transparent
                    ),
                    elevation = ButtonDefaults.elevation(0.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(
                        Icons.Filled.ArrowBackIos,
                        contentDescription = "Back to Home",
                        tint = colorResource(id = R.color.white),
                        modifier = Modifier.size(15.dp)
                    )
                    Text("Cancel", color = colorResource(id = R.color.white))
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 55.dp, start = 30.dp, end = 30.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box {
                    OutlinedButton(
                        onClick = { isDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.blue)),
                        border = null,
                        contentPadding = PaddingValues(5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                selectedOption,
                                color = colorResource(id = R.color.white),
                                fontSize = MaterialTheme.typography.h6.fontSize
                            )
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Dropdown Arrow",
                                tint = colorResource(id = R.color.white),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier
                            .background(color = colorResource(id = R.color.white))
                            .padding(start = 5.dp)
                    ) {
                        folderModel.folders.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedOption = option.title
                                    isDropdownExpanded = false
                                    folderId = option.id
                                    itemModel = ItemModel(SupabaseClient, ::currentMoment, folderId)
                                    coroutineScope.launch {
                                        itemModel.init()
                                    }
                                },
                                modifier = Modifier.background(color = colorResource(id = R.color.white))
                            ) {
                                Text(
                                    option.title,
                                    color = colorResource(id = R.color.blue),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LeftRoundedRadioButton(
                        selected = isRecordMode,
                        text = "Record",
                        onClick = { selectedMode = "Record" }
                    )
                    RightRoundedRadioButton(
                        selected = !isRecordMode,
                        text = "Text",
                        onClick = { selectedMode = "Text" }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            hideKeyboard()
                            if (isRecordMode) {
                                if (recordedText != "Recorded Text will appear here") {
                                    audioState = AudioState.LOADING
                                    isSheetVisible = true
                                    summarization.getSummary(recordedText) { response ->
                                        summaryText = response
                                        audioState = AudioState.IDLE
                                    }
                                } else {
                                    coroutineScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("Please record or upload audio first!")
                                    }
                                }
                            } else {
                                if (textInput.isNotEmpty()) {
                                    audioState = AudioState.LOADING
                                    isSheetVisible = true
                                    summarization.getSummary(textInput) { response ->
                                        summaryText = response
                                        audioState = AudioState.IDLE
                                    }
                                } else {
                                    coroutineScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("Please input text to summarize!")
                                    }
                                }
                            }
                        },
                        enabled = audioState == AudioState.IDLE,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(id = R.color.orange),
                            disabledBackgroundColor = colorResource(id = R.color.orange).copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .height(35.dp)
                            .width(110.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        if (audioState == AudioState.LOADING) {
                            CircularProgressIndicator(
                                color = colorResource(id = R.color.white),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                "Submit",
                                color = colorResource(id = R.color.white)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Filled.AutoAwesome,
                                contentDescription = "Send Icon",
                                tint = colorResource(id = R.color.white),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (isRecordMode) {
                    Button(
                        onClick = {
                            when (audioState) {
                                AudioState.RECORDING -> {
                                    coroutineScope.launch {
                                        stopRecording()
                                    }
                                }
                                AudioState.IDLE -> {
                                    launcher.launch(audioPermission)
                                }
                                else -> {
                                    // do nothing
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = when (audioState) {
                                AudioState.RECORDING -> colorResource(id = R.color.red)
                                else -> colorResource(id = R.color.blue)
                            }
                        )
                    ) {
                        Text(
                            when (audioState) {
                                AudioState.RECORDING -> "Stop Recording"
                                else -> "Record"
                            },
                            color = colorResource(id = R.color.white)
                        )
                    }
                    Button(
                        onClick = { openFilePicker() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(id = R.color.blue)
                        )
                    ) {
                        Text(
                            "Upload Audio File",
                            color = colorResource(id = R.color.white)
                        )
                    }

                    Text(
                        recordedText,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = colorResource(id = R.color.white)
                    )

                    if (audioState == AudioState.TRANSCRIBING) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = colorResource(id = R.color.white)
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        label = { Text("Enter text", color = colorResource(id = R.color.white)) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = colorResource(id = R.color.white),
                            cursorColor = colorResource(id = R.color.white),
                            focusedBorderColor = colorResource(id = R.color.white),
                            unfocusedBorderColor = colorResource(id = R.color.white),
                            placeholderColor = colorResource(id = R.color.white)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (isSheetVisible) {
                BottomSheetFragment(
                    summaryText = summaryText,
                    isLoading = audioState == AudioState.LOADING,
                    itemModel = itemModel,
                    onClose = {
                        isSheetVisible = false
                        summaryText = ""
                    },
                )
            }
        }
    }
}


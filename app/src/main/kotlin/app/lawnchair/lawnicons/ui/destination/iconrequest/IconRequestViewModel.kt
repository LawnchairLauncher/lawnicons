/*
 * Copyright 2025 Lawnchair Launcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.lawnchair.lawnicons.ui.destination.iconrequest

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.lawnicons.LawniconsScope
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.data.model.IconRequestData
import app.lawnchair.lawnicons.data.model.SystemIconInfo
import app.lawnchair.lawnicons.data.repository.iconrequest.IconRequestHandler
import app.lawnchair.lawnicons.data.repository.iconrequest.IconRequestRepository
import app.lawnchair.lawnicons.data.repository.iconrequest.formatIconRequestList
import app.lawnchair.lawnicons.ui.util.copyTextToClipboard
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ViewModelKey(IconRequestViewModel::class)
@ContributesIntoMap(LawniconsScope::class)
class IconRequestViewModel(
    private val iconRequestRepository: IconRequestRepository,
    private val requestHandler: IconRequestHandler,
) : ViewModel() {
    val availableIcons = iconRequestRepository.iconRequestList
        .map { it?.list ?: emptyList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    private val _selectedIcons = MutableStateFlow<List<SystemIconInfo>>(emptyList())
    val selectedIcons: StateFlow<List<SystemIconInfo>> = _selectedIcons.asStateFlow()

    private val _isSavingInProgress = MutableStateFlow(false)
    val isSavingInProgress: StateFlow<Boolean> = _isSavingInProgress.asStateFlow()

    fun toggleSelection(icon: SystemIconInfo) {
        _selectedIcons.update {
            val currentList = it.toMutableList()
            if (!currentList.remove(icon)) {
                currentList.add(icon)
            }
            currentList.sortedBy { systemIconInfo -> systemIconInfo.label }
        }
    }

    fun selectAll() {
        val currentAvailableIcons = availableIcons.value
        _selectedIcons.update { currentAvailableIcons }
    }

    fun clearSelection() {
        _selectedIcons.update { emptyList() }
    }

    fun requestIcons(
        context: Context,
    ) {
        if (_selectedIcons.value.isEmpty()) {
            Log.d(TAG, "No icons selected for request.")
            return
        }

        viewModelScope.launch {
            _isSavingInProgress.update { true }
            when (val result = createIconRequestZipFile(_selectedIcons.value)) {
                is ZipCreationResult.Success -> {
                    val zipFile = result.file
                    val componentList = formatIconRequestList(_selectedIcons.value)

                    val requestData = IconRequestData(zipFile, componentList)

                    withContext(Dispatchers.Main) {
                        requestHandler.execute(context, requestData)
                    }
                }

                is ZipCreationResult.Error -> {
                    // no-op
                }
            }
            _isSavingInProgress.update { false }
        }
    }

    fun saveFile(context: Context, uri: Uri?, wasSuccessful: Boolean) {
        if (!wasSuccessful || uri == null) {
            Log.d(TAG, "Save operation cancelled or failed to get URI.")
            return
        }

        viewModelScope.launch {
            _isSavingInProgress.update { true }
            when (val result = createIconRequestZipFile(_selectedIcons.value)) {
                is ZipCreationResult.Success -> {
                    val success = writeZipToUriInternal(context, uri, result.file)

                    if (success) {
                        showToast(context, R.string.toast_file_saved_successful)
                    } else {
                        Log.e(TAG, "Error writing file to destination.")
                        showToast(context, R.string.toast_file_save_error)
                    }
                    result.file.delete()
                }

                is ZipCreationResult.Error -> {
                    showToast(context, R.string.toast_file_zip_error)
                }
            }
            _isSavingInProgress.update { false }
        }
    }

    fun shareFile(context: Context) {
        prepareAndShareIconRequest(context) { uri ->
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "application/zip"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                Intent.createChooser(this, "Share icon request")
            }
        }
    }

    fun copyComponents(context: Context) {
        context.copyTextToClipboard(formatIconRequestList(selectedIcons.value))
    }

    sealed class ZipCreationResult {
        data class Success(val file: File) : ZipCreationResult()
        data object Error : ZipCreationResult()
    }

    private suspend fun createIconRequestZipFile(icons: List<SystemIconInfo>): ZipCreationResult {
        return try {
            val tempZipFile = iconRequestRepository.createIconRequestZip(icons)
            if (tempZipFile != null) {
                ZipCreationResult.Success(tempZipFile)
            } else {
                Log.e(TAG, "Could not create temporary ZIP.")
                ZipCreationResult.Error
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during ZIP creation", e)
            ZipCreationResult.Error
        }
    }

    private suspend fun writeZipToUriInternal(
        context: Context,
        destinationUri: Uri,
        sourceZipFile: File,
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openFileDescriptor(destinationUri, "w")?.use { pfd ->
                FileOutputStream(pfd.fileDescriptor).use { outputStream ->
                    sourceZipFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error writing ZIP to URI", e)
            false
        }
    }

    private fun prepareAndShareIconRequest(context: Context, intentCreator: (Uri) -> Intent) {
        if (_selectedIcons.value.isEmpty()) {
            Log.d(TAG, "No icons selected.")
            return
        }

        viewModelScope.launch {
            when (val result = createIconRequestZipFile(_selectedIcons.value)) {
                is ZipCreationResult.Success -> {
                    try {
                        val authority = "${context.packageName}.fileprovider"
                        val uri = FileProvider.getUriForFile(context, authority, result.file)
                        val intent = intentCreator(uri)

                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        } else {
                            Log.e(TAG, "No app found to handle the intent.")
                            showToast(context, R.string.toast_no_app_available)
                        }
                    } catch (e: IllegalArgumentException) {
                        Log.e(TAG, "FileProvider Error: ${e.localizedMessage}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error handling intent: ${e.localizedMessage}")
                    }
                }

                is ZipCreationResult.Error -> {
                    showToast(context, R.string.toast_file_zip_error)
                }
            }
        }
    }

    private fun showToast(context: Context, messageResId: Int) {
        Toast.makeText(context, context.getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}

private const val TAG = "IconRequestViewModel"

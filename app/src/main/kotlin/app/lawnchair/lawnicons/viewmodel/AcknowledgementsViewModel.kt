package app.lawnchair.lawnicons.viewmodel

import androidx.lifecycle.ViewModel
import app.lawnchair.lawnicons.repository.OssLibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AcknowledgementsViewModel @Inject constructor(ossLibraryRepository: OssLibraryRepository) : ViewModel() {
    val ossLibraries = ossLibraryRepository.ossLibraries
}

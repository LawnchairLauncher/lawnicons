package app.lawnchair.lawnicons

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AcknowledgementsViewModel @Inject constructor(ossLibraryRepository: OssLibraryRepository) : ViewModel() {
    val ossLibraries = ossLibraryRepository.ossLibraries
}

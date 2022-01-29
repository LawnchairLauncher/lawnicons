package app.lawnchair.lawnicons

import android.app.Application
import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AcknowledgementViewModel @Inject constructor(
    private val application: Application,
    private val ossLibraryRepository: OssLibraryRepository,
) : ViewModel() {

    fun getNotice(
        name: String,
        linkStyle: SpanStyle,
    ) = flow {
        ossLibraryRepository.ossLibraries.value?.find { it.name == name }?.run {
            val notice = getNotice(application, R.raw.third_party_licenses)
            val spannable = SpannableString(notice)
            Linkify.addLinks(spannable, Linkify.WEB_URLS)
            val urlSpans = spannable.getSpans(0, notice.length, URLSpan::class.java)
            val annotated = buildAnnotatedString {
                append(notice)
                urlSpans.forEach {
                    val start = spannable.getSpanStart(it)
                    val end = spannable.getSpanEnd(it)
                    addStyle(
                        start = start,
                        end = end,
                        style = linkStyle,
                    )
                    addStringAnnotation(
                        tag = "URL",
                        annotation = it.url,
                        start = start,
                        end = end,
                    )
                }
            }
            emit(annotated)
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
}

package app.lawnchair.lawnicons.viewmodel

import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.lawnicons.repository.OssLibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AcknowledgementViewModel @Inject constructor(
    private val ossLibraryRepository: OssLibraryRepository,
) : ViewModel() {

    val ossLibraries = ossLibraryRepository.ossLibraries

    fun getNoticeForOssLibrary(
        ossLibraryName: String,
        linkStyle: SpanStyle,
    ): StateFlow<AnnotatedString?> = ossLibraryRepository.getNoticeForOssLibrary(
        ossLibraryName = ossLibraryName,
        annotate = { annotate(it, linkStyle) },
    )
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private fun annotate(
        notice: String,
        linkStyle: SpanStyle,
    ): AnnotatedString {
        val spannable = SpannableString(notice)
        Linkify.addLinks(spannable, Linkify.WEB_URLS)
        val urlSpans = spannable.getSpans(0, notice.length, URLSpan::class.java)
        return buildAnnotatedString {
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
    }
}

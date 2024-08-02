package app.lawnchair.lawnicons.repository

import android.content.SharedPreferences
import javax.inject.Inject

// TODO: Add additional tips
interface UserTipsRepository {
    fun hasClickedIconRequestButton(): Boolean
    fun onIconRequestButtonClicked()

    fun clearTips()
}

class UserTipsRepositoryImpl @Inject constructor(private val prefs: SharedPreferences) : UserTipsRepository {

    companion object {
        const val ICON_REQUEST_BUTTON_CLICKED = "icon_request_button_clicked"
    }

    override fun hasClickedIconRequestButton(): Boolean = prefs.get(ICON_REQUEST_BUTTON_CLICKED)
    override fun onIconRequestButtonClicked() = prefs.set(ICON_REQUEST_BUTTON_CLICKED, true)

    override fun clearTips() = prefs.edit().clear().apply()
}

fun SharedPreferences.get(key: String): Boolean = getBoolean(key, false)
fun SharedPreferences.set(key: String, value: Boolean) = edit().putBoolean(key, value).apply()

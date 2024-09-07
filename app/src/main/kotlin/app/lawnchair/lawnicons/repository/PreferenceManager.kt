package app.lawnchair.lawnicons.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext

/**
 * A class that abstracts the functionality of SharedPreferences
 * @param prefs The SharedPreferences instance to use
 */
abstract class BasePreferenceManager(
    private val prefs: SharedPreferences,
) {
    private val editor = prefs.edit()

    /**
     * A class that represents a boolean preference
     * @param key The key of the preference
     * @param defaultValue The default value of the preference
     */
    inner class BoolPref(
        val key: String,
        private val defaultValue: Boolean,
    ) {
        fun get() = prefs.getBoolean(key, defaultValue)
        fun set(value: Boolean) = editor.putBoolean(key, value).apply()

        fun toggle() = set(!get())

        @Composable
        fun asState(): State<Boolean> {
            return produceState(initialValue = get(), this) {
                val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
                    if (changedKey == key) {
                        value = get() // Update the state value when the preference changes
                    }
                }
                prefs.registerOnSharedPreferenceChangeListener(listener)
                awaitDispose {
                    prefs.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }
        }
    }
}

/**
 * Provides a class to handle Lawnicons preferences
 */
class PreferenceManager private constructor(
    prefs: SharedPreferences,
) : BasePreferenceManager(prefs) {
    val showFirstLaunchSnackbar = BoolPref("show_first_launch_snackbar", true)

    companion object {
        @Volatile
        private var instance: PreferenceManager? = null

        /**
         * Returns a singleton instance of PreferenceManager
         */
        fun getInstance(context: Context): PreferenceManager {
            return instance ?: synchronized(this) {
                instance ?: PreferenceManager(
                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE),
                ).also { instance = it }
            }
        }
    }
}

@Composable
fun preferenceManager(context: Context = LocalContext.current) = PreferenceManager.getInstance(context)

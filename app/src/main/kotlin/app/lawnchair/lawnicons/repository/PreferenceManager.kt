package app.lawnchair.lawnicons.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import app.lawnchair.lawnicons.BuildConfig

/**
 * A class that abstracts the functionality of SharedPreferences
 * We use SharedPreferences to avoid the unnecessary complexity Preference DataStore has
 *
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

    /**
     * A class that represents a integer preference
     * @param key The key of the preference
     * @param defaultValue The default value of the preference
     */
    inner class IntPref(
        val key: String,
        private val defaultValue: Int,
    ) {
        fun get() = prefs.getInt(key, defaultValue)
        fun set(value: Int) = editor.putInt(key, value).apply()

        @Composable
        fun asState(): State<Int> {
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

class PreferenceManager private constructor(
    prefs: SharedPreferences,
) : BasePreferenceManager(prefs) {
    val showFirstLaunchSnackbar = BoolPref("show_first_launch_snackbar", true)
    val showNewIconsCard = BoolPref("show_new_icons_card", true)
    val showDebugMenu = BoolPref("debug_menu", false)
    val forceEnableIconRequest = BoolPref("force_icon_request", false)
    val currentLawniconsVersion = IntPref("current_lawnicons_version", BuildConfig.VERSION_CODE)

    /**
     * Provides a class to handle Lawnicons preferences.
     *
     * Use [PreferenceManager.getInstance] to get the instance for use thoughout the app.
     * @see preferenceManager
     */
    companion object {
        @Volatile
        private var instance: PreferenceManager? = null

        /**
         * Returns a singleton instance of [PreferenceManager]
         */
        fun getInstance(context: Context): PreferenceManager {
            return instance ?: synchronized(this) {
                instance ?: PreferenceManager(
                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE),
                ).also { instance = it }
            }
        }

        /**
         * Get dummy instance of [PreferenceManager] for testing and Compose previews
         */
        fun getDummyInstance(): PreferenceManager {
            return PreferenceManager(DummySharedPreferences())
        }
    }
}

/**
 * Returns a singleton instance of [PreferenceManager] for use in Composable UIs.
 *
 * In [LocalInspectionMode], the dummy instance is returned instead.
 *
 * @param context the context to use for getting the shared preferences
 * @return a singleton instance of [PreferenceManager]
 */
@Composable
fun preferenceManager(context: Context = LocalContext.current) = if (LocalInspectionMode.current) {
    PreferenceManager.getDummyInstance()
} else {
    PreferenceManager.getInstance(context)
}

/**
 * Dummy implementation of [SharedPreferences] for Compose previews, with mock default values
 */
class DummySharedPreferences : SharedPreferences {
    override fun getAll() = mutableMapOf<String, List<*>>()
    override fun getBoolean(key: String?, defValue: Boolean) = true
    override fun getString(key: String?, defValue: String?) = ""
    override fun getStringSet(key: String?, defValues: MutableSet<String>?) = mutableSetOf<String>()
    override fun getLong(key: String?, defValue: Long) = 0L
    override fun getFloat(key: String?, defValue: Float) = 0.0f
    override fun getInt(key: String?, defValue: Int) = 0
    override fun contains(key: String?) = true
    override fun edit() = DummyEditor()
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

    /**
     * Dummy implementation of [SharedPreferences.Editor] for Compose previews
     */
    class DummyEditor : SharedPreferences.Editor {
        override fun putString(key: String?, value: String?) = DummyEditor()
        override fun putStringSet(key: String?, values: MutableSet<String>?) = DummyEditor()

        override fun putInt(key: String?, value: Int) = DummyEditor()
        override fun putLong(key: String?, value: Long) = DummyEditor()
        override fun putFloat(key: String?, value: Float) = DummyEditor()
        override fun putBoolean(key: String?, value: Boolean) = DummyEditor()
        override fun remove(key: String?) = DummyEditor()
        override fun clear() = DummyEditor()

        override fun commit() = true
        override fun apply() {}
    }
}

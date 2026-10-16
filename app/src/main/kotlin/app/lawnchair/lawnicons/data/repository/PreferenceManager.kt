package app.lawnchair.lawnicons.data.repository

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.core.content.edit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun interface PreferenceChangeListener {
    fun onPreferenceChanged(key: String)
}

/**
 * Backend-agnostic preference storage used by [BasePreferenceManager].
 */
interface PreferenceStore {
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)

    fun getInt(key: String, defaultValue: Int): Int
    fun putInt(key: String, value: Int)

    fun registerListener(listener: PreferenceChangeListener)
    fun unregisterListener(listener: PreferenceChangeListener)
}

class SharedPreferencesStore(
    private val prefs: SharedPreferences,
) : PreferenceStore {
    private val listeners =
        mutableMapOf<PreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener>()

    override fun getBoolean(key: String, defaultValue: Boolean) = prefs.getBoolean(key, defaultValue)

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    override fun getInt(key: String, defaultValue: Int) = prefs.getInt(key, defaultValue)

    override fun putInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    override fun registerListener(listener: PreferenceChangeListener) {
        val sharedPrefsListener =
            SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
                changedKey?.let(listener::onPreferenceChanged)
            }
        listeners[listener] = sharedPrefsListener
        prefs.registerOnSharedPreferenceChangeListener(sharedPrefsListener)
    }

    override fun unregisterListener(listener: PreferenceChangeListener) {
        val sharedPrefsListener = listeners.remove(listener) ?: return
        prefs.unregisterOnSharedPreferenceChangeListener(sharedPrefsListener)
    }
}

class InMemoryPreferenceStore : PreferenceStore {
    private val values = mutableMapOf<String, Any>()
    private val listeners = linkedSetOf<PreferenceChangeListener>()

    override fun getBoolean(key: String, defaultValue: Boolean) = values[key] as? Boolean ?: defaultValue

    override fun putBoolean(key: String, value: Boolean) {
        values[key] = value
        notifyChanged(key)
    }

    override fun getInt(key: String, defaultValue: Int) = values[key] as? Int ?: defaultValue

    override fun putInt(key: String, value: Int) {
        values[key] = value
        notifyChanged(key)
    }

    override fun registerListener(listener: PreferenceChangeListener) {
        listeners += listener
    }

    override fun unregisterListener(listener: PreferenceChangeListener) {
        listeners -= listener
    }

    private fun notifyChanged(key: String) {
        listeners.forEach { it.onPreferenceChanged(key) }
    }
}

abstract class BasePreferenceManager(
    private val store: PreferenceStore,
) {
    fun registerListener(listener: PreferenceChangeListener) = store.registerListener(listener)
    fun unregisterListener(listener: PreferenceChangeListener) = store.unregisterListener(listener)

    /**
     * A class that represents a boolean preference
     * @param key The key of the preference
     * @param defaultValue The default value of the preference
     */
    inner class BoolPref(
        val key: String,
        private val defaultValue: Boolean,
    ) {
        fun get() = store.getBoolean(key, defaultValue)
        fun set(value: Boolean) = store.putBoolean(key, value)

        fun toggle() = set(!get())

        fun asFlow(): Flow<Boolean> = callbackFlow {
            trySend(get())
            val listener = PreferenceChangeListener { changedKey ->
                if (changedKey == key) {
                    trySend(get())
                }
            }
            registerListener(listener)
            awaitClose {
                unregisterListener(listener)
            }
        }

        @Composable
        fun asState(): State<Boolean> {
            return produceState(initialValue = get(), this) {
                val listener = PreferenceChangeListener { changedKey ->
                    if (changedKey == key) {
                        value = get()
                    }
                }
                store.registerListener(listener)
                awaitDispose {
                    store.unregisterListener(listener)
                }
            }
        }
    }

    /**
     * A class that represents an integer preference
     * @param key The key of the preference
     * @param defaultValue The default value of the preference
     */
    inner class IntPref(
        val key: String,
        private val defaultValue: Int,
    ) {
        fun get() = store.getInt(key, defaultValue)
        fun set(value: Int) = store.putInt(key, value)

        @Composable
        fun asState(): State<Int> {
            return produceState(initialValue = get(), this) {
                val listener = PreferenceChangeListener { changedKey ->
                    if (changedKey == key) {
                        value = get()
                    }
                }
                store.registerListener(listener)
                awaitDispose {
                    store.unregisterListener(listener)
                }
            }
        }
    }
}

@Inject
@SingleIn(AppScope::class)
class PreferenceManager(
    store: PreferenceStore,
) : BasePreferenceManager(store) {
    val forceEnableIconRequest = BoolPref("force_icon_request", false)
    val isFirstLaunch = BoolPref("first_launch", true)
}

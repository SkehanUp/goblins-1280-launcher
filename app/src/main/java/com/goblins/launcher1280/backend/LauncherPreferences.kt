package com.goblins.launcher1280.backend

import android.content.Context
import android.content.SharedPreferences

class LauncherPreferences(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        "goblins_1280_prefs",
        Context.MODE_PRIVATE,
    )

    var themeId: String
        get() = preferences.getString(KEY_THEME_ID, DEFAULT_THEME_ID) ?: DEFAULT_THEME_ID
        set(value) {
            preferences.edit().putString(KEY_THEME_ID, value).apply()
        }

    var highScoreSnake: Int
        get() = preferences.getInt(KEY_HIGH_SCORE_SNAKE, 0)
        set(value) {
            preferences.edit().putInt(KEY_HIGH_SCORE_SNAKE, value).apply()
        }

    var carrierOverride: String
        get() = preferences.getString(KEY_CARRIER_OVERRIDE, DEFAULT_CARRIER) ?: DEFAULT_CARRIER
        set(value) {
            preferences.edit().putString(KEY_CARRIER_OVERRIDE, value).apply()
        }

    fun getSpeedDials(): Map<Int, String> = (MIN_SPEED_DIAL..MAX_SPEED_DIAL)
        .mapNotNull { digit ->
            preferences.getString(speedDialKey(digit), null)?.let { packageName ->
                digit to packageName
            }
        }
        .toMap()

    fun setSpeedDial(digit: Int, packageName: String?) {
        require(digit in MIN_SPEED_DIAL..MAX_SPEED_DIAL) { "Speed-dial digit must be 2..9" }
        val editor = preferences.edit()
        if (packageName == null) {
            editor.remove(speedDialKey(digit))
        } else {
            editor.putString(speedDialKey(digit), packageName)
        }
        editor.apply()
    }

    fun getPinnedPackages(): List<String> = preferences.getStringSet(KEY_PINNED_PACKAGES, emptySet())
        .orEmpty()
        .sorted()

    fun togglePinnedPackage(packageName: String): Boolean {
        val pinnedPackages = preferences.getStringSet(KEY_PINNED_PACKAGES, emptySet())
            .orEmpty()
            .toMutableSet()
        val isPinned = if (pinnedPackages.add(packageName)) {
            true
        } else {
            pinnedPackages.remove(packageName)
            false
        }
        preferences.edit().putStringSet(KEY_PINNED_PACKAGES, pinnedPackages).apply()
        return isPinned
    }

    private fun speedDialKey(digit: Int): String = "$KEY_SPEED_DIAL_PREFIX$digit"

    private companion object {
        const val KEY_THEME_ID = "theme_id"
        const val KEY_HIGH_SCORE_SNAKE = "high_score_snake"
        const val KEY_CARRIER_OVERRIDE = "carrier_override"
        const val KEY_PINNED_PACKAGES = "pinned_packages"
        const val KEY_SPEED_DIAL_PREFIX = "speed_dial_"
        const val DEFAULT_THEME_ID = "GOBLIN_GREEN"
        const val DEFAULT_CARRIER = "GOBLINS"
        const val MIN_SPEED_DIAL = 2
        const val MAX_SPEED_DIAL = 9
    }
}

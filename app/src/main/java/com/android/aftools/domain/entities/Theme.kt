package com.android.aftools.domain.entities

import androidx.appcompat.app.AppCompatDelegate

enum class Theme {
  DARK_THEME, LIGHT_THEME, SYSTEM_THEME;

  fun asMode(): Int {
    return when(this) {
      DARK_THEME -> AppCompatDelegate.MODE_NIGHT_YES
      LIGHT_THEME -> AppCompatDelegate.MODE_NIGHT_NO
      SYSTEM_THEME -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
  }
}

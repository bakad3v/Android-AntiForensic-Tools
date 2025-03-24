package com.sonozaki.utils

import android.view.View

fun booleanToVisibility(visible: Boolean): Int = if (visible) {
    View.VISIBLE
} else {
    View.GONE
}
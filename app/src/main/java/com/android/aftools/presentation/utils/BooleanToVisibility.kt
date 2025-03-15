package com.android.aftools.presentation.utils

import android.view.View

fun booleanToVisibility(visible: Boolean): Int = if (visible) {
    View.VISIBLE
} else {
    View.GONE
}
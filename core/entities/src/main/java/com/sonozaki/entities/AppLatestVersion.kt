package com.sonozaki.entities

data class AppLatestVersion(val usualVersionLink: String, val adbVersionLink: String, val newVersion: Boolean, val isTestOnly: Boolean, val version: String)
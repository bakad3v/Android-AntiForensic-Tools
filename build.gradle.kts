// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.6.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.49" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10" apply false
    id("org.jetbrains.kotlin.kapt") version "2.0.0-Beta4" apply false
    id("com.android.library") version "8.6.0" apply false
    id("org.jetbrains.kotlin.jvm") version "2.0.0-Beta4" apply false
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        val navVer = "2.7.7"
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:$navVer")
    }
}
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlin.kapt)
    kotlin("plugin.serialization")
    alias(libs.plugins.dependency.analysis)
}

android {
    namespace = "com.sonozaki.data.logs"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core:entities"))
    implementation(project(":core:encryptedDatastore"))
    implementation(project(":core:utils"))
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(project(":core:bedatastore"))
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.core.ktx)
    implementation(libs.kotlinx.datetime.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.datastore.preferences)
}
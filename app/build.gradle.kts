plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    kotlin("plugin.serialization")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.android.aftools"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.aftools"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "1.3.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

  buildFeatures {
    dataBinding = true
    viewBinding = true
  }

}

dependencies {
    // Core Android dependencies
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // Testing libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // Dagger Hilt for dependency injection
    val daggerVersion = "2.49"
    implementation("com.google.dagger:hilt-android:$daggerVersion")
    kapt("com.google.dagger:hilt-compiler:$daggerVersion")

    // Lifecycle and coroutines
    val lifecycleVersion = "2.8.4"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")

    val coroutinesVersion = "1.7.3"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    // Navigation
    val navigationVersion = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    // Additional libraries
    val datastoreVersion = "1.1.1"
    implementation("androidx.datastore:datastore-preferences:$datastoreVersion")

    val securityVersion = "1.1.0-alpha06"
    implementation("androidx.security:security-crypto-ktx:$securityVersion")

    val desugaringVersion = "2.0.4"
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$desugaringVersion")

    // LeakCanary (debug only)
    val leakCanaryVersion = "2.12"
    debugImplementation("com.squareup.leakcanary:leakcanary-android:$leakCanaryVersion")

    // Coil for image loading
    val coilVersion = "2.5.0"
    implementation("io.coil-kt:coil:$coilVersion")

    // Serialization
    val serializeVersion = "1.6.2"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializeVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")

    //Dhizuku
    val dhizukuVersion = "2.5.3"
    implementation ("io.github.iamr0s:Dhizuku-API:$dhizukuVersion")

    // Additional dependencies for your project
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
    implementation("com.github.topjohnwu.libsu:core:5.0.0")
    implementation("com.anggrayudi:storage:1.5.5")

}

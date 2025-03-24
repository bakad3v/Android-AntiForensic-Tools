plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.serialization)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.dependency.analysis)
}

android {
    namespace = "net.typeblog.shelter"
    compileSdk = 35

    defaultConfig {
        applicationId = "net.typeblog.shelter"
        minSdk = 26
        targetSdk = 35
        versionCode = 5
        versionName = "1.4.0"
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
    viewBinding = true
  }

}

dependencies {
    // Core Android dependencies
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(project(":core:activityState"))
    implementation(project(":core:dialogs"))
    implementation(project(":features:rootCommands"))
    implementation(project(":features:settings"))
    implementation(project(":core:entities"))
    implementation(project(":features:triggerReceivers"))
    implementation(project(":features:splash"))
    implementation(project(":features:lockscreen"))
    implementation(project(":features:services"))
    implementation(project(":core:encryptedDatastore"))
    implementation(project(":data:password"))
    implementation(project(":data:root"))
    implementation(project(":superuser"))
    implementation(project(":data:settings"))
    implementation(project(":data:files"))
    implementation(project(":data:logs"))
    implementation(project(":data:profiles"))

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Dagger Hilt for dependency injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Lifecycle and coroutines
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    implementation(libs.kotlinx.coroutines.core)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    // Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    implementation(libs.security.crypto.ktx)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    //Better Encrypted DataStore
    implementation(libs.better.datastore)

    // LeakCanary (debug only)
    debugImplementation(libs.leakcanary.android)

    // Coil for image loading
    implementation(libs.coil)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime.jvm)
    implementation(libs.kotlinx.collections.immutable)

    //Dhizuku
    implementation (libs.dhizuku.api)

    // Additional dependencies for your project
    implementation(libs.zxcvbn)
    implementation(libs.joda.time)
    implementation(libs.hiddenapibypass)
    implementation(libs.core)
    implementation(libs.storage)

    //Project modules dependencies
    implementation(project(":core:validators"))
    implementation(project(":core:passwordStrength"))
    implementation(project(":core:resources"))
    implementation(project(":core:utils"))
    implementation(project(":features:passwordSetup"))
    implementation(project(":features:profiles"))
    implementation(project(":features:files"))
    implementation(project(":features:logs"))
    implementation(project(":features:aboutApp"))
}

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
    namespace = "com.android.aftools"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.android.aftools"
        minSdk = 26
        targetSdk = 35
        versionCode = 8
        versionName = "2.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += listOf("name", "testOnly")

    productFlavors {
        create("original") {
            dimension = "name"
            applicationId = "com.android.aftools"
        }

        create("island") {
            dimension = "name"
            applicationId = "com.oasisfeng.island"
        }

        create("shelter") {
            dimension = "name"
            applicationId = "net.typeblog.shelter"
        }

        create("normal") {
            dimension = "testOnly"
        }

        create("adb") {
            dimension = "testOnly"
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
    buildConfig = true
    viewBinding = true
  }

}

dependencies {
    // Core Android dependencies
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Dagger Hilt for dependency injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)
    implementation(libs.hilt.work)

    // Lifecycle and coroutines
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    implementation(libs.kotlinx.coroutines.core)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    // Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

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

    //Network
    implementation(libs.retrofit)
    implementation(libs.converter.scalars)

    //Project modules dependencies
    implementation(project(":core:validators"))
    implementation(project(":core:passwordStrength"))
    implementation(project(":core:resources"))
    implementation(project(":core:utils"))
    implementation(project(":core:network"))
    implementation(project(":features:passwordSetup"))
    implementation(project(":features:profiles"))
    implementation(project(":features:files"))
    implementation(project(":features:logs"))
    implementation(project(":features:aboutApp"))
    implementation(project(":core:activityState"))
    implementation(project(":core:dialogs"))
    implementation(project(":features:rootCommands"))
    implementation(project(":features:appUpdateCenter"))
    implementation(project(":features:settings"))
    implementation(project(":core:entities"))
    implementation(project(":features:triggerReceivers"))
    implementation(project(":features:splash"))
    implementation(project(":features:lockscreen"))
    implementation(project(":features:services"))
    implementation(project(":features:setupWizard"))
    implementation(project(":core:encryptedDatastore"))
    implementation(project(":data:password"))
    implementation(project(":data:root"))
    implementation(project(":superuser"))
    implementation(project(":appInstaller"))
    implementation(project(":data:settings"))
    implementation(project(":data:files"))
    implementation(project(":data:logs"))
    implementation(project(":data:profiles"))
    implementation(project(":data:appUpdater"))
}

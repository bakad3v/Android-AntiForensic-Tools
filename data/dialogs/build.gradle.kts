plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dependency.analysis)
}

android {
    namespace = "com.bakad3v.dialogs"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.datastore.preferences)
    implementation(libs.hilt.android)
    implementation(libs.material)
    kapt(libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}

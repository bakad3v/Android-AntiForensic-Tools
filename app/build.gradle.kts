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
        versionCode = 3
        versionName = "1.2.0"
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
  val daggerVersion = "2.48"
  val datastoreVersion = "1.1.1"
  val dhizukuVersion = "2.5.3"
  val runtimeKtxVersion = "2.9.1"
  val lifecycleVersion = "2.8.4"
  val coroutinesVersion = "1.7.3"
  val navigationVersion = "2.7.7"
  val fragmentVersion = "1.8.2"
  val hiltworkerVersion = "1.2.0"
  val securityVersion = "1.1.0-alpha06"
  val desugaringVersion = "2.0.4"
  val hiltVersion = "2.49"
  val leakCanaryVersion = "2.12"
  val coilVersion = "2.5.0"
  val storageVersion = "1.5.5"
  val serializeVersion = "1.6.2"
  val libsuVersion = "5.0.0"
  val hiddenApiBypassVersion = "4.3"
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
  implementation ("com.google.dagger:dagger:$daggerVersion")
  kapt ("com.google.dagger:dagger-compiler:$daggerVersion")
  implementation ("androidx.datastore:datastore-preferences:$datastoreVersion")
  implementation( "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
  implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
  implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
  implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
  implementation("org.lsposed.hiddenapibypass:hiddenapibypass:$hiddenApiBypassVersion")
  implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
  kapt ("androidx.hilt:hilt-compiler:$hiltworkerVersion")
  implementation ("com.github.topjohnwu.libsu:core:$libsuVersion")
  implementation( "androidx.security:security-crypto-ktx:$securityVersion")
  coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:$desugaringVersion")
  debugImplementation ("com.squareup.leakcanary:leakcanary-android:$leakCanaryVersion")
  implementation ("com.google.dagger:hilt-android:$hiltVersion")
  kapt ("com.google.dagger:hilt-compiler:$hiltVersion")
  implementation("io.coil-kt:coil:$coilVersion")
  implementation ("com.anggrayudi:storage:$storageVersion")
  api("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.4.0")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializeVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
  implementation ("io.github.iamr0s:Dhizuku-API:$dhizukuVersion")
  androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
}

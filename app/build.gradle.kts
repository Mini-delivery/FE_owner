plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.minidelivery"
    compileSdk = 34

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.minidelivery"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildToolsVersion = "35.0.0"

    packagingOptions {
        resources {
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/io.netty.versions.properties")
        }
    }

    dependenciesInfo {
        includeInApk = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    implementation(libs.firebase.firestore.ktx)
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // HiveMQ MQTT 클라이언트
    implementation("com.hivemq:hivemq-mqtt-client:1.2.0")
}

kapt {
    correctErrorTypes = true
}

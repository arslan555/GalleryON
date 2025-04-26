plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.arslan.galleryon"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.arslan.galleryon"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {
    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Jetpack Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.preview)
    debugImplementation(libs.compose.ui.tooling)

    // Lifecycle & ViewModel
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.runtime)

    // Coroutines
    implementation(libs.coroutines.android)

    // Navigation
    implementation(libs.navigation.compose)

    // Media, Permissions & Data
    implementation(libs.media3.common)
    implementation(libs.accompanist.permissions)
    implementation(libs.datastore.preferences)

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.hilt.testing)
    // UI Testing
    androidTestImplementation(libs.compose.test.junit4)
    debugImplementation(libs.compose.test.manifest)
}

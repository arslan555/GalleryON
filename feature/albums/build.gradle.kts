plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "com.arslan.feature.albums"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {

    // Core AndroidX libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
// Use BOM
    implementation(platform(libs.compose.bom))

    // Compose dependencies
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.preview)
    implementation(libs.compose.navigation)
    implementation(libs.material.icons.extended)

    // ViewModel integration with Compose
    implementation(libs.lifecycle.viewmodel.compose)

    // MVI needs
    implementation(libs.coroutines.core)

    // Domain layer dependency (UseCases, Models)
    implementation(project(":domain"))
    implementation(project(":core"))

    implementation(libs.androidx.lifecycle.runtime.compose.android)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // Hilt for Compose ViewModels
    implementation(libs.hilt.navigation.compose)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
   // implementation(libs.coil.video)

    // (Optional) Mockk and Coroutines Test for local testing inside Albums module
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
}
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.arslan.core"
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.coroutines.android)
// Use BOM
    implementation(platform(libs.compose.bom))

    // Optional common UI tools
    implementation(libs.lifecycle.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.preview)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.foundation.android)
    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    }
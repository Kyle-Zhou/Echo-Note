plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version libs.versions.kotlin
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.echonote"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.echonote"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // Compatible with Kotlin 1.9.10
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx.v1101)
    implementation(libs.androidx.appcompat.v170)

    // Compose dependencies
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material)
    implementation (libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui.tooling.preview)
    implementation("androidx.compose.runtime:runtime-livedata:1.5.2")
    implementation("androidx.compose.material:material-icons-extended:1.5.2")

    // Navigation for Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.7.2")
    implementation(libs.androidx.junit.ktx)

    // Required for the Compose preview feature
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.2")

    // Testing dependencies
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Supabase:
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.1"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-android:3.0.0")

    // Other dependencies
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // OkHttp library
    implementation(libs.markwon.core)  // Markwon core dependency
    implementation(libs.markwon.latex) // Markwon KaTeX extension
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1") // Datetime
}

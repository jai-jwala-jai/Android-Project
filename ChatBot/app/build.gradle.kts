import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")

    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.android.hilt)
}

android {
    namespace = "com.example.chatbot"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.chatbot"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties().apply {
            load(rootProject.file("local.properties").inputStream())
        }

        val supabaseKey = properties.getProperty("SUPABASE_KEY")
        val supabaseUrl = properties.getProperty("SUPABASE_URL")
        val serverClientId = properties.getProperty("GOOGLE_SERVER_CLIENT_ID")

        buildConfigField("String", "SUPABASE_KEY", "\"$supabaseKey\"")
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "GOOGLE_SERVER_CLIENT_ID", "\"$serverClientId\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Firebase BOM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)

    // Supabase BOM
    implementation(platform(libs.bom))
    implementation(libs.postgrest.kt)
    implementation(libs.auth.kt)
    implementation(libs.realtime.kt)

    // Ktor engine
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.okhttp)

    // Dagger hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Google Credential
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Hiltviewmodel
    implementation(libs.androidx.hilt.navigation.compose)

    // Compose navigation
    implementation(libs.androidx.navigation.compose)

    // Lifecycle viewmodel
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Coil image processing
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Mark down
    implementation(libs.compose.markdown)
}
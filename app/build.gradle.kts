import java.util.Properties;

plugins {
    alias(libs.plugins.android.application)
}

val localProps = Properties()
localProps.load(rootProject.file("local.properties").inputStream())
val supabaseAnonKey: String = localProps.getProperty("SUPABASE_ANON_KEY")

android {
    namespace = "com.github.abrarshakhi.mmap"
    compileSdk {
        version = release(36)
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.github.abrarshakhi.mmap"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "SUPABASE_ANON_KEY",
            "\"$supabaseAnonKey\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // room
    implementation("androidx.room:room-runtime:2.8.4")
    annotationProcessor("androidx.room:room-compiler:2.8.4")
    testImplementation("androidx.room:room-testing:2.8.4")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // OkHttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // crypto
    implementation("androidx.security:security-crypto:1.1.0-alpha03")

//    implementation("io.github.lehuyh:supabase-java:1.1.0")
//    implementation("com.github.skhanal5:supabase-java:1.0.0")
//    implementation("io.github.supabase-community:supabase-kt:2.1.0")
}
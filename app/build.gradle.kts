plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.visionsignapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.visionsignapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core and lifecycle libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose libraries (using BOM for version management)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.5.3")

    // Additional UI dependencies
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material:material-icons-core:1.5.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.0")
    implementation("androidx.compose.material:material:1.5.0")
    implementation("com.google.accompanist:accompanist-navigation-material:0.25.1")
    implementation("com.google.android.material:material:1.10.0")

    //coil
    implementation("io.coil-kt.coil3:coil-compose:3.0.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.0")
    implementation( "io.coil-kt:coil-compose:2.0.0")
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debugging dependencies
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //retrofit import
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Additional UI dependencies
    implementation("com.github.CanHub:Android-Image-Cropper:4.0.0")

    //Datestore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    //Onboarding
    implementation("androidx.compose.foundation:foundation:1.5.2")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("com.google.accompanist:accompanist-pager:0.25.1")

    //AsyncImage
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("io.coil-kt:coil-gif:2.1.0")

    //Stripe
    implementation("com.stripe:stripe-android:20.36.0")

    //SwipeRefresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.36.0")

    //Agora VideoCall
    implementation("io.agora.rtc:full-sdk:3.5.0")

    implementation("com.google.android.gms:play-services-auth:20.4.0")

    //MediaPipe hand detection model
    //implementation("com.google.mediapipe:tasks-vision:0.10.14")
    //implementation("org.tensorflow:tensorflow-lite:2.12.0")
    //implementation("org.tensorflow:tensorflow-lite-support:0.4.3")
}
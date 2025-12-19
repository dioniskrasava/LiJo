plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Hilt 2.51.1 (ОК, если совместим)
    id("com.google.dagger.hilt.android") version "2.52"

    // !!! ИЗМЕНЕНИЕ: Убедитесь, что KSP соответствует версии Kotlin.
    // Если у вас не Kotlin 2.0.0, замените ее на версию для Kotlin 1.9.23, например:
    // id("com.google.devtools.ksp") version "1.9.23-1.0.20"

    // Или, если вы используете Kotlin 2.0.0, оставьте:
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
}

android {
    namespace = "com.majo.lijo"
    compileSdk = 34 // Рекомендую пока 34 или 35, 36 (preview) может быть нестабилен

    defaultConfig {
        applicationId = "com.majo.lijo"
        minSdk = 26 // Hilt и современные либы лучше работают с 26+
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Hilt требует Java 17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- Основное ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // Расширенные иконки (для корзины, стрелочек и т.д.)
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    // --- Navigation Compose ---
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // --- Room Database (KSP) ---
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // --- Hilt (Dependency Injection) ---
    val hiltVersion = "2.52"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    // !!! ИЗМЕНЕНИЕ: Используйте переменную для версии
    ksp("com.google.dagger:hilt-android-compiler:$hiltVersion")

    // --- Тесты ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ЛОКАЛ СТОРАДЖ
    implementation("androidx.datastore:datastore-preferences:1.1.1")
}


configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.squareup" && requested.name == "javapoet") {
            useVersion("1.13.0")
        }
    }
}
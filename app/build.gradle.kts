plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.connect.demo"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["PN_PROJECT_ID"] = "34c6b829-5b89-44e8-90a9-6d982787b9c9"
        manifestPlaceholders["PN_PROJECT_CLIENT_KEY"] = "c6Z44Ml4TQeNhctvwYgdSv6DBzfjf6t6CB0JDscR"
        manifestPlaceholders["PN_APP_ID"] = "e35f880b-5d71-425f-97aa-5f26de7bc4d7"
        buildConfigField(
            "String",
            "PN_API_BASE_URL",
            "\"https://api.particle.network\""
        )
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }



    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    dataBinding {
        isEnabled = true
    }
    namespace = "com.connect.demo"
}


dependencies {
    modules {
        module("org.bouncycastle:bcprov-jdk15to18") {
            replacedBy("org.bouncycastle:bcprov-jdk15on")
        }
        module("org.bouncycastle:bcprov-jdk18on") {
            replacedBy("org.bouncycastle:bcprov-jdk15on")
        }
    }
    //required dependencies
    implementation(libs.particle.auth) // deprecated use auth-core-service instead
    implementation(libs.particle.auth.core)
    implementation(libs.particle.api)
    implementation(libs.connect.common)
    implementation(libs.connect)
    implementation(libs.connect.kit)
    //optional dependencies
    implementation(libs.connect.auth.adapter)// deprecated use auth-core-adapter instead
    implementation(libs.connect.auth.core.adapter)
    implementation(libs.connect.evm.adapter)
    implementation(libs.connect.sol.adapter)
    implementation(libs.connect.phantom.adapter)
    implementation(libs.connect.wallet.connect.adapter)

    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.material)
    implementation(libs.utilcodex)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.okhttp3)
    implementation(libs.recyclerview.adapter)
    implementation(libs.coil)
    implementation(libs.coil.svg)
    implementation(libs.coil.gif)
    implementation(libs.zxing.barcodescanner)

    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso.core)

}
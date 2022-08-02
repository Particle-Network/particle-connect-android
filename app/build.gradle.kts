import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

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
    }

    signingConfigs {
        register("release") {
            val props = getSignProperties()
            storePassword = props["blockescape.storePassword"].toString()
            keyAlias = props["blockescape.keyAlias"].toString()
            keyPassword = props["blockescape.keyPassword"].toString()
            storeFile = file(System.getenv("HOME") + "/.ssh/golden_block_escape.keystore")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs["release"]
            isMinifyEnabled = false
        }
        release {
            signingConfig = signingConfigs["release"]
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
}

fun getSignProperties(): Properties {
    val keyConfigPath = System.getenv("HOME") + "/.ssh/minijoy.properties"
    val props = Properties()
    if (File(keyConfigPath).exists()) {
        props.load(FileInputStream(file(keyConfigPath)))
    }
    return props
}

dependencies {
    //required dependencies
    implementation(libs.particle.auth)
    implementation(libs.connect)
    //optional dependencies
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
    implementation(libs.glide.core) {
        exclude(group="com.squareup.okhttp3", module = "okhttp")
    }
    annotationProcessor(libs.glide.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso.core)
}
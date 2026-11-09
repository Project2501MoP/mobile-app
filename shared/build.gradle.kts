import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.File

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)

}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    android {
       namespace = "com.example.te5st.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.androidx.uiToolingPreview)
            implementation(libs.androidx.uiTooling)
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.auth)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
    androidRuntimeClasspath(libs.androidx.uiTooling)
}

tasks.register("copyFijlkamLogo") {
    notCompatibleWithConfigurationCache("Copies FIJLKAM logos to composeResources")
    doLast {
        val src = File("C:/Users/vange/.android/studio/agent/conversations/Te5st-c54c6cc98bb89522d225d5c9a061d7/c9cf6ae7-904f-477f-b9e8-42fbfc0d4d9b/blobs/58cc6a44-814f-48f1-b2e7-7dcec51603c5.blob")
        val dest1 = project.file("src/commonMain/composeResources/drawable/fijlkam_logo.png")
        val dest2 = project.file("src/commonMain/composeResources/drawable/logo2024_compatto.png")
        dest1.parentFile.mkdirs()
        src.copyTo(dest1, overwrite = true)
        src.copyTo(dest2, overwrite = true)
        println("Logos copied successfully!")
    }
}
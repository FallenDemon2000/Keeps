import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.sqldelight)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedLogic"
            isStatic = true
        }
    }
    
    android {
       namespace = "com.example.keeps.sharedLogic"
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
    }
    
    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.datastore.preferences.core)
            api(libs.sqldelight.runtime)
            implementation(libs.kotlinx.coroutines.core)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.driver.native)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.testing.mockk)
            implementation(libs.testing.turbine)
        }
        getByName("androidHostTest").dependencies {
            implementation(libs.sqldelight.driver.sqlite)
        }
    }
}

sqldelight {
    databases {
        create("KeepsDatabase") {
            packageName.set("com.example.keeps.db")
        }
    }
}
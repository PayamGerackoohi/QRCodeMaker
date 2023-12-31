plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    kotlin("kapt")
    jacoco
    id("org.jetbrains.kotlinx.kover")
}

val packageName = "com.payamgr.qrcodemaker"
jacoco { toolVersion = Versions.dependencies.test.jacoco }

android {
    signingConfigs {
        create("release") {
            storeFile = file("../keystore/test")
            storePassword = "123456"
            keyPassword = "123456"
            keyAlias = "test"
        }
    }

    namespace = packageName
    compileSdk = 34

    defaultConfig {
        applicationId = packageName
        minSdk = 24
        targetSdk = 33
        Versions.app {
            versionCode = code
            versionName = name
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    Versions.apply {
        compileOptions {
            sourceCompatibility = java
            targetCompatibility = java
        }
        kotlinOptions {
            jvmTarget = java.toString()
        }
        kotlin {
            jvmToolchain(java.ordinal + 1)
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }
    testOptions {
        packaging {
            jniLibs {
                useLegacyPackaging = true // mockK
            }
        }
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

Versions.dependencies {
    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")
        implementation("androidx.core:core-ktx:${android.core}")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:${android.lifecycleRuntime}")
        implementation("androidx.activity:activity-compose:${android.compose.activity}")
        implementation(platform("androidx.compose:compose-bom:${android.compose.bom}"))
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.ui:ui-graphics")
        implementation("androidx.compose.ui:ui-tooling-preview")
        implementation("androidx.compose.material3:material3")
        implementation("androidx.compose.material:material-icons-extended:${android.compose.icons}")
        implementation("com.airbnb.android:mavericks:$mavericks")
        implementation("com.airbnb.android:mavericks-compose:$mavericks")
        implementation("com.airbnb.android:mavericks-hilt:$mavericks")
        implementation("com.google.dagger:hilt-android:$hilt")
        implementation("androidx.navigation:navigation-compose:${android.compose.nav}")
        implementation("androidx.room:room-ktx:$room")
        implementation("androidx.room:room-runtime:$room")
        kapt("androidx.room:room-compiler:$room")
        testImplementation("org.junit.jupiter:junit-jupiter:${test.jUnit}")
        testImplementation("androidx.test:runner:${test.android.runner}")
        testImplementation("org.assertj:assertj-core:${test.assertj}")
        testImplementation("io.mockk:mockk-android:${test.mockk}")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${test.kotlin.coroutine}")
        androidTestImplementation("androidx.test.espresso:espresso-core:${test.espresso}")
        androidTestImplementation(platform("androidx.compose:compose-bom:${android.compose.bom}"))
        androidTestImplementation("androidx.compose.ui:ui-test-junit4")
        androidTestImplementation("org.assertj:assertj-core:${test.assertj}")
        androidTestImplementation("io.mockk:mockk-android:${test.mockk}")
        androidTestImplementation("com.airbnb.android:mavericks-testing:$mavericks")
        androidTestImplementation("com.airbnb.android:mavericks-mocking:$mavericks")
        androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${test.kotlin.coroutine}")
        androidTestImplementation("androidx.navigation:navigation-testing:${android.compose.nav}")
        kapt("com.google.dagger:hilt-android-compiler:$hilt")
        debugImplementation("androidx.compose.ui:ui-tooling")
        debugImplementation("androidx.compose.ui:ui-test-manifest")
        implementation(kotlin("script-runtime"))
    }
}

kapt {
    correctErrorTypes = true
}

tasks.withType<Test> {
    useJUnitPlatform()
    extensions.configure(JacocoTaskExtension::class) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register("jacocoCoverage", JacocoReport::class) {
    val testTaskName = "testDebugUnitTest"
    dependsOn(testTaskName)

    reports {
        html.required.set(true)
        xml.required.set(false)
        csv.required.set(false)
    }

    val sourceBase = packageName.replace(".", "/")
    val excludes = listOf(
        "$sourceBase/QrCodeMakerApp*",
        "$sourceBase/data/hilt/**",
        "$sourceBase/data/model/**",
        "$sourceBase/ui/**",
    )

    sourceDirectories.setFrom("${project.projectDir}/src/main/java")
    classDirectories.setFrom(files(fileTree("$buildDir/tmp/kotlin-classes/debug") { exclude(excludes) }))
    executionData.setFrom(file("$buildDir/jacoco/$testTaskName.exec"))
}

tasks.register("makeTestReport") {
    dependsOn("connectedDebugAndroidTest", "jacocoCoverage", "koverHtmlReportDebug")
}

koverReport {
    filters {
        excludes {
            classes(
                "dagger*",
                "hilt*",
                "*_Factory",
                "$packageName.QrCodeMakerApp*",
                "$packageName.data.hilt.*",
                "$packageName.data.model.*",
                "$packageName.ui.*",
            )
        }
    }
}

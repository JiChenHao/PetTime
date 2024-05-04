plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    //hilt
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.jichenhao.pettime_jichenhao"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jichenhao.pettime_jichenhao"
        minSdk = 29
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

    // ==用于加载网络图片
    implementation("io.coil-kt:coil-compose:2.6.0")
    // ==
    //用于compose权限的使用
    implementation("com.google.accompanist:accompanist-permissions:0.31.0-alpha")
    //闪光
    implementation("com.google.accompanist:accompanist-placeholder-material:0.31.0-alpha")
    //Material3
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.0.0-alpha05")
    //Material-Icons
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    //使用NavController的前提=========
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    //=============
    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")

    // ViewModel()如果与Hilt结合，会自动使用Hilt通过@HiltViewModel构造的ViewModel
    /*viewModel() 会返回一个现有的 ViewModel，或在给定作用域内创建一个新的 ViewModel。
    只要该作用域处于有效状态，就会保留 ViewModel。例如，如果在某个 Activity 中使用了可组合项，
    则在该 Activity 完成或进程终止之前，viewModel() 会返回同一实例。*/
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    //Hilt与Navigation
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    //retrofit2 和 GSON
    implementation("com.squareup.retrofit2:retrofit:2.6.1")
    implementation("com.squareup.retrofit2:converter-gson:2.6.1")

    //用于compose权限的使用
    implementation("com.google.accompanist:accompanist-permissions:0.31.0-alpha")
    //闪光
    implementation("com.google.accompanist:accompanist-placeholder-material:0.31.0-alpha")
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("com.blankj:utilcodex:1.31.1")

    // 使用阿里云的云存储实现图片的上传和下载
    //implementation("com.aliyun.dpa:oss-android-sdk:2.9.18")
    implementation("com.liulishuo.filedownloader:library:1.7.7")

    // 附加阿里云OSS
    implementation("com.aliyun.dpa:oss-android-sdk:+")

    implementation("com.aliyun.dpa:oss-android-sdk:2.9.18")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.squareup.okio:okio:3.8.0")

    // 下拉刷新依赖
    implementation("androidx.compose.material:material")
}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}
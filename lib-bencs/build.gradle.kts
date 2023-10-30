@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)

}

android {
    namespace = "com.ben.bencustomerserver"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
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
    buildFeatures{
        viewBinding =true
        dataBinding =true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.lifcycle.exteion)
    implementation(libs.viewmodel.lifcycle)
    implementation(libs.rv)
    implementation(libs.glide)
    implementation(libs.vrvh)
    implementation(libs.xxpermission)
    implementation(libs.mmkv)
    implementation(project(mapOf("path" to ":module-base")))
    implementation(project(mapOf("path" to ":lib-net")))
    implementation(project(mapOf("path" to ":lib-picture-selector")))
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.okhttp)


}
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    id("com.jfrog.artifactory") version "5.1.10"
    id("maven-publish")
    signing
}

val localDependency: String by project
val isSnapshot: String by project
val mavenName: String by project
val mavenPassword: String by project
val repoKey: String by project

val moduleCommonArtifactId = "cs-sdk"
val versionName="1.0.0"


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
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    //以下 moduleCommonArtifactId 为自定义名称，version 为 defalut 的 version 值，当然怎么命名自己决定
    libraryVariants.configureEach {
        outputs.configureEach {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            if (output.outputFileName.endsWith(".aar")) {
                output.outputFileName = "${moduleCommonArtifactId}-${versionName}.aar"
            }
        }
    }

}


//新增一个 androidSourcesJar 任务，将源码打包，最终打包一个 build/libs/XXX-sources.jar
tasks.register("androidSourcesJar", Jar::class.java) {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
    from((android.sourceSets["main"].kotlin as com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet).srcDirs)
}

publishing {
    publications {

        //以下 "aar" 可以自定义
        register("aar", MavenPublication::class.java) {
            this.groupId = "io.github.lxb-symbol"
            this.artifactId = moduleCommonArtifactId
            this.version =versionName

//            artifact(tasks["androidSourcesJar"])//打包 jar
            artifact("$buildDir/outputs/aar/${artifactId}-${versionName}.aar")//打包 aar

            //生成 POM 文件，将添加的远程库传递给主工程
            //大致思路时遍历添加该 Module 下通过 implementation 依赖的所有远程库
            pom {
                withXml {
                    val root = asNode()
                    val dependencies = (root["dependencies"] as groovy.util.NodeList).firstOrNull() as groovy.util.Node? ?: root.appendNode("dependencies")
                    configurations.configureEach {
                        this.dependencies.forEach {
                            if (this.name == "implementation") {
                                if (it.group?.isNotBlank() == true && (it.name.isNotBlank() || "unspecified" == it.name) && it.version?.isNotBlank() == true) {
                                    val dependency = dependencies.appendNode("dependency")
                                    dependency.appendNode("groupId", it.group)
                                    dependency.appendNode("artifactId", it.name)
                                    dependency.appendNode("version", it.version)
                                    //关键，笔者碰到没添加这句时，一个远程库没有传递的问题
                                    dependency.appendNode("scope", "implementation")
                                }
                            }
                        }
                    }
                }
            }


        }

        repositories {
            maven {
                name = "Release"
                setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = project.findProperty("ossrhUsername") as String?
                    password = project.findProperty("ossrhPassword") as String?
                }
            }
        }
    }
}



tasks.named<org.jfrog.gradle.plugin.artifactory.task.ArtifactoryTask>("artifactoryPublish") {
    artifactory {
        setContextUrl("https://s01.oss.sonatype.org/content/repositories/releases/")
        clientConfig.publisher.repoKey = repoKey
        clientConfig.publisher.username = mavenName//maven 库用户名
        clientConfig.publisher.password = mavenPassword//maven 库密码
    }

    dependsOn(tasks["assemble"])
    //"aar" 同上
    publications(publishing.publications["aar"])
}

//signing {
//    sign (publishing.publications["aar"])
//}




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
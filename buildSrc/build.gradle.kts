import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//plugins {
//    kotlin("jvm") version "1.8.0"
//}

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.8.0"
}

repositories {
    google()
    mavenCentral()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apiVersion = "1.3"
}

//dependencies {
//    implementation("com.android.tools.build:gradle-api:8.1.4")
//    implementation(kotlin("stdlib"))
//    gradleApi()
//}

dependencies {
    implementation("com.android.tools.build:gradle-api:8.1.4")
    implementation(kotlin("stdlib"))
    gradleApi()
    implementation("org.ow2.asm:asm-util:9.2")
    implementation("commons-io:commons-io:2.8.0")
    implementation("commons-codec:commons-codec:1.15")
    implementation("org.ow2.asm:asm-commons:9.2")
    implementation("org.ow2.asm:asm-tree:9.2")
}
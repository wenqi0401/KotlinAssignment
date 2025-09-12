// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false


}
buildscript {
    repositories {
        google()
        mavenCentral();
        maven { url = uri("https://maven.google.com") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.11.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
        classpath("com.google.gms:google-services:4.4.0")

    }

}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

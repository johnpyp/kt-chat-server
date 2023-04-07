/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.6/userguide/building_java_projects.html
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
  // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.

  // Apply the application plugin to add support for building a CLI application in Java.
  application
  kotlin("jvm") version "1.8.20"

  kotlin("plugin.serialization") version "1.8.20"
}

repositories {
  // Use Maven Central for resolving dependencies.
  mavenCentral()
}

val ktorVersion = "2.2.4"

dependencies {
  // This dependency is used by the application.
  implementation("com.google.guava:guava:31.1-jre")
  implementation("io.ktor:ktor-server-core:$ktorVersion")
  implementation("io.ktor:ktor-server-netty:$ktorVersion")
  implementation("io.ktor:ktor-server-websockets:$ktorVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
  implementation("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-22")
  implementation("org.slf4j:slf4j-simple:2.0.3")
  implementation(kotlin("stdlib-jdk8"))
}

testing {
  suites {
    // Configure the built-in test suite
    val test by
        getting(JvmTestSuite::class) {
          // Use Kotlin Test test framework
          useKotlinTest("1.7.10")

          dependencies {
            // Use newer version of JUnit Engine for Kotlin Test
            implementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
          }
        }
  }
}

application {
  // Define the main class for the application.
  mainClass.set("kt.chat.server.AppKt")
}
kotlin {
  jvmToolchain(11)
}
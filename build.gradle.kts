@file:Suppress("WARNINGS")

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
}

group = "com.mikael"
version = "1.0.0"

application {
    mainClass.set("com.mikael.tictactoebackend.TicTacToeKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/")
}

val exposed_version: String by project

dependencies {
    // Ktor server
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-double-receive-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    // Ktor server (WebSockets)
    implementation("io.ktor:ktor-server-websockets-jvm")

    // Ktor client
    implementation("io.ktor:ktor-client-core-jvm")
    implementation("io.ktor:ktor-client-apache-jvm")

    // Exposed
    implementation("org.jetbrains.exposed:exposed-core:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:${exposed_version}")

    // Caching
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Database drivers
    implementation("com.mysql:mysql-connector-j:9.0.0") // MySQL
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.0") // MariaDB

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.12")

    // Dotenv
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.2")
}
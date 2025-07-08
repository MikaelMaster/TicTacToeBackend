plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("io.ktor.plugin") version "3.2.0"
}

group = "com.mikael.tictactoe"
version = "1.0.0"

application {
    mainClass.set("com.mikael.tictactoe.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/")
}

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
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-websockets-jvm") // Websockets support

    // Ktor client
    implementation("io.ktor:ktor-client-core-jvm")
    implementation("io.ktor:ktor-client-apache-jvm")

    // Exposed
    val exposedVersion: String by project // defined in gradle.properties
    implementation("org.jetbrains.exposed:exposed-core:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:${exposedVersion}")
    implementation("com.zaxxer:HikariCP:6.2.1") // HikariCP for connection pooling

    // Database drivers for Exposed
    implementation("com.mysql:mysql-connector-j:9.3.0") // MySQL driver
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.3") // MariaDB driver

    // Caching
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.1")

    // Password hashing
    implementation("org.mindrot:jbcrypt:0.4")

    // JWT library
    implementation("com.auth0:java-jwt:4.4.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.18")

    // Dotenv
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
}
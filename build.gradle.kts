plugins {
    kotlin("jvm") version "2.0.10"
    kotlin("plugin.serialization") version "2.1.0"
    application
}

group = "io.github.meatwo310.media_integration_rpc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.github.caoimhebyrne:KDiscordIPC:0.2.2")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("io.ktor:ktor-client-cio:3.0.3")
    implementation("io.ktor:ktor-client-logging:3.0.3")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("com.sealwu:kscript-tools:1.0.22")
//    implementation("com.sksamuel.hoplite:hoplite-core:2.9.0")
//    implementation("com.sksamuel.hoplite:hoplite-toml:2.9.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.0")
}

application {
    mainClass = "$group.MainKt"
}

tasks.test {
    useJUnitPlatform()
}

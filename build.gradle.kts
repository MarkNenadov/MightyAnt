
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
}

group = "com.pythonbyte.mightyant"
version = "0.3.3"

application {
    mainClass.set("com.pythonbyte.mightyant.main.MightyAntApp")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/pythonbyte-krux.jar"))
    implementation(platform(Http4k.bom))
    implementation(Http4k.core)
    implementation(Http4k.server.jetty)
    implementation(Http4k.client.websocket)
    implementation(Http4k.format.jackson)
    implementation("org.yaml:snakeyaml:_")
    implementation("ch.qos.logback:logback-classic:_")
    implementation("org.slf4j:slf4j-api:1.7.30")
    testImplementation(Kotlin.test.junit)
}

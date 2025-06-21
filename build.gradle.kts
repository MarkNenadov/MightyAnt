val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

group = "com.pythonbyte.mightyant"
version = "0.7.7"

application {
    mainClass.set("com.pythonbyte.mightyant.main.MightyAntApp")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/pythonbyte-krux-jar-with-dependencies.jar"))
    implementation(platform(Http4k.bom))
    implementation(Http4k.core)
    implementation(Http4k.server.jetty)
    implementation(Http4k.client.websocket)
    implementation(Http4k.format.jackson)
    implementation("org.yaml:snakeyaml:_")
    implementation("ch.qos.logback:logback-classic:_")
    implementation("org.slf4j:slf4j-api:_")
    testImplementation(Kotlin.test.junit)
}

// Configure ktlint
ktlint {
    version.set("1.0.1")
    android.set(false)
    verbose.set(true)
    filter {
        exclude { element -> element.file.path.contains("build/") }
    }
}

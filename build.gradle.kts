plugins {
    id("org.jetbrains.intellij") version "1.11.0"
    kotlin("jvm") version "1.9.0"
    java
}

group = "pers.wjx.plugin.demo"
version = "1.7"

repositories {
    mavenLocal()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")
}

intellij {
    version.set("2023.3.4")
    plugins.set(
        listOf(
            "com.intellij.java"
        )
    )
    updateSinceUntilBuild.set(false)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        version.set("${project.version}")
        sinceBuild.set("233")
        changeNotes.set(file("./changeNodes.html").readText())
    }
}
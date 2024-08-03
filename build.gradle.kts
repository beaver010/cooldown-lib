plugins {
    id("java")
    `maven-publish`
}

group = "com.github.beaver010"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            groupId = project.group as String
            artifactId = "cooldown-lib"
            version = project.version as String
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
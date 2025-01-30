import java.time.Instant
import java.time.temporal.ChronoUnit.SECONDS

plugins {
    id("base")
    id("java-platform")
}

dependencies {
    constraints {
        api("com.fasterxml.jackson.core:jackson-databind:" + project.property("versions.jackson"))
        api("com.google.code.findbugs:jsr305:" + project.property("versions.findbugsJsr305"))
        api("com.ninja-squad:springmockk:" + project.property("versions.springMockk"))
        api("com.tngtech.archunit:archunit-junit5:" + project.property("versions.archunit"))
        api("de.siegmar:fastcsv:" + project.property("versions.fastcsv"))
        api("jakarta.validation:jakarta.validation-api:" + project.property("versions.jakartaValidation"))
        api("org.javamoney:moneta:" + project.property("versions.javamoneyMoneta"))
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:" + project.property("versions.kotlinCoroutines"))
        api("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:" + project.property("versions.kotlinCoroutines"))
        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:" + project.property("versions.springDocOpenapi"))
    }
}

tasks {
    register<Jar>("stage") {
        dependsOn(":apps:client:build", ":apps:server:build")

        group = "build"
        description = "Creates composite Jar file including client and server part"

        entryCompression = ZipEntryCompression.STORED
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        val serverBuild = project(":apps:server").layout.buildDirectory.dir("libs/server.jar").map(::zipTree)
        val clientBuild = project(":apps:client").layout.buildDirectory.dir("dist")

        from(serverBuild) { into("/") }
        from(clientBuild) { into("/BOOT-INF/classes/static") }

        manifest {
            attributes(
                "Implementation-Title" to project.name,
                "Build-Time" to Instant.now().truncatedTo(SECONDS),
            )
            from(serverBuild.map { it.find { file -> file.name == "MANIFEST.MF" } }) {
                eachEntry {
                    if (!baseValue.isNullOrBlank()) {
                        value = baseValue
                    }
                }
            }
        }
    }
}

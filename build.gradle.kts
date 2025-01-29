import io.github.arhor.dotenv.Dotenv

plugins {
    id("base")
    id("java-platform")
}

ext {
    set("env", Dotenv.configure().location("$rootDir").filename(".env").load())
}

dependencies {
    constraints {
        api("com.fasterxml.jackson.core:jackson-databind:" + project.property("versions.jackson"))
        api("com.google.code.findbugs:jsr305:" + project.property("versions.findbugsJsr305"))
        api("com.ninja-squad:springmockk:" + project.property("versions.springMockk"))
        api("com.tngtech.archunit:archunit-junit5:" + project.property("versions.archunit"))
        api("de.siegmar:fastcsv:" + project.property("versions.fastcsv"))
        api("jakarta.validation:jakarta.validation-api:" + project.property("versions.jakartaValidation"))
        api("org.codehaus.janino:janino:" + project.property("versions.janino"))
        api("org.javamoney:moneta:" + project.property("versions.javamoneyMoneta"))
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:" + project.property("versions.kotlinCoroutines"))
        api("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:" + project.property("versions.kotlinCoroutines"))
        api("org.mapstruct:mapstruct:" + project.property("versions.mapstruct"))
        api("org.mapstruct:mapstruct-processor:" + project.property("versions.mapstruct"))
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

        val serverBuild = zipTree("${project(":apps:server").layout.buildDirectory}/libs/server.jar")
        val clientBuild = "${project(":apps:client").projectDir}/dist"

        from(serverBuild).into("/")
        from(clientBuild).into("/BOOT-INF/classes/static")

        manifest {
            from(serverBuild.find { it.name == "MANIFEST.MF" }) {

            }

            attributes["Implementation-Title"] = project.name
        }
    }
}

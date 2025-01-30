pluginManagement {
    plugins {
        fun prop(name: String): String = extra[name].toString()

        // @formatter:off
        id("com.adarshr.test-logger")            version prop("versions.gradleTestLogger")
        id("com.avast.gradle.docker-compose")    version prop("versions.dockerComposeGradlePlugin")
        id("com.github.node-gradle.node")        version prop("versions.nodeGradlePlugin")
        id("io.spring.dependency-management")    version prop("versions.springDependencyManagement")
        id("org.jsonschema2pojo")                version prop("versions.jsonschema2pojo")
        id("org.jetbrains.kotlin.jvm")           version prop("versions.kotlin")
        id("org.jetbrains.kotlin.plugin.spring") version prop("versions.kotlin")
        id("org.springframework.boot")           version prop("versions.springBoot")
        // @formatter:on
    }
}

rootProject.name = "simple-expense-tracker"

include(":app-client")
include(":apps:client")
include(":apps:schema")
include(":apps:server")

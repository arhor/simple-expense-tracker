import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("java-library")
    id("org.jsonschema2pojo")
    id("simple-expense-tracker.node-conventions")
}

description = "Shared models described via JSON Schema, used to generate sources for the Client/Server communication"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(rootProject))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.google.code.findbugs:jsr305")
    implementation("jakarta.validation:jakarta.validation-api")
}

jsonSchema2Pojo {
    setSource(files("${projectDir}/schema"))
    targetVersion = libs.versions.java.get()
    targetPackage = "com.github.arhor.simple.expense.tracker.model"
    removeOldOutput = true
    includeJsr303Annotations = true
    includeJsr305Annotations = true
    includeSetters = true
    includeGetters = true
    includeConstructors = true
    useLongIntegers = true
    useJakartaValidation = true
    dateTimeType = "java.time.LocalDateTime"
    dateType = "java.time.LocalDate"
}

tasks {
    val generateJsonSchema2TypeScript by registering(NpmTask::class) {
        dependsOn(npmInstall)

        group = "build"
        description = "Builds d.ts files according to the provided schemas"
        workingDir.fileValue(projectDir)

        inputs.dir("schema")
        inputs.dir("node_modules")
        inputs.files("index.js", "package.json", "package-lock.json")

        outputs.dir("build/generated/sources/js2ts")
        outputs.cacheIf { true }

        args = listOf("run", "build")
    }

    build {
        dependsOn(generateJsonSchema2TypeScript)
    }
}

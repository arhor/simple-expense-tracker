import com.github.gradle.node.npm.task.NpmTask
import com.github.gradle.node.npm.task.NpxTask

plugins {
    id("simple-expense-tracker.node-conventions")
}

description = "Client-side application based on React.js and Node.js ${libs.versions.node}"

tasks {
    val synchronizeModel by registering(Sync::class) {
        dependsOn(":apps:schema:build")

        from(project(":apps:schema").layout.buildDirectory.dir("generated/sources/js2ts"))
        into("$projectDir/src/generated")
    }

    val updateBrowserList by registering(NpxTask::class) {
        dependsOn(npmInstall)

        workingDir.fileValue(projectDir)
        command.set("browserslist@latest")

        args = listOf("--update-db")
    }

    val test by registering(NpmTask::class) {
        dependsOn(npmInstall, updateBrowserList, synchronizeModel)

        group = "verification"
        workingDir.fileValue(projectDir)

        args = listOf("run", "test")
    }

    register<NpmTask>("build") {
        dependsOn(npmInstall, updateBrowserList, synchronizeModel, test)

        group = "build"
        workingDir.fileValue(projectDir)

        inputs.dir("src")
        inputs.dir("public")
        inputs.dir("node_modules")
        inputs.files(
            "index.html",
            "package.json",
            "package-lock.json",
            "tsconfig.json",
            "tsconfig.node.json",
            "vite.config.json",
        )

        outputs.dir("build/dist")
        outputs.cacheIf { true }

        args = listOf("run", "build")
    }
}

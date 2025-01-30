import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.adarshr.test-logger")
    id("io.spring.dependency-management")
    id("jacoco")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.springframework.boot")
}

description = String.format(
    "Server-side application based on Spring Boot %s and Java %s",
    project.property("versions.springBoot"),
    project.property("versions.java"),
)

extra["kotlin.version"] = libs.versions.kotlin.get()
val javaVersion: String = libs.versions.java.get()

java {
    javaVersion.let(JavaVersion::toVersion).let {
        sourceCompatibility = it
        targetCompatibility = it
    }
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(annotationProcessor.get())
    }
    implementation {
        exclude(group = "org.javamoney.moneta", module = "moneta-convert-ecb")
        exclude(group = "org.javamoney.moneta", module = "moneta-convert-imf")
    }
    testImplementation {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "org.mockito", module = "mockito-core")
        exclude(group = "org.mockito", module = "mockito-junit-jupiter")
    }
    all {
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    }
}

dependencies {
    implementation(platform(rootProject))
    implementation(project(":apps:schema"))
    implementation("de.siegmar:fastcsv")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    implementation("org.javamoney:moneta")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.session:spring-session-jdbc")

    runtimeOnly("com.github.ben-manes.caffeine:caffeine")
    runtimeOnly("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation(platform(rootProject))
    testImplementation("com.ninja-squad:springmockk")
    testImplementation("com.tngtech.archunit:archunit-junit5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion))
            javaParameters.set(true)
            freeCompilerArgs.set(
                listOf(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all",
                    "-Xcontext-receivers",
                )
            )
        }
    }

    withType<JavaCompile> {
        options.compilerArgs.addAll(
            listOf(
                "-Xlint:deprecation",
                "-Xlint:fallthrough",
                "-Xlint:unchecked",
                "-parameters",
            )
        )
    }

    withType<Test> {
        jvmArgs = listOf("-XX:+EnableDynamicAgentLoading")
        systemProperty("spring.profiles.active", "test")
        useJUnitPlatform()
    }

    processResources {
        filesMatching("application.yml") {
            expand(project.properties)
        }
    }

    jacocoTestReport {
        shouldRunAfter(test)
        shouldApplyExclusionsTo(classDirectories)

        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    jacocoTestCoverageVerification {
        shouldRunAfter(jacocoTestReport)
        shouldApplyExclusionsTo(classDirectories)

        violationRules {
            rule {
                limit {
                    minimum = 0.65.toBigDecimal()
                    maximum = 0.90.toBigDecimal()
                }
            }
        }
    }

    val jacocoTestReportAndVerification by registering {
        dependsOn(
            jacocoTestReport,
            jacocoTestCoverageVerification,
        )
        group = "verification"
    }

    if (providers.environmentVariable("CI").getOrNull() != "true") {
        check {
            finalizedBy(jacocoTestReportAndVerification)
        }
    }
}

fun shouldApplyExclusionsTo(classDirectories: ConfigurableFileCollection) {
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "com/github/arhor/simple/expense/tracker/Application.class",
                        "com/github/arhor/simple/expense/tracker/**/aop/*",
                        "com/github/arhor/simple/expense/tracker/**/config/*",
                        "com/github/arhor/simple/expense/tracker/**/error/*",
                        "com/github/arhor/simple/expense/tracker/**/exception/*",
                        "com/github/arhor/simple/expense/tracker/**/model/*",
                    )
                }
            }
        )
    )
}

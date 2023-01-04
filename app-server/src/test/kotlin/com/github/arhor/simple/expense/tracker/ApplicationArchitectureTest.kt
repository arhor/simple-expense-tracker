package com.github.arhor.simple.expense.tracker

import com.github.arhor.simple.expense.tracker.util.zoneIdOrDefaultUTC
import com.tngtech.archunit.core.domain.JavaCall.Predicates.target
import com.tngtech.archunit.core.domain.JavaClass.Predicates.implement
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.properties.HasName.Predicates.name
import com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTag
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import io.mockk.declaringKotlinFile
import java.time.temporal.Temporal

@ArchTag("architecture")
@AnalyzeClasses(packagesOf = [Application::class], importOptions = [DoNotIncludeTests::class])
internal class ApplicationArchitectureTest {

    @ArchTest
    fun `should check that correct layered architecture is observed`(appClasses: JavaClasses) {
        // given
        val applicationPackage = Application::class.java.getPackage().name

        // when
        val architecture =
            layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Web").definedBy("${applicationPackage}.web..")
                .layer("Service").definedBy("${applicationPackage}.service..")
                .layer("Persistence").definedBy("${applicationPackage}.data..")
                .layer("Configuration").definedBy("${applicationPackage}.config..")
                .whereLayer("Web").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Web", "Configuration")
                .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service")

        // then
        architecture.check(appClasses)
    }

    @ArchTest
    fun `should check that only TimeUtils class calls now method on temporal objects`(appClasses: JavaClasses) {
        // given
        val restrictions = noClasses()
            .that()
            .doNotHaveFullyQualifiedName(::zoneIdOrDefaultUTC.declaringKotlinFile.qualifiedName)
            .should()
            .callMethodWhere(target(name("now")).and(target(owner(implement(Temporal::class.java)))))

        // then
        restrictions.check(appClasses)
    }
}

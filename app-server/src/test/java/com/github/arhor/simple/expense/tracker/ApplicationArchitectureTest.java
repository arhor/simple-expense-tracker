package com.github.arhor.simple.expense.tracker;

import java.time.temporal.Temporal;

import com.github.arhor.simple.expense.tracker.util.TimeUtils;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;

import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.implement;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.name;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@ArchTag("architecture")
@AnalyzeClasses(
    packagesOf = {
        Application.class,
    },
    importOptions = {
        ImportOption.DoNotIncludeTests.class,
    }
)
class ApplicationArchitectureTest {

    @ArchTest
    void should_check_that_correct_layered_architecture_is_observed(
        final JavaClasses applicationClasses
    ) {
        layeredArchitecture()
            .layer("Web").definedBy(applicationPackage(".web.."))
            .layer("Service").definedBy(applicationPackage(".service.."))
            .layer("Persistence").definedBy(applicationPackage(".data.."))
            .whereLayer("Web").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Web")
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service")
            .check(applicationClasses);
    }

    @ArchTest
    void should_check_that_only_TimeUtils_class_calls_now_method_on_temporal_objects(
        final JavaClasses applicationClasses
    ) {
        noClasses()
            .that()
            .doNotHaveFullyQualifiedName(TimeUtils.class.getName())
            .should()
            .callMethodWhere(target(name("now")).and(target(owner(implement(Temporal.class)))))
            .check(applicationClasses);
    }

    private static String applicationPackage(final String name) {
        return Application.class.getPackage().getName() + name;
    }
}

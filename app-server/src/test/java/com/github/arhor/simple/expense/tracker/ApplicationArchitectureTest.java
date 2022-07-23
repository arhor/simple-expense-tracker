package com.github.arhor.simple.expense.tracker;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@ArchTag("architecture")
@AnalyzeClasses(
    packagesOf = {
        Application.class,
    },
    importOptions = {
        DoNotIncludeTests.class,
    }
)
class ApplicationArchitectureTest {

    static final String ROOT_PACKAGE = Application.class.getPackage().getName();

    @ArchTest
    void correct_layered_architecture_should_be_observed(final JavaClasses applicationClasses) {
        layeredArchitecture()
            .layer("Web").definedBy(ROOT_PACKAGE + ".web..")
            .layer("Service").definedBy(ROOT_PACKAGE + ".service..")
            .layer("Persistence").definedBy(ROOT_PACKAGE + ".data..")
            .whereLayer("Web").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Web")
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service")
            .check(applicationClasses);
    }
}

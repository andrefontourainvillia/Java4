package com.mergingtonhigh.schoolmanagement.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class LayerArchitectureTest {

    private final JavaClasses javaClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.mergingtonhigh.schoolmanagement");

    @Test
    void shouldFollowCleanArchitectureLayers() {
        Architectures.LayeredArchitecture architecture = layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Application").definedBy("..application..")
                .layer("Domain").definedBy("..domain..")
                .layer("Infrastructure").definedBy("..infrastructure..")
                .layer("Presentation").definedBy("..presentation..")
                
                .whereLayer("Presentation").mayOnlyAccessLayers("Application", "Domain")
                .whereLayer("Application").mayOnlyAccessLayers("Domain")
                .whereLayer("Infrastructure").mayOnlyAccessLayers("Domain", "Application")
                .whereLayer("Domain").mayNotAccessAnyLayer();

        architecture.check(javaClasses);
    }

    @Test
    void shouldNotHaveCyclicDependencies() {
        SlicesRuleDefinition.slices()
                .matching("com.mergingtonhigh.schoolmanagement.(*)..")
                .should().beFreeOfCycles()
                .check(javaClasses);
    }

    @Test
    void shouldHaveProperServiceNaming() {
        classes()
                .that().resideInAPackage("..application.usecases..")
                .should().haveSimpleNameEndingWith("UseCase")
                .check(javaClasses);
    }

    @Test
    void shouldHaveProperRepositoryNaming() {
        classes()
                .that().resideInAPackage("..domain.repositories..")
                .should().beInterfaces()
                .andShould().haveSimpleNameEndingWith("Repository")
                .check(javaClasses);
    }

    @Test
    void shouldHaveProperControllerNaming() {
        classes()
                .that().resideInAPackage("..presentation.controllers..")
                .and().areNotAnnotatedWith("org.springframework.web.bind.annotation.RestControllerAdvice")
                .should().haveSimpleNameEndingWith("Controller")
                .check(javaClasses);
    }

    @Test
    void shouldOnlyHaveExceptionsInExceptionPackage() {
        classes()
                .that().resideInAPackage("..domain.exceptions..")
                .should().beAssignableTo(RuntimeException.class)
                .check(javaClasses);
    }
}
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("ru.vyarus.quality").version("4.7.0")
    id("java-library")
    id("jacoco")
    id("maven-publish")
}

val suitesDir = "src/test/resources/suites/"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("org.testng", "testng", "7.5")
    implementation("io.rest-assured", "rest-assured", "4.4.0")
    implementation("org.yaml", "snakeyaml", "1.30")
    implementation("ch.qos.logback", "logback-classic", "1.2.10")
    implementation("com.google.code.gson", "gson", "2.8.9")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.test {
    jvmArgs("--enable-preview")

    useTestNG {
        // Add test suites
        File(projectDir.absolutePath + "/" + suitesDir)
            .walk()
            .forEach {
                if (it.isFile) {
                    suites(it)
                }
            }

        testLogging {
            events("passed", "skipped", "failed")
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = true
        }
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("${buildDir}/reports/coverage").get().asFile)
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

quality {
    checkstyle = true
    pmd = true
    codenarc = true
    spotbugs = true
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

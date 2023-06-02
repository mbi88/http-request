import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("ru.vyarus.quality").version("4.9.0")
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
    implementation("org.testng:testng:7.8.0")
    implementation("io.rest-assured:rest-assured:5.3.0")
    implementation("org.yaml:snakeyaml:2.0")
    implementation("ch.qos.logback:logback-classic:1.4.7")
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.test {
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

tasks.withType<Javadoc> {
    val opts = options as StandardJavadocDocletOptions
    opts.addBooleanOption("Xdoclint:none", true)
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.mbi"
            artifactId = "http-request"
            version = "1.0"

            from(components["java"])
        }
    }
}
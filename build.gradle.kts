import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("ru.vyarus.quality").version("5.0.0")
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
    implementation("org.testng:testng:7.11.0")
    implementation("io.rest-assured:rest-assured:5.5.1")
    implementation("org.yaml:snakeyaml:2.4")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("com.google.code.gson:gson:2.12.1")
}

tasks.test {
    useTestNG {
        // Add test suites
        File(projectDir.absolutePath + "/" + suitesDir).walk().forEach {
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
        html.outputLocation.set(layout.buildDirectory.dir("reports/coverage"))
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
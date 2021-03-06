import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.spring.dependency-management") version "1.0.1.RELEASE"
    id("com.google.cloud.tools.jib") version "2.7.1"
}

group = "com.study.udemy"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val vertxVersion = "4.1.5"
val junitJupiterVersion = "5.8.1"

val mainVerticleName = "com.study.udemy.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
    mainClass.set(launcherClassName)
}

dependencyManagement {
    imports {
        mavenBom("org.apache.logging.log4j:log4j-bom:2.14.1")
    }
}

dependencies {
    implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    implementation("io.vertx:vertx-core:4.1.5")

    // Productivity dependencies
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0")
    implementation("com.fasterxml.uuid:java-uuid-generator:4.0.1")
    implementation("com.jayway.jsonpath:json-path:2.6.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.jayway.jsonpath:json-path:2.6.0")
    implementation("org.jetbrains:annotations:22.0.0")

    // Vertx web
    implementation("io.vertx:vertx-web-client:4.1.5")
    implementation("io.vertx:vertx-web:4.1.5")
    implementation("io.vertx:vertx-web-openapi:4.1.5")
    implementation("io.vertx:vertx-web-sstore-cookie:4.1.5")
    implementation("io.vertx:vertx-web-sstore-redis:4.1.5")

    // logging dependencies
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
    implementation("org.slf4j:slf4j-api:1.7.32")

    testImplementation("io.vertx:vertx-junit5:4.1.5")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


jib {
    from {
        image = "amazoncorretto:17-alpine"
    }
    to {
        image = "example/jib/vertx-starter"
    }
    container {
        mainClass = "io.vertx.core.Launcher"
        args = listOf("run", mainVerticleName)
        ports = listOf("9999")
    }
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("fat")
    manifest {
        attributes(mapOf("Main-Verticle" to mainVerticleName))
    }
    mergeServiceFiles()
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("--enable-preview")
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
}

tasks.withType<JavaExec> {
    args = listOf(
        "run",
        mainVerticleName,
        "--redeploy=$watchForChange",
        "--launcher-class=$launcherClassName",
        "--on-redeploy=$doOnChange"
    )
}

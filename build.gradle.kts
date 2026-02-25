plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("org.example.mario_pr51")
    mainClass.set("org.example.mario_pr51.Launcher")
}

tasks.javadoc {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:none", "-quiet")
        addStringOption("-add-exports", "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED")
        addStringOption("-add-exports", "javafx.base/com.sun.javafx.event=ALL-UNNAMED")
    }
}

javafx {
    version = "23.0.1"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.swing", "javafx.web")
}

dependencies {
    implementation("net.sf.jasperreports:jasperreports:7.0.3")
    implementation("net.sf.jasperreports:jasperreports-jdt:7.0.3")
    implementation("net.sf.jasperreports:jasperreports-pdf:7.0.3")
    implementation("com.github.librepdf:openpdf:1.3.39")
    implementation("net.sf.jasperreports:jasperreports-charts:7.0.3")
    implementation("org.xerial:sqlite-jdbc:3.51.1.0")
    implementation("tools.jackson.dataformat:jackson-dataformat-xml:3.0.2")
    implementation("commons-logging:commons-logging:1.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.1")

    implementation("commons-logging:commons-logging:1.3.5")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    jvmArgs = listOf(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.desktop/java.awt=ALL-UNNAMED",
        "--add-opens", "java.desktop/sun.awt=ALL-UNNAMED",
        "--add-opens", "java.desktop/sun.swing=ALL-UNNAMED",
        "--add-exports", "javafx.base/com.sun.javafx.event=ALL-UNNAMED"
    )
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("electroFactura.jar")
    mainClass.set("org.example.mario_pr51.Launcher")
}

tasks.register<Jar>("standardJar") {
    archiveFileName.set("electroFacturaStandard.jar")
    manifest {
        attributes(
            "Main-Class" to "org.example.mario_pr51.Launcher"
        )
    }

    // Incluir todas las clases compiladas
    from(sourceSets.main.get().output)

    // Incluir dependencias (para que sea ejecutable independiente)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    // Evitar duplicados
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // Excluir archivos de módulo si causan problemas
    exclude("module-info.class")
}
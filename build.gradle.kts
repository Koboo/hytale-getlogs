import eu.koboo.pluginmanifest.gradle.plugin.extension.AuthMode

plugins {
    id("java")
    id("com.gradleup.shadow") version("9.3.1")
    id("eu.koboo.pluginmanifest") version("1.0.24-rc.1")
}

group = "eu.koboo"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "entixReposilite"
        url = uri("https://repo.entix.eu/releases")
    }
}

dependencies {

}

pluginManifest {
    runtimeConfiguration {
        serverRuntimePath = "D:/PluginManifestRuntime"
        authMode = AuthMode.AUTHENTICATED
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
    withJavadocJar()
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    javadoc {
        options.encoding = "UTF-8"
        (options as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
    }
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/java"))
        resources.setSrcDirs(listOf("src/resources"))
    }
    test {
        java.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
}
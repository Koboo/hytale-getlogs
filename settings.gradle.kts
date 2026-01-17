pluginManagement {
    repositories {
        mavenLocal()
        maven {
            name = "entixReposilite"
            url = uri("https://repo.entix.eu/releases")
        }
        gradlePluginPortal()
    }
}
rootProject.name = "hytale-getlogs"
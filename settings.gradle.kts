pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
        maven { setUrl("https://s01.oss.sonatype.org/content/groups/staging/") }
        maven { setUrl("https://s01.oss.sonatype.org/content/repositories/releases/") }
        maven { setUrl("https://maven.google.com/") }
    }
}

rootProject.name = "particle-android"
include(":app")

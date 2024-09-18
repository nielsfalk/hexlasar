rootProject.name = "Laserhexagon"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    pluginManagement {
        repositories {
            specialRepositories()
            gradlePluginPortal()
        }
    }
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        specialRepositories()
    }
}

fun RepositoryHandler.specialRepositories() {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        name = "MavenCentralSnapshots"
        mavenContent { snapshotsOnly() }
    }
}

include(":composeApp")
include(":server")
include(":shared")
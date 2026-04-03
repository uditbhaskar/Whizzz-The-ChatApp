pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Whizzz"

include(":app")
include(":core:common")
include(":core:strings")
include(":core:ui")
include(":domain")
include(":data")
include(":feature:auth")
include(":feature:home")
include(":feature:chat")
include(":feature:profile")

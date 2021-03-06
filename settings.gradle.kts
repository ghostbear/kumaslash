dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven(url="https://jitpack.io")
    }
}

rootProject.name = "kumaslash"
include("data")
include("app")
include("web")
include("common")

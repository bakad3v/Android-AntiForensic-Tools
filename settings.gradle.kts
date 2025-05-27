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
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "syrenaPass"
include(":app")
include(":core:encryptedDatastore")
include(":core:dialogs")
include(":core:validators")
include(":superuser")
include(":core:resources")
include(":core:passwordStrength")
include(":core:utils")
include(":core:entities")
include(":features:settings")
include(":features:rootCommands")
include(":features:files")
include(":features:lockscreen")
include(":features:passwordSetup")
include(":features:logs")
include(":features:profiles")
include(":features:services")
include(":features:triggerReceivers")
include(":core:activityState")
include(":features:aboutApp")
include(":data:password")
include(":data:root")
include(":features:splash")
include(":data:files")
include(":data:logs")
include(":data:settings")
include(":data:profiles")
include(":core:network")
include(":data:appUpdater")

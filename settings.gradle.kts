pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.3"
}

rootProject.name = "krossovochkin-web-apps"

include(":apps:dimensions-utils")
include(":apps:time-utils")
include(":apps:color-utils")
include(":apps:card-soccer")
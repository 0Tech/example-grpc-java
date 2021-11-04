pluginManagement {
    plugins {
        id("com.google.protobuf") version "0.8.17"
    }
}

enableFeaturePreview("VERSION_CATALOGS")
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "example-grpc-java"
include(
    "protobuf",
    "server",
    "client",
)

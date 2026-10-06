// En Windows, algunas sesiones de Gradle reciben user.home=C:/.
// Usamos el perfil real antes de cargar el plugin de Android.
System.getenv("USERPROFILE")?.takeIf { it.isNotBlank() }?.let { perfil ->
    System.setProperty("user.home", perfil)
}
rootProject.name = "PharmaMobil"

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
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":shared")

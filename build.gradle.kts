import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.kotlin.dsl.withType

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.detekt)
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    dependencies {
        // libs.versions.toml isn't registered from allprojects { } in the root script
        // Look the version up directly from the version catalog extension instead.
        val libsCatalog = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
        val detektVersion = libsCatalog.findVersion("detekt").get().requiredVersion
        detekt("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
        detekt("io.gitlab.arturbosch.detekt:detekt-cli:$detektVersion")
    }
}

subprojects {
    tasks.withType<Detekt> {
        config.setFrom(files("${project.rootDir}/config/detekt.yml"))
        autoCorrect = true
        buildUponDefaultConfig = true
    }
}
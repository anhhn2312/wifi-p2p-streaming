package projectConfig

import org.gradle.api.Project
import java.io.FileInputStream
import java.util.*

data class SigningConfig(
    val storePath: String,
    val storePassword: String,
    val alias: String,
    val aliasPassword: String
)

fun Project.getSigningConfig(file: String): SigningConfig {
    val signingProperties = Properties()
    signingProperties.load(FileInputStream(this.file(file)))
    return SigningConfig(
        storePath = signingProperties.getProperty("storePath"),
        storePassword = signingProperties.getProperty("storePassword"),
        alias = signingProperties.getProperty("alias"),
        aliasPassword = signingProperties.getProperty("aliasPassword"),
    )
}

fun Project.getDebugSigningConfig(): SigningConfig =
    getSigningConfig("signing-config/signing-debug-config.properties")

fun Project.getReleaseSigningConfig(): SigningConfig =
    getSigningConfig("signing-config/signing-release-config.properties")
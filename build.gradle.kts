import groovy.json.JsonSlurper

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.skie) apply false
}

val generateDesignTokens by tasks.registering {
    group = "design"
    description = "Generates Android + iOS color tokens from design-tokens.json."
    val tokensFile = rootProject.file("design-tokens.json")
    val ktOut = rootProject.file(
        "core/designsystem/build/generated/designtokens/com/yfy/kmp/core/designsystem/theme/DesignTokens.kt",
    )
    val swiftOut = rootProject.file(
        "iosApp/Packages/DesignSystem/Sources/DesignSystem/DesignTokens.swift",
    )
    inputs.file(tokensFile)
    outputs.files(ktOut, swiftOut)
    doLast {
        @Suppress("UNCHECKED_CAST")
        val root = JsonSlurper().parse(tokensFile) as Map<String, Any?>
        @Suppress("UNCHECKED_CAST")
        val colors = root["colors"] as Map<String, Map<String, String>>

        val kt = buildString {
            appendLine("// GENERATED from design-tokens.json — do not edit by hand.")
            appendLine("package com.yfy.kmp.core.designsystem.theme")
            appendLine()
            appendLine("import androidx.compose.ui.graphics.Color")
            appendLine()
            appendLine("object DesignTokens {")
            colors.forEach { (name, pair) ->
                appendLine("    val ${name}Light = Color(0xFF${pair.getValue("light").uppercase()})")
                appendLine("    val ${name}Dark = Color(0xFF${pair.getValue("dark").uppercase()})")
            }
            appendLine("}")
        }
        ktOut.parentFile.mkdirs()
        ktOut.writeText(kt)

        val swift = buildString {
            appendLine("// GENERATED from design-tokens.json — do not edit by hand.")
            appendLine("import Foundation")
            appendLine()
            appendLine("public enum DesignTokens {")
            colors.forEach { (name, pair) ->
                appendLine("    public static let ${name}Light: UInt32 = 0x${pair.getValue("light").uppercase()}")
                appendLine("    public static let ${name}Dark: UInt32 = 0x${pair.getValue("dark").uppercase()}")
            }
            appendLine("}")
        }
        swiftOut.parentFile.mkdirs()
        swiftOut.writeText(swift)
    }
}

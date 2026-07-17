package com.yfy.kmp.archtest

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.test.Test

class ArchitectureTest {

    private val scope = Konsist.scopeFromProject()

    private val sourceFiles get() = scope.files.filterNot { it.path.contains("/build/") }

    private fun sourceClasses() = scope.classes().filterNot { it.path.contains("/build/") }

    private fun String.baseName() = substringBefore('<')

    @Test
    fun `feature modules do not import other features`() {
        sourceFiles
            .filter { it.path.contains("/feature/") }
            .assertTrue { file ->
                val ownFeature = file.path.substringAfter("/feature/").substringBefore("/")
                file.imports.none { import ->
                    import.name.startsWith("com.yfy.kmp.feature.") &&
                        !import.name.startsWith("com.yfy.kmp.feature.$ownFeature.")
                }
            }
    }

    @Test
    fun `commonMain does not import platform packages`() {
        val forbiddenPrefixes = listOf("android.", "java.", "javax.", "platform.", "kotlinx.cinterop.")
        sourceFiles
            .filter { it.path.contains("/commonMain/") }
            .assertTrue { file ->
                file.imports.none { import -> forbiddenPrefixes.any { import.name.startsWith(it) } }
            }
    }

    @Test
    fun `domain and presentation do not import ktor`() {
        sourceFiles
            .filter { file ->
                val pkg = file.packagee?.name.orEmpty()
                pkg.contains(".domain") || pkg.contains(".presentation")
            }
            .assertTrue { file ->
                file.imports.none { it.name.startsWith("io.ktor.") }
            }
    }

    @Test
    fun `the Swift boundary does not expose bare Flow`() {
        val boundaryFiles = sourceFiles.filter { file ->
            file.path.contains("/commonMain/") &&
                (file.path.contains("/shared/") || file.packagee?.name?.contains(".presentation") == true)
        }

        val bareFlow = Regex("""^(kotlinx\.coroutines\.flow\.)?Flow<""")
        val phaseFlow = Regex("""^(kotlinx\.coroutines\.flow\.)?Flow<(com\.yfy\.kmp\.core\.common\.result\.)?Phase<""")
        val isBareFlow = { typeText: String? ->
            typeText != null && bareFlow.containsMatchIn(typeText) && !phaseFlow.containsMatchIn(typeText)
        }

        boundaryFiles.assertTrue { file ->
            val publicFunctions = file.functions(includeNested = true).filter { it.hasPublicOrDefaultModifier }
            val publicProperties = file.properties(includeNested = true).filter { it.hasPublicOrDefaultModifier }

            publicFunctions.none { isBareFlow(it.returnType?.text) } &&
                publicProperties.none { isBareFlow(it.type?.text) }
        }
    }

    @Test
    fun `presentation ViewModels extend BaseViewModel`() {
        val viewModels = sourceClasses().filter {
            it.path.contains("/commonMain/") &&
                it.name.endsWith("ViewModel") &&
                it.name != "BaseViewModel" &&
                !it.hasAbstractModifier
        }
        if (viewModels.isNotEmpty()) {
            viewModels.assertTrue { koClass ->
                koClass.parents(indirectParents = true).any { it.name.baseName() == "BaseViewModel" }
            }
        }
    }

    @Test
    fun `androidMain does not define BaseViewModel subclasses`() {
        val androidClasses = sourceClasses().filter { it.path.contains("/androidMain/") }
        if (androidClasses.isNotEmpty()) {
            androidClasses.assertTrue { koClass ->
                koClass.parents(indirectParents = true).none { it.name.baseName() == "BaseViewModel" }
            }
        }
    }

    @Test
    fun `repository implementations reside in a data package`() {
        val repositoryImpls = sourceClasses().filter { it.name.endsWith("RepositoryImpl") }
        if (repositoryImpls.isNotEmpty()) {
            repositoryImpls.assertTrue { it.resideInPackage("..data..") }
        }
    }

    @Test
    fun `commonMain Impl classes are internal`() {
        val implClasses = sourceClasses().filter {
            it.path.contains("/commonMain/") && it.name.endsWith("Impl")
        }
        if (implClasses.isNotEmpty()) {
            implClasses.assertTrue { it.hasInternalModifier }
        }
    }
}

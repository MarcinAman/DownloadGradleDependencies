package org.jetbrains.dokka.download

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import java.io.InputStream
import java.net.URI
import java.net.URL

abstract class TemplateExampleTask : DefaultTask() {

    @get:Input
    abstract val allowedRepositories: ListProperty<String>

    @get:OutputFile
    abstract val outputDir: RegularFileProperty

    @TaskAction
    fun sampleAction() {
        val allowedRepositoriesUrls =
            allowedRepositories.getOrElse(emptyList())

        val notAllowedRepositories = project.repositories.toList().filter { !it.isAllowed(allowedRepositoriesUrls) }

        project.configurations.forEach { configuration ->
            configuration.allDependencies.forEach { dependency ->
                val isAvailableInAllowedRepositories = project.repositories.asSequence()
                    .map { it as MavenArtifactRepository }
                    .filter { it.url.toASCIIString() in allowedRepositoriesUrls }
                    .mapNotNull { repository -> checkIfDependencyIsDownloadable(repository, dependency) }.any()

                if (!isAvailableInAllowedRepositories && outputDir.asFile.orNull?.resolve(dependency.url())?.exists() == false) {
                    logger.lifecycle("Dependency ${dependency.name} not present, downloading...")
                    notAllowedRepositories.mapNotNull { repository ->
                        checkIfDependencyIsDownloadable(repository as MavenArtifactRepository, dependency)
                    }.firstOrNull()?.let {
                        val destination = outputDir.asFile.orNull?.resolve(dependency.url().substringBeforeLast("/"))?.also { it.mkdirs() }
                        destination?.resolve(dependency.url().substringAfterLast("/"))?.writeBytes(it.readAllBytes())
                        logger.lifecycle("Dependency ${dependency.name} downloaded to ${destination?.absolutePath}")
                    }
                }
            }
        }
    }

    private fun checkIfDependencyIsDownloadable(
        repository: MavenArtifactRepository,
        dependency: Dependency
    ): InputStream? {
        val repositoryUrl = repository.url.let {
            if (!it.toString().endsWith("/")) {
                "$it/"
            } else {
                it.toString()
            }
        }

        return kotlin.runCatching { URL(dependency.url(repositoryUrl)).openStream() }.getOrNull()
    }

    private fun Dependency.url(repositoryUrl: String): String =
        if (!repositoryUrl.endsWith("/")) {
            "$repositoryUrl/" + url()
        } else repositoryUrl + url()

    private fun Dependency.url(): String = String.format(
        "%s/%s/%s/%s-%s.jar",
        group?.replace('.', '/'), name, version,
        name, version
    )

    private fun ArtifactRepository.isAllowed(allowedUrls: List<String>): Boolean =
        (this as MavenArtifactRepository).url.toASCIIString() in allowedUrls
}

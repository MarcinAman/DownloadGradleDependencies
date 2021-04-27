package org.jetbrains.dokka.download

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

const val DEFAULT_OUTPUT_FILE = "template-example.txt"

@Suppress("UnnecessaryAbstractClass")
abstract class TemplateExtension @Inject constructor(project: Project) {

    private val objects = project.objects

    val allowedRepositories: ListProperty<String> = objects.listProperty(String::class.java)

    val outputDir: RegularFileProperty = objects.fileProperty().convention(
        project.layout.buildDirectory.file(DEFAULT_OUTPUT_FILE)
    )
}

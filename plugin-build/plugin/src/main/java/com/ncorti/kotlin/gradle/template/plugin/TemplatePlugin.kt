package org.jetbrains.dokka.download

import org.gradle.api.Plugin
import org.gradle.api.Project

const val EXTENSION_NAME = "downloadDependencies"
const val TASK_NAME = "downloadDependencies"

abstract class TemplatePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Add the 'template' extension object
        val extension = project.extensions.create(EXTENSION_NAME, TemplateExtension::class.java, project)

        // Add a task that uses configuration from the extension object
        project.tasks.register(TASK_NAME, TemplateExampleTask::class.java) {
            it.allowedRepositories.set(extension.allowedRepositories)
            it.outputDir.set(extension.outputDir)
        }
    }
}

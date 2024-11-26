package org.hivevm.gradle;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileType;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.work.ChangeType;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;

public abstract class DevOpsTask extends DefaultTask {

    @Incremental
    @InputDirectory
    @PathSensitive(PathSensitivity.NAME_ONLY)
    @Option(option = "inputDir", description = "Gets the source path")
    abstract DirectoryProperty getInputDir();

    @OutputDirectory
    @Option(option = "outputDir", description = "Gets the source path")
    abstract DirectoryProperty getOutputDir();

    @Input
    @Option(option = "input", description = "Gets the source path")
    abstract Property<String> getInputProperty();

    @TaskAction
    void execute(InputChanges inputChanges) {
        System.out.println(inputChanges.isIncremental()
            ? "Executing incrementally"
            : "Executing non-incrementally"
        );

        inputChanges.getFileChanges(getInputDir()).forEach(change -> {
             if (change.getFileType() == FileType.DIRECTORY) 
                 return;

            System.out.println("${change.changeType}: ${change.normalizedPath}");
            File targetFile = getOutputDir().file(change.getNormalizedPath()).get().getAsFile();
            if (change.getChangeType() == ChangeType.REMOVED) {
                targetFile.delete();
            } else {
                // targetFile.text = change.file.text.reverse()
            }
        });
    }
}
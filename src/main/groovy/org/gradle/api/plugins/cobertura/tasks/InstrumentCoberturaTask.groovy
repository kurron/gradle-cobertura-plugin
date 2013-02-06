package org.gradle.api.plugins.cobertura.tasks

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.tasks.util.PatternSet

class InstrumentCoberturaTask extends SourceTask {

    @InputFiles
    FileCollection coberturaClasspath

    @OutputDirectory
    File classesDir

    @OutputFile
    File serFile

    @Input
    List<String> ignores

    @TaskAction
    def run() {
        def ant = getServices().get(IsolatedAntBuilder).withClasspath(getCoberturaClasspath())
        ant.execute {
            taskdef(name: 'cobertura-instrument', classname: "net.sourceforge.cobertura.ant.InstrumentTask")
            'cobertura-instrument'(toDir: getClassesDir(), datafile: getSerFile()) {
                getSourceClassFiles().addToAntBuilder(delegate, "fileset", FileCollection.AntType.FileSet)
            }
        }

//
    }

    protected FileCollection getSourceClassFiles() {
        getIgnores().each { println "Excluding $it from Cobertura instrumentation." }
        PatternSet pattern = new PatternSet()
        pattern.exclude( getIgnores() )
        pattern.include ( '**/*.class' )
        return getSource().matching( pattern )
    }

    FileCollection getInstrumentedClassFiles() {
        project.files({ getClassesDir() }) {
            builtBy this
        }
    }
}

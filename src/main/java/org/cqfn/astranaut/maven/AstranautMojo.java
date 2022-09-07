/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.maven;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.cqfn.astranaut.analyzer.EnvironmentPreparator;
import org.cqfn.astranaut.codegen.java.Environment;
import org.cqfn.astranaut.codegen.java.License;
import org.cqfn.astranaut.codegen.java.ProgramGenerator;
import org.cqfn.astranaut.codegen.java.TaggedChild;
import org.cqfn.astranaut.exceptions.BaseException;
import org.cqfn.astranaut.parser.ProgramParser;
import org.cqfn.astranaut.rules.Program;
import org.cqfn.astranaut.utils.FilesReader;

/**
 * Parses a DSL file and transforms its rules into Java source files.
 *
 * @since 0.1
 */
@Mojo(
    name = "generate",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE,
    requiresProject = true)
@SuppressWarnings("PMD.DataClass")
public final class AstranautMojo extends AbstractMojo {
    /**
     * The file that contains DSL rules.
     */
    @Parameter(property = "dsl", defaultValue = "${basedir}/src/main/dsl/rules.dsl")
    private File dsl;

    /**
     * The name of file that contains license header.
     */
    @Parameter(property = "license", defaultValue = "LICENSE.txt")
    private String license;

    /**
     * The output directory where the Java files are generated.
     */
    @Parameter(property = "output",
        defaultValue = "${project.build.directory}/generated-sources/astranaut")
    private File output;

    /**
     * The Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    /**
     * Set Maven Project (used mostly for unit testing).
     * @param proj The project to set
     */
    public void setProject(final MavenProject proj) {
        this.project = proj;
    }

    /**
     * Set DSL file (mostly for unit testing).
     * @param rules The file that contains DSL rules
     */
    public void setDsl(final File rules) {
        this.dsl = rules;
    }

    /**
     * Set path to the license (mostly for unit testing).
     * @param path The path to the license
     */
    public void setLicense(final String path) {
        this.license = path;
    }

    /**
     * Set DSL file (mostly for unit testing).
     * @param dir The output directory where the Java files are generated
     */
    public void setOutput(final File dir) {
        this.output = dir;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (this.project != null) {
            this.addSourceRoot(this.output);
            final String rules = this.dsl.getPath();
            String code = "";
            try {
                code = new FilesReader(rules).readAsString();
            } catch (final IOException exception) {
                getLog().error(exception);
                throw new MojoExecutionException(
                    "Cannot read DSL file", exception
                );
            }
            try {
                final ProgramParser parser = new ProgramParser(code);
                final Program program = parser.parse();
                final Map<String, Environment> env =
                    new EnvironmentPreparator(program, new AstranautMojo.EnvironmentImpl())
                        .prepare();
                final ProgramGenerator generator =
                    new ProgramGenerator(this.output.getPath(), program, env);
                generator.generate();
            } catch (final BaseException exception) {
                throw new MojoExecutionException(
                    "Cannot generate source files", exception
                );
            }
        }
    }

    /**
     * Adds new sources to the Maven's build.
     * @param dir The root directory of generated files
     */
    private void addSourceRoot(final File dir) {
        if (dir.isDirectory()) {
            this.project.addCompileSourceRoot(dir.getPath());
        }
    }

    /**
     * Environment implementation.
     *
     * @since 0.1.5
     */
    private class EnvironmentImpl implements Environment {
        /**
         * The license.
         */
        private final License license;

        /**
         * Constructor.
         */
        EnvironmentImpl() {
            this.license = new License(AstranautMojo.this.license);
        }

        @Override
        public License getLicense() {
            return this.license;
        }

        @Override
        public String getVersion() {
            return "0.1";
        }

        @Override
        public String getRootPackage() {
            return "org.uast.uast.generated.tree";
        }

        @Override
        public String getBasePackage() {
            return "org.uast.uast.base";
        }

        @Override
        public boolean isTestMode() {
            return false;
        }

        @Override
        public String getLanguage() {
            return "";
        }

        @Override
        public List<String> getHierarchy(final String name) {
            return Collections.singletonList(name);
        }

        @Override
        public List<TaggedChild> getTags(final String type) {
            return Collections.emptyList();
        }

        @Override
        public Set<String> getImports(final String type) {
            return Collections.emptySet();
        }
    }
}

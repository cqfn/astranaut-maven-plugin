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

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
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
    @Parameter(property = "astranaut.dsl", defaultValue = "${basedir}/src/main/dsl/rules.dsl")
    private File dsl;

    /**
     * The name of file that contains license header.
     */
    @Parameter(property = "astranaut.license", defaultValue = "${basedir}/LICENSE.txt")
    private File license;

    /**
     * The output directory where the Java files are generated.
     */
    @Parameter(property = "astranaut.output",
        defaultValue = "${project.build.directory}/generated-sources/astranaut")
    private File output;

    /**
     * The package of the generated Java source files.
     */
    @Parameter(property = "astranaut.pkg",
        defaultValue = "org.cqfn.astranaut.generated.tree")
    private String pkg;

    /**
     * The package of the generated Java source files.
     */
    @Parameter(property = "astranaut.version", defaultValue = "0.0.0")
    private String version;

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
     * Set DSL file (used mostly for unit testing).
     * @param rules The file that contains DSL rules
     */
    public void setDsl(final File rules) {
        this.dsl = rules;
    }

    /**
     * Set license file (used mostly for unit testing).
     * @param path The path to the license
     */
    public void setLicense(final File path) {
        this.license = path;
    }

    /**
     * Set DSL file (used mostly for unit testing).
     * @param dir The output directory where the Java files are generated
     */
    public void setOutput(final File dir) {
        this.output = dir;
    }

    /**
     * Set package (used mostly for unit testing).
     * @param genpkg The package of the generated files
     */
    public void setPackage(final String genpkg) {
        this.pkg = genpkg;
    }

    /**
     * Set version (used mostly for unit testing).
     * @param genversion The version of the implementation
     */
    public void setVersion(final String genversion) {
        this.version = genversion;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (this.project != null) {
            this.validateLicense();
            this.validatePackage();
            this.addSourceRoot();
            final String rules = this.dsl.getAbsolutePath();
            String code = "";
            try {
                code = new FilesReader(rules).readAsString();
            } catch (final IOException exception) {
                Logger.info(
                    this,
                    "Specified DSL file does not exist: %s",
                    this.dsl.getPath()
                );
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
                Logger.info(this, "Generation completed");
            } catch (final BaseException exception) {
                throw new MojoExecutionException(
                    "Cannot generate source files", exception
                );
            }
        }
    }

    /**
     * Validates custom license or existence of license expected by default.
     * @throws MojoExecutionException If the specified path is invalid
     */
    private void validateLicense() throws MojoExecutionException {
        if (!this.license.exists()) {
            Logger.info(
                this,
                "Specified license file does not exist: %s",
                this.license
            );
            throw new MojoExecutionException(
                "Cannot find the license file"
            );
        }
    }

    /**
     * Validates a string that should contain a package name.
     * @throws MojoExecutionException If the specified path is invalid
     */
    private void validatePackage() throws MojoExecutionException {
        final String pattern = "(([a-z])+\\.)+([a-z])+";
        final boolean valid = Pattern.matches(pattern, this.pkg);
        if (!valid) {
            Logger.info(
                this,
                "Specified package for generation does not follow Java's package name rules: %s",
                this.pkg
            );
            throw new MojoExecutionException("Cannot create package for generation");
        }
    }

    /**
     * Adds the root directory of generated files to the Maven's build.
     * @throws MojoExecutionException If the specified path is invalid
     */
    private void addSourceRoot() throws MojoExecutionException {
        final File dir = new File(this.output.getAbsolutePath());
        final List<String> sources = this.project.getCompileSourceRoots();
        final String path = dir.getAbsolutePath();
        for (final String src : sources) {
            if (path.startsWith(src) && !path.equals(src)) {
                Logger.info(
                    this,
                    "Specified target directory for generation is inside existing source root: %s",
                    path
                );
                throw new MojoExecutionException("Cannot create output directory");
            }
        }
        final boolean exist;
        if (dir.exists()) {
            exist = true;
        } else {
            exist = dir.mkdirs();
        }
        if (exist) {
            this.project.addCompileSourceRoot(path);
        } else {
            Logger.info(
                this,
                "Specified target directory for generation is invalid: %s",
                path
            );
            throw new MojoExecutionException("Cannot find output directory");
        }
    }

    /**
     * Environment implementation.
     *
     * @since 0.1.5
     */
    private class EnvironmentImpl implements Environment {
        @Override
        public License getLicense() {
            return new License(AstranautMojo.this.license.getAbsolutePath());
        }

        @Override
        public String getVersion() {
            final String result;
            if (AstranautMojo.this.version == null) {
                result = AstranautMojo.this.project.getVersion();
            } else {
                result = AstranautMojo.this.version;
            }
            return result;
        }

        @Override
        public String getRootPackage() {
            return AstranautMojo.this.pkg;
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

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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

/**
 * Test for the {@link AstranautMojo} class.
 *
 * @since 0.1
 */
public class AstranautMojoTest {
    /**
     * Test plugin with custom parameters and an empty Maven project.
     * @param source A temporary directory
     * @throws IOException If fails
     */
    @Test
    public void testWithCustomParameters(@TempDir final Path source) throws IOException {
        final Path dsl = this.createTempFile(
            source, "rules.dsl", "StringLiteral <- $String$, $#$, $#$;"
        );
        final Path license = this.createTempFile(
            source, "LICENSE.txt", "The MIT License"
        );
        final Path dir = Files.createTempDirectory(source, "generated-sources");
        final AstranautMojo mojo = new AstranautMojo();
        mojo.setOutput(dir.toFile());
        mojo.setDsl(dsl.toFile());
        mojo.setLicense(license.toString());
        final MavenProject project = Mockito.mock(MavenProject.class);
        boolean oops = false;
        try {
            mojo.setProject(project);
            mojo.execute();
        } catch (final MojoExecutionException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
        final Set<String> files = this.listFilesInDir(dir.toAbsolutePath().toString());
        final int expected = 3;
        Assertions.assertEquals(expected, files.size());
    }

    /**
     * Creates a temporary file to test.
     * @param source A temporary directory
     * @param filename The name (directory) of a file
     * @param data The data of a file
     * @return The path to a temporary file
     * @throws IOException If fails to create a temporary file
     */
    private Path createTempFile(
        @TempDir final Path source,
        final String filename,
        final String data) throws IOException {
        final Path file = source.resolve(filename);
        final List<String> lines = Collections.singletonList(data);
        Files.write(file, lines);
        return file;
    }

    /**
     * Gets a list of names of generated files.
     * @param dir A temporary directory for files generation
     * @return List of names of generated files
     * @throws IOException If fails
     */
    private Set<String> listFilesInDir(final String dir) throws IOException {
        final Set<String> files = new HashSet<>();
        Files.walkFileTree(
            Paths.get(dir), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
                    if (!Files.isDirectory(file)) {
                        files.add(file.getFileName().toString());
                    }
                    return FileVisitResult.CONTINUE;
                }
            }
        );
        return files;
    }
}

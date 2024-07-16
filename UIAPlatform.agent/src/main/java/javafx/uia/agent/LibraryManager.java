/*
 * -----------------------------------------------------------------
 * Copyright (c) 2021 BestSolution.at EDV Systemhaus GmbH
 * All Rights Reserved.
 *
 * BestSolution.at MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE  OR NON - INFRINGEMENT.
 * BestSolution.at SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * This software is released under the terms of the
 *
 *                  "GNU General Public License, Version 2
 *                         with classpath exception"
 *
 * and may only be distributed and used under the terms of the
 * mentioned license. You should have received a copy of the license
 * along with this software product, if not you can download it from
 * http://www.gnu.org/licenses/gpl.html
 * ----------------------------------------------------------------
 */
package javafx.uia.agent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import at.bestsolution.uia.agent.internal.AgentLogger;
import at.bestsolution.uia.agent.internal.AgentLoggerFactory;

public class LibraryManager {

    private static final AgentLogger LOG = AgentLoggerFactory.create(LibraryManager.class);

    static final Path tempDir;
    static final Path extDir;

    static final Path coreJar;

    static {
        try {
            tempDir = Files.createTempDirectory("openfx-uia");
            extDir = Files.createDirectories(tempDir.resolve("lib"));

            coreJar = extract("UIAPlatform.core.jar", extDir);

            Runtime.getRuntime().addShutdownHook(createCleanup());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Path extract(String file, Path dir) throws IOException {
        URL source = LibraryManager.class.getResource("/" + file);
        Path target = dir.resolve(file);
        Files.copy(source.openStream(), target);
        LOG.debug(null, () -> "Using " + target.toString());
        return target;
    }

    private static Thread createCleanup() {
        Thread cleanup = new Thread(() -> {
            try {
                //Logger.debug(LibraryManager.class, () -> "deleting " + tempDir + " on shutdown");

                FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        //Logger.debug(LibraryManager.class, () -> "  deleting file " + file);
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            // not deleted
                        }
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        //Logger.debug(LibraryManager.class, () -> "  deleting directory " + dir);
                        try {
                            Files.delete(dir);
                        } catch (IOException e) {
                            // not deleted
                        }
                        return FileVisitResult.CONTINUE;
                    }
                };
                Files.walkFileTree(tempDir, visitor);
            } catch (IOException e) {
                //
            }
        });
        return cleanup;
    }
}

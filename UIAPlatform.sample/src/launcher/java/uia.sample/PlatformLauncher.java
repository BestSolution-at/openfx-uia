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
package uia.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PlatformLauncher {

    static Path base;
    static Path samples;
    static Path platform;
    
    static Path getBase() throws IOException {
        if (base == null) {
            base = Files.createTempDirectory("openfx-uia-sample");
        }
        return base;
    }

    static Path getSamplesJar() throws IOException {
        if (samples == null) {
            URL file = PlatformLauncher.class.getResource("/UIAPlatform.sample.jar");
            samples= getBase().resolve("UIAPlatform.sample.jar");
            Files.copy(file.openStream(), samples);
        }
        return samples;
    }

    static Path getPlatformJar() throws IOException {
        if (platform == null) {
            URL file = PlatformLauncher.class.getResource("/UIAPlatform.jar");
            Path targetDir = getBase().resolve("ext");
            Files.createDirectories(targetDir);
            platform = targetDir.resolve("UIAPlatform.jar");
            Files.copy(file.openStream(), platform);
        }
        return platform;
    }
    
    static Path getDefaultExtPath() {
        Path jdk8fx = Paths.get(System.getenv("JDK8FX"));
        return jdk8fx.resolve("jre/lib/ext");
    }

    static String getClasspath() throws IOException {
        List<Path> classpath = new ArrayList<>();
        classpath.add(getSamplesJar());
        return classpath.stream().map(Object::toString).collect(Collectors.joining(System.getProperty("path.separator")));
    }

    static String getExtPath() throws IOException {
        List<Path> extPath = new ArrayList<>();
        extPath.add(getDefaultExtPath());
        extPath.add(getPlatformJar().getParent());
        return extPath.stream().map(Object::toString).collect(Collectors.joining(System.getProperty("path.separator")));
    }

    static String getJavaBinary() {
        Path jdk8fx = Paths.get(System.getenv("JDK8FX"));
        Path java = jdk8fx.resolve("bin/java");
        return java.toString();
    }

    public static void main(String[] args) throws IOException {

        try {
            
            //Process p0 = new ProcessBuilder("dir", getClasspath()).start();
            //inheritIO(p0.getInputStream(), System.out);
            //inheritIO(p0.getErrorStream(), System.err);

            List<String> commandLine = Arrays.asList(
                getJavaBinary(), 
                "-Dglass.platform=UIA",
                "-Dglass.accessible.force=true",
                "-Djava.ext.dirs="+getExtPath(),
                "-cp", getClasspath(),
                "uia.sample.Simple"
            );
            System.err.println("Launching " + commandLine);
            Process p = new ProcessBuilder(commandLine).start();
            inheritIO(p.getInputStream(), System.out);
            inheritIO(p.getErrorStream(), System.err);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void inheritIO(final InputStream src, final PrintStream dest) {
        new Thread(new Runnable() {
            public void run() {
                try (Scanner sc = new Scanner(src)) {
                    while (sc.hasNextLine()) {
                        dest.println(sc.nextLine());
                    }
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
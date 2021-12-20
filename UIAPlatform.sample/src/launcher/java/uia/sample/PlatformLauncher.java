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

    final static Path base;
    final static Path samples;
    final static Path platform;
    final static Path agent;

    static {
        try {
            base = Files.createTempDirectory("openfx-uia-sample");
            platform = extract("UIAPlatform.jar", base);
            samples = extract("UIAPlatform.sample.jar", base);
            agent = extract("UIAPlatform.agent.jar", base);
        } catch (IOException e) {
            throw new RuntimeException("Failed to launch", e);
        }
    }

    static Path extract(String name, Path targetDir) throws IOException {
        URL file = PlatformLauncher.class.getResource("/" + name);
        Files.createDirectories(targetDir);
        Path target = targetDir.resolve(name);
        Files.copy(file.openStream(), target);
        return target;
    }

    static String getClasspath() throws IOException {
        List<Path> classpath = new ArrayList<>();
        classpath.add(platform);
        classpath.add(samples);
        return classpath.stream().map(Object::toString).collect(Collectors.joining(System.getProperty("path.separator")));
    }

    static String getJavaBinary() {
        Path jdk8fx = Paths.get(System.getenv("JDK8FX"));
        Path java = jdk8fx.resolve("bin/java");
        return java.toString();
    }

    static boolean checkJDK() {
        String jdk8fx = System.getenv("JDK8FX");
        if (jdk8fx == null) {
            return false;
        }
        Path path = Paths.get(jdk8fx);
        if (!Files.exists(path)) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
       
        if (!checkJDK()) {
            System.out.println("The environment variable JDK8FX must point to your Java 8 JDK with FX for this to work.");
            System.exit(-1);
        }
         
        try {
            List<String> commandLine = Arrays.asList(
                getJavaBinary(), 
                "-javaagent:"+agent,
                "-cp", getClasspath(),
                "-Duia.log="+Boolean.getBoolean("uia.log"),
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
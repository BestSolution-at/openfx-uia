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
package at.bestsolution.uia.core;

import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import at.bestsolution.uia.core.internal.CoreLogger;
import at.bestsolution.uia.core.internal.CoreLoggerFactory;


public class Lib {

    private static final CoreLogger LOG = CoreLoggerFactory.create(Lib.class);

    public final static String NAME = "UIAPlatform.core";
    public final static String GIT_HASH;
    public final static String GIT_VERSION;
    
    static {
      Optional<Manifest> manifest = readManifest(Lib.class);
      GIT_HASH = manifest.map(m -> m.getMainAttributes().getValue("Git-Hash")).orElse("<unknown>");
      GIT_VERSION = manifest.map(m -> m.getMainAttributes().getValue("Git-Version")).orElse("<unknown>");
    }

    private static Optional<Manifest> readManifest(Class<?> cls) {
    try {
      URL urlManifest = cls.getClassLoader().getResource(JarFile.MANIFEST_NAME);
      InputStream is = urlManifest.openStream();
      if (is != null) {
        Manifest manifest = new Manifest(is);
        return Optional.of(manifest);
      }
    } catch (Exception e) {
      LOG.error(() -> "error reading manifest", e);
    }
    return Optional.empty();
  }

  public static void reportVersionInfo() {
    LOG.info(NAME, () -> "Git-Hash: " + GIT_HASH);
    LOG.info(NAME, () -> "Git-Version: " + GIT_VERSION);
  }

}

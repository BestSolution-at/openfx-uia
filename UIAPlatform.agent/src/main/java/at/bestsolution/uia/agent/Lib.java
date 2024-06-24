package at.bestsolution.uia.agent;

import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Lib {

    private static boolean LOG = Boolean.getBoolean("uia.log");

    public final static String NAME = "UIAPlatform.agent";
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
      if (LOG) {
        System.out.println("error reading manifest");
        e.printStackTrace(System.out);
      }
    }
    return Optional.empty();
  }

  public static void reportVersionInfo() {
    if (LOG) {
      System.out.println(NAME + ": Git-Hash: " + GIT_HASH);
      System.out.println(NAME + ": Git-Version: " + GIT_VERSION);
    }
  }
}

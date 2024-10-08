package at.bestsolution.uia.agent;

import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import at.bestsolution.uia.agent.internal.AgentLogger;
import at.bestsolution.uia.agent.internal.AgentLoggerFactory;

public class Lib {

    private static final AgentLogger LOG = AgentLoggerFactory.create(Lib.class);

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
      LOG.error(() -> "error reading manifest", e);
    }
    return Optional.empty();
  }

  public static void reportVersionInfo() {
    LOG.info(NAME, () -> "Git-Hash: " + GIT_HASH);
    LOG.info(NAME, () -> "Git-Version: " + GIT_VERSION);
  }
  
}

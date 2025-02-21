package at.bestsolution.uia;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.sun.javafx.stage.WindowHelper;

import javafx.scene.Scene;

@SuppressWarnings({ "restriction", "deprecation" })
public class PlatformBoundsUtil {

  private static final Logger LOG = Logger.create(PlatformBoundsUtil.class);

  static class ReflectiveWindowStageAccess {
    private Field platformWindow;

    ReflectiveWindowStageAccess() throws ClassNotFoundException, SecurityException, NoSuchFieldException {
      Class<?> windowStageClass = Class.forName("com.sun.javafx.tk.quantum.WindowStage");

      this.platformWindow = windowStageClass.getDeclaredField("platformWindow");
      this.platformWindow.setAccessible(true);
    }

    com.sun.glass.ui.Window getPlatformWindow(Object windowStage) throws IllegalArgumentException, IllegalAccessException {
      return (com.sun.glass.ui.Window) this.platformWindow.get(windowStage);
    }
  }

  static class ReflectiveScreenAccessModernAPI {
    private Method toPlatformX;
    private Method toPlatformY;
    private Method getPlatformScaleX;
    private Method getPlatformScaleY;

    ReflectiveScreenAccessModernAPI() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
      Class<com.sun.glass.ui.Screen> screenClass = com.sun.glass.ui.Screen.class;
      this.toPlatformX = screenClass.getMethod("toPlatformX", float.class);
      this.toPlatformY = screenClass.getMethod("toPlatformY", float.class);

      this.getPlatformScaleX = screenClass.getMethod("getPlatformScaleX");
      this.getPlatformScaleY = screenClass.getMethod("getPlatformScaleY");
    }

    public int toPlatformX(com.sun.glass.ui.Screen screen, float ux) throws IllegalAccessException, InvocationTargetException{
      return (int) this.toPlatformX.invoke(screen, ux);
    }

    public int toPlatformY(com.sun.glass.ui.Screen screen, float ux) throws IllegalAccessException, InvocationTargetException{
      return (int) this.toPlatformY.invoke(screen, ux);
    }

    public float getPlatformScaleX(com.sun.glass.ui.Screen screen) throws IllegalAccessException, InvocationTargetException{
      return (float) this.getPlatformScaleX.invoke(screen);
    }

    public float getPlatformScaleY(com.sun.glass.ui.Screen screen) throws IllegalAccessException, InvocationTargetException{
      return (float) this.getPlatformScaleY.invoke(screen);
    }
  }

  static class ReflectiveScreenAccessLegacyAPI {
    private Method getUIScale;

    ReflectiveScreenAccessLegacyAPI() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
      Class<com.sun.glass.ui.Screen> screenClass = com.sun.glass.ui.Screen.class;
      this.getUIScale = screenClass.getMethod("getUIScale");
    }

    public float getUIScale(com.sun.glass.ui.Screen screen) throws IllegalAccessException, InvocationTargetException{
      return (float) this.getUIScale.invoke(screen);
    }
  }

  private static ReflectiveScreenAccessModernAPI screenAccessModern = null;
  private static ReflectiveScreenAccessLegacyAPI screenAccessLegacy = null;
  private static ReflectiveWindowStageAccess windowStageAccess = null;

  static {
    // try to figure out if current javafx fix is there (https://github.com/openjdk/jfx/pull/853)
    // and make it accessible via reflection

    try {
      PlatformBoundsUtil.screenAccessModern = new ReflectiveScreenAccessModernAPI();
    } catch (Exception e) {
      LOG.debug(PlatformBoundsUtil.class, () -> " could not find modern screen api", e);
    }

    try {
      PlatformBoundsUtil.screenAccessLegacy = new ReflectiveScreenAccessLegacyAPI();
    } catch (Exception e) {
      LOG.debug(PlatformBoundsUtil.class, () -> "could not find legacy screen api", e);
    }

    try {
      PlatformBoundsUtil.windowStageAccess = new ReflectiveWindowStageAccess();
    } catch (Exception e) {
      LOG.debug(PlatformBoundsUtil.class, () -> "could not access screen", e);
    }

    boolean stageAccess = PlatformBoundsUtil.windowStageAccess != null;
    boolean legacy = PlatformBoundsUtil.screenAccessLegacy != null;
    boolean modern = PlatformBoundsUtil.screenAccessModern != null;

    if (stageAccess && ( legacy || modern)) {
      LOG.debug(PlatformBoundsUtil.class, () -> "api available using " + (modern ? "modern" : "legacy") + " api to access screen");
    } else {
      LOG.warning(PlatformBoundsUtil.class, () -> "no api available. platform scaling not supported.");
    }


  }

  public static com.sun.glass.ui.Screen getScreen(Scene scene) {
    if (windowStageAccess == null) {
      return null;
    }

    javafx.stage.Window window = scene.getWindow();
    try {

      // Method getPeer = window.getClass().getDeclaredMethod("getPeer");
      // getPeer.setAccessible(true);
      // com.sun.javafx.tk.TKStage tkStage = (com.sun.javafx.tk.TKStage) getPeer.invoke(window);

      com.sun.javafx.tk.TKStage tkStage = WindowHelper.getPeer(window);

      com.sun.glass.ui.Window platformWindow = windowStageAccess.getPlatformWindow(tkStage);
      return platformWindow.getScreen();
    } catch (Exception e) {
      LOG.debug(PlatformBoundsUtil.class, () -> "WARNING: could not get screen", e);
      return null;
    }
  }

  public static float[] convertToPlatformBounds(Scene scene, float x, float y, float w, float h) {
    float[] platformBounds = new float[] { x, y, w, h };
    com.sun.glass.ui.Screen screen = getScreen(scene);
    if (screen == null) return platformBounds;

    if (PlatformBoundsUtil.screenAccessModern != null) {
      // try modern api
      try {
        platformBounds[0] = screenAccessModern.toPlatformX(screen, x);
        platformBounds[1] = screenAccessModern.toPlatformY(screen, y);
        platformBounds[2] = (float) Math.ceil(w * screenAccessModern.getPlatformScaleX(screen));
        platformBounds[3] = (float) Math.ceil(h * screenAccessModern.getPlatformScaleY(screen));
      } catch (Exception e) {
        LOG.debug(PlatformBoundsUtil.class, () -> "WARNING: could not use modern screen api", e);
      }
      return platformBounds;
    } else if (PlatformBoundsUtil.screenAccessLegacy != null) {
      // try legacy api
      try {
        float uiScale = screenAccessLegacy.getUIScale(screen);
        platformBounds[0] = platformBounds[0] * uiScale;
        platformBounds[1] = platformBounds[1] * uiScale;
        platformBounds[2] = platformBounds[2] * uiScale;
        platformBounds[3] = platformBounds[3] * uiScale;
      } catch (Exception e) {
        LOG.debug(PlatformBoundsUtil.class, () -> "WARNING: could not use legacy screen api", e);
      }
    }
    return platformBounds;
  }

}

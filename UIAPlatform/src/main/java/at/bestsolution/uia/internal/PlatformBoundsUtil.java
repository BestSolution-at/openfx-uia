package at.bestsolution.uia.internal;

import com.sun.javafx.stage.WindowHelper;

import com.sun.javafx.tk.TKStage;
import com.sun.javafx.tk.quantum.WindowStage;
import com.sun.glass.ui.Screen;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.stage.Window;


public class PlatformBoundsUtil {

  private static final Logger LOG = Logger.create(PlatformBoundsUtil.class);


  public static float getPlatformScaleX(Window window) {
    return WindowHelper.getWindowAccessor().getPlatformScaleX(window);
  }
  public static float getPlatformScaleX(Scene scene) {
    return getPlatformScaleX(scene.getWindow());
  }
  public static float getPlatformScaleY(Window window) {
    return WindowHelper.getWindowAccessor().getPlatformScaleY(window);
  }
  public static float getPlatformScaleY(Scene scene) {
    return getPlatformScaleY(scene.getWindow());
  }

  public static Point2D convertToPlatformPoint(Scene scene, Point2D p) {
    Screen screen = getScreen(scene);
    LOG.info(() -> "convertToPlatformPoint on " + screen);
    return new Point2D(screen.toPlatformX((float) p.getX()), screen.toPlatformY((float) p.getY()));
  }
  public static Point2D convertFromPlatformPoint(Scene scene, Point2D p) {
    Screen screen = getScreen(scene);
    LOG.info(() -> "convertFromPlatformPoint on " + screen);
    return new Point2D(screen.fromPlatformX((int) Math.round(p.getX())), screen.fromPlatformY((int) Math.round(p.getY())));
  }

  public static Screen getScreen(Scene scene) {
    if (scene == null || scene.getWindow() == null) return null;
    TKStage tkStage = WindowHelper.getPeer(scene.getWindow());
    if (!(tkStage instanceof WindowStage)) return null;
    WindowStage windowStage = (WindowStage) tkStage;
    if (windowStage.getPlatformWindow() == null) return null;
    return windowStage.getPlatformWindow().getScreen();
  }

  public static float[] convertToPlatformBounds(Scene scene, float x, float y, float w, float h) {
    float[] platformBounds = new float[] { x, y, w, h };
    Screen screen = getScreen(scene);
    if (screen == null) return platformBounds;
    platformBounds[0] = screen.toPlatformX(x);
    platformBounds[1] = screen.toPlatformY(y);
    platformBounds[2] = (float) Math.ceil(w * screen.getPlatformScaleX());
    platformBounds[3] = (float) Math.ceil(h * screen.getPlatformScaleY());
    return platformBounds;
  }

}

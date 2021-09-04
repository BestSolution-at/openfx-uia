package uia.sample.samples.model;

import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.uia.IUIAElement;
import javafx.uia.IUIAVirtualRootElement;

public class UIACanvas extends BaseModel implements /*IUIAElement,*/ IUIAVirtualRootElement {

        public Canvas canvas;

        @Override
        public List<IUIAElement> getChildren() {
            return getModelChildren().stream().map(child -> (IUIAElement) child).collect(Collectors.toList());
        }

        @Override
        public IUIAElement getChildFromPoint(Point2D point) {
            return null;
        }

        @Override
        public Bounds getBounds() {
            return canvas.localToScreen(canvas.getLayoutBounds());
        }

        @Override
        public void SetFocus() {
        }

        @Override
        public void layout() {
            getModelChildren().forEach(child -> child.layout());
        }

        public static Canvas getCanvas(IModel model) {
            return ((BaseModel) model).parents().filter(p -> p instanceof UIACanvas)
            .map(p -> (UIACanvas) p)
            .findFirst()
            .map(p -> p.canvas)
            .orElse(null);
        }
}

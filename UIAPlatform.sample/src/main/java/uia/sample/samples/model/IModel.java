package uia.sample.samples.model;

import java.util.List;
import java.util.stream.Stream;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import uia.sample.samples.model.TextSupport.Glyph;

public interface IModel {

    void layout();

    int getBegin();
    int getEnd();

    int computeIndices(int curBegin);

    int computeGlyphs(int beginIndex);

    Stream<Glyph> streamGlyphs();

    void render(GraphicsContext gc);

    Bounds getLayoutBounds();
    Bounds getLocalBounds();

    String getContent();

    IModel getModelParent();
    List<IModel> getModelChildren();


    List<IModel> pick(double x, double y);
    int pickText(double x, double y);

    List<Bounds> getTextBounds(int begin, int end);
    List<IModel> getTextChildren(int begin, int end);
    IModel getTextEnclosing(int begin, int end);
    
}

package uia.sample.samples.model;

import java.util.List;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;

public interface IModel {

    void layout();

    int getBegin();
    int getEnd();

    int computeIndices(int curBegin);

    void render(GraphicsContext gc);

    Bounds getLayoutBounds();
    Bounds getLocalBounds();

    String getContent();

    IModel getModelParent();
    List<IModel> getModelChildren();


    List<Bounds> getTextBounds(int begin, int end);
    List<IModel> getTextChildren(int begin, int end);
    
}

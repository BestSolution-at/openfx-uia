package uia.sample.samples.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;

public abstract class BaseModel implements IModel {

    protected IModel modelParent;
    private List<IModel> modelChildren = new ArrayList<>();

    protected int begin;
    protected int end;

    public double layoutX;
    public double layoutY;

    public double layoutW;
    public double layoutH;

    @Override
    public int computeIndices(int curBegin) {
        int length = 0;
        begin = curBegin;
        for (IModel child : modelChildren) {
            length += child.computeIndices(begin + length);
        }
        end = begin + length;
        return length;
    }



    Stream<BaseModel> flatten() {
        return Stream.concat(
            Stream.of(this),
            getModelChildren().stream()
            .map(m -> (BaseModel)m)
            .flatMap(BaseModel::flatten)
        );
    }

    public Stream<BaseModel> parents() {
        BaseModel parent = (BaseModel) getModelParent();
        if (parent == null) {
            return Stream.empty();
        } else {
            return Stream.concat(
                Stream.of(parent),
                parent.parents()
            );
        }
    }

    @Override
    public String getContent() {
        return getModelChildren().stream().map(IModel::getContent).collect(Collectors.joining());
    }

    @Override
    public void render(GraphicsContext gc) {
        for (IModel child : getModelChildren()) {
            child.render(gc);
        }
    }

    
    double computeParentLayoutX() {
        double parentLayoutX = 0;
        IModel parent = modelParent;
        while (parent != null) {
            parentLayoutX += ((BaseModel) parent).layoutX;
            parent = parent.getModelParent();
        }
        return parentLayoutX;
    }

    double computeParentLayoutY() {
        double parentLayoutY = 0;
        IModel parent = modelParent;
        while (parent != null) {
            parentLayoutY += ((BaseModel) parent).layoutY;
            parent = parent.getModelParent();
        }
        return parentLayoutY;
    }

    @Override
    public int getBegin() {
        return begin;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public Bounds getLayoutBounds() {
        return new BoundingBox(computeParentLayoutX() + layoutX, computeParentLayoutY() + layoutY, layoutW, layoutH);
    }

    @Override
    public Bounds getLocalBounds() {
        return new BoundingBox(0, 0, layoutW, layoutH);
    }

    @Override
    public IModel getModelParent() {
        return modelParent;
    }
    @Override
    public List<IModel> getModelChildren() {
        return Collections.unmodifiableList(modelChildren);
    }

    public void addChild(IModel child) {
        modelChildren.add(child);
        ((BaseModel)child).modelParent = this;
    }
    


    @Override
    public List<Bounds> getTextBounds(int begin, int end) {
        List<Bounds> result = new ArrayList<>();
        for (IModel child : getModelChildren()) {
            result.addAll(child.getTextBounds(begin, end));
        }
        return result;
    }

    @Override
    public List<IModel> getTextChildren(int begin, int end) {
        List<IModel> result = new ArrayList<>();

        if (begin <= getBegin() && end >= getEnd()) {
            result.add(this);
        }

        result.addAll(getModelChildren().stream().flatMap(child -> child.getTextChildren(begin, end).stream()).collect(Collectors.toList()));

        return result;
    }
}

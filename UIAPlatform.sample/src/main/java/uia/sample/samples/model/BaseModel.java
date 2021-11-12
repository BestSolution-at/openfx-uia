package uia.sample.samples.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uia.sample.samples.model.TextSupport.Glyph;

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


    @Override
    public int computeGlyphs(int beginIndex) {
        int length = 0;
        begin = beginIndex;
        for (IModel child : modelChildren) {
            length += child.computeGlyphs(begin + length);
        }
        end = begin + length;
        return length;
    }

    @Override
    public Stream<Glyph> streamGlyphs() {
        return modelChildren.stream().flatMap(child -> child.streamGlyphs());
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

        renderBounds(gc);
    }
    
    protected void renderBounds(GraphicsContext gc) {
        Bounds local = getLayoutBounds();
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);
        gc.strokeRect(local.getMinX(), local.getMinY(), local.getWidth(), local.getHeight());
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
    public List<IModel> pick(double x, double y) {
        List<IModel> result = new ArrayList<>();

        if (getLayoutBounds().contains(x, y)) {
            result.add(this);
            for (IModel child : getModelChildren()) {
                result.addAll(child.pick(x, y));
            }
        }

        return result;
    }

    @Override
    public int pickText(double x, double y) {
        List<IModel> pickResult = pick(x, y);
        List<IModel> texts = pickResult.stream().filter(m -> m instanceof Text || m instanceof Cell)
        .collect(Collectors.toList());

        Optional<IModel> last = texts.isEmpty() ? Optional.empty() : Optional.of(texts.get(texts.size() - 1));

        // need x and y local to text
        return last.map(t -> t.pickText(x, y)).orElse(-1);
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

    @Override
    public IModel getTextEnclosing(int begin, int end) {
        if (begin >= getBegin() && end <= getEnd()) {
            // within
            for (IModel child : getModelChildren()) {
                IModel childResult = child.getTextEnclosing(begin, end);
                if (childResult != null) {
                    return childResult;
                }
            }
            return this;
        }
        return null;
    }

    public static List<Bounds> merge(List<Bounds> input) {
        // simple bounds merging
        
        List<Bounds> result = new ArrayList<>();
        Bounds cur = null;

        for (Bounds b : input) {
            if (cur == null) {
                cur = b;

            } else {
                if (Math.abs(cur.getMinY() - b.getMinY()) < 0.0001
                && Math.abs(cur.getMaxY() - b.getMaxY()) < 0.0001) {
                    // same line
                    if (cur.intersects(b)) {
                        double minX = Math.min(cur.getMinX(), b.getMinX());
                        double maxX = Math.max(cur.getMaxX(), b.getMaxX());

                        cur = new BoundingBox(minX, cur.getMinY(), maxX - minX, cur.getHeight());
                        continue;
                    }
                }

                result.add(cur);
                cur = b;
            }


        }

        if (cur != null) {
            result.add(cur);
        }

        //System.err.println("=> " + input.stream().map(Object::toString).collect(Collectors.joining("\n")));
        //System.err.println("<= " + result);
        return result;

    }
}

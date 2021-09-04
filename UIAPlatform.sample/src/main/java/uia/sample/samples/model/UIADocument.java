package uia.sample.samples.model;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.uia.ControlType;
import javafx.uia.IInitContext;
import javafx.uia.ITextProvider;
import javafx.uia.ITextRangeProvider;
import javafx.uia.IUIAElement;
import javafx.uia.SupportedTextSelection;

public class UIADocument extends UIACanvas implements ITextProvider {

    @Override
    public void initialize(IInitContext init) {
        
    }

    @Override
    public ControlType getControlType() {
        return ControlType.UIA_DocumentControlTypeId;
    }

    @Override
    public void layout() {
        
        double nextY = 0;
        for (IModel child : getModelChildren()) {
            ((BaseModel)child).layoutY = nextY;
            
            child.layout();

            nextY += child.getLocalBounds().getHeight() + 20;
        }
        
        
        for (IModel child : getModelChildren()) {
            Bounds childBounds = child.getLayoutBounds();

            layoutW = Math.max(childBounds.getMaxX(), layoutW);
            layoutH = Math.max(childBounds.getMaxY(), layoutH);
        }

    }

    public void addPage(UIAPage page) {
        addChild(page);
    }

    @Override
    public ITextRangeProvider get_DocumentRange() {
        return new UIATextRange(this, getBegin(), getEnd());
    }

    @Override
    public SupportedTextSelection get_SupportedTextSelection() {
        return SupportedTextSelection.None;
    }

    @Override
    public ITextRangeProvider[] GetSelection() {
        return new ITextRangeProvider[0];
    }

    @Override
    public ITextRangeProvider[] GetVisibleRanges() {
        return new ITextRangeProvider[] { get_DocumentRange() };
    }

    @Override
    public ITextRangeProvider RangeFromChild(IUIAElement childElement) {
        if (childElement instanceof IModel) {
            IModel child = (IModel) childElement;
            return new UIATextRange(this, child.getBegin(), child.getEnd());
        }
        return null;
    }

    @Override
    public ITextRangeProvider RangeFromPoint(Point2D point) {
        return null;
    }
    
}

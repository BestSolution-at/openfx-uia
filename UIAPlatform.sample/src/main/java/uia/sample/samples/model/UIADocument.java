package uia.sample.samples.model;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.uia.ControlType;
import javafx.uia.IEvent;
import javafx.uia.IInitContext;
import javafx.uia.ITextProvider;
import javafx.uia.ITextProvider2;
import javafx.uia.ITextRangeProvider;
import javafx.uia.IUIAElement;
import javafx.uia.StandardEventIds;
import javafx.uia.StandardPropertyIds;
import javafx.uia.StandardVariantConverters;
import javafx.uia.SupportedTextSelection;

import uia.sample.samples.DocumentModelSample.EditorPane;

public class UIADocument extends UIACanvas implements ITextProvider, ITextProvider2 {

    private IEvent selChangeEvent;

    private EditorPane control;

    @Override
    public void initialize(IInitContext init) {
        init.addNameProperty(() -> "Dokument");
//        focusChangeEvent = init.addEvent(StandardEventIds.UIA_AutomationFocusChangedEventId);
        selChangeEvent = init.addEvent(StandardEventIds.UIA_Text_TextSelectionChangedEventId);

        init.addHasKeyboardFocusProperty(() -> getControl().isFocused());
        init.addIsKeyboardFocusableProperty(() -> true);

        init.addProperty(StandardPropertyIds.UIA_IsEnabledPropertyId, () -> true, StandardVariantConverters.BOOL);
    }


    @Override
    public ControlType getControlType() {
        return ControlType.UIA_DocumentControlTypeId;
    }

    @Override
    public void layout() {

        double nextY = 0;
        for (IModel child : getModelChildren()) {
            ((BaseModel) child).layoutY = nextY;

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
        return SupportedTextSelection.Single;
    }

    @Override
    public ITextRangeProvider[] GetSelection() {
        int a = getControl().selectionBeginProperty().get();
        int b = getControl().selectionEndProperty().get();
        if (a != -1) {
            int lower = Math.min(a, b);
            int upper = Math.max(a, b);
            return new ITextRangeProvider[]{ new UIATextRange(this, lower, upper) };
        }
        return new ITextRangeProvider[] {};
        //final int caretPosition = getControl().getCaretPosition();
        //return new ITextRangeProvider[]{new UIATextRange(this, caretPosition, caretPosition)};
    }

    @Override
    public ITextRangeProvider[] GetVisibleRanges() {
        return new ITextRangeProvider[]{ get_DocumentRange() };
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
        Point2D coords = getControl().screenToLocal(point);
        int picked = pickText(coords.getX(), coords.getY());
        if (picked != -1) {
            return new UIATextRange(this, picked, picked);
        }
        return null;
    }

    public EditorPane getControl() {
        return control;
    }

    public void setControl(EditorPane editorPane) {
        this.control = editorPane;

        this.control.caretOffsetProperty().addListener((obs, ol, ne) -> {
            if (selChangeEvent != null) {
                selChangeEvent.fire();
            }
        });
    }

    public void fireSelection() {
        if (selChangeEvent != null) {
            selChangeEvent.fire();
        }
    }

    @Override
    public CaretRangeResult GetCaretRange() {
        final int caretPosition = getControl().caretOffsetProperty().get();
        System.err.println("GetCaretRange() called => " + caretPosition);
        return new CaretRangeResult(new UIATextRange(this, caretPosition, caretPosition), true);
    }

    @Override
    public ITextRangeProvider RangeFromAnnotation(IUIAElement annotationElement) {
      
        return null;
    }
}

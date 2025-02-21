package uia.sample.samples.model;

import at.bestsolution.uia.javafx.uia.ControlType;
import at.bestsolution.uia.javafx.uia.IInitContext;
import at.bestsolution.uia.javafx.uia.IProperty;
import at.bestsolution.uia.javafx.uia.ITextProvider2;
import at.bestsolution.uia.javafx.uia.ITextRangeProvider;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import at.bestsolution.uia.javafx.uia.StandardPropertyIds;
import at.bestsolution.uia.javafx.uia.StandardVariantConverters;
import at.bestsolution.uia.javafx.uia.SupportedTextSelection;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import uia.sample.samples.DocumentModelSample.EditorPane;

public class UIADocument extends UIACanvas implements /*ITextProvider,*/ ITextProvider2 {

    private EditorPane control;

    private IProperty<Boolean> HasKeyboardFocus;

    @Override
    public void initialize(IInitContext init) {
        init.addNameProperty(() -> "Dokument");

        HasKeyboardFocus = init.addHasKeyboardFocusProperty(() -> getControl().isFocused());

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
        return rangeOf(getBegin(), getEnd());
    }

    @Override
    public SupportedTextSelection get_SupportedTextSelection() {
        return SupportedTextSelection.Single;
    }




    @Override
    public ITextRangeProvider[] GetSelection() {
        if (!isSelectionEmpty()) {
            return new ITextRangeProvider[] { rangeOf(lowerSelectionPos(), upperSelectionPos()) };
        } else {
            return new ITextRangeProvider[] { degenerateRangeOf(caretPos()) };
        }
    }

    @Override
    public ITextRangeProvider[] GetVisibleRanges() {
        return new ITextRangeProvider[]{ get_DocumentRange() };
    }

    @Override
    public ITextRangeProvider RangeFromChild(IUIAElement childElement) {
        if (childElement instanceof IModel) {
            IModel child = (IModel) childElement;
            return rangeOf(child.getBegin(), child.getEnd());
        }
        return null;
    }

    @Override
    public ITextRangeProvider RangeFromPoint(Point2D point) {
        Point2D coords = getControl().screenToLocal(point);
        int picked = pickText(coords.getX(), coords.getY());
        if (picked != -1) {
            return degenerateRangeOf(picked);
        }
        return null;
    }

    public EditorPane getControl() {
        return control;
    }

    public void setControl(EditorPane editorPane) {
        this.control = editorPane;

        this.control.caretOffsetProperty().addListener((obs, ol, ne) -> {
            withContext(TextProviderContext.class, ctx -> ctx.TextSelectionChangedEvent.fire());
        });

        this.control.selectionBeginProperty().addListener((obs, ol, ne) -> {
            withContext(TextProviderContext.class, ctx -> ctx.TextSelectionChangedEvent.fire());
        });
        this.control.selectionEndProperty().addListener((obs, ol, ne) -> {
            withContext(TextProviderContext.class, ctx -> ctx.TextSelectionChangedEvent.fire());
        });

        this.control.focusedProperty().addListener((obs, ol, ne) -> {
            if (HasKeyboardFocus != null) HasKeyboardFocus.fireChanged(ol, ne);
        });
    }

    @Override
    public CaretRangeResult GetCaretRange() {
        final int caretPosition = getControl().caretOffsetProperty().get();
        return new CaretRangeResult(degenerateRangeOf(caretPosition), true);
    }

    @Override
    public ITextRangeProvider RangeFromAnnotation(IUIAElement annotationElement) {

        return null;
    }


    ITextRangeProvider rangeOf(int begin, int end) {
        return new UIATextRange(this, begin, end);
    }
    ITextRangeProvider degenerateRangeOf(int pos) {
        return new UIATextRange(this, pos, pos);
    }

    boolean isSelectionEmpty() {
        final int selBegin = getControl().selectionBeginProperty().get();
        final int selEnd = getControl().selectionEndProperty().get();
        return selBegin == -1 || selEnd == -1 || selBegin == selEnd;
    }
    int lowerSelectionPos() {
        final int selBegin = getControl().selectionBeginProperty().get();
        final int selEnd = getControl().selectionEndProperty().get();
        return Math.min(selBegin, selEnd);
    }
    int upperSelectionPos() {
        final int selBegin = getControl().selectionBeginProperty().get();
        final int selEnd = getControl().selectionEndProperty().get();
        return Math.max(selBegin, selEnd);
    }
    int caretPos() {
        return getControl().caretOffsetProperty().get();
    }
}

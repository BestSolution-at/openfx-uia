package uia.sample.samples.model;

import java.util.List;
import java.util.stream.Collectors;

import at.bestsolution.uia.ControlType;
import at.bestsolution.uia.ITextChildProvider;
import at.bestsolution.uia.ITextRangeProvider;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.IUIAVirtualElement;
import javafx.geometry.Bounds;

public class UIAPage extends Text implements IUIAVirtualElement, ITextChildProvider {

    @Override
    public ControlType getControlType() {
        return ControlType.UIA_CustomControlTypeId;
    }

    @Override
    public IUIAElement getParent() {
        return (IUIAElement) getModelParent();
    }

    @Override
    public List<IUIAElement> getChildren() {
        return getModelChildren().stream().map(child -> (IUIAElement) child).collect(Collectors.toList());
    }

    @Override
    public Bounds getBounds() {
        return UIACanvas.getCanvas(this).localToScreen(getLayoutBounds());
    }

    @Override
    public void SetFocus() {
    }

    private UIADocument getDocument() {
        return parents().filter(p -> p instanceof UIADocument)
        .map(p -> (UIADocument) p)
        .findFirst()
        .orElse(null);
    }

    @Override
    public IUIAElement get_TextContainer() {
        return getDocument();
    }

    @Override
    public ITextRangeProvider get_TextRange() {
        return new UIATextRange(getDocument(), getBegin(), getEnd());
    }
}

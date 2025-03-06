package uia.sample.samples.vlist;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import at.bestsolution.uia.ControlType;
import at.bestsolution.uia.IEvent;
import at.bestsolution.uia.IInitContext;
import at.bestsolution.uia.IProperty;
import at.bestsolution.uia.IScrollItemProvider;
import at.bestsolution.uia.IScrollProvider;
import at.bestsolution.uia.ISelectionItemProvider;
import at.bestsolution.uia.ISelectionProvider;
import at.bestsolution.uia.IStructureChangedEvent;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.IUIAVirtualElement;
import at.bestsolution.uia.IUIAVirtualRootElement;
import at.bestsolution.uia.ScrollAmount;
import at.bestsolution.uia.StandardEventIds;
import at.bestsolution.uia.StructureChangeType;
import at.bestsolution.uia.UIA;
import at.bestsolution.uia.internal.HResultException;
import at.bestsolution.uia.internal.Logger;
import at.bestsolution.uia.internal.LoggerFactory;
import javafx.animation.AnimationTimer;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class VList<T> extends BorderPane {

    private static Logger LOG = LoggerFactory.create("sample.VList");

    class UIAItem implements IUIAVirtualElement, ISelectionItemProvider, IScrollItemProvider/*, IVirtualizedItemProvider*/ {
        T model;

        IProperty<String> name;
        IProperty<Boolean> IsEnabled;
        IProperty<Boolean> IsOffscreen;

        IProperty<Boolean> IsKeyboardFocusable;
        IProperty<Boolean> HasKeyboardFocus;

        IEvent SelectionItem_ElementSelected;
        IEvent AutomationFocusChanged;

        IProperty<Boolean> IsControlElement;
        IProperty<Boolean> IsContentElement;

        IEvent SelectionItem_ElementAddedToSelection;
        IEvent SelectionItem_ElementRemovedFromSelection;

        BooleanBinding selected;



        UIAItem(T model) {
            this.model = model;

            selected = Bindings.createBooleanBinding(() -> {
                return Objects.equals(VList.this.selected.get(), model);
            }, VList.this.selected);

        }

        @Override
        public String toString() {
            return "UIAItem("+model+")";
        }

        @Override
        public void initialize(IInitContext init) {

            IsEnabled = init.addIsEnabledElementProperty(this::isEnabled);
            IsOffscreen = init.addIsOffscreenProperty(this::isOffscreen);
            name = init.addNameProperty(() -> labelFunc.apply(model));

            AutomationFocusChanged = init.addEvent(StandardEventIds.UIA_AutomationFocusChangedEventId);

            SelectionItem_ElementSelected = init.addEvent(StandardEventIds.UIA_SelectionItem_ElementSelectedEventId);
            SelectionItem_ElementAddedToSelection = init.addEvent(StandardEventIds.UIA_SelectionItem_ElementAddedToSelectionEventId);
            SelectionItem_ElementRemovedFromSelection = init.addEvent(StandardEventIds.UIA_SelectionItem_ElementRemovedFromSelectionEventId);

            IsControlElement = init.addIsControlElementProperty(() -> true);
            IsContentElement = init.addIsContentElementProperty(() -> true);

            IsKeyboardFocusable = init.addIsKeyboardFocusableProperty(() -> true);

            ObservableValue<T> focused = VList.this.uia.virtualFocused;
            HasKeyboardFocus = init.addHasKeyboardFocusProperty(() -> Objects.equals(model, focused.getValue()));
            focused.addListener((x, o, n) -> {
                boolean oldValue = model.equals(o);
                boolean newValue = model.equals(n);
                if (oldValue != newValue) {
                    HasKeyboardFocus.fireChanged(oldValue, newValue);
                }

                if (newValue) {
                    AutomationFocusChanged.fire();
                }
            });


            selected.addListener((x, o, n) -> {
                if (o != n) {
                    withContext(SelectionItemProviderContext.class, ctx -> ctx.IsSelected.fireChanged(o, n));
                    if (n) {
                        SelectionItem_ElementSelected.fire();
                    }
                }
            });

        }

        boolean isEnabled() {
            return true;
        }

        boolean isOffscreen() {
            return VList.this.isOffscreen(model);
        }

        boolean isRealized() {
            return cells.containsKey(model);
        }

        // @Override
        // public void Realize() {
        //     if (!isRealized()) {
        //         getOrCreateCell(model);
        //     }
        // }

        @Override
        public ControlType getControlType() {
            return ControlType.UIA_ListItemControlTypeId;
        }

        @Override
        public void AddToSelection() {
        }
        @Override
        public void RemoveFromSelection() {
        }
        @Override
        public void Select() {
            LOG.debug(() -> "UIA__Select");
            VList.this.selected.set(model);
        }
        @Override
        public boolean get_IsSelected() {
            return selected.get();
        }
        @Override
        public IUIAElement get_SelectionContainer() {
            return VList.this.uia;
        }

        @Override
        public Bounds getBounds() {
            if (!isRealized()) {
                throw new HResultException(0x80040201);
            }
            Cell cell = getOrCreateCell(model);
            return cell.localToScreen(cell.getBoundsInLocal());
        }

        @Override
        public void SetFocus() {
            LOG.debug(() -> "UIA__SetFocus");
            VList.this.uia.virtualFocused.set(model);
        }

        @Override
        public void ScrollIntoView() {
            scrollIntoView(model);
        }

        @Override
        public IUIAElement getParent() {
            return uia;
        }

        @Override
        public List<IUIAElement> getChildren() {
            return Collections.emptyList();
        }
    }

    class UIAList implements IUIAVirtualRootElement, ISelectionProvider, IScrollProvider/*, IItemContainerProvider*/ {
        ObservableList<IUIAElement> children = FXCollections.observableArrayList();

        IEvent AutomationFocusChanged;
        IStructureChangedEvent StructureChanged;

        IEvent Selection_Invalidated;

        IProperty<Boolean> IsKeyboardFocusable;
        IProperty<Boolean> HasKeyboardFocus;

        IProperty<Boolean> IsControlElement;
        IProperty<Boolean> IsContentElement;
        IProperty<Boolean> IsEnabled;

        IProperty<String> Name;

        ObjectProperty<T> virtualFocused = new SimpleObjectProperty<>();

        IUIAElement[] toSel(T model) {
            if (model == null) {
                IUIAElement[] r = {};
                return r;
            }
            IUIAElement[] result = { VList.this.getOrCreateUIAItem(model) };
            return result;
        }



        UIAList() {
            VList.this.selected.addListener((x, o, n) -> {
                LOG.debug(() -> "VList.selected changed: " + o + " -> " + n);

                withContext(SelectionProviderContext.class, ctx -> {
                    ctx.Selection.fireChanged(toSel(o), toSel(n));
                });

                // always update virtualFocused when selection changes
                virtualFocused.set(n);
            });

            children.addListener((InvalidationListener)inv -> {
                if (StructureChanged != null) {
                    StructureChanged.fire(StructureChangeType.ChildrenInvalidated, null);
                }

                // ensure the virtual focus is set to the first item, otherwise JAWS says 'unavailable'
                if (items.size() > 0 && virtualFocused.get() == null) {
                    virtualFocused.set(items.get(0));
                }
            });

            virtualFocused.addListener((x, o, n) -> {
                LOG.debug(() -> "virtualFocused changed: " + o + " -> " + n);
            });



        }

        @Override
        public IUIAElement getFocus() {
            T vFocused = virtualFocused.get();
            System.err.println("!!!! getFocus() -> " + vFocused);
            if (vFocused != null) {
                UIAItem item = VList.this.getOrCreateUIAItem(vFocused);
                return item;
            }
            return this;
        }

        @Override
        public String toString() {
            return "UIAList";
        }

        @Override
        public void initialize(IInitContext init) {
            Name = init.addNameProperty(this::getName);
            Selection_Invalidated = init.addEvent(StandardEventIds.UIA_Selection_InvalidatedEventId);
            AutomationFocusChanged = init.addEvent(StandardEventIds.UIA_AutomationFocusChangedEventId);
            IsKeyboardFocusable = init.addIsKeyboardFocusableProperty(() -> true);
            HasKeyboardFocus = init.addHasKeyboardFocusProperty(() -> VList.this.isFocused());

            IsControlElement = init.addIsControlElementProperty(() -> true);
            IsContentElement = init.addIsContentElementProperty(() -> true);
            IsEnabled = init.addIsEnabledElementProperty(() -> !isDisabled());
            disabledProperty().addListener((x, o, n) -> {
                IsEnabled.fireChanged(o, n);
            });

            StructureChanged = init.addStructureChangedEvent();

            VList.this.focusedProperty().addListener((x, o, n) -> {
                HasKeyboardFocus.fireChanged(o, n);
            });
            focusedProperty().addListener((x, o, n) -> {
                LOG.debug(() -> "focus changed");
                if (n) {
                    T selectedModel = VList.this.selected.get();
                    Optional<UIAItem> item = children.stream().map(it -> (UIAItem) it).filter(it -> it.model == selectedModel).findFirst();
                    if (item.isPresent()) {
                        // Platform.runLater(() -> {
                            item.get().HasKeyboardFocus.fireChanged(false, true);
                            item.get().SelectionItem_ElementSelected.fire();
                            item.get().AutomationFocusChanged.fire();
                        // });
                    }

                    //Selection_Invalidated.fire();

                    //AutomationFocusChanged.fire();
                }
            });
        }

        public String getName() {
            return "VList";
        }

        // @Override
        // public IUIAElement FindItemByProperty(IUIAElement startAfter, IPropertyId propertyId, Variant value) {

        //     UIAItem itemStartAfter = (UIAItem) startAfter;

        //     System.err.println("FindItemByProperty("+itemStartAfter.model+", " + propertyId + ", " + value + ")");

        //     return null;
        // }

        @Override
        public ControlType getControlType() {
            return ControlType.UIA_ListControlTypeId;
        }

        @Override
        public Bounds getBounds() {
            return VList.this.localToScreen(VList.this.getBoundsInLocal());
        }

        @Override
        public void SetFocus() {
            VList.this.requestFocus();
        }

        @Override
        public boolean get_CanSelectMultiple() {
            return false;
        }

        @Override
        public boolean get_IsSelectionRequired() {
            return false;
        }

        @Override
        public IUIAElement[] GetSelection() {
            T selection = selected.get();

            if (selection != null) {
                IUIAElement[] sel = {getOrCreateUIAItem(selection)};
                return sel;
            } else {
                IUIAElement[] sel = {};
                return sel;
            }
        }

        @Override
        public boolean get_HorizontallyScrollable() {
            return false;
        }

        @Override
        public double get_HorizontalScrollPercent() {
            return 0;
        }

        @Override
        public double get_HorizontalViewSize() {
            return viewport.getWidth();
        }

        @Override
        public boolean get_VerticallyScrollable() {
            return true;
        }

        @Override
        public double get_VerticalScrollPercent() {
            return scrollPos.get() / (contentHeight.get() - viewportHeight.get());
        }

        @Override
        public double get_VerticalViewSize() {
            return viewportHeight.get();
        }

        @Override
        public void Scroll(ScrollAmount horizontalAmount, ScrollAmount verticalAmount) {
           //horizontalAmount.getNativeValue()

        }

        @Override
        public void SetScrollPercent(double horizontalPercent, double verticalPercent) {

        }

        @Override
        public List<IUIAElement> getChildren() {
            return children;
        }

        @Override
        public IUIAElement getChildFromPoint(Point2D point) {
            // TODO Auto-generated method stub
            //throw new UnsupportedOperationException("Unimplemented method 'getChildFromPoint'");

            Point2D listLocal = screenToLocal(point);

            Bounds boundsInLocal = getBoundsInLocal();

            Bounds layoutBounds = getLayoutBounds();

            Bounds boundsInParent = getBoundsInParent();

            LOG.debug(() -> "point = " + listLocal);
            LOG.debug(() -> "boundsInLocal = " + boundsInLocal);
            LOG.debug(() -> "layoutBounds = " + layoutBounds);
            LOG.debug(() -> "boundsInParent = " + boundsInParent);

            Bounds vpBounds = viewport.getBoundsInParent();
            if (vpBounds.contains(listLocal)) {
                // pick item
                int index = (int) Math.floor((scrollPos.get() + listLocal.getY()) / itemHeight.get());
                LOG.debug(() -> "" + index);
                return (children.size() > index) ? children.get(index) : this;
            } else {
                // pick self
                return this;
            }
        }

    }

    class Cell extends BorderPane {
        T model;

        String baseStyle = "-fx-padding: 10px; -fx-border-size: 0 1px 0 0; -fx-border-color: gray;";

        Cell(T model) {
            this.model = model;
            Label l = new Label(labelFunc.apply(model));
            setCenter(l);


            addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                selected.set(model);
            });

            selected.addListener(inv -> updateStyle());
            VList.this.focusedProperty().addListener(inv -> updateStyle());
            updateStyle();
        }

        void updateStyle() {
            String selectionColor = VList.this.isFocused() ? "orange" : "gray";


            if (model.equals(selected.get())) {
                setStyle(baseStyle + " -fx-background-color: " + selectionColor);
            } else {
                setStyle(baseStyle);
            }
        }

        int index() {
            return items.indexOf(model);
        }

        @Override
        public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
            if (UIA.isUIAQuery(attribute, parameters)) {
                return getOrCreateUIAItem(model);
            }
            return super.queryAccessibleAttribute(attribute, parameters);
        }
    }

    Function<T, String> labelFunc;

    ScrollBar scrollbar = new ScrollBar();

    boolean isOffscreen(T model) {
        double yPos = items.indexOf(model) * itemHeight.get() - scrollPos.get();
        return yPos < -itemHeight.get() || yPos > viewportHeight.get();
    }

    Pane viewport = new Pane() {
        @Override
        protected void layoutChildren() {

            getChildrenUnmodifiable().forEach(c -> c.setVisible(false));

            for (int index = beginIndex.get(); index <= endIndex.get(); index++) {
                Cell cell = getOrCreateCell(items.get(index));
                if (!getChildren().contains(cell)) {
                    getChildren().add(cell);
                }
                double yPos = index * itemHeight.get() - scrollPos.get();
                cell.resizeRelocate(0d, yPos, getWidth(), itemHeight.get());
                cell.setVisible(true);
            }

/*
            System.err.println("layoutChildren " + getChildrenUnmodifiable().size());
            for (Node child : getChildrenUnmodifiable()) {
                if (child instanceof VList.Cell) {

                    Cell cell = (Cell) child;
                    int index = cell.index();
                    if (index >= beginIndex.get() && index <= endIndex.get()) {
                        double yPos = index * itemHeight.get() - scrollPos.get();
                        child.setVisible(true);
                        child.resizeRelocate(0d, yPos, getWidth(), itemHeight.get());
                    } else {
                        child.setVisible(false);
                    }
                }
            } */
        }

    };
    Label data = new Label();

    DoubleProperty itemHeight = new SimpleDoubleProperty(40);

    DoubleProperty scrollPos = new SimpleDoubleProperty(0);

    ObjectProperty<T> selected = new SimpleObjectProperty<>();

    DoubleBinding contentHeight;
    DoubleBinding viewportHeight;

    IntegerBinding beginIndex;
    IntegerBinding endIndex;

    Map<T, Cell> cells = new HashMap<>();
    Map<T, UIAItem> uiaItems = new HashMap<>();

    UIAList uia;

    AnimationTimer timer;

    long lastTick = 0;

    public VList(Function<T, String> labelFunc) {
        this.labelFunc = labelFunc;

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTick != 0) {
                    double duration = (now - lastTick) / 1000000;
                    if (duration > 20) {
                        LOG.info(() -> "SLOW FRAME: duration: " + duration);
                    }
                }
                lastTick = now;
            }

        };
        //timer.start();

        viewport.setMinHeight(200);
        viewport.setMaxHeight(Double.MAX_VALUE);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);

        setFocusTraversable(true);

        scrollbar.setOrientation(Orientation.VERTICAL);

        viewport.setStyle("-fx-border-width: 1px; -fx-border-color: gray");
        setCenter(viewport);
        setRight(scrollbar);
        setBottom(data);

        data.setStyle("-fx-color: gray");

        contentHeight = itemHeight.multiply(Bindings.size(items));
        viewportHeight = Bindings.add(0d, viewport.heightProperty());

        scrollbar.minProperty().set(0);
        scrollbar.maxProperty().bind(contentHeight.subtract(viewportHeight));

        scrollbar.valueProperty().bindBidirectional(scrollPos);

        viewport.addEventHandler(ScrollEvent.SCROLL, e -> {
            double step = 14;
            double newPos = scrollPos.get() - Math.signum(e.getDeltaY()) * step;
            newPos = Math.min(contentHeight.get() - viewportHeight.get(), newPos);
            newPos = Math.max(0, newPos);
            scrollPos.set(newPos);
        });

        beginIndex = Bindings.createIntegerBinding(() ->
            (int) Math.max(0, Math.floor(scrollPos.get() / itemHeight.get())), scrollPos, itemHeight);
        endIndex = Bindings.createIntegerBinding(() ->
            (int) Math.min(items.size()-1, Math.floor((scrollPos.get() + viewportHeight.get()) / itemHeight.get())), items, scrollPos, viewportHeight, itemHeight);

        data.textProperty().bind(Bindings.createStringBinding(() -> {
            String d = "";
            d += "scrollPos: " + scrollPos.doubleValue() + "\n";
            d += "selected: " + selected.get() + "\n";
            d += "itemHeight: " + itemHeight.get();
            return d;
        }, scrollPos, selected, itemHeight));

        beginIndex.addListener((inv) -> viewport.requestLayout());
        endIndex.addListener((inv) -> viewport.requestLayout());

        // items.addListener((InvalidationListener)(inv) -> {
        //     viewport.getChildren().clear();
        //     cells.clear();
        //     items.forEach(it -> {
        //         Cell cell = getOrCreateCell(it);
        //         viewport.getChildren().add(cell);
        //     });
        // });

        setStyle("-fx-border-width: 5px; -fx-border-color: gray");

        focusedProperty().addListener((x, o, n) -> {
            String col = n ? "orange" : "gray";
            setStyle("-fx-border-width: 5px; -fx-border-color: " + col);
        });

        addEventHandler(MouseEvent.MOUSE_PRESSED, e -> requestFocus());

        addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            switch (e.getCode()) {
                case DOWN:{
                    if (e.isShortcutDown()) {
                        T cur = VList.this.uia.virtualFocused.get();
                        if (cur == null) {
                            if (items.size() > 0) VList.this.uia.virtualFocused.set(items.get(0));
                        } else {
                            T next = items.get(Math.min(items.size()-1, items.indexOf(cur) + 1));
                            VList.this.uia.virtualFocused.set(next);
                            scrollIntoView(next);
                        }
                    } else {
                        T cur = selected.get();
                        if (cur == null) {
                            if (items.size() > 0) selected.set(items.get(0));
                        } else {
                            T next = items.get(Math.min(items.size()-1, items.indexOf(cur) + 1));
                            selected.set(next);
                            scrollIntoView(next);
                        }
                    }

                break;}

                case UP:{
                    if (e.isShortcutDown()) {
                        T cur = VList.this.uia.virtualFocused.get();
                        if (cur == null) {
                            if (items.size() > 0) VList.this.uia.virtualFocused.set(items.get(0));
                        } else {
                            T next = items.get(Math.max(0, items.indexOf(cur) - 1));
                            VList.this.uia.virtualFocused.set(next);
                            scrollIntoView(next);
                        }
                    } else {
                        T cur = selected.get();
                        if (cur == null) {
                            if (items.size() > 0) selected.set(items.get(0));
                        } else {
                            T next = items.get(Math.max(0, items.indexOf(cur) - 1));
                            selected.set(next);
                            scrollIntoView(next);
                        }
                    }
                break;}
                default:
            }
        });


        items.addListener((InvalidationListener) inv -> {
            items.forEach(it -> getOrCreateUIAItem(it));
        });

        this.uia = new UIAList();
    }

    @Override
    public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
        if (UIA.isUIAQuery(attribute, parameters)) {
            return uia;
        }
        return super.queryAccessibleAttribute(attribute, parameters);
    }

    void scrollIntoView(T model) {
        int index = items.indexOf(model);
        if (index < beginIndex.get()+1) {
            // scroll up
            scrollPos.set(index * itemHeight.get());

        } else if (index > endIndex.get()-1) {
            // scroll down
            scrollPos.set((index+1) * itemHeight.get()-viewportHeight.get());
        }
    }


    Cell getOrCreateCell(T model) {
        return cells.computeIfAbsent(model, m -> createCell(m));
    }

    Cell createCell(T model) {
       return new Cell(model);
    }

    UIAItem getOrCreateUIAItem(T model) {
        UIAItem item = uiaItems.computeIfAbsent(model, m -> {
            //System.err.println("*** VList.LOG = " + LOG);
            //LOG.trace(() -> "create UIAItem for " + m);
            UIAItem it = new UIAItem(m);
            uia.children.add(it);
            return it;
        });

        return item;
    }


    ObservableList<T> items = FXCollections.observableArrayList();

    public ObservableList<T> getItems() {
        return items;
    }

}

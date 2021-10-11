/*
 * -----------------------------------------------------------------
 * Copyright (c) 2021 BestSolution.at EDV Systemhaus GmbH
 * All Rights Reserved.
 *
 * BestSolution.at MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE  OR NON - INFRINGEMENT.
 * BestSolution.at SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * This software is released under the terms of the
 *
 *                  "GNU General Public License, Version 2 
 *                         with classpath exception"
 *
 * and may only be distributed and used under the terms of the
 * mentioned license. You should have received a copy of the license
 * along with this software product, if not you can download it from
 * http://www.gnu.org/licenses/gpl.html
 * ----------------------------------------------------------------
 */
package uia.sample.samples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.uia.ISelectionItemProvider;
import javafx.uia.ISelectionProvider;
import javafx.uia.IUIAElement;
import javafx.uia.IUIAVirtualElement;
import javafx.uia.IUIAVirtualRootElement;
import javafx.uia.UIA;
import uia.sample.Sample;

public class SimpleISelectionProvider implements Sample {

    private BooleanProperty canSelectMultiple = new SimpleBooleanProperty(false);
    private BooleanProperty isSelectionRequired = new SimpleBooleanProperty(false);

    private ObservableList<IUIAElement> selection = FXCollections.observableArrayList();

    List<IUIAElement> items = new ArrayList<>();

    class UIAItem implements IUIAVirtualElement, ISelectionItemProvider {

        UIAElement parent;
        Node node;
        @Override
        public Bounds getBounds() {
            return node.localToScreen(node.getBoundsInLocal());
        }

        @Override
        public IUIAElement getParent() {
            return parent;
        }

        @Override
        public List<IUIAElement> getChildren() {
            return Collections.emptyList();
        }

        @Override
        public void AddToSelection() {
            selection.add(this);
        }

        @Override
        public void RemoveFromSelection() {
            selection.remove(this);
        }

        @Override
        public void Select() {
            selection.setAll(this);
        }

        @Override
        public boolean get_IsSelected() {
            return selection.contains(this);
        }

        @Override
        public IUIAElement get_SelectionContainer() {
            return parent;
        }

    }

    List<IUIAElement> old = new ArrayList<>();

    class UIAElement implements IUIAVirtualRootElement, ISelectionProvider {
        
        Pane node;
        
        public UIAElement() {
            canSelectMultiple.addListener((obs, oldValue, newValue) -> {
                withContext(SelectionProviderContext.class, ctx -> {
                    ctx.CanSelectMultiple.fireChanged(oldValue, newValue);
                });
            });
            isSelectionRequired.addListener((obs, oldValue, newValue) -> {
                withContext(SelectionProviderContext.class, ctx -> {
                    ctx.IsSelectionRequried.fireChanged(oldValue, newValue);
                });
            });
            selection.addListener((InvalidationListener) inv -> {
                withContext(SelectionProviderContext.class, ctx -> {
                    ctx.Selection.fireChanged(old.toArray(new IUIAElement[0]), selection.toArray(new IUIAElement[0]));
                    old = new ArrayList<>(selection);
                });
            });
        }
        @Override
        public Bounds getBounds() {
            return node.localToScreen(node.getBoundsInLocal());
        }
        @Override
        public void SetFocus() {
            node.requestFocus();
        }
        
        @Override
        public List<IUIAElement> getChildren() {
            return items;
        }
        @Override
        public IUIAElement getChildFromPoint(Point2D point) {
            return null;
        }
        @Override
        public boolean get_CanSelectMultiple() {
            return canSelectMultiple.get();
        }
        @Override
        public boolean get_IsSelectionRequired() {
            return isSelectionRequired.get();
        }
        @Override
        public IUIAElement[] GetSelection() {
            return selection.toArray(new IUIAElement[0]);
        }
    }

    Label desc;

    Pane host;

    VBox ctrl;

    private Node createItem(UIAElement parent, String label) {

        UIAItem uia = new UIAItem();
        HBox item = new HBox() {
            {
                uia.node = this;
                uia.parent = parent;
                items.add(uia);
            }
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return uia;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };

        Button select = new Button("toggle select");
        select.setOnAction(ev -> {

            if (canSelectMultiple.get()) {
                if (!selection.contains(uia)) {
                    selection.add(uia);
                } else {
                    if (isSelectionRequired.get() && selection.size() == 1) {
                        // cannot remove
                    } else {
                        selection.remove(uia);
                    }
                }
            } else {
                if (selection.contains(uia)) {
                    if (isSelectionRequired.get() && selection.size() == 1) {
                        // cannot remove
                    } else {
                        selection.remove(uia);
                    }
                } else {
                    selection.setAll(uia);
                }
            }

        });

        Label x = new Label();
        x.textProperty().bind(Bindings.createStringBinding(() -> selection.contains(uia) ? "[ X ]" : "[   ]", selection));
        item.getChildren().addAll(x, new Label(label), select);

        return item;
    }

    public SimpleISelectionProvider() {
        desc = new Label("Basic usage of ISelectionProvider & ISelectionItemProvider.");
        desc.setWrapText(true);
        
        host = new Pane();
        
        UIAElement uiaEl = new UIAElement();
        VBox pane0 = new VBox() {
            {
                uiaEl.node = this;
            }
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return uiaEl;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };
        pane0.setStyle("-fx-border-width: 1px; -fx-border-color: red;");

        pane0.getChildren().add(createItem(uiaEl, "One"));
        pane0.getChildren().add(createItem(uiaEl, "Two"));
        pane0.getChildren().add(createItem(uiaEl, "Three"));
        pane0.getChildren().add(createItem(uiaEl, "Four"));

        host.getChildren().add(pane0);

        isSelectionRequired.addListener(inv -> {
            if (isSelectionRequired.get()) {
                if (selection.isEmpty()) {
                    selection.add(items.get(0));
                }
            }
        });

        canSelectMultiple.addListener(inv -> {
            if (!canSelectMultiple.get()) {
                if (selection.size() > 1) {
                    selection.setAll(selection.get(0));
                }
            }
        });



        ctrl = new VBox();

        CheckBox multi = new CheckBox("CanSelectMultiple");
        multi.selectedProperty().bindBidirectional(canSelectMultiple);
        ctrl.getChildren().add(multi);
        CheckBox req = new CheckBox("IsSelectionRequired");
        req.selectedProperty().bindBidirectional(isSelectionRequired);
        ctrl.getChildren().add(req);

    }

  

    @Override
    public String getName() {
        return "ISelectionProvider & ISelectionItemProvider";
    }

    @Override
    public Node getDescription() {
        return desc;
    }

    @Override
    public Node getSample() {
        return host;
    }

    @Override
    public Node getControls() {
        return ctrl;
    }
    


}

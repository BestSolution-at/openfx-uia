package uia.sample.samples;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import at.bestsolution.uia.javafx.uia.UIA;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import uia.sample.Sample;
import uia.sample.samples.model.BaseModel;
import uia.sample.samples.model.TextSupport.Glyph;
import uia.sample.samples.model.UIADocument;
import uia.sample.samples.model.UIAPage;
import uia.sample.samples.model.UIATable;
import uia.sample.samples.model.UIATableCell;
import uia.sample.samples.model.UIATableHeaderCell;

public class DocumentModelSample implements Sample {

    static class EditorState {
        int caretOffset;
        int selectionBegin;
        int selectionEnd;

        EditorState(int caretOffset, int selectionBegin, int selectionEnd) {
            this.caretOffset = caretOffset;
            this.selectionBegin = selectionBegin;
            this.selectionEnd = selectionEnd;
        }
    }
    static interface InputHandler {
        EditorState handle(EditorState state);
    }

    private UIATable createTable() {
        UIATable table = new UIATable();
        table.colCount = 3;
        table.rowCount = 2;
        table.colWidth = 100;
        table.rowHeight = 40;

        {
            UIATableHeaderCell cell = new UIATableHeaderCell();
            cell.setContent("Banane ");
            cell.isColumnHeader = true;
            cell.grid = table;
            cell.row = 0;
            cell.col = 0;
            cell.rowSpan = 1;
            cell.colSpan = 1;
            table.addCell(cell);
        }
        {
            UIATableHeaderCell cell = new UIATableHeaderCell();
            cell.setContent("Tomate ");
            cell.isColumnHeader = true;
            cell.grid = table;
            cell.row = 0;
            cell.col = 1;
            cell.rowSpan = 1;
            cell.colSpan = 1;
            table.addCell(cell);
        }
        {
            UIATableHeaderCell cell = new UIATableHeaderCell();
            cell.setContent("Gurke ");
            cell.isColumnHeader = true;
            cell.grid = table;
            cell.row = 0;
            cell.col = 2;
            cell.rowSpan = 1;
            cell.colSpan = 1;
            table.addCell(cell);
        }
        {
            UIATableCell cell = new UIATableCell();
            cell.setContent("gelb ");
            cell.grid = table;
            cell.row = 1;
            cell.col = 0;
            cell.rowSpan = 1;
            cell.colSpan = 1;
            table.addCell(cell);
        }
        {
            UIATableCell cell = new UIATableCell();
            cell.setContent("rot ");
            cell.grid = table;
            cell.row = 1;
            cell.col = 1;
            cell.rowSpan = 1;
            cell.colSpan = 1;
            table.addCell(cell);
        }
        {
            UIATableCell cell = new UIATableCell();
            cell.setContent("grün ");
            cell.grid = table;
            cell.row = 1;
            cell.col = 2;
            cell.rowSpan = 1;
            cell.colSpan = 1;
            table.addCell(cell);
        }

        return table;
    }

    Node content;

    public DocumentModelSample() {

        UIADocument doc = new UIADocument();
        doc.layoutY = 30;
        doc.layoutX = 30;

        UIAPage page1 = new UIAPage();
        page1.addText("Hello World\n", Font.font(22), Color.BLACK);
        page1.addText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", Font.font(14), Color.ROYALBLUE);
        page1.addText("Tabelle?\n", Font.font(16), Color.BLACK);
        page1.addEmbed(createTable());
        page1.addText("\n", Font.font(16), Color.BLACK);
        page1.addText("End\n", Font.font(16), Color.BLACK);

        page1.layoutW = 300;

        doc.addPage(page1);

        UIAPage page2 = new UIAPage();
        page2.addText("A second page\n", Font.font(22), Color.RED);
        page2.addText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", Font.font(14), Color.ROYALBLUE);

        page2.layoutW = 300;

        doc.addPage(page2);


        doc.layout();
        doc.computeIndices(0);
        doc.computeGlyphs(0);

        BorderPane root = new BorderPane();
        EditorPane editorPane = new EditorPane(doc);
        doc.setControl(editorPane);

        root.setCenter(editorPane);

        doc.canvas = editorPane.canvas;

        // text.render(canvas.getGraphicsContext2D());


        // TextArea test = new TextArea();
        // test.setEditable(false);
        // test.setText(doc.getContent()
        // //.replaceAll("\n", "\u23CE\n")
        // //.replaceAll("\u0007", "|")
        // );


        // Pane ov = new Pane();
        // ov.setStyle("-fx-background-color: transparent;");
        // // ov.setStyle("-fx-background-color: rgba(0, 255, 0, 0.4);");
        // //ov.setBackground(Color.rgb(0, 255, 0, 0.4));
        // ov.setManaged(false);
        // ov.resize(400, 500);
        // root.getChildren().add(ov);

        // test.selectionProperty().addListener((obs, ol, ne) -> {
        //     System.err.println(ne);
        //     System.err.println(doc.getContent().substring(ne.getStart(), ne.getEnd()));

        //     List<Bounds> b = doc.getTextBounds(ne.getStart(), ne.getEnd());
        //     System.err.println(b);
        //     ov.getChildren().clear();

        //     for (Bounds x : b) {
        //         Pane r = new Pane();
        //         r.setStyle("-fx-background-color: rgba(0, 255, 0, 0.4);");
        //         r.setManaged(false);
        //         r.resizeRelocate(x.getMinX(), x.getMinY(), x.getWidth(), x.getHeight());
        //         ov.getChildren().add(r);
        //     }
        // });

        // root.setBottom(test);

        content = root;
    }

    @Override
    public Node getSample() {
        return content;
    }

    @Override
    public Node getControls() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Node getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return "Document Model Sample";
    }

    public static class EditorPane extends AnchorPane {

        final Canvas canvas;
        private final UIADocument doc;
        private final IntegerProperty caretOffset = new SimpleIntegerProperty(-1);

        private final IntegerProperty selectionBegin = new SimpleIntegerProperty(-1);
        private final IntegerProperty selectionEnd = new SimpleIntegerProperty(-1);

        private Tooltip tt;
        int cur = -1;

        private boolean inMouseSelection = false;
        private int mouseSelectionBegin = -1;

        public EditorPane(UIADocument uiaDocument) {
            this.doc = uiaDocument;



            canvas = new Canvas() {

            };

            caretOffset.addListener(inv -> repaint());
            selectionBegin.addListener(inv -> repaint());
            selectionEnd.addListener(inv -> repaint());
            focusedProperty().addListener(inv -> repaint());

            canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
                requestFocus();

                Optional<Glyph> glyph = pickGlyph(evt);
                glyph.ifPresent(g -> {
                    caretOffset.set(g.index);
                    if (evt.isShiftDown() && selectionBegin.get() != -1) {
                        selectionEnd.set(g.index);
                    } else {
                        selectionBegin.set(-1);
                        selectionEnd.set(-1);
                    }

                    inMouseSelection = true;
                    mouseSelectionBegin = g.index;
                });

            });

            canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, evt -> {

                inMouseSelection = false;
                mouseSelectionBegin = -1;
            });

            canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
                if (inMouseSelection) {
                    Optional<Glyph> glyph = pickGlyph(evt);
                    glyph.ifPresent(g -> {
                        selectionBegin.set(mouseSelectionBegin);
                        selectionEnd.set(g.index);
                        caretOffset.set(g.index);
                    });
                }
            });

            canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, evt -> {

            });

            canvas.addEventHandler(MouseEvent.MOUSE_MOVED, evt -> {
                Optional<Glyph> glyph = pickGlyph(evt);
                if (glyph.isPresent()) {
                    setCursor(Cursor.TEXT);
                    Glyph g = glyph.get();
                    if (g.index == cur) {
                        return;
                    } else {
                        if (tt == null) {
                            tt = new Tooltip();
                        }
                        tt.setText("index: " + g.index);
                        cur = g.index;
                        Point2D coords = canvas.localToScreen(g.x + g.w + 10, g.y + g.h + 10);
                        tt.show(canvas, coords.getX(), coords.getY());
                        Platform.runLater(() -> tt.show(canvas, coords.getX(), coords.getY()));
                    }
                } else {
                    setCursor(null);
                    if (tt != null) {
                        tt.hide();
                        cur = -1;
                    }
                }
            });

            setFocusTraversable(true);

            final KeyCombination LEFT = KeyCombination.keyCombination("Left");;
            final KeyCombination RIGHT = KeyCombination.keyCombination("Right");
            final KeyCombination SHIFT_LEFT = KeyCombination.keyCombination("Shift+Left");
            final KeyCombination SHIFT_RIGHT = KeyCombination.keyCombination("Shift+Right");
            final KeyCombination CTRL_LEFT = KeyCombination.keyCombination("Ctrl+Left");
            final KeyCombination CTRL_RIGHT = KeyCombination.keyCombination("Ctrl+Right");

            InputHandler onCtrlLeft = state -> {
                System.err.println("Ctrl+Left");
                return state;
            };
            InputHandler onCtrlRight = state -> {
                System.err.println("Ctrl+Right");


                return state;
            };
            InputHandler onShiftLeft = state -> {
                System.err.println("Shift+Left");

                if (state.selectionBegin == -1) state.selectionBegin = state.caretOffset;
                if (state.caretOffset != 0) state.caretOffset -= 1;
                state.selectionEnd = state.caretOffset;

                return state;
            };
            InputHandler onShiftRight = state -> {
                System.err.println("Shift+Right");

                if (state.selectionBegin == -1) state.selectionBegin = state.caretOffset;
                state.caretOffset += 1;
                state.selectionEnd = state.caretOffset;

                return state;
            };
            InputHandler onLeft = state -> {
                System.err.println("Left");

                if (state.caretOffset != 0) state.caretOffset -= 1;
                state.selectionBegin = -1;
                state.selectionEnd = -1;

                return state;
            };
            InputHandler onRight = state -> {
                System.err.println("Right");

                state.caretOffset += 1;
                state.selectionBegin = -1;
                state.selectionEnd = -1;

                return state;
            };


            addEventHandler(KeyEvent.KEY_PRESSED, evt -> {
                EditorState state = new EditorState(caretOffset.get(), selectionBegin.get(), selectionEnd.get());

                if (LEFT.match(evt)) {
                   state = onLeft.handle(state);
                   evt.consume();
                }
                if (RIGHT.match(evt)) {
                    state = onRight.handle(state);
                    evt.consume();
                }
                if (SHIFT_LEFT.match(evt)) {
                    state = onShiftLeft.handle(state);
                    evt.consume();
                }
                if (SHIFT_RIGHT.match(evt)) {
                    state = onShiftRight.handle(state);
                    evt.consume();
                }
                if (CTRL_LEFT.match(evt)) {
                    state = onCtrlLeft.handle(state);
                    evt.consume();
                }
                if (CTRL_RIGHT.match(evt)) {
                    state = onCtrlRight.handle(state);
                    evt.consume();
                }

                caretOffset.set(limit(state.caretOffset));
                if (state.selectionBegin == state.selectionEnd) {
                    state.selectionBegin = state.selectionEnd = -1;
                }
                selectionBegin.set(limit(state.selectionBegin));
                selectionEnd.set(limit(state.selectionEnd));
            });

            getChildren().setAll(canvas);

            AnchorPane.setTopAnchor(canvas, 0D);
            AnchorPane.setRightAnchor(canvas, 0D);
            AnchorPane.setBottomAnchor(canvas, 0D);
            AnchorPane.setLeftAnchor(canvas, 0D);
        }

        private int limit(int index) {
            if (index == -1) {
                return index;
            }
            return Math.min(doc.getEnd(), Math.max(0, index));
        }

        private Optional<Glyph> pickGlyph(MouseEvent evt) {
            double x = evt.getX();
            double y = evt.getY();
        /*
            return doc.streamGlyphs().filter(g ->
                g.y <= y && g.y + g.h > y &&
                g.x <= x && g.x + g.w > x)
                .findFirst();
*/
            Iterator<Glyph> it = doc.streamGlyphs().iterator();

            while (it.hasNext()) {
                Glyph cur = it.next();
                double left = cur.x;
                double middle = cur.x + cur.w / 2;
                double right = cur.x + cur.w;

                if (cur.y <= y && cur.y + cur.h > y) {

                    if (left <= x && middle > x) {
                        return Optional.of(cur);
                    }

                    if (middle <= x && right > x) {
                        if (it.hasNext()) {
                            return Optional.of(it.next());
                        }
                    }

                }
            }

            return Optional.empty();
        }

        public IntegerProperty caretOffsetProperty() {
            return caretOffset;
        }

        public IntegerProperty selectionBeginProperty() {
            return selectionBegin;
        }

        public IntegerProperty selectionEndProperty() {
            return selectionEnd;
        }

        @Override
        public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
            if (UIA.isUIAQuery(attribute, parameters)) {
                return doc;
            }
            return super.queryAccessibleAttribute(attribute, parameters);
        }

        private void repaint() {
            requestLayout();
        }

        private void render() {
            final GraphicsContext g2D = canvas.getGraphicsContext2D();
            final double width = getWidth();
            final double height = getHeight();

            g2D.clearRect(0, 0, width, height);// reset/clean canvas

            doc.render(g2D);

            drawFocusBorder(g2D);





            g2D.setFill(Color.rgb(1, 0, 0, 0.2));
            doc.streamGlyphs().forEach(g -> {
                //g2D.fillRect(g.x, g.y, g.w, g.h);
            });


            drawSelection(g2D);


            drawCaret(g2D);
        }

        @Override
        protected void layoutChildren() {
            super.layoutChildren();
            final double width = getWidth();
            final double height = getHeight();

            canvas.setWidth(width);
            canvas.setHeight(height);

            render();
        }

        private void drawFocusBorder(GraphicsContext g2D) {
            if (!isFocused()) {
                return;
            }

            final Paint stroke = g2D.getStroke();
            g2D.setStroke(Color.BLACK);

            g2D.strokeRect(1, 1, getWidth() - 1, getHeight() - 1);

            g2D.setStroke(stroke);
        }

        private void drawCaret(GraphicsContext gc) {
            if (!isFocused()) {
                return;
            }
            final int offset = caretOffset.get();

            Optional<Glyph> glyph = doc.streamGlyphs().filter(g -> g.index == offset).findFirst();

            glyph.ifPresent(g -> {
                gc.setFill(Color.RED);
                gc.fillRect(g.x - 3, g.y - 2, 6, 2);
                gc.fillRect(g.x - 1, g.y, 2, g.h);
                gc.fillRect(g.x - 3, g.y + g.h, 6, 2);
            });
        }

        private void drawSelection(GraphicsContext gc) {

            int lower = Math.min(selectionBegin.get(), selectionEnd.get());
            int upper = Math.max(selectionBegin.get(), selectionEnd.get());

            Supplier<Stream<Glyph>> selected = () -> doc.streamGlyphs().filter(g -> g.index >= lower && g.index < upper);

            List<Bounds> perGlyph = selected.get().map(g -> new BoundingBox(g.x, g.y, g.w, g.h)).collect(Collectors.toList());
            List<Bounds> merged = BaseModel.merge(perGlyph);


            gc.setFill(Color.BLUE);
            merged.forEach(m -> {
                gc.fillRect(m.getMinX(), m.getMinY(), m.getWidth(), m.getHeight());
            });
            selected.get().forEach(g -> {
                g.render.render(gc, Color.WHITE);
            });


        }


    }
}

package uia.sample.samples;

import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.uia.UIA;
import uia.sample.Sample;
import uia.sample.samples.model.UIADocument;
import uia.sample.samples.model.UIAPage;
import uia.sample.samples.model.UIATable;
import uia.sample.samples.model.UIATableCell;
import uia.sample.samples.model.UIATableHeaderCell;

public class DocumentModelSample implements Sample {
    
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
        page1.addText("Hello World\n", Font.font(16), Color.BLACK);
        page1.addText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", Font.font(10), Color.ROYALBLUE);
        page1.addText("Tabelle?\n", Font.getDefault(), Color.BLACK);
        page1.addEmbed(createTable());
        page1.addText("\n", Font.getDefault(), Color.BLACK);
        page1.addText("End\n", Font.getDefault(), Color.BLACK);

        page1.layoutW = 300;

        doc.addPage(page1);

        UIAPage page2 = new UIAPage();
        page2.addText("A second page\n", Font.getDefault(), Color.RED);
        page2.addText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", Font.font(10), Color.ROYALBLUE);

        page2.layoutW = 300;

        doc.addPage(page2);


        doc.layout();
        doc.computeIndices(0);

        BorderPane root = new BorderPane();

        Canvas canvas = new Canvas(400, 600) {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return doc;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };
        root.setCenter(canvas);

        doc.canvas = canvas;

        doc.render(canvas.getGraphicsContext2D());

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
}

package at.bestsolution.uia;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import at.bestsolution.uia.javafx.scene.NoA11YScene;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AccessibleMonitor extends Stage {

    ProxyAccessibleRegistry registry = ProxyAccessibleRegistry.getInstance();

    private  ListView<ProxyAccessible> accessibles;
    private TreeView<ProxyAccessible> tree;

    TextArea txt;


    public AccessibleMonitor() {

        BorderPane root = new BorderPane();
        Scene scene = new NoA11YScene(root);
        setScene(scene);


        SplitPane split = new SplitPane();

        BorderPane master = new BorderPane();
        BorderPane detail = new BorderPane();
        split.getItems().addAll(master, detail);

        root.setCenter(split);

        accessibles = new ListView<>();
        tree = new TreeView<>();
        tree.setCellFactory(view -> new TreeCell<ProxyAccessible>() {
            @Override
            public void updateItem(ProxyAccessible item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    setText(toLabel(item));
                } else {
                    setText("<null>");
                }

                if (empty) {
                    setText("");
                }

            }
        });

        TabPane menu = new TabPane();

        Tab listTab = new Tab("List");
        listTab.setClosable(false);
        Tab treeTab = new Tab("Tree");
        treeTab.setClosable(false);

        menu.getTabs().addAll(listTab, treeTab);

        listTab.setContent(accessibles);
        treeTab.setContent(tree);


       // tree.setRoot(treeRoot);

       // tree.config

        Button update = new Button("update");
        update.setOnAction(e -> update());

        root.setTop(update);

        txt = new TextArea();
        txt.setFont(Font.font("monospace"));
        txt.setEditable(false);

        master.setCenter(menu);
        detail.setCenter(txt);



        accessibles.getSelectionModel().selectedItemProperty().addListener((obs, ol, ne) -> {
            select(ne);
        });
        tree.getSelectionModel().selectedItemProperty().addListener((obs, ol, ne) -> {
            select(ne == null ? null : ne.getValue());
        });

        show();


    }

    String toLabel(ProxyAccessible item) {
        String text = "";
        if (item.getUIAElement() != null) {
            String cls = item.getUIAElement().getClass().getSimpleName();
            if (cls.isEmpty()) {
                // getClass().getSimpleName() returns empty for anonymous classes
                cls = "IUIAElement";
            }
            text += cls;
        }

        if (item.getGlassAccessible() != null) {
            if (!text.isEmpty()) text += " ";
            text += item.getGlassAccessible();
        }

        return text + " (" + item.getNum() + ")";
    }

    void select(ProxyAccessible item) {
        if (item == null) {
            txt.setText("");
        } else {

            String name = item.toString() + "\n";

            if (item.getUIAElement() != null) {
                name += item.getUIAElement() + "\n";
                name += item.getUIAElement().getClass() + "\n";
                name += item.getUIAElement().getClass().getSimpleName() + "\n";
            }




            if (item.src != null) {
                String t = Arrays.stream(item.src)
                .map(el -> el.toString())
                .collect(Collectors.joining("\n"));

                txt.setText(name + "\n\n" + t);
            }
        }
    }

    public void update() {
        accessibles.getItems().setAll(registry.getAccessibles());

        Optional<ProxyAccessible> root = registry.getAccessibles().stream().findFirst();
        tree.setRoot(root.map(this::buildTree).orElse(null));
    }

    TreeItem<ProxyAccessible> buildTree(ProxyAccessible root) {

        TreeItem<ProxyAccessible> result = new TreeItem<>(root);
        populate(result);
        return result;

    }

    TreeItem<ProxyAccessible> create(ProxyAccessible accessible) {
        TreeItem<ProxyAccessible> item = new TreeItem<>(accessible);
        item.setExpanded(true);
        return item;
    }

    TreeItem<ProxyAccessible> populate(TreeItem<ProxyAccessible> item) {
        ProxyAccessibleRegistry registry = ProxyAccessibleRegistry.getInstance();
        ProxyAccessible accessible = item.getValue();

        // find children
        final int NavigateDirection_NextSibling       = 1;
        final int NavigateDirection_FirstChild        = 3;

        long childPeer = accessible.IRawElementProviderFragment_Navigate(NavigateDirection_FirstChild);
        while(childPeer != 0) {
            ProxyAccessible child = registry.getByNative(childPeer);

            item.getChildren().add(populate(new TreeItem<>(child)));

            childPeer = child.IRawElementProviderFragment_Navigate(NavigateDirection_NextSibling);
        }

        return item;
    }


}

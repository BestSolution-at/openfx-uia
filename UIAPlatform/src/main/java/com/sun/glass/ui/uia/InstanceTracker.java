package com.sun.glass.ui.uia;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class InstanceTracker {

  private static Logger LOG = Logger.create(InstanceTracker.class);

  private static native void _initIDs();

  public static void require() {
    // static init
  }

  static {
    ProxyAccessible.requireLibrary();
    _initIDs();
  }

  public static class Instance {
    long pointer;
    String type;
    String reason;
    Object java;
    int refCount = 1;
    boolean destroyed = false;

    Instance(long pointer) {
      this.pointer = pointer;
    }
  }

  private static Map<Long, Instance> instances = new HashMap<>();
  
  private static void withInstance(long pointer, Consumer<Instance> func) {
    synchronized (instances) {
      Instance instance = instances.get(pointer);
      if (instance != null) {
        func.accept(instance);
      } else {
        LOG.warning("-", () -> "There is no Instance for " + pointer);
        Thread.dumpStack();
      }
    }
  }

  public static void create(long pointer) {
    synchronized (instances) {
      instances.put(pointer, new Instance(pointer));
    }

    Platform.runLater(InstanceTracker::update);
  }

  public static void destroy(long pointer) {
    synchronized (instances) {
      instances.get(pointer).destroyed = true;
    }
    Platform.runLater(InstanceTracker::update);

    LOG.debug("-", () -> "Report: \n" + instances.values().stream().filter(v -> v.destroyed == false).map(el -> " * " + el.type + "(0x" + Long.toHexString(el.pointer) + ")" +  " has " + el.refCount + " refs, reason=" + el.reason + "   " + el.java).collect(Collectors.joining("\n")));
  }

  public static void setType(long pointer, String type) {
    withInstance(pointer, instance -> instance.type = type);
    Platform.runLater(InstanceTracker::update);
  }

  public static void setReason(long pointer, String reason) {
    withInstance(pointer, instance -> instance.reason = reason);
    Platform.runLater(InstanceTracker::update);
  }

  public static void setJava(long pointer, Object java) {
    withInstance(pointer, instance -> instance.java = java);
    Platform.runLater(InstanceTracker::update);
  }

  public static void addRef(long pointer) {
    withInstance(pointer, instance -> instance.refCount += 1);
    Platform.runLater(InstanceTracker::update);
  }

  public static void release(long pointer) {
    withInstance(pointer, instance -> instance.refCount -= 1);
    Platform.runLater(InstanceTracker::update);
    
  }

  private static TableView<Instance> table;

  private static void update() {
    if (table != null) {
      table.setItems(FXCollections.emptyObservableList());
      table.setItems(FXCollections.observableArrayList(instances.values()));
    }
  }

  private static Callback<TableColumn<Instance,Instance>, TableCell<Instance,Instance>> text(Callback<Instance, String> cb) {
    return column -> new TableCell<Instance, Instance>() {
      protected void updateItem(Instance item, boolean empty) {
        super.updateItem(item, empty);

          try {
            String s = cb.call(item);
            setText("'" + s + "'");
          } catch (Exception e) {
            setText(e.getMessage());
          }
        
      }
    };
  }

  private static TableColumn<Instance, Instance> col(String label, Callback<Instance, String> text) {
    TableColumn<Instance, Instance> col = new TableColumn<>(label);
    col.setCellValueFactory(val -> {
      SimpleObjectProperty<Instance> foo = new SimpleObjectProperty<>();
      foo.setValue(val.getValue());
      return foo;
    });
    col.setCellFactory(text(text));
    return col;
  }

  private static BorderPane createContent() {
    BorderPane root = new BorderPane();

    table = new TableView<>();

    table.getColumns().add(col("pointer", instance -> "0x" + Long.toHexString(instance.pointer)));
    table.getColumns().add(col("type", instance -> instance.type));
    table.getColumns().add(col("refCount", instance -> "" + instance.refCount));
    table.getColumns().add(col("reason", instance -> instance.reason));
    table.getColumns().add(col("java", instance -> "" + instance.java));

    table.getColumns().add(col("destroyed", instance -> "" +  instance.destroyed));

    root.setCenter(table);

    Button update = new Button("update");
    update.setOnAction(e -> update());
    root.setBottom(update);

    return root;
  }

  public static void showReport() {
    Stage stage = new Stage();
    Scene scene = new Scene(createContent());
    stage.setScene(scene);
    stage.show();
  }


}

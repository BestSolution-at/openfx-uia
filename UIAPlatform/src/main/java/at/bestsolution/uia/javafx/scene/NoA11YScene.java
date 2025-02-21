package at.bestsolution.uia.javafx.scene;

import com.sun.glass.ui.Accessible;

import javafx.scene.Parent;
import javafx.scene.Scene;

// TODO uses namespace private override

@SuppressWarnings("restriction")
public class NoA11YScene extends Scene {

    public NoA11YScene(Parent root) {
        super(root);
    }

    // @Override
    // Accessible getAccessible() {
    //     return null;
    // }
}

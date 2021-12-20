package javafx.scene;

import com.sun.glass.ui.Accessible;

@SuppressWarnings("restriction")
public class NoA11YScene extends Scene {

    public NoA11YScene(Parent root) {
        super(root);
    }

    @Override
    Accessible getAccessible() {
        return null;
    }
}

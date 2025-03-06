//import at.bestsolution.uia.core.IAccessibleFactory;

module at.bestsolution.uia {
    requires transitive at.bestsolution.uia.core;

    requires transitive javafx.base;
    requires transitive javafx.graphics;

    requires javafx.controls; // for AccessibleMonitor (TODO get rid of me!)



    exports at.bestsolution.uia;

    // exports at.bestsolution.uia.internal to javafx.graphics;
    // opens at.bestsolution.uia.internal to javafx.graphics;

//    provides IAccessibleFactory with UIAAccessibleFactory;
}

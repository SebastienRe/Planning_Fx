module com.ceri.planningfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.mnode.ical4j.core;

    opens com.ceri.planningfx to javafx.fxml;
    exports com.ceri.planningfx;
    exports com.ceri.planningfx.controller;
    opens com.ceri.planningfx.controller to javafx.fxml;
}
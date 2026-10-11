module net.xoknox.czernicesweeper {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires static lombok;
    requires jdk.jshell;


    opens net.xoknox.czernicesweeper to javafx.fxml;
    exports net.xoknox.czernicesweeper;
    exports net.xoknox.czernicesweeper.applicationinstallation;
    opens net.xoknox.czernicesweeper.applicationinstallation to javafx.fxml;
}
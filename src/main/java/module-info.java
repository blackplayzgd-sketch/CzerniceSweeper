module net.xoknox.veritysweeper {
    requires javafx.controls;
    requires javafx.fxml;


    opens net.xoknox.czernicesweeper to javafx.fxml;
    exports net.xoknox.czernicesweeper;
}
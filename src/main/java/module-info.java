module net.xoknox.veritysweeper {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens net.xoknox.czernicesweeper to javafx.fxml;
    exports net.xoknox.czernicesweeper;
}
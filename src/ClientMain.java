import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.Socket;

public class ClientMain extends Application {

    public static Socket socket;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fload = new FXMLLoader(getClass().getResource("ClientLogin.fxml"));
        Parent par = fload.load();

        ClientLogin login = fload.getController();
        login.setStage(primaryStage);

        primaryStage.setScene(new Scene(par));
        primaryStage.setTitle("登入器");
        primaryStage.getIcons().add(new Image("img/icon.png"));
        primaryStage.setResizable(false);
        par.getStylesheets().add(ClientMain.class.getResource("Login.css").toExternalForm());
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
        System.exit(0);
    }
}

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        ClientManager manager = new ClientManager();
        manager.setStage(primaryStage);

        primaryStage.setScene(new Scene(manager.getLoginRoot()));
        primaryStage.setTitle("登入器");
        primaryStage.getIcons().add(new Image("img/icon.png"));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
        System.exit(0);
    }
}

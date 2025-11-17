import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mom extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(root, 960, 600);
        
        try {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        } catch(Exception e) {
            System.out.println("CSS file not found: " + e.getMessage());
        }
        
        stage.setTitle("Triangle Connect — Basic Prototype");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
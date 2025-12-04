import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.nio.file.Paths;

public class Main extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
      try
      {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
          Parent root = loader.load();

          MainView controller = loader.getController();
          controller.setFileController(new FileController(
                  Paths.get(System.getProperty("user.home"), "FileManagerGUI")
          ));

          primaryStage.setTitle("File Management System");
          primaryStage.setScene(new Scene(root, 800, 600));
          primaryStage.show();
      }
      catch(Exception e)
      {
          e.printStackTrace();
      }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}

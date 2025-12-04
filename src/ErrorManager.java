import javafx.scene.control.Alert;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.AccessDeniedException;

public class ErrorManager
{
    /**
     * Handles errors gracefully by printing console message and showing alert popup.
     *
     * @param e Exception thrown
     * @param context Description of the operation being performed
     */
    public static void handle(Exception e, String context)
    {
        String message;

        if (e instanceof FileAlreadyExistsException)
        {
            message = "A file or directory with that name already exists.";
        }
        else if (e instanceof NoSuchFileException)
        {
            message = "The file or directory could not be found.";
        }
        else if (e instanceof AccessDeniedException)
        {
            message = "Permission denied. You do not have access to this file or directory.";
        }
        else if (e instanceof IOException)
        {
            message = "An I/O error occurred while " + context + ".";
        }
        else
        {
            message = "An unexpected error occurred: " + e.getMessage();
        }

        // Console log
        System.err.println("Error: " + message);

        // Popup alert for user
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Operation Failed");
        alert.setHeaderText("Error " + context);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

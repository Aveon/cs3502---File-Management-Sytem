import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * JavaFX controller for the File Manager GUI
 */
public class MainView
{
    @FXML private ListView<String> fileList;
    @FXML private TextArea fileContent;
    @FXML private Label statusBar;

    private FileController controller;

    protected void setFileController(FileController controller)
    {
        this.controller = controller;
        refreshFileList();
    }

    @FXML
    private void onList()
    {
        refreshFileList();
    }

    @FXML
    private void onCreateFile()
    {
        TextInputDialog dialog = new TextInputDialog("newFile.txt");
        dialog.setTitle("Create File");
        dialog.setHeaderText("Please enter a file name");
        dialog.setContentText("File name:");

        dialog.showAndWait().ifPresent(name ->
        {
            try
            {
                controller.createFile(name, "");
                refreshFileList();

                Path newFile = controller.getCurrentDirectory().resolve(name);
                if (Files.exists(newFile))
                {
                    setStatus("File created: " + name);
                    showAlert(Alert.AlertType.INFORMATION, "File Created", "File created successfully: " + name);
                }
                else
                {
                    setStatus("Failed to create: " + name);
                    showAlert(Alert.AlertType.ERROR, "Create Failed", "File could not be created: " + name);
                }
            }
            catch (Exception e)
            {
                ErrorManager.handle(e, "creating file");
                setStatus("Error creating: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onCreateDirectory()
    {
        TextInputDialog dialog = new TextInputDialog("NewDirectory");
        dialog.setTitle("Create Directory");
        dialog.setHeaderText("Please enter a directory name:");
        dialog.setContentText("Directory name:");

        dialog.showAndWait().ifPresent(name ->
        {
            try
            {
                controller.createDirectory(name);
                refreshFileList();
                setStatus("Directory created: " + name);
                showAlert(Alert.AlertType.INFORMATION, "Directory Created", "Directory created successfully: " + name);
            }
            catch (Exception e)
            {
                ErrorManager.handle(e, "creating directory");
                setStatus("Error creating directory: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create directory:\n" + e.getMessage());
            }
        });
    }

    @FXML
    private void onReadFile()
    {
        String selected = fileList.getSelectionModel().getSelectedItem();
        if (selected == null)
        {
            setStatus("No file selected.");
            return;
        }

        try
        {
            String content = controller.readFile(selected);
            fileContent.setText(content);
            setStatus("Opened: " + selected);
        }
        catch (Exception e)
        {
            ErrorManager.handle(e, "reading file");
            setStatus("Error reading: " + selected);
        }
    }

    @FXML
    private void onUpdateFile()
    {
        String selected = fileList.getSelectionModel().getSelectedItem();
        if (selected == null)
        {
            setStatus("No file selected.");
            return;
        }

        Path path = controller.getCurrentDirectory().resolve(selected);
        String content = fileContent.getText();

        try
        {
            controller.updateFile(selected, content);

            if (!Files.isWritable(path))
            {
                setStatus("Failed to update (read-only): " + selected);
                showAlert(Alert.AlertType.ERROR, "Update Failed",
                        "Could not update file: " + selected + "\nThe file is read-only or access is denied.");
            }
            else
            {
                setStatus("File updated: " + selected);
                showAlert(Alert.AlertType.INFORMATION, "File Updated",
                        selected + " was updated successfully!");
            }
        }
        catch (Exception e)
        {
            ErrorManager.handle(e, "updating file");
            setStatus("Error updating: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteFile()
    {
        String selected = fileList.getSelectionModel().getSelectedItem();
        if (selected == null)
        {
            setStatus("No file selected.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Are you sure you want to delete this?");
        confirm.setContentText(selected);

        confirm.showAndWait().ifPresent(response ->
        {
            if (response == ButtonType.OK)
            {
                try
                {
                    controller.delete(selected);
                    refreshFileList();

                    Path deletedPath = controller.getCurrentDirectory().resolve(selected);
                    if (Files.exists(deletedPath))
                    {
                        setStatus("Failed to delete: " + selected);
                        showAlert(Alert.AlertType.ERROR, "Delete Failed",
                                "Could not delete: " + selected + "\nCheck file permissions.");
                    }
                    else
                    {
                        setStatus("Deleted: " + selected);
                        showAlert(Alert.AlertType.INFORMATION, "Delete Successful",
                                selected + " was deleted successfully.");
                    }
                }
                catch (Exception e)
                {
                    ErrorManager.handle(e, "deleting file");
                    setStatus("Error deleting: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onRenameFile()
    {
        String selected = fileList.getSelectionModel().getSelectedItem();
        if (selected == null)
        {
            setStatus("No file selected.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected);
        dialog.setTitle("Rename File");
        dialog.setHeaderText("Enter a new name:");
        dialog.showAndWait().ifPresent(newName ->
        {
            try
            {
                controller.rename(selected, newName);
                refreshFileList();

                Path newPath = controller.getCurrentDirectory().resolve(newName);
                if (Files.exists(newPath))
                {
                    setStatus("Renamed: " + selected + " → " + newName);
                    showAlert(Alert.AlertType.INFORMATION, "Rename Successful",
                            "File renamed successfully to: " + newName);
                }
                else
                {
                    setStatus("Failed to rename: " + selected);
                    showAlert(Alert.AlertType.ERROR, "Rename Failed",
                            "Could not rename file: " + selected + "\nFile may already exist or permission denied.");
                }
            }
            catch (Exception e)
            {
                ErrorManager.handle(e, "renaming file/directory");
                setStatus("Error renaming: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onNavigateFile()
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Navigate");
        dialog.setHeaderText("Enter folder name:");
        dialog.showAndWait().ifPresent(folder ->
        {
            try
            {
                controller.navigateTo(Path.of(folder));
                refreshFileList();
                setStatus("Navigated to: " + folder);
            }
            catch (Exception e)
            {
                ErrorManager.handle(e, "navigating to directory");
                setStatus("Error navigating: " + folder);
            }
        });
    }

    // Refresh the file list in GUI
    private void refreshFileList()
    {
        try
        {
            fileList.getItems().clear();
            List<FileItem> items = controller.fileManager.listDirectory(controller.getCurrentDirectory());
            for (FileItem item : items)
            {
                fileList.getItems().add(item.getName());
            }
        }
        catch (Exception e)
        {
            setStatus("Error refreshing list: " + e.getMessage());
        }
    }

    // Update status bar text
    private void setStatus(String status)
    {
        statusBar.setText(status);
    }

    // Show JavaFX alert message
    private void showAlert(Alert.AlertType type, String title, String message)
    {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

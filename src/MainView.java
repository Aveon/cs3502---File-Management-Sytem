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

    // Initializes controller and populates file list when GUI is loaded
    protected void setFileController(FileController controller)
    {
        this.controller = controller;
        refreshFileList();
    }

    // Refreshes the displayed directory list
    @FXML
    private void onList()
    {
        refreshFileList();
    }

    // Creates new file in current directory
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
                Path newFile = controller.getCurrentDirectory().resolve(name);

                // Check if it already exists before attempting creation
                if (Files.exists(newFile))
                {
                    showAlert(Alert.AlertType.WARNING, "File Exists",
                            "A file with that name already exists: " + name);
                    setStatus("File already exists: " + name);
                    return;
                }

                // Attempt to create file
                controller.createFile(name, "");

                // Confirm it was actually created
                if (Files.exists(newFile))
                {
                    refreshFileList();
                    setStatus("File created: " + name);
                    showAlert(Alert.AlertType.INFORMATION, "File Created",
                            "File created successfully: " + name);
                }
                else
                {
                    setStatus("File creation failed: " + name);
                    showAlert(Alert.AlertType.ERROR, "Create Failed",
                            "The file could not be created: " + name);
                }
            }
            catch (Exception e)
            {
                ErrorManager.handle(e, "creating file");
                setStatus("Error creating file: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "File Creation Failed",
                        "Error creating file:\n" + e.getMessage());
            }
        });
    }

    // Creates new file in current directory
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
                Path newDir = controller.getCurrentDirectory().resolve(name);

                // Pre-check if it already exists
                if (Files.exists(newDir))
                {
                    showAlert(Alert.AlertType.WARNING, "Directory Exists",
                            "A directory with that name already exists: " + name);
                    setStatus("Directory already exists: " + name);
                    return;
                }

                // Try to create directory
                controller.createDirectory(name);

                // Post-check: verify if directory was created
                if (Files.isDirectory(newDir))
                {
                    refreshFileList();
                    setStatus("Directory created: " + name);
                    showAlert(Alert.AlertType.INFORMATION, "Directory Created",
                            "Directory created successfully: " + name);
                }
                else
                {
                    setStatus("Directory creation failed: " + name);
                    showAlert(Alert.AlertType.ERROR, "Create Failed",
                            "The directory could not be created: " + name);
                }
            }
            catch (Exception e)
            {
                ErrorManager.handle(e, "creating directory");
                setStatus("Error creating directory: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Directory Creation Failed",
                        "Error creating directory:\n" + e.getMessage());
            }
        });
    }


    // Reads/displays content of the selected file in the text area
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
            setStatus("Error reading file: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Read Failed",
                    "Could not read file: " + selected + "\n" + e.getMessage());
        }
    }

    // Saves changes made to file's content
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
            if (!Files.isWritable(path))
            {
                showAlert(Alert.AlertType.ERROR, "Permission Denied",
                        "Cannot write to file: " + selected + " (read-only or restricted access).");
                setStatus("Update failed: file not writable.");
                return;
            }

            controller.updateFile(selected, content);
            setStatus("File updated: " + selected);
            showAlert(Alert.AlertType.INFORMATION, "File Updated",
                    selected + " was updated successfully!");
        }
        catch (Exception e)
        {
            ErrorManager.handle(e, "updating file");
            setStatus("Error updating file: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Update Failed",
                    "Failed to update file: " + e.getMessage());
        }
    }

    // Deletes selected file/directory after user confirmation
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
                    setStatus("Deleted: " + selected);
                    showAlert(Alert.AlertType.INFORMATION, "Delete Successful",
                            selected + " was deleted successfully.");
                }
                catch (Exception e)
                {
                    ErrorManager.handle(e, "deleting file");
                    setStatus("Error deleting file: " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Delete Failed",
                            "Failed to delete file: " + e.getMessage());
                }
            }
        });
    }

    // Renames selected file/directory
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
            if (newName.equals(selected))
            {
                showAlert(Alert.AlertType.WARNING, "Invalid Rename",
                        "The new name is the same as the current name.");
                return;
            }

            try
            {
                controller.rename(selected, newName);
                refreshFileList();
                setStatus("Renamed: " + selected + " → " + newName);
                showAlert(Alert.AlertType.INFORMATION, "Rename Successful",
                        "File renamed successfully to: " + newName);
            }
            catch (Exception e)
            {
                ErrorManager.handle(e, "renaming file");
                setStatus("Error renaming file: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Rename Failed",
                        "Failed to rename file: " + e.getMessage());
            }
        });
    }

    // Changes current working directory
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
                setStatus("Error navigating: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Navigation Failed",
                        "Failed to navigate: " + e.getMessage());
            }
        });
    }

    // Refresh the file list in GUI
    private void refreshFileList()
    {
        try
        {
            fileList.getItems().clear();
            List<FileItem> items = controller.listCurrentDirectoryContents();
            for (FileItem item : items)
            {
                fileList.getItems().add(item.getName());
            }
        }
        catch (Exception e)
        {
            setStatus("Error refreshing list: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Refresh Failed",
                    "Could not refresh file list: " + e.getMessage());
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

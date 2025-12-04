import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TestFileController - Simple console tester for FileController and FileManager.
 *
 * Runs basic CRUD and navigation operations to verify functionality.
 */
public class TestFileController
{
    public static void main(String[] args)
    {
        // Create a safe test directory inside the user's home directory
        Path testDir = Paths.get(System.getProperty("user.home"), "FileManagerTest");
        FileController controller = new FileController(testDir);

        System.out.println("=== FILE MANAGEMENT SYSTEM TEST START ===");

        // Create the test directory if it doesn't exist
        controller.createDirectory(testDir.getFileName().toString());

        // List current directory
        controller.listCurrentDirectory();

        // Create a file and write content
        controller.createFile("demo.txt", "Hello world from FileManager!");
        controller.listCurrentDirectory();

        // Read the file back
        controller.readFile("demo.txt");

        // Update the file content
        controller.updateFile("demo.txt", "Updated content successfully!");
        controller.readFile("demo.txt");

        // Rename the file
        controller.rename("demo.txt", "renamed_demo.txt");
        controller.listCurrentDirectory();

        // Delete the file
        controller.delete("renamed_demo.txt");
        controller.listCurrentDirectory();

        // Create a subdirectory
        controller.createDirectory("SubFolder");
        controller.listCurrentDirectory();

        // Navigate into subdirectory
        controller.navigateTo(testDir.resolve("SubFolder"));
        controller.listCurrentDirectory();

        // Navigate back up to parent directory
        controller.navigateTo(testDir.getParent());
        controller.listCurrentDirectory();

        System.out.println("=== TEST COMPLETE ===");
    }
}

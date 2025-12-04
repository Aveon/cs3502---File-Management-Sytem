import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;

/**
 * FileController - Connects user's actions to FileManager methods.
 *
 * Prints feedback to the console.
 */
public class FileController
{
    protected final FileManager fileManager;
    private Path currentDirectory;

    /**
     * Constructs a FileController with starting directory.
     *
     * @param startDirectory : Initial working directory.
     */
    public FileController(Path startDirectory)
    {
        this.fileManager = new FileManager();
        this.currentDirectory = startDirectory;

        // Ensure the starting directory exists
        try
        {
            if (!Files.exists(currentDirectory))
            {
                Files.createDirectories(currentDirectory);
                System.out.println("Created working directory: " + currentDirectory);
            }
        }
        catch (IOException e)
        {
            ErrorManager.handle(e, "initializing FileController directory");
        }
    }

    /**
     * Lists contents of current directory.
     */
    public void listCurrentDirectory()
    {
        try
        {
            List<FileItem> items = fileManager.listDirectory(currentDirectory);
            System.out.println("\nContents of: " + currentDirectory);

            if (items.isEmpty())
            {
                System.out.println("(Directory is empty)");
            }
            else
            {
                for (FileItem item : items)
                {
                    System.out.println(item.toString());
                }
            }
        }
        catch (IOException e)
        {
            ErrorManager.handle(e, "listing directory");
        }
    }

    /**
     * Creates a new file in the current directory.
     *
     * @param fileName : Name of new file.
     * @param content  : Content to be written to file.
     */
    public void createFile(String fileName, String content)
    {
        try
        {
            Path newFile = currentDirectory.resolve(fileName);
            fileManager.createFile(newFile, content);
            System.out.println("File created: " + newFile);
        }
        catch (Exception e)
        {
            ErrorManager.handle(e, "creating file");
            throw new RuntimeException(e); // tell MainView this failed
        }
    }


    /**
     * Creates new subdirectory in current directory.
     *
     * @param directoryName : Name of the directory to create.
     */
    public void createDirectory(String directoryName)
    {
        try
        {
            Path newDir = currentDirectory.resolve(directoryName);
            fileManager.createDirectory(newDir);
            System.out.println("Directory created: " + newDir);
        }
        catch (Exception e)
        {
            ErrorManager.handle(e, "creating directory");
            throw new RuntimeException(e); // rethrow
        }
    }


    /**
     * Reads contents of file.
     *
     * @param fileName : File to be read.
     */
    public String readFile(String fileName)
    {
        try
        {
            Path path = currentDirectory.resolve(fileName);
            return fileManager.readFile(path);
        }
        catch (Exception e)
        {
            ErrorManager.handle(e, "reading file");
            return "";
        }
    }

    /**
     * Updates contents of an existing file.
     *
     * @param fileName : File to be updated.
     * @param content  : New content to write.
     */
    public void updateFile(String fileName, String content)
    {
        try
        {
            Path path = currentDirectory.resolve(fileName);
            fileManager.updateFile(path, content);
            System.out.println("File updated: " + path);
        }
        catch (Exception e)
        {
            ErrorManager.handle(e, "updating file");
            throw new RuntimeException(e); // rethrow
        }
    }


    /**
     * Deletes file/directory.
     *
     * @param name : File/directory name.
     */
    public void delete(String name)
    {
        try
        {
            Path path = currentDirectory.resolve(name);
            fileManager.delete(path);
            System.out.println("Deleted: " + path);
        }
        catch (Exception e)
        {
            ErrorManager.handle(e, "deleting file");
            throw new RuntimeException(e); // rethrow
        }
    }


    /**
     * Renames file or directory.
     *
     * @param oldName Current file/directory name
     * @param newName New file/directory name
     */
    public void rename(String oldName, String newName)
    {
        try
        {
            Path oldPath = currentDirectory.resolve(oldName);
            Path newPath = currentDirectory.resolve(newName);
            fileManager.rename(oldPath, newPath);
            System.out.println("Renamed: " + oldPath + " → " + newPath);
        }
        catch (Exception e)
        {
            ErrorManager.handle(e, "renaming file/directory");
            throw new RuntimeException(e); // rethrow the error for MainView to handle
        }
    }

    /**
     * Changes the current working directory.
     *
     * @param path : New directory path.
     */
    public void navigateTo(Path path)
    {
        try
        {
            if (!path.isAbsolute())
            {
                path = currentDirectory.resolve(path);
            }

            if (!path.toFile().isDirectory())
            {
                throw new IOException("Not a directory: " + path);
            }

            currentDirectory = path.normalize();
            System.out.println("Navigated to: " + currentDirectory);
        }
        catch (Exception e)
        {
            ErrorManager.handle(e, "navigating to file");
            throw new RuntimeException(e);
        }
    }


    /**
     * Returns current working directory.
     *
     * @return Path of current directory.
     */
    public Path getCurrentDirectory()
    {
        return currentDirectory;
    }

    /**
     * Returns a list of files and directories in the current working directory.
     *
     * @return List of FileItem objects representing the current directory contents.
     * @throws IOException if the directory cannot be accessed.
     */
    public List<FileItem> listCurrentDirectoryContents() throws IOException
    {
        return fileManager.listDirectory(currentDirectory);
    }

}

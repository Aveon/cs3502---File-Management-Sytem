import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * FileManager - Core class for performing file and directory operations.
 * Supports Create, Read, Update, Delete, and Rename operations.
 */
public class FileManager
{

    /**
     * Lists all the files and directories within a given directory path.
     *
     * @param directory The directory to list.
     * @return List of FileItem objects representing files/subdirectories.
     * @throws IOException if directory can't be accessed.
     */
    public List<FileItem> listDirectory(Path directory) throws IOException
    {
        List<FileItem> items = new ArrayList<>();
        if (!Files.exists(directory))
        {
            throw new NoSuchFileException("Directory doesn't exist: " + directory);
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory))
        {
            for (Path path : stream)
            {
                items.add(new FileItem(path));
            }
        }
        return items;
    }

    /**
     * Creates a new file with optional content.
     *
     * @param path The file path.
     * @param content The content to write to the file (either null or empty).
     * @throws IOException if it fails to create the file or file already exists.
     */
    public void createFile(Path path, String content) throws IOException
    {
        if (Files.exists(path))
        {
            throw new FileAlreadyExistsException("File already exists: " + path);
        }

        Files.write(path,
                content == null ? new byte[0] : content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE_NEW);
    }

    /**
     * Creates a new directory.
     *
     * @param path The directory path.
     * @throws IOException if it fails to create the directory or directory already exists.
     */
    public void createDirectory(Path path) throws IOException
    {
        if (Files.exists(path))
        {
            throw new FileAlreadyExistsException("Directory already exists: " + path);
        }

        Files.createDirectory(path);
    }

    /**
     * Reads the contents of a file as a UTF-8 string.
     *
     * @param path The file path.
     * @return The file contents.
     * @throws IOException if file doesn't exist or can't be read.
     */
    public String readFile(Path path) throws IOException
    {
        if (!Files.exists(path))
        {
            throw new NoSuchFileException("File not found: " + path);
        }

        return Files.readString(path, StandardCharsets.UTF_8);
    }

    /**
     * Updates (overwrites) the contents of an existing file.
     *
     * @param path The file path.
     * @param content New content to write to the file.
     * @throws IOException if the file can't be written to.
     */
    public void updateFile(Path path, String content) throws IOException
    {
        if (!Files.exists(path))
        {
            throw new NoSuchFileException("File not found: " + path);
        }

        if (!Files.isWritable(path))
        {
            throw new AccessDeniedException("Permission denied: " + path);
        }

        Files.writeString(path, content, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Renames or moves a file/directory.
     *
     * @param oldPath The existing path.
     * @param newPath The new desired path.
     * @throws IOException if rename fails.
     */
    public void rename(Path oldPath, Path newPath) throws IOException
    {
        if (!Files.exists(oldPath))
        {
            throw new NoSuchFileException("File not found: " + oldPath);
        }

        if (Files.exists(newPath))
        {
            throw new FileAlreadyExistsException("File already exists: " + newPath);
        }

        Files.move(oldPath, newPath);
    }

    /**
     * Deletes a file or directory (recursively for directories).
     *
     * @param path The path to delete.
     * @throws IOException if deletion fails.
     */
    public void delete(Path path) throws IOException
    {
        if (!Files.exists(path))
        {
            throw new NoSuchFileException("File or Directory not found: " + path);
        }

        if (Files.isDirectory(path))
        {
            Files.walkFileTree(path, new SimpleFileVisitor<>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException
                {
                    Files.deleteIfExists(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else
        {
            Files.delete(path);
        }
    }
}

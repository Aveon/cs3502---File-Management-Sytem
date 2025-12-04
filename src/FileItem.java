import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;

/**
 * FileItem - Represents a file or directory for display in the UI.
 */
public class FileItem
{
    private final Path path;
    private final String name;
    private final boolean isDirectory;
    private final long size;
    private final Instant lastModified;

    /**
     * Constructs a FileItem from the given path.
     *
     * @param path The file or directory path.
     * @throws IOException if file attributes cannot be read.
     */
    public FileItem(Path path) throws IOException
    {
        this.path = path;
        this.name = path.getFileName().toString();
        this.isDirectory = Files.isDirectory(path);
        this.size = isDirectory ? 0L : Files.size(path);
        this.lastModified = Files.getLastModifiedTime(path).toInstant();
    }

    public Path getPath()
    {
        return path;
    }

    public String getName()
    {
        return name;
    }

    public boolean isDirectory()
    {
        return isDirectory;
    }

    public long getSize()
    {
        return size;
    }

    public Instant getLastModified()
    {
        return lastModified;
    }

    @Override
    public String toString()
    {
        return (isDirectory ? "[DIR]" : "[FILE]") + " " + name;
    }
}

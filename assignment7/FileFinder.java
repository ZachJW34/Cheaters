import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * FileFinder will recursively search through files in a supplied directory and store them in an ArrayList
 */
public class FileFinder {

    /**
     * ArrayList to store all the files found in a directory
     */
    private List<File> files;

    /**
     * Total space of all the files found in bytes
     */
    private long totalSpace;

    /**
     * @param path to the directory to recursively search through via {@link #searchFiles(File)}. Use getter method to
     *             retrieve the files. List will be null if path supplied is not compatible
     */
    public FileFinder(String path) {
        files = new ArrayList<>();
        totalSpace = 0;
        File folder = new File(path);
        if (folder.isFile()) {
            totalSpace += folder.getTotalSpace();
            files.add(folder);
        } else if (folder.isDirectory()) {
            searchFiles(folder);
        } else {
            files = null;
        }
    }

    /**
     * Recursive function to add files recursively to an ArrayList
     * @param folder is the starting point
     */
    private void searchFiles(File folder) {
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                totalSpace += file.getTotalSpace();
                this.files.add(file);
            } else {
                searchFiles(file);
            }
        }
    }

    public List<File> getFiles() {
        return files;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

}

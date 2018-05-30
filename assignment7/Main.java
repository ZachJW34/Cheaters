import java.io.*;
import java.util.List;

public class Main {

    /**
     * Command line processing and running of {@link FileFinder} and {@link StringMaps}
     * @param args are commnad line options. args[0] = path, args[1] = sequence size, args[2] = threshold
     */
    public static void main(String[] args){
        if (args.length == 0) {
            System.out.println("Please run again with command line input");
        } else {
            FileFinder fileFinder = new FileFinder(args[0]);
            List<File> files = fileFinder.getFiles();
            StringMaps stringMaps = null;
            StringMapsConcurrent stringMapsConcurrent = null;
            if (args.length == 1){
                stringMaps = new StringMaps(files, 6, 100);
            } else if (args.length == 2) {
                stringMaps = new StringMaps(files, Integer.parseInt(args[1]), 100);
            } else {
                stringMaps = new StringMaps(files, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                //stringMapsConcurrent = new StringMapsConcurrent(files, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            }
            if (stringMaps != null) {
                System.out.println(stringMaps.process());
            } else if (stringMapsConcurrent != null){
                System.out.println(stringMapsConcurrent.process());
            }
        }
    }
}

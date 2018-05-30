

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * StringMaps functions to search through all of the files provided to its constructor, breaking each txt file into
 * string sequences of length six and storing these sequences in a list unique for each file, and adding each unique
 * sequence to a hashmap.
 */
public class StringMaps {

    /**
     * Parameter that defines number of strings in a sequence
     */
    private int sequenceSize;

    /**
     * Threshold that determine is someone cheated or not according to how many similar sequences they find
     */
    private int threshold;

    /**
     * A list that stores all sequences of {@link #sequenceSize} length for each file
     */
    private List<FileSequences> fileSequences;

    /**
     * Maps a sequence to a list of files that contain that sequence
     */
    private Map<String, ArrayList<String>> hashMap;

    /**
     * Map for faster retrieval of matrix index
     */
    private Map<String, Integer> matrixSearch;

    /**
     * Matrix that stores the files that share similar sequences
     */
    private int[][] matrix;

    /**
     * Each file is assigned a {@link FileSequences} object that stores all {@link #sequenceSize} sequences and uID
     * (file name) and the hashmap of all sequences is generated
     * @param files is an ArrayList that contains all the files to be parsed
     * @param sequenceSize is the number of strings that defines a sequence
     * @param threshold is the threshold for which someone is considered cheating
     */
    public StringMaps(List<File> files, int sequenceSize, int threshold){
        //Init
        this.threshold = threshold;
        this.sequenceSize = sequenceSize;
        fileSequences = new ArrayList<>();
        hashMap = new HashMap<>();
        matrixSearch = new HashMap<>();
        matrix = new int[files.size()][files.size()];

        // Parse files
        for (File file: files){
            fileSequences.add(new FileSequences(file));
        }

        // Generate hashmap for matrix indexing
        for (int i = 0; i < fileSequences.size(); i++){
            matrixSearch.put(fileSequences.get(i).fileName, i);
        }
    }

    /**
     * FileSequences contains only a constructor. This class contains the logic for parsing and storing the txt files
     * as sequences in {@link #fileSequences} and for {@link #hashMap}
     */
    private class FileSequences{
        private String fileName;
        List<String> sequences;

        /**
         * Parses a given txt file. Logic breakdown:
         * <ul>
             *  <li>Create a {@link BufferedReader} and parse the entire file line-by-line (appending a space after each line)
             *      and store in a {@link StringBuilder object}</li>
             *  <li>Iterate through entire {@link StringBuilder} object, char-by-char, and remove all characters that aren't
             *      characters or digits or whitespace</li>
             *  <li>Split the string by using //s+</li>
             *  <li>Iterate through String array and combine strings based upon {@link #sequenceSize} into a sequence.
             *      If this new sequence does not have a corresponding key in {@link #hashMap}, then put a new entry
             *      into the map. If the key already exists, and the sequence is from a different file, add the filename
             *      to the list associated with the hashed value of the sequence (Key)</li>
             *  <li>Store the list of sequences in {@link #sequences}</li>
         *  </ul>
         * @param file is the txt file to parse.
         */
        private FileSequences(File file){
            // Init
            fileName = file.getName();
            sequences = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();

            // Read the entire file and store strings line by line, separated by whitespace
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String string;
                while ((string = bufferedReader.readLine()) != null) {
                    stringBuilder.append(string).append(" ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Replace any unrecognized elements so that only letters, numbers and whitespace remains
            for (int i = 0; i < stringBuilder.length(); i++){
                char character = stringBuilder.charAt(i);
                if (!Character.isLetterOrDigit(character) && !Character.isWhitespace(character)){
                    stringBuilder.deleteCharAt(i);
                    i--;
                } else if (Character.isUpperCase(stringBuilder.charAt(i))){
                    stringBuilder.setCharAt(i, Character.toLowerCase(stringBuilder.charAt(i)));
                }
            }

            // Split the string by whitespace and store x words into a new string sequence.
            String[] strings = stringBuilder.toString().trim().split("\\s+");
            for (int i = 0; i < strings.length - (sequenceSize - 1) ; i++){
                stringBuilder = new StringBuilder();
                for (int j = i; j < (i + sequenceSize); j++){
                    stringBuilder.append(strings[j]).append(" ");
                }
                stringBuilder.deleteCharAt(stringBuilder.length()-1);

                // Check the hashmap for the key, if it exists append the filename that is associated with it else create new pair
                if (hashMap.containsKey(stringBuilder.toString())){
                    ArrayList<String> files = hashMap.get(stringBuilder.toString());
                    if (!files.get(0).equals(fileName)){ //Repeats from the same file do not count
                        files.add(0, fileName);
                    }
                } else {
                    hashMap.put(stringBuilder.toString(), new ArrayList<String>(){{ add(fileName); }});
                }
                sequences.add(stringBuilder.toString());
            }
        }
    }

    /**
     * Iterate through the {@link #fileSequences}. For each, search the sequences in the hashmap and obtain a list
     * of all files that share that sequence. For each file, increment a spot in the matrix according to the {@link
     * FileSequences} being searched and the filename found in the {@link #hashMap} value list.
     * @return a string to print to console.
     */
    public String process(){
        for (int i = 0; i < fileSequences.size(); i++){
            FileSequences fileSequence = fileSequences.get(i);
            for (String sequence: fileSequence.sequences){
                for (String fileName: hashMap.get(sequence)){
                    if (!fileSequence.fileName.equals(fileName)){
                        matrix[i][matrixSearch.get(fileName)]++;
                    }
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[i].length; j++){
                if (i >= j) {
                    int number = matrix[i][j];
                    if (number > threshold) {
                        stringBuilder.append(number).append(": ")
                                .append(fileSequences.get(i).fileName).append(", ")
                                .append(fileSequences.get(j).fileName).append("\n");
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

}


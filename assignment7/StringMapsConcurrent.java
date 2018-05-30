import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StringMapsConcurrent {
    private int sequenceSize;
    private int threshold;
    private List<FileSequences> fileSequences;
    private Map<String, ArrayList<String>> hashMap;
    private Map<String, Integer> matrixSearch;
    private int[][] matrix;

    public StringMapsConcurrent(List<File> files, int sequenceSize, int threshold){
        this.threshold = threshold;
        this.sequenceSize = sequenceSize;
        fileSequences = new ArrayList<>();
        hashMap = new ConcurrentHashMap<>();
        matrixSearch = new HashMap<>();
        matrix = new int[files.size()][files.size()];

        Thread[] threads = new Thread[files.size()];
        for (File file: files){
            fileSequences.add(new FileSequences(file));
        }
        for (int i = 0; i < fileSequences.size(); i++){
            threads[i] = new Thread(fileSequences.get(i));
            threads[i].start();
        }
        for (Thread thread: threads){
            try{
                thread.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        for (int i = 0; i < fileSequences.size(); i++){
            matrixSearch.put(fileSequences.get(i).fileName, i);
        }
    }

    private class FileSequences implements Runnable{
        private File file;
        private String fileName;
        List<String> sequences;

        public FileSequences(File file) {
            this.file = file;
            fileName = file.getName();
            sequences = new ArrayList<>();
        }

        @Override
        public void run() {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String string;
                while ((string = bufferedReader.readLine()) != null) {
                    char[] chars = string.toCharArray();
                    stringBuilder.append(string).append(" ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < stringBuilder.length(); i++){
                char character = stringBuilder.charAt(i);
                if (!Character.isLetterOrDigit(character) && !Character.isWhitespace(character)){
                    stringBuilder.deleteCharAt(i);
                    i--;
                } else if (Character.isUpperCase(stringBuilder.charAt(i))){
                    stringBuilder.setCharAt(i, Character.toLowerCase(stringBuilder.charAt(i)));
                }
            }
            String[] strings = stringBuilder.toString().trim().split("\\s+");
            for (int i = 0; i < strings.length - (sequenceSize - 1) ; i++){
                StringBuilder stringBuilder1 = new StringBuilder();
                for (int j = i; j < (i + sequenceSize); j++){
                    stringBuilder1.append(strings[j]).append(" ");
                }
                stringBuilder1.deleteCharAt(stringBuilder1.length()-1);
                if (hashMap.containsKey(stringBuilder1.toString())){
                    ArrayList<String> files = hashMap.get(stringBuilder1.toString());
                    if (!files.get(0).equals(fileName)){
                        files.add(0, fileName);
                        hashMap.replace(stringBuilder1.toString(), files);
                    }
                } else {
                    hashMap.put(stringBuilder1.toString(), new ArrayList<String>(){{
                        add(fileName);
                    }});
                }
                sequences.add(stringBuilder1.toString());
            }
        }
    }

    public String process(){
        Thread[] threads = new Thread[fileSequences.size()];
        for (int i = 0; i < fileSequences.size(); i++){
            threads[i] = new Thread(new Processor(fileSequences.get(i), i));
            threads[i].start();
        }

        for (Thread thread: threads){
            try{
                thread.join();
            } catch (InterruptedException e){
                e.printStackTrace();
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

    private class Processor implements Runnable{

        private FileSequences fileSequences;
        private int row;

        public Processor(FileSequences fileSequences, int row){
            this.fileSequences = fileSequences;
            this.row = row;
        }

        @Override
        public void run() {
            for (String sequence: fileSequences.sequences){
                for (String fileName: hashMap.get(sequence)){
                    if (!fileSequences.fileName.equals(fileName)){
                        matrix[row][matrixSearch.get(fileName)]++;
                    }
                }
            }
        }
    }

}
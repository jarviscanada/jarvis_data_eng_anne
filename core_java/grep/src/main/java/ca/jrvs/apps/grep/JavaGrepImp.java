package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaGrepImp implements JavaGrep {

    private String regex;
    private String rootPath;
    private String outFile;

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: regex rootPath outFile");
        }

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try{
            javaGrepImp.process();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Traverses a given directory and returns all files.
    @Override
    public void process() throws IOException{

        try {
            // List of files.
            List<File> fileList = listFiles(rootPath);
            // List of text lines derived from the list of files.
            List<String> lines = new ArrayList<>();
            // Subset of the list of text lines made up of lines that match the regex.
            List<String> matchedLines = new ArrayList<>();

            // Go through every file and read the lines.
            // If the line matches the given regex, record the line and write all of the
            // matched lines to file.
            for (File file: fileList) {
                lines = readLines(file);
                for (String line : lines) {
                    if (containsPattern(line)) {
                        matchedLines.add(line);
                    }
                }
            }
            writeToFile(matchedLines);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    // List the files in a given directory, including files in subdirectories.
    @Override
    public List<File> listFiles(String rootDir){
        File root = new File(rootDir);
        File[] list = root.listFiles();
        List<File> fileList = new ArrayList<>();

        // If there are no files or subdirectories in the given directory,
        // there's nothing to list so return null.
        try {
            for (File filename: list) {
                // To account for files in subdirectories, obtain a list of files in those
                // subdirectories and then add them to the overarching file list.
                if (filename.isDirectory()) {
                    List<File> subdirectoryContents = File.listFiles(filename.getAbsolutePath());
                    fileList.addAll(subdirectoryContents);
                }
                // If the filename isn't a directory, add it to the list.
                else {
                    fileList.add(filename);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileList;
    }

    // Read the text from the input file (line by line).
    @Override
    public List<String> readLines(File inputFile){
        List<String> listLines = new ArrayList<>();

        try {
            // Text scanner to read the input file line by line.
            Scanner scanner = new Scanner(inputFile);
            // If there are more lines to be read from the file, move onto the next
            // line and add the line to the list.
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                listLines.add(line);
            }
            // Once there are no more lines, close the scanner.
            scanner.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return listLines;
    }

    // Given a line, check to see if it matches given regex.
    @Override
    public boolean containsPattern(String line){
        // Built-in String.matches(pattern) to see if line matches given regex.
        return line.matches(this.regex);
    }

    //
    @Override
    void writeToFile(List<String> lines) throws IOException{
        Path file = (new File(outFile)).toPath();
        try{
            // NOTE: creates a new, empty file named by abstract pathname only if a file
            //       with this name does not already exist.
            Files.createFile(file);
        } catch (IOException e) {
            // NOTE: changed from ignore because something should be done even if
            //       the file wasn't created.
            e.printStackTrace();
        }
        Files.write(file, lines);
    }

    @Override
    public String getRootPath() {
        return rootPath;
    }
    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
    @Override
    public String getRegex() {
        return regex;
    }
    @Override
    public void setRegex(String regex) {
        this.regex = regex;
    }
    @Override
    public String getOutFile() {
        return outFile;
    }
    @Override
    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }
}
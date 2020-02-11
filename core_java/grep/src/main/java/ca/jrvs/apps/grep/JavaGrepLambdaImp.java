package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaGrepLambdaImp extends JavaGrepImp {

    public static void main(String[] args) {

        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: regex rootPath outFile");
        }

        JavaGrepLambdaImp javaGrepLambdaImp = new JavaGrepLambdaImp();
        javaGrepLambdaImp.setRegex(args[0]);
        javaGrepLambdaImp.setRootPath(args[1]);
        javaGrepLambdaImp.setOutFile(args[2]);

        try {
            javaGrepLambdaImp.process();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<String> readLines(File inputFile){
        List<String> lineList = new ArrayList<>();

        // Modified from https://www.mkyong.com/java8/java-8-stream-read-a-file-line-by-line/
        // Come back and change this if I can figure out a better method.
        try (Stream<String> lines = Files.lines(Paths.get(String.valueOf(inputFile)))) {
            lines.forEach(line -> lineList.add(line));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineList;
    }

    @Override
    public List<File> listFiles(String rootDir){

        File root = new File(rootDir);
        File[] list = root.listFiles();
        List<File> fileList = new ArrayList<>();

        if (list == null){
            return null;
        }

        // Modified from https://www.mkyong.com/java/java-how-to-list-all-files-in-a-directory/
        try (Stream<Path> paths = Files.walk(Paths.get(rootDir))){
            // Files.walk() walks through the file tree, so it already includes the files
            // found in subdirectories. From there, filter to make sure that the argument given
            // is a file, map to transform the filename to a String, and then collect it in the
            // form of a List.
            List<String> result = (List<String>) paths.filter(file -> Files.isRegularFile(file))
                    .map(file -> file.toString())
                    .collect(Collectors.toList());
            // Transfer the file collection from List<String> result to List<String> fileList which
            // exists in this method external from the try.
            result.forEach(file -> fileList.add(new File(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }
}

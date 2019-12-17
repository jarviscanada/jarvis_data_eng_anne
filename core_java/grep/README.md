# Introduction

grep` is a Java implementation of the `grep` Bash command-line utility used for searching through plain-text data sets for lines that match a text pattern.

The app is given a directory and searches for a text pattern recursively. (This means that the app searches for the text pattern in the files in the deepest subdirectory first, then moves upwards until the algorithm is back in the root/given directory.)

Fun fact! `grep` is named in reference to `g/re/p`: **g**lobal search with **r**egular **e**xpression and **p**rinting the lines that match.

# Usage

`grep` takes three arguments.
1. `regex`: text pattern that lines are matched against
2. `rootPath`: the path to the root directory where the search takes place
3. `outFile`: output file name where the results are stored.

For example, given arguments 

```"Anne Nguyen" ~/Documents ~/Documents/results.txt```

`grep` will search through all of the files present in the documents folder, including subdirectories, for "Anne Nguyen." When the app finds a file with a line that contains "Anne Nguyen," it will record that line, and continue its search. Once it completes the search, the app creates a file called `results.txt` in the Documents folder. In `results.txt` is every line that the `grep` app found with "Anne Nguyen" (the given text pattern).

# Pseudocode
Below is a simplified version of the `process()` code found within this `grep` app.

```
matchedLines = []

for file in listFilesRecursivelyFrom(rootDirectory)
    for line in readLines(file)
        if containsPattern(line)
            matchedLines.add(line)

writeToFile(matchedLines)
```

Two nestled loops are required to go through every file and to go through every line of each file. `grep` checks each line to see if it matches the text pattern. If it does, then `grep` adds the line to its record. At the end of the nestled loops, `grep` writes the matched lines into an output file.

# Performance Issue

Given a large enough file, `grep` may not have sufficient memory space to process and store matched lines due to the initial design of using a while-loop to read the file line by line and storing the lines in a `ArrayList<String>`. If there is not enough room on the stack to accomodate every line in the file, lines will be lost while `readLines()` is running. 

To address this perfomance issue, the line collecting in `grep` will use `Stream` instead of `Scanner` contained in a while-loop. `Stream` can break down a data set into piecework, which allows for parallelization. This is in sharp contrast to loops, which work in serial. The switch to `Stream` improves both space and time performance for large file processing by opening the possibility for concurrent processing and ensures no data is lost. 

# Improvements

1. Highlight the part of the line that matches the given `regex` input for every line stored in the output file.
2. Add filename and line number to the matched line. This is so for every matched line, the user reading the output file knows which file and where in the file every line is.
3. Proper error log recording. `printStackTrace()` is not an adequate or human-readable solution.

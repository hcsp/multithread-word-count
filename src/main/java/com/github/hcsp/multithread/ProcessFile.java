package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessFile {
    private final ConcurrentHashMap<String, Integer> resultMap = new ConcurrentHashMap<>();
    private final File file;

    public ProcessFile(File file) {
        this.file = file;
    }

    public ConcurrentHashMap<String, Integer> processFile() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String[] wordList = line.split("\\s+");
                for (String word : wordList) {
                    if (resultMap.containsKey(word)) {
                        resultMap.put(word, resultMap.get(word) + 1);
                    } else {
                        resultMap.put(word, 1);
                    }
                }
            }
            return resultMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class ProcessFile {
    private Map<String, Integer> resultMap;
    private File file;

    public ProcessFile(Map<String, Integer> resultMap, File file) {
        this.resultMap = resultMap;
        this.file = file;
    }

    public Map<String, Integer> processFile() {
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

package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProcessFileList extends Thread {
    private final List<File> fileList;
    private final Map<String, Integer> resultMap;

    public ProcessFileList(List<File> fileList, Map<String, Integer> resultMap) {
        this.fileList = fileList;
        this.resultMap = resultMap;
    }

    @Override
    public void run() {
        insertContentToMapFromFile();
    }

    private void insertContentToMapFromFile() {
        try {
            for (File file : fileList) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] wordList = line.split("\\s+");
                    insertContentToMapFromList(wordList);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertContentToMapFromList(String[] wordList) {
        for (String word : wordList) {
            if (resultMap.containsKey(word)) {
                resultMap.put(word, resultMap.get(word) + 1);
            } else {
                resultMap.put(word, 1);
            }
        }
    }
}

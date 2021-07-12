package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount1 extends Thread {
    public static Map<String, Integer> resultMap = new HashMap<>();

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        insertContentToMapFromFile(files);
        return resultMap;
    }

    private static void insertContentToMapFromFile(List<File> fileList) {
        for (File file : fileList) {
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

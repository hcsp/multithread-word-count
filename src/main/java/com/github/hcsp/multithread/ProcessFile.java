package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProcessFile {
    public static Map<String, Integer> convertWordsInFileToMap(File file, Map<String, Integer> targetMap) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Map<String, Integer> sourceMap = new HashMap<>();
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String[] wordList = line.split("\\s+");
                for (String word : wordList) {
                    if (sourceMap.containsKey(word)) {
                        sourceMap.put(word, sourceMap.get(word) + 1);
                    } else {
                        sourceMap.put(word, 1);
                    }
                }
            }
            MergeMap.merge(sourceMap, targetMap);
            return targetMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

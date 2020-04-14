package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CountFileUtils {
    public static HashMap<String, Integer> countWord(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        HashMap<String, Integer> wordMapOfEveryWorker = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
                wordMapOfEveryWorker.put(word, wordMapOfEveryWorker.getOrDefault(word, 0) + 1);
            }
        }
        return wordMapOfEveryWorker;
    }

    public static Map<String, Integer> mergeWorkerJobToFinalResult(Map<String, Integer> finalResult, Map<String, Integer> resultFromWorker) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();
            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResult);
        }
        return finalResult;
    }
}

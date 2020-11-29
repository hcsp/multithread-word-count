package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class WordCount implements Callable<Map<String, Integer>> {
    private BufferedReader reader;

    WordCount(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public Map<String, Integer> call() throws Exception {
        String line;
        Map<String, Integer> result = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
                result.put(word, result.getOrDefault(word, 0) + 1);
            }
        }
        return result;
    }
}

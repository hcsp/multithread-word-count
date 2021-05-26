package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
    public static Map<String, Integer> count(File file) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, Integer> countResult = new HashMap<>();
        for (String line : lines) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                countResult.put(word, countResult.getOrDefault(word, 0) + 1);
            }
        }
        return countResult;
    }

    public static Map<String, Integer> merge(Map<String, Integer> resultFromWorkers,
                                             Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorkers.entrySet()) {
            int mergedResult = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), mergedResult);
        }
        return finalResult;
    }
}

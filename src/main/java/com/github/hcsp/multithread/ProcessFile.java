package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProcessFile {
    public static Map<String, Integer> processFile(File file) {
        try {
            Map<String, Integer> result = new HashMap<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                Arrays.stream(line.split("\\s+")).forEach(word -> result.merge(word, 1, Integer::sum));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

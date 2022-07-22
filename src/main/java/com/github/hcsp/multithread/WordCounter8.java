package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class WordCounter8 extends RecursiveTask<Map<String, Integer>> {
    List<File> files;

    public WordCounter8(List<File> files) {
        this.files = files;
    }

    @Override
    protected Map<String, Integer> compute() {
        if (files.isEmpty()) {
            return Collections.emptyMap();
        }

        // 递归的执行任务 => Fork
        Map<String, Integer> result = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(files.get(0)));
            String line;
            while ((line = reader.readLine()) != null) {
                Arrays.stream(line.split("\\s+")).forEach(word -> result.merge(word, 1, Integer::sum));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Map<String, Integer> countOfRestFiles = new WordCounter8(files.subList(1, files.size())).compute();

        // Join
        countOfRestFiles.forEach((key, val) -> result.merge(key, val, Integer::sum));
        return result;
    }
}

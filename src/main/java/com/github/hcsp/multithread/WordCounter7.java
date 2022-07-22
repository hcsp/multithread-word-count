package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class WordCounter7 implements Callable<Map<String, Integer>> {
    private final List<File> files;

    public WordCounter7(List<File> files) {
        this.files = files;
    }

    @Override
    public Map<String, Integer> call() throws Exception {
        Map<String, Integer> result = new HashMap<>();
        files.forEach(file -> ProcessFile.processFile(file).forEach((key, value) -> result.merge(key, value, Integer::sum)));
        return result;
    }
}

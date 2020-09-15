package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class WorkCountTask implements Callable<Map<String, Integer>> {
    private File file;

    public WorkCountTask(File file) {
        this.file = file;
    }


    @Override
    public Map<String, Integer> call() throws Exception {
        // final String name = Thread.currentThread().getName();
        // if (name.contains("3")) {
            Thread.sleep(50L);
        // }
        // System.out.println(name + " start running...");
        FileReader fileReader = new FileReader(file);
        BufferedReader bf = new BufferedReader(fileReader);
        Map<String, Integer> collect = new HashMap<>();
        final List<String> strList = bf.lines().collect(Collectors.toList());
        for (String line : strList) {
            final String[] keys = line.split("\\s+");
            for (String key : keys) {
                final Integer val = collect.getOrDefault(key, 0);
                collect.put(key, val + 1);
            }
        }
        return collect;
    }
}

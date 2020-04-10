package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class ReadFileWithCallable implements Callable<Map<String, Integer>> {
    private BufferedReader reader;
    private Map<String, Integer> map = new HashMap<>();

    public Map<String, Integer> getMap() {
        return map;
    }

    public ReadFileWithCallable(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public Map<String, Integer> call() throws Exception {
        ReadFileUtils.readWordsToMap(map, reader);
        return map;
    }
}

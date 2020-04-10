package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ReadFileWithCountLatch implements Runnable {
    private BufferedReader reader;
    private CountDownLatch latch;
    private Map<String, Integer> map = new HashMap<>();
    private List<Map<String, Integer>> maps = new ArrayList<>();

    public List<Map<String, Integer>> getMaps() {
        return maps;
    }

    public ReadFileWithCountLatch(BufferedReader reader, CountDownLatch latch, List<Map<String, Integer>> mapList) {
        this.reader = reader;
        this.latch = latch;
        maps = mapList;
    }

    @Override
    public void run() {
        try {
            ReadFileUtils.readWordsToMap(map, reader);
            maps.add(map);
        } catch (IOException e) {
            throw new RuntimeException();
        }finally {
            latch.countDown();
        }
    }
}

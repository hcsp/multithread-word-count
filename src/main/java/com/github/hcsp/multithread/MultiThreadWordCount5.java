package com.github.hcsp.multithread;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount5 {

    public static ReentrantLock lock = new ReentrantLock();
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {

        ConcurrentHashMap<String, Integer> res = new ConcurrentHashMap<>();

        files.parallelStream()
                .map(MultiThreadWordCount5::readAndCompute)
                .forEach(map -> {
                    for (String word : map.keySet()) {
                        lock.lock();
                        Integer cnt = res.getOrDefault(word, 0);

                        res.put(word, cnt + map.get(word));
                        lock.unlock();
                    }
                });


        return res;
    }



    protected static Map<String, Integer> readAndCompute(File file) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        try (
                InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                String[] words = line.split("\\s+");

                for (String word : words) {
                    Integer cnt = map.getOrDefault(word, 0);

                    map.put(word, cnt + 1);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }
}

package com.github.hcsp.multithread;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount5 {

    private static final Object obj = new Object();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {

        ConcurrentHashMap<String, Integer> res = new ConcurrentHashMap<>();

        files.parallelStream()
                .map(MultiThreadWordCount5::readAndCompute)
                .forEach(map -> {
                    for (String word : map.keySet()) {
                        Integer cnt = res.getOrDefault(word, 0);

                        res.put(word, cnt + map.get(word));
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

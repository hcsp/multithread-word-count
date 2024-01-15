package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiThreadWordCount2 {

    //使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {
        HashMap<String, Integer> resultMap = new HashMap<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Object lock = new Object();
            HashMap<String, Integer> oneFileResultMap = new HashMap<>();
            for (int i = 0; i < 9; i++) {
                new Thread(() -> {
                    synchronized (lock) {
                        String line = null;
                        while (true) {
                            try {
                                if (!((line = reader.readLine()) != null)) {
                                    break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String[] words = line.split(" ");
                            for (String word : words) {
                                if (oneFileResultMap.containsKey(word)) {
                                    oneFileResultMap.put(word, oneFileResultMap.get(word) + 1);
                                } else {
                                    oneFileResultMap.put(word, 1);
                                }
                            }
                        }
                    }
                });
            }
            mergeOneLineCountToOneFileCountByMap(resultMap, oneFileResultMap);
        }
        return resultMap;
    }


    private static void mergeOneLineCountToOneFileCountByMap(Map<String, Integer> resultMap,
                                                             Map<String, Integer> oneFileResultMap) {
        Set<String> keys = oneFileResultMap.keySet();
        for (String key : keys) {
            resultMap.put(key, oneFileResultMap.getOrDefault(key, oneFileResultMap.get(key)));
        }
    }
}

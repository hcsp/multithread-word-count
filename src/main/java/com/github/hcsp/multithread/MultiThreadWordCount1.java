package com.github.hcsp.multithread;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map result = new ConcurrentHashMap();
        for (int i = 0; i <= threadNum; i++) {
            new Thread() {
                @Override
                public void run() {
                    for (File file : files
                    ) {
                        try {
                            FileReader fileReader = new FileReader(file);
                            int s = 0;
                            while (!(s == -1)) {
                                s = fileReader.read();
                                if (result.containsKey(s)) {
                                    int value = (int) result.get(s);
                                    result.put(s, value + 1);
                                } else {
                                    result.put(s, 0);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            };
        }

        return result;
    }
}

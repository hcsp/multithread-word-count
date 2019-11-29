package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadWordCount3 {
    //     使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        if (files == null) {
            return null;
        }
        ArrayBlockingQueue<Map<String, Integer>> blockingQueue = new ArrayBlockingQueue<>(files.size(), true);
        Map<String, Integer> resultmap = new HashMap<>();
        AtomicInteger atomicInteger = new AtomicInteger(files.size());
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);


        for (File file : files) {

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Map<String, Integer> map = new HashMap<>();

                    try {
                        List<String> lines = Files.readAllLines(file.toPath());
                        for (String line : lines) {
                            String[] words = line.split("\\s+");

                            for (String word : words) {
                                Integer aDefault = map.getOrDefault(word, 0);
                                map.put(word, aDefault + 1);
                            }
                        }
                        blockingQueue.put(map);
                        atomicInteger.decrementAndGet();

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });


        }



        boolean flag = true;
        while (flag) {
            if (atomicInteger.get()==0){
                while (blockingQueue.size() != 0) {
                    Map<String, Integer> stringIntegerMap = blockingQueue.take();
                    mergemap(resultmap, stringIntegerMap);
                }
                flag=false;
            }
        }

        return resultmap;
    }

    private static void mergemap(Map<String, Integer> resultmap, Map<String, Integer> stringIntegerMap) {
        for (String s : stringIntegerMap.keySet()) {
            Integer map1OrDefault = stringIntegerMap.getOrDefault(s, 0);
            Integer reslutmapOrDefault = resultmap.getOrDefault(s, 0);
            resultmap.put(s, map1OrDefault + reslutmapOrDefault);
        }
    }
}

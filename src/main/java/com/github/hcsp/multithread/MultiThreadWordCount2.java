package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {

        //线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        CountDownLatch count = new CountDownLatch(files.size());
        List<Map<String, Integer>> maplist = new ArrayList<>();

        //每一个线程读一个文件
        for (File everyfile : files) {
            //匿名内部类
            //每个线程完成后结果添加到futures列表中

            BufferedReader bufferedReader = new BufferedReader(new FileReader(everyfile));
            threadPool.execute(new Runnable() {
                @Override
                public void run() {

                    Map<String, Integer> everyFilemessage = new HashMap<>();
                    //读取每个文件
                    while (true) {
                        String everyline = null;
                        try {
                            everyline = bufferedReader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (everyline == null) {
                            break;
                        }
                        //分割
                        String[] words = everyline.split(" ");
                        //统计并添加到第一个map
                        for (String word : words) {
                            everyFilemessage.put(word, everyFilemessage.getOrDefault(word, 0) + 1);
                        }
                    }
                    maplist.add(everyFilemessage);
                    count.countDown();
                }
            });
        }
        try {
            count.await();

            //汇总map列表中的信息， 总结成最终结果
            Map<String, Integer> finalmap = new HashMap<>();
            for (Map<String, Integer> everymap : maplist) {
                for (Map.Entry<String, Integer> entry : everymap.entrySet()) {
                    finalmap.put(entry.getKey(), finalmap.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }

            }
            return finalmap;
        } finally {
            threadPool.shutdown();
        }
    }
}

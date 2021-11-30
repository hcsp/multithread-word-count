package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {

        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);

        //每个文件的future都收集起来 最后统一处理
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();


        for (File everyfile : files) {
            //读取字符流
            BufferedReader bufferedReader = new BufferedReader(new FileReader(everyfile));


            //多线程创建
            for (int i = 0; i < threadNum; i++) {
                //匿名内部类
                //每个线程完成后结果添加到futures列表中
                futures.add(threadPool.submit(new Callable<Map<String, Integer>>() {

                    @Override
                    public Map<String, Integer> call() throws Exception {

                        Map<String, Integer> everylinemessage = new HashMap<>();
                        //读取每一行
                        String everyline = null;
                        while ((everyline = bufferedReader.readLine()) != null) {
                            //分割
                            String[] words = everyline.split(" ");
                            //统计并添加到第一个map
                            for (String word : words) {
                                everylinemessage.put(word, everylinemessage.getOrDefault(word, 0) + 1);
                            }
                        }
                        return everylinemessage;
                    }
                }));
            }

        }
        //收集futures列表中的map
        List<Map<String, Integer>> mapcollection = new ArrayList<>();
        for (Future<Map<String, Integer>> everyfuture : futures) {
            mapcollection.add(everyfuture.get());
        }
        //汇总map列表中的信息， 总结成最终结果
        Map<String, Integer> finalmap = new HashMap<>();
        for (Map<String, Integer> everymap : mapcollection) {
            for (Map.Entry<String, Integer> entry : everymap.entrySet()) {
                finalmap.put(entry.getKey(), finalmap.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }

        }
        return finalmap;
    }
}

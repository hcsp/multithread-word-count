package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class WordCount {
    private final int threadNum;
    private final ExecutorService threadPool;

    public WordCount(int threadNum) {
        this.threadNum = threadNum;
        threadPool = Executors.newFixedThreadPool(threadNum);
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        ArrayList<BufferedReader> readers = new ArrayList<>();
        //创建多个BufferedReader
        for (File file : files) {
            readers.add(new BufferedReader(new FileReader(file)));
        }
        //最终查询结果
        Map<String, Integer> finalResult = new HashMap<>();
        //各个线程的查询结果
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        //多线程查询,每个文件使用一个线程
        for (BufferedReader reader : readers) {
            Future<Map<String, Integer>> result = threadPool.submit(getMap(reader));
            futures.add(result);
        }
        //合并查询结果
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWork = future.get();
            mergeWorkToFinal(resultFromWork, finalResult);
        }

        return finalResult;
    }

    //合并多线程的查询结果
    private void mergeWorkToFinal(Map<String, Integer> resultFromWork, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWork.entrySet()) {
            int mergeAmount = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), mergeAmount);
        }
    }
    //线程工作内容
    private Callable<Map<String, Integer>> getMap(BufferedReader reader) {
        return () -> {
            Map<String, Integer> map = new HashMap<>();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    String[] list = line.split(" ");
                    for (String s : list) {
                        map.put(s, map.getOrDefault(s, 0) + 1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return map;
        };
    }
}

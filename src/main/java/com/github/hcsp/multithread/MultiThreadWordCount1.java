package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private final int threadNum;
    private ExecutorService threadPool;

    public MultiThreadWordCount1(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }


    public Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        List<Map<String, Integer>> resultList = new ArrayList<>();
        Map<String, Integer> lastMap = new HashMap<>();

        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Map<String, Integer> resultMap = new HashMap<>();
            List<Future<Map<String, Integer>>> list = new ArrayList<>();
            for (int i = 0; i < threadNum; i++) {
                list.add(threadPool.submit(new Callable<Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> call() throws Exception {
                        String line;
                        Map<String, Integer> result = new HashMap<>();
                        while ((line = reader.readLine()) != null) {
                            String[] words = line.split(" ");

                            for (String word : words) {
                                result.put(word, result.getOrDefault(word, 1));
                            }
                        }
                        return result;
                    }
                }));
            }

            //此处得到了Future的集合List，要将它们合并
            for (Future<Map<String, Integer>> mapFuture : list) {
                //得到每个结果中的Map
                Map<String, Integer> stringIntegerMap = mapFuture.get();
                Set<Map.Entry<String, Integer>> entries = stringIntegerMap.entrySet();

                for (Map.Entry<String, Integer> entry : entries) {
                    String word = entry.getKey();

                    int mergeValue = entry.getValue() + resultMap.getOrDefault(word, 0);
                    //一个set一个set构成本文件中的map
                    resultMap.put(word, mergeValue);

                }

            }
            //至此，一个文件中的Map正式处理完，加入结果要处理的list中
            resultList.add(resultMap);
        }

        //对list中的各个文件的Map进行合并

        for (Map<String, Integer> eachMap : resultList) {
            Set<Map.Entry<String, Integer>> entries = eachMap.entrySet();
            for (Map.Entry<String, Integer> entry : entries) {
                String word = entry.getKey();

                int mergeValue = entry.getValue() + lastMap.getOrDefault(word, 0);
                //一个set一个set构成本文件中的map
                lastMap.put(word, mergeValue);
            }
        }


        //返回最终返回语句
        return lastMap;

    }


}

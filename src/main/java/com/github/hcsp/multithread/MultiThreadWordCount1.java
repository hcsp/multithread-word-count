package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        WorldCount worldCount = new WorldCount(10);
        Map<String, Integer> result = worldCount.count(10, Arrays.asList(new File("D://Leo/a.txt")));
        System.out.println(result);
    }

    public static class WorldCount {
        private final Integer threadNum;
        public static ExecutorService threadPool;

        public WorldCount(Integer threadNum) {
            threadPool = Executors.newFixedThreadPool(threadNum);
            this.threadNum = threadNum;
        }

        // 使用threadNum个线程，并发统计文件中各单词的数量
        public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
            BufferedReader reader = new BufferedReader(new FileReader(files.get(0)));
            List<Future<Map<String, Integer>>> futures = new ArrayList<>();
            for (int i = 0; i < threadNum; i++) {
                Future<Map<String, Integer>> mapFuture = threadPool.submit(new workerJob(reader));
                futures.add(mapFuture);
            }
            threadPool.shutdown();
            Map<String, Integer> finalResult = new HashMap<>();
            for (Future<Map<String, Integer>> future :
                    futures) {
                margeWorksResultIntoFinalResult(future.get(), finalResult);
            }
            return finalResult;
        }

        private static void margeWorksResultIntoFinalResult(Map<String, Integer> stringIntegerMap, Map<String, Integer> finalResult) {
            for (Map.Entry<String, Integer> entry : stringIntegerMap.entrySet()
            ) {
                finalResult.put(entry.getKey(), finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
    }

    static class workerJob implements Callable<Map<String, Integer>> {
        private BufferedReader reader;

        public workerJob(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] worlds = line.split(" ");
                for (String world :
                        worlds) {
                    result.put(world, result.getOrDefault(world, 0) + 1);
                }
            }
            return result;
        }
    }
}

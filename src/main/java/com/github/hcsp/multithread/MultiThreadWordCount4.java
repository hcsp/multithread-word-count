package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.*;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        final CountDownLatch latch = new CountDownLatch(files.size());
        ExecutorService executorService = newFixedThreadPool(threadNum);
        for (File file : files) {
            Task task = new Task(latch, file);
            Future<Map<String, Integer>> result = executorService.submit(task);
            for (Map.Entry<String, Integer> entry : result.get().entrySet()) {
                Integer num = map.getOrDefault(entry.getKey(), 0);
                map.put(entry.getKey(), num + entry.getValue());
            }
        }
        latch.await(30, TimeUnit.SECONDS);
        executorService.shutdown();
        return map;
    }

    public static class Task implements Callable<Map<String, Integer>> {
        private CountDownLatch latch;
        private File file;

        @Override
        public Map<String, Integer> call() {
            Map<String, Integer> fileResult = null;
            try {
                fileResult = readFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
            return fileResult;
        }

        private static Map<String, Integer> readFile(File file) throws Exception {
            Map<String, Integer> temp = new ConcurrentHashMap<>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String strLine = null;
            while (null != (strLine = bufferedReader.readLine())) {
                String[] words = strLine.split("\\s");
                for (String word : words) {
                    Integer num = temp.getOrDefault(word, 0);
                    num++;
                    temp.put(word, num);
                }
            }
            bufferedReader.close();
            return temp;
        }

        public Task(CountDownLatch latch, File file) {
            this.latch = latch;
            this.file = file;
        }
    }

}

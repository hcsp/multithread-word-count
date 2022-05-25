package com.github.hcsp.multithread;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;



public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        CountDownLatch countDownLatch = new CountDownLatch(files.size());


        ArrayList<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> map = null;

        for (File file: files) {
            CountTask countTask = new CountTask(file, countDownLatch);

            Future<Map<String, Integer>> res = executorService.submit(countTask);

            futures.add(res);
        }

        try {
            countDownLatch.await();
            map = compute(futures);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return map;
    }


    public static Map<String, Integer> compute(ArrayList<Future<Map<String, Integer>>> futureList) throws ExecutionException, InterruptedException {

        HashMap<String, Integer> res = new HashMap<>();

        for (Future<Map<String, Integer>> future: futureList) {
            Map<String, Integer> map = future.get();
            for (String word : map.keySet()) {
                Integer cnt = res.getOrDefault(word, 0);

                res.put(word, cnt + map.get(word));
            }
        }


        return res;

    }


    static class CountTask implements Callable<Map<String, Integer>> {

        private final File file;

        private CountDownLatch countDownLatch;

        CountTask(File file, CountDownLatch countDownLatch) {
            this.file = file;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public Map<String, Integer> call() throws Exception {

            HashMap<String, Integer> map = new HashMap<>();

            try(
                    InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            ) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.trim();
                    String[] words = line.split("\\s+");

                    for (String word: words) {
                        Integer cnt = map.getOrDefault(word, 0);
                        map.put(word, cnt + 1);
                    }
                }
            } finally {
                countDownLatch.countDown();
            }

            return map;
        }
    }
}

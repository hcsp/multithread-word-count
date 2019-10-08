package com.github.hcsp.multithread;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

public class WordCount {

    private final int nThread;

    private final ExecutorService executor;

    public WordCount(int threadNum) {
        nThread = threadNum;
        executor = Executors.newFixedThreadPool(nThread);
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> files) throws InterruptedException, ExecutionException {
        // validate
        if (null == files || 0 == files.size()) {
            return new HashMap<>();
        }
        final Map<String, Integer> res = new HashMap<>();
        final List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            Future<Map<String, Integer>> future = executor.submit(new CountTask(file));
            futures.add(future);
        }
        mergeResult(futures, res);
        return res;
    }

    private void mergeResult(List<Future<Map<String, Integer>>> futures, Map<String, Integer> res) throws ExecutionException, InterruptedException {
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> temp = future.get();
            for (Map.Entry<String, Integer> entry : temp.entrySet()) {
                res.put(entry.getKey(), res.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
    }

    class CountTask implements Callable<Map<String, Integer>> {

        private final File file;

        CountTask(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            final Map<String, Integer> map = new HashMap<>();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        map.put(word, map.getOrDefault(word, 0) + 1);
                    }
                }
            }
            return map;
        }
    }

}

package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount1 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException, FileNotFoundException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        MultiFilesReader multiFilesReader = new MultiFilesReader(files);

        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(new MultiFilesReaderWorkerJob(multiFilesReader)));
        }

        Map<String, Integer> wordCountResult = new HashMap<>();

        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkerResultIntoWordCountResult(wordCountResult, resultFromWorker);
        }

        threadPool.shutdown();

        return wordCountResult;
    }

    public static class MultiFilesReaderWorkerJob implements Callable<Map<String, Integer>> {
        MultiFilesReader multiFilesReader;

        public MultiFilesReaderWorkerJob(MultiFilesReader multiFilesReader) {
            this.multiFilesReader = multiFilesReader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = multiFilesReader.readLine()) != null) {
                String[] words = line.split(" ");

                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }

    public static void mergeWorkerResultIntoWordCountResult(
            Map<String, Integer> wordCountResult,
            Map<String, Integer> workerResult) {
        for (Map.Entry<String, Integer> entry : workerResult.entrySet()) {
            String word = entry.getKey();
            wordCountResult.put(word, wordCountResult.getOrDefault(word, 0) + entry.getValue());
        }
    }

    public static class MultiFilesReader {
        Queue<File> files = new LinkedList<>();
        private BufferedReader reader;

        public MultiFilesReader(List<File> files) throws FileNotFoundException {
            this.files.addAll(files);
            pollReader();
        }

        private synchronized void pollReader() throws FileNotFoundException {
            File file = files.poll();
            if (file == null) {
                reader = null;
            } else {
                reader = new BufferedReader(new FileReader(file));
            }
        }

        public synchronized String readLine() throws IOException {
            if (reader == null) {
                return null;
            }
            String line = reader.readLine();
            if (line == null) {
                pollReader();
                return readLine();
            }
            return line;
        }
    }
}

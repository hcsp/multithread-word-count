package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount5 {
    // (ForkJoinPool) 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {
        Map<String, Integer> results = new LinkedHashMap<>();
        List<WordsInFile> tasks = new ArrayList<>(files.size());
        List<BufferedReader> readers = new ArrayList<>(files.size());

        ForkJoinPool pool = new ForkJoinPool(threadNum);
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            readers.add(reader);
            WordsInFile process = new WordsInFile(reader);
            pool.execute(process);
            tasks.add(process);
        }

        joinTaskResults(results, tasks);
        closeReaders(readers);
        return results;
    }

    public static class WordsInFile extends RecursiveTask<Map<String, Integer>> {
        private static final int THRESHOLD = 20;
        private int countLine = 0;
        private BufferedReader reader;

        public WordsInFile(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        protected Map<String, Integer> compute() {
            Map<String, Integer> result = new LinkedHashMap<>();
            List<WordsInFile> tasks = new ArrayList<>();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    countLine += 1;
                    for (String word : line.split(" ")) {
                        if (!"".equals(word)) {
                            result.put(word, result.getOrDefault(word, 0) + 1);
                        }
                    }

                    if (countLine >= THRESHOLD) {
                        WordsInFile task = new WordsInFile(reader);
                        task.fork();
                        tasks.add(task);
                    }
                    joinTaskResults(result, tasks);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    private static void joinTaskResults(Map<String, Integer> results, List<WordsInFile> tasks) {
        for (WordsInFile item : tasks) {
            Map<String, Integer> map = item.join();
            mergeToFinalMap(results, map);
        }
    }

    private static void mergeToFinalMap(Map<String, Integer> results, Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String word = entry.getKey();
            results.put(word, results.getOrDefault(word, 0) + entry.getValue());
        }
    }

    private static void closeReaders(List<BufferedReader> readers) throws IOException {
        for (BufferedReader bReader : readers) {
            bReader.close();
        }
    }
}

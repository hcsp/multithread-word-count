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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount3 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException, FileNotFoundException {
        ForkJoinPool pool = ForkJoinPool.commonPool();

        MultiFilesReader multiFilesReader = new MultiFilesReader(files);

        return pool.invoke(new MultiFilesReaderTask(multiFilesReader, threadNum));
    }

    public static class MultiFilesReaderTask extends RecursiveTask<Map<String, Integer>> {
        MultiFilesReader multiFilesReader;
        int threadNum;

        public MultiFilesReaderTask(MultiFilesReader multiFilesReader, int threadNum) {
            this.multiFilesReader = multiFilesReader;
            this.threadNum = threadNum;
        }

        @Override
        protected Map<String, Integer> compute() {
            Map<String, Integer> result = new HashMap<>();

            if (threadNum > 1) {
                List<ForkJoinTask<Map<String, Integer>>> tasks = new ArrayList<>();
                for (int i = 0; i < threadNum; i++) {
                    tasks.add(new MultiFilesReaderTask(multiFilesReader, 1));
                }

                for (ForkJoinTask<Map<String, Integer>> task : tasks) {
                    task.fork();
                }

                for (ForkJoinTask<Map<String, Integer>> task : tasks) {
                    mergeTaskResultIntoWordCountResult(result, task.join());
                }
            } else {
                String line = null;
                while (true) {
                    try {
                        if ((line = multiFilesReader.readLine()) == null) {
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert line != null;
                    String[] words = line.split(" ");

                    for (String word : words) {
                        result.put(word, result.getOrDefault(word, 0) + 1);
                    }
                }
            }
            return result;
        }
    }

    public static void mergeTaskResultIntoWordCountResult(
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

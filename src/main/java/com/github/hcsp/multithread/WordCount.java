package com.github.hcsp.multithread;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WordCount {
    private final int threadNum;
    private ExecutorService threadPool;
    private final Object lock = new Object();

    public WordCount(int threadNum) {
        this.threadNum = threadNum;
        threadPool = Executors.newFixedThreadPool(threadNum);
    }


    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> files) throws ExecutionException, InterruptedException {
        synchronized (lock) {
            List<Future<Map<String, Integer>>> list = new ArrayList<>();
            Map<String, Integer> finalResult = new ConcurrentHashMap<>();
            for (int i = 0; i < threadNum; ++i) {
                list.add(threadPool.submit(
                        new Worker(averageList(files, threadNum).get(i))));
            }

            for (Future<Map<String, Integer>> future : list
            ) {
                Map<String, Integer> resultFromThread = future.get();
                mergeResultFromThread(resultFromThread, finalResult);
            }
            System.out.println(finalResult);
            threadPool.shutdown();
            return finalResult;
        }
    }

    static class Worker implements Callable<Map<String, Integer>> {
        List<File> files;

        public Worker(List<File> files) {
            this.files = files;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> result = new ConcurrentHashMap<>();
            while (!files.isEmpty()) {
                for (File file : files) {
                    List<String> oneFileToString = Files.readAllLines(file.toPath());
                    for (String content : oneFileToString) {
                        String[] contentToArray = content.split(" ");
                        for (String element : contentToArray) {
                            result.put(element, result.getOrDefault(element, 0) + 1);
                        }
                    }
                }
            }
            return result;
        }
    }

    private void mergeResultFromThread(Map<String, Integer> resultFromThread,
                                       Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromThread.entrySet()) {
            int resultNumber = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), resultNumber);
        }
    }

    private synchronized static List<List<File>> averageList(List<File> files, Integer number) {
        List<List<File>> aPartOfList = new ArrayList<>();
        int remainder = files.size() % number;
        int division = files.size() / number;
        int offset = 0;
        for (int i = 0; i < number; i++) {
            List<File> list;
            if (remainder > 0) {
                list = files.subList(i * division + offset, (i + 1) * division + offset + 1);
                remainder--;
            } else {
                list = files.subList(i * division, (i + 1) * division);
            }
            aPartOfList.add(list);
        }
        return aPartOfList;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, FileNotFoundException {
        List<File> files = new ArrayList<>();
        File file = new File("C:\\Users\\catsme\\IdeaProjects\\multithread-word-count2\\1.txt");
        File file1 = new File("C:\\Users\\catsme\\IdeaProjects\\multithread-word-count2\\2.txt");
        files.add(file);
        files.add(file1);
        WordCount wordCount = new WordCount(2);
        wordCount.count(files);
    }
}

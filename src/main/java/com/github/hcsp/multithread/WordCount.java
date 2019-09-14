package com.github.hcsp.multithread;

import com.sun.deploy.security.SelectableSecurityManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WordCount {
    private final int threadNum;
    private ExecutorService threadPool;

    public WordCount(int threadNum) throws ExecutionException, InterruptedException {
        this.threadNum = threadNum;
        threadPool = Executors.newFixedThreadPool(threadNum);
    }


    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> files) throws ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> list = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();
        for (int i = 0; i < threadNum; ++i) {
            while (files.get(0).exists()) {
                File file = files.get(0);
                list.add(threadPool.submit(() -> {
                    Map<String, Integer> result = new HashMap<>();
                    List<String> oneFileToString = Files.readAllLines(file.toPath());
                    for (String content : oneFileToString) {
                        String[] contentToArray = content.split(" ");
                        for (String element : contentToArray
                        ) {
                            result.put(element, result.getOrDefault(element, 0) + 1);
                        }
                    }
                    files.remove(0);
                    threadPool.shutdown();
                    return result;
                }));
            }
            break;
        }
        for (Future<Map<String, Integer>> future : list
        ) {
            Map<String, Integer> resultFromThread = future.get();
            mergeResultFromThread(resultFromThread, finalResult);
        }
        System.out.println(finalResult);
        return finalResult;
    }

    private void mergeResultFromThread(Map<String, Integer> resultFromThread,
                                       Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromThread.entrySet()) {
            int resultNumber = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), resultNumber);
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, FileNotFoundException {
        List<File> files = new ArrayList<>();
        File file = new File("C:\\Users\\catsme\\IdeaProjects\\multithread-word-count2\\1.txt");
        File file1 = new File("C:\\Users\\catsme\\IdeaProjects\\multithread-word-count2\\2.txt");
        files.add(file);
        files.add(file1);
        WordCount wordCount = new WordCount(10);
        wordCount.count(files);
    }
}

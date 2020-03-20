package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount1 {
    private static final ReentrantLock lock = new ReentrantLock();
    private static volatile int fileIndexToProcess = 0;
    private static List<Map<String, Integer>> finalResult = Collections.synchronizedList(new ArrayList<>());
    private static volatile BufferedReader reader;
    private static volatile AtomicInteger completed = new AtomicInteger(0);

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        final File firstFile = files.remove(0);
        reader = new BufferedReader(new FileReader(firstFile));
        for (int i = 0; i < threadNum; i++) {
            final FileCounting fileCounting = new FileCounting(reader, files, finalResult, completed);
            fileCounting.start();
        }
        while (true) {
            if (completed.intValue() == threadNum) {
                reader.close();
                break;
            }
        }
        return mergeFinalResult(finalResult);
    }

    private static Map<String, Integer> mergeFinalResult(List<Map<String, Integer>> finalResult) {
        Map<String, Integer> results = new HashMap<>();
        for (Map<String, Integer> result : finalResult
        ) {
            putAndAdd(result, results);
        }
        return results;
    }

    private static void putAndAdd(Map<String, Integer> result, Map<String, Integer> results) {
        result.forEach((key, value) -> results.put(key, results.getOrDefault(key, 0) + value));
    }


    static boolean hasFileToRead(List<File> files, ThreadLocal<Integer> processedIndex) {
        lock.lock();
        try {
            File target;
            if (fileIndexToProcessHasMoved(processedIndex.get())) {
                updateProcessedIndex(processedIndex);
                return true;
            } else if (files.size() > 0) {
                target = files.remove(0);
                reader = new BufferedReader(new FileReader(target));
                fileIndexToProcess++;
                updateProcessedIndex(processedIndex);
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return false;
    }

    private static void updateProcessedIndex(ThreadLocal<Integer> processedIndex) {
        processedIndex.set(fileIndexToProcess);
    }

    private static boolean fileIndexToProcessHasMoved(Integer processedIndex) {
        return fileIndexToProcess != processedIndex;
    }

    static Map<String, Integer> countSingleLine(String line) {
        HashMap<String, Integer> result = new HashMap<>();
        String[] words = line.split(" ");
        for (String word : words
        ) {
            result.put(word, result.getOrDefault(word, 0) + 1);
        }
        return result;
    }


    public static BufferedReader getReader() {
        return reader;
    }
}

package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        List<Processor> processorList = new ArrayList<>();
        List<Map<String, Integer>> oneFileCountList = new ArrayList<>();
        for (File file : files) {
            Processor thread = new Processor(file);
            processorList.add(thread);
            thread.start();
        }
        for (Processor processor : processorList) {
            processor.join();
            oneFileCountList.add(processor.getFileCount());
        }
        return mergeAllFileWordCountResult(oneFileCountList);
    }

    private static Map<String, Integer> mergeAllFileWordCountResult(List<Map<String, Integer>> oneFileCountList) {
        Map<String, Integer> allFileCountResult = new HashMap<>();
        for (Map<String, Integer> oneFileCount : oneFileCountList) {
            for (Map.Entry<String, Integer> entry : oneFileCount.entrySet()) {
                allFileCountResult.put(entry.getKey(), allFileCountResult.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return allFileCountResult;
    }
}

class Processor extends Thread {
    private File file;
    private Map<String, Integer> fileCount;

    Processor(File file) {
        this.file = file;
        this.fileCount = new HashMap<>();
    }

    Map<String, Integer> getFileCount() {
        return fileCount;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            fileCount = countWordInFile(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Integer> countWordInFile(BufferedReader reader) throws IOException {
        Map<String, Integer> wordCount = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
        return wordCount;
    }
}

package com.github.hcsp.multithread;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCountTask {

    private void wordCount(File file, Map<String, Integer> wordCount) throws IOException {
        LineIterator iterator = FileUtils.lineIterator(file, "UTf-8");
        while (iterator.hasNext()) {
            String[] words = iterator.nextLine().split(" ");
            for (String word : words) {
//                System.out.println("线程：" + Thread.currentThread().getName() + "：" + "单词：" + word + "文件：" + file.getName());
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
    }

    public Map<String, Integer> task(File file) {
        Map<String, Integer> wordCount = new ConcurrentHashMap<>();
        try {
            wordCount(file, wordCount);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return wordCount;
    }

    public Map<String, Integer> task(File file, AtomicInteger fileCount) {
        Map<String, Integer> wordCount = new ConcurrentHashMap<>();
        try {
            wordCount(file, wordCount);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            fileCount.decrementAndGet();
        }
        return wordCount;
    }
}

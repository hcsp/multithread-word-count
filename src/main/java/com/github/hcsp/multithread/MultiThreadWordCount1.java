package com.github.hcsp.multithread;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount1 {
    static CountDownLatch latch = null;
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Integer>> wordCountMapList = new ArrayList<>();
        latch = new CountDownLatch(threadNum);
        List<List<File>> fileGroup = Lists.partition(files, threadNum);
        for (List<File> fileList : fileGroup) {
            new Thread(new ReadFileWithCountLatch(fileList, latch, wordCountMapList)).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Map<String, Integer> wordCountMap : wordCountMapList) {
            ReadFileUtils.mergeMap(result, wordCountMap);
        }
        return result;
    }

    public static class ReadFileWithCountLatch implements Runnable {
        private CountDownLatch latch;
        private Map<String, Integer> map = new HashMap<>();
        private List<Map<String, Integer>> maps;
        private List<File> files;

        public List<Map<String, Integer>> getMaps() {
            return maps;
        }

        public ReadFileWithCountLatch(List<File> files, CountDownLatch latch,
                                      List<Map<String, Integer>> mapList) {
            this.files = files;
            this.latch = latch;
            maps = mapList;
        }

        @Override
        public void run() {
            for (File file : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    ReadFileUtils.readWordsToMap(map, reader);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("file not found: " + file.getName());
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                } finally {
                    latch.countDown();
                }
            }
            maps.add(map);
        }
    }

    public static void main(String[] args) {
        System.out.println(count(10, new ReadFileUtils().createTestFiles()));
    }

}

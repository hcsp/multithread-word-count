package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MultiThreadWordCount1 {
    private static ConcurrentHashMap<String, Integer> filesResult = new ConcurrentHashMap<String, Integer>();

    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {
        for (File file : files) {
            BufferedReader reader = Files.newBufferedReader(file.toPath());
            createThreadPools(threadNum, reader);
        }
        return filesResult;
    }

    private static void createThreadPools(int threadNum, BufferedReader reader) {
        for (int i = 0; i < threadNum; i++) {
            CountWords countWords = new CountWords(filesResult, reader);
            countWords.run();
        }
    }
}

class CountWords implements Runnable {
    ConcurrentHashMap<String, Integer> filesResult;
    BufferedReader reader;

    CountWords(ConcurrentHashMap<String, Integer> filesResult, BufferedReader reader) {
        this.filesResult = filesResult;
        this.reader = reader;
    }

    @Override
    public void run() {
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) == null) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            String[] words = line.split(" ");
            for (String word : words) {
                filesResult.put(word, filesResult.getOrDefault(word, 0) + 1);
            }
        }
    }
}

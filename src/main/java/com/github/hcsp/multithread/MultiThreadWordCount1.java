package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount1 {

    private static final ConcurrentHashMap<String, Integer> analyse = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {
        for (File file : files) {
            BufferedReader bufferedReader = Files.newBufferedReader(file.toPath());
            for (int i = 0; i < threadNum; i++) {
                CountWord countWord = new CountWord(analyse, bufferedReader);
                countWord.run();
            }
        }
        return analyse;
    }

}

class CountWord implements Runnable {

    private final ConcurrentHashMap<String, Integer> analyse;
    private final BufferedReader bufferedReader;

    CountWord(ConcurrentHashMap<String, Integer> analyse, BufferedReader bufferedReader) {
        this.analyse = analyse;
        this.bufferedReader = bufferedReader;
    }

    @Override
    public void run() {
        String line;
        while (true) {
            try {
                if ((line = bufferedReader.readLine()) == null) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            String[] words = line.split(" ");
            for (String word : words) {
                analyse.put(word, (analyse.getOrDefault(word, 0) + 1));
            }
        }
    }
}

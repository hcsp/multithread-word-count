package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount1 extends Thread {
    public static final Object lock = new Object();
    public static final Map<String, Integer> resultMap = new HashMap<>();

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        int fileLength = files.size();
        int threadExecuteFileNum = (int) Math.floor((double) fileLength / threadNum);

        for (int i = 0; i < threadNum; ++i) {
            int startIndex = i * threadExecuteFileNum;
            int endIndex = i == threadNum - 1 ? fileLength : (i + 1) * threadExecuteFileNum;
            List<File> fileList = files.subList(startIndex, endIndex);

            new Thread(() -> {
                for (File file : fileList) {
                    insertContentToMapFromFile(file);
                }
            }).start();
        }

        return resultMap;
    }

    private static void insertContentToMapFromFile(File file) {
        synchronized (lock) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    String[] wordList = line.split("\\s+");
                    for (String word : wordList) {
                        if (resultMap.containsKey(word)) {
                            resultMap.put(word, resultMap.get(word) + 1);
                        } else {
                            resultMap.put(word, 1);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

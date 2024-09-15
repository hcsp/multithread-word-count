package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class FileDetector extends Thread {
    List<Map<String, Integer>> result;
    Object object;
    File file;
    CountDownLatch countDownLatch;

    public FileDetector(List<Map<String, Integer>> countResults, Object object, File file) {
        result = countResults;
        this.object = object;
        this.file = file;
    }

    public FileDetector(List<Map<String, Integer>> result, Object object, File file, CountDownLatch countDownLatch) {
        this.result = result;
        this.object = object;
        this.file = file;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        System.out.println("子线程：" + Thread.currentThread().getName() + "执行");
        Map<String, Integer> myCountResult = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file));) {
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split(" ");
                for (String word : split) {
                    if (myCountResult.containsKey(word)) {
                        myCountResult.put(word, myCountResult.get(word) + 1);
                    } else {
                        myCountResult.put(word, 1);
                    }
                }
            }
            result.add(myCountResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

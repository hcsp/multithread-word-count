package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @author kwer
 * @date 2020/5/5 18:56
 */
public class WordCountUtil {
    public static Map<String, Integer> count(File file) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, Integer> countResult = new HashMap<>();
        for (String line : lines) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                countResult.put(word, countResult.getOrDefault(word, 0) + 1);
            }
        }
        return countResult;
    }

    public static Map<String, Integer> merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        HashSet<String> wordSet = new HashSet<>(map1.keySet());
        Map<String, Integer> finalResult = new HashMap<>();
        wordSet.addAll(map2.keySet());
        for (String word : wordSet) {
            finalResult.put(word, map1.getOrDefault(word, 0) + map2.getOrDefault(word, 0));
        }
        return finalResult;
    }
   public static class WordCountTask implements Callable<List<Map<String, Integer>>> {
        List<File> files;

        public WordCountTask(List<File> files) {
            this.files = files;
        }

        @Override
        public List<Map<String, Integer>> call() throws Exception {
            List<Map<String, Integer>> countResults = new ArrayList<>(files.size());
            for (File file : files) {
                System.out.println(Thread.currentThread().getName() + " 执行文件解析……");
                countResults.add(WordCountUtil.count(file));
            }
            return countResults;
        }
    }

    public static void main(String[] args) {
        System.out.println(15%10);
        System.out.println(100%10);
    }
}

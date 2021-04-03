package com.github.hcsp.multithread;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultiThreadWordCount5 {

    private static Multiset<String> multiset = HashMultiset.create();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        files.parallelStream().forEach(MultiThreadWordCount5::countOneFile);
        return multiset.entrySet().stream().collect(Collectors.toMap(Multiset.Entry::getElement, Multiset.Entry::getCount));
    }

    public static void countOneFile(File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] s = line.split(" ");
                Arrays.stream(s).forEach(MultiThreadWordCount5::add);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static synchronized void add(String w) {
        multiset.add(w);
    }
}

package com.github.hcsp.multithread;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(threadNum));
        Map<String, Long> result;
        try (Stream<String> lines = files.parallelStream().flatMap(MultiThreadWordCount1::apply)) {
            result = lines.flatMap(line -> Arrays.stream(line.split(" "))).collect(Collectors.toList()).
                    parallelStream().collect(Collectors.groupingBy(String::toString, counting()));
        }
        return Maps.transformValues(result, Long::intValue);
    }

    private static Stream<String> apply(File file) {
        try {
            return Files.readLines(file, Charsets.UTF_8).stream().map(stringList -> stringList.split(" ")).
                    flatMap(Arrays::stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

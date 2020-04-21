package com.github.hcsp.multithread;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * java8 并发流完成Word Count
 */
public class MultiThreadWordCount4 {
    public static void main(String[] args) {
        List<File> files = Arrays.asList(
                new File("src/main/java/com/github/hcsp/multithread/1.txt"),
                new File("src/main/java/com/github/hcsp/multithread/2.txt"),
                new File("src/main/java/com/github/hcsp/multithread/3.txt")
        );
        Map<String, Integer> resultMap = files.parallelStream()
                .map(MultiThreadWordCount5::count)
                .reduce(new HashMap<>(), MultiThreadWordCount5::merge);
        System.out.println(resultMap);
    }
}

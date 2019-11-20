package com.github.hcsp.multithread;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author BirdSnail
 * @date 2019/11/19
 */
public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<File> files = Arrays.asList(new File("1.txt"),
                new File("2.txt"));

        System.out.println(MultiThreadWordCount1.count(1, files));
        System.out.println(MultiThreadWordCount2.count(3, files));
        System.out.println(MultiThreadWordCount3.count(3, files));
        System.out.println(MultiThreadWordCount4.count(3, files));
        System.out.println(MultiThreadWordCount5.count(1, files));
    }
}

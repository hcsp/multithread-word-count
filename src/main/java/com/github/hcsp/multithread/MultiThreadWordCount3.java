package com.github.hcsp.multithread;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import static com.github.hcsp.multithread.MultiThreadWordCount5.count;
import static com.github.hcsp.multithread.MultiThreadWordCount5.merge;

/**
 * 使用fork/join完成Word Count
 */
public class MultiThreadWordCount3 {
    public static void main(String[] args) {
        List<File> files = Arrays.asList(
                new File("src/main/java/com/github/hcsp/multithread/1.txt"),
                new File("src/main/java/com/github/hcsp/multithread/2.txt"),
                new File("src/main/java/com/github/hcsp/multithread/3.txt")
        );
        WordCount wordCount = new WordCount(files);
        System.out.println(wordCount.compute());
//        ForkJoinPool threadPool = new ForkJoinPool();
//        Map<String, Integer> resultMap = new HashMap<>();
//        for (File file : files) {
//            try {
//                resultMap = merge(resultMap, threadPool.submit(() -> count(file)).get());
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
//        threadPool.shutdown();
//        System.out.println(resultMap);
    }

    static class WordCount extends RecursiveTask<Map<String, Integer>> {
        List<File> files;

        public WordCount(List<File> files) {
            this.files = files;
        }

        @Override
        protected Map<String, Integer> compute() {
            if(files.isEmpty()){
                return Collections.emptyMap();
            }
            Map<String, Integer> map = count(files.get(0));
            Map<String, Integer> restMap = new WordCount(files.subList(1, files.size())).compute();
            return merge(map, restMap);
        }

    }
}

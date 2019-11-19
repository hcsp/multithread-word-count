package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MultiThreadWordCount1 {

    public static Map<String, Integer> result = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {

        // 创建并开启多个线程
        List<WordCountTask> tasks = IntStream.rangeClosed(1, threadNum)
                .mapToObj(i -> createWordCountTask(files)).collect(Collectors.toList());

        tasks.forEach(Thread::start);

        // 多个线程join
        tasks.forEach(task-> {
            try {
                task.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        // 结果加总
        tasks.stream().map(WordCountTask::get).forEach(result::putAll);
        return result;
    }

    public static WordCountTask createWordCountTask(List<File> files){
        return new WordCountTask(files);
    }

    public static class WordCountTask extends Thread implements WordCountSupplier {
        public List<File> files;
        public Map<String, Integer> result = new ConcurrentHashMap<>();

        public WordCountTask(List<File> files) {
            this.files = files;
        }

        @Override
        public void run(){
            List<String> fileContent = new ArrayList<>();

            for (File file : files) {
                try {
                    List<String> lines = Files.readAllLines(file.toPath());
                    fileContent.addAll(lines);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            result = fileContent.stream().map(w -> w.split("\\s+"))
                    .flatMap(Arrays::stream)
                    .collect(Collectors.toMap(w -> w, w -> 1, Integer::sum));
        }
        @Override
        public Map<String, Integer> get(){
            return this.result;
        }
    }

    public interface WordCountSupplier {
        Map<String, Integer> get();
    }

}


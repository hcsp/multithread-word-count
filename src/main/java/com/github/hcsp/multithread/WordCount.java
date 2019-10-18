package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WordCount {

    public final int threadNum;

    public WordCount(int threadNum) {
        this.threadNum = threadNum;
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> file) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> list = new ArrayList<>();

        file.forEach(x -> {
            Future<Map<String, Integer>> submit = executorService.submit(() -> countFileWordCount(x));
            list.add(submit);
        });

        Map<String, Integer> map = new HashMap<>();
        list.forEach(x -> {
            try {
                map.putAll(x.get());

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        return map;
    }

    private Map<String, Integer> countFileWordCount(File file) throws IOException {
        return Files.newBufferedReader(file.toPath()).lines()
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.reducing(0, str -> 1, Integer::sum)));

    }
}

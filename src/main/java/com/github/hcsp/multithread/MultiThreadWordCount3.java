package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        MultiThreadWordCount2.result.clear();
        // 创建包含Runtime.getRuntime().availableProcessors()返回值作为个数的并行线程的ForkJoinPool
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        // 提交可分解的PrintTask任务
        forkJoinPool.submit(new WordCounter(files));

        //阻塞当前线程直到 ForkJoinPool 中所有的任务都执行结束
        forkJoinPool.awaitTermination(5, TimeUnit.SECONDS);

        // 关闭线程池
        forkJoinPool.shutdown();
        return MultiThreadWordCount2.result;
    }

    private static class WordCounter extends RecursiveAction {

        private List<File> files;

        public WordCounter(List<File> files) {
            this.files = files;
        }

        @Override
        protected void compute() {
            if (files.size() == 1) {
                MultiThreadWordCount2.countOneFile(files.get(0));
            } else {
                this.files.forEach(file -> {
                    WordCounter wordCounter = new WordCounter(new ArrayList<File>(1) {{
                        add(file);
                    }});
                    wordCounter.fork();
                });
            }
        }
    }
}



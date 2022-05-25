package com.github.hcsp.multithread;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {

        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);

        CountRecursiveTask task = new CountRecursiveTask(files);

        return forkJoinPool.invoke(task);
    }


    static class CountRecursiveTask extends RecursiveTask<Map<String, Integer>> {

        private List<File> files;

        CountRecursiveTask(List<File> files) {
            this.files = files;
        }

        @Override
        protected Map<String, Integer> compute() {

            HashMap<String, Integer> map = new HashMap<>();
            int fileAmt = files.size();

            // 列表中只有一个文件
            if (fileAmt == 1) {
                readAndCount(files.get(0), map);
            } else {

                int mid = fileAmt / 2;
                List<File> leftFiles = this.files.subList(0, mid);
                List<File> rightFiles = this.files.subList(mid, fileAmt);

                CountRecursiveTask leftTask = new CountRecursiveTask(leftFiles);
                CountRecursiveTask rightTask = new CountRecursiveTask(rightFiles);

                invokeAll(leftTask, rightTask);

                Map<String, Integer> leftMap = leftTask.join();
                Map<String, Integer> rightMap = rightTask.join();

                ArrayList<Map<String, Integer>> srcMaps = new ArrayList<>();
                srcMaps.add(leftMap);
                srcMaps.add(rightMap);

                // 合并map
                mapsMerge(srcMaps, map);

            }

            return map;
        }

        private void mapsMerge(ArrayList<Map<String, Integer>> srcMaps, HashMap<String, Integer> map) {

            for (Map<String, Integer> srcMap : srcMaps) {
                for (String word : srcMap.keySet()) {
                    Integer cnt = map.getOrDefault(word, 0);

                    map.put(word, cnt + srcMap.get(word));
                }
            }
        }


        /**
         * 从文件中读取数据并进行单词统计
         *
         * @param file 文件句柄
         * @return 返回统计结果
         */
        private void readAndCount(File file, Map<String, Integer> map) {

            try(
                    InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            ) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.trim();
                    String[] words = line.split("\\s+");

                    for (String word: words) {
                        Integer cnt = map.getOrDefault(word, 0);

                        map.put(word, cnt + 1);
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}

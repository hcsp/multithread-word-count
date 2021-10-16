package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

public class MultiThreadWordCount2 {
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException, FileNotFoundException {
        MultiFilesReader multiFilesReader = new MultiFilesReader(files);

        List<Map<String, Integer>> wordCountList = new CopyOnWriteArrayList<>();
        List<WordCountCollector> wordCountCollectorList = new ArrayList<>();

        for (int i = 0; i < threadNum; i++) {
            wordCountCollectorList.add(new WordCountCollector(multiFilesReader, wordCountList));
        }

        for (WordCountCollector wordCountCollector : wordCountCollectorList) {
            wordCountCollector.start();
        }

        for (WordCountCollector wordCountCollector : wordCountCollectorList) {
            wordCountCollector.join();
        }

        Map<String, Integer> wordCountResult = new HashMap<>();

        for (Map<String, Integer> wordCount : wordCountList) {
            mergeThreadResultIntoWordCountResult(wordCountResult, wordCount);
        }

        return wordCountResult;
    }

    public static void mergeThreadResultIntoWordCountResult(
            Map<String, Integer> wordCountResult,
            Map<String, Integer> workerResult) {
        for (Map.Entry<String, Integer> entry : workerResult.entrySet()) {
            String word = entry.getKey();
            wordCountResult.put(word, wordCountResult.getOrDefault(word, 0) + entry.getValue());
        }
    }

    public static class WordCountCollector extends Thread {
        MultiFilesReader multiFilesReader;
        List<Map<String, Integer>> wordCountList;

        public WordCountCollector(MultiFilesReader multiFilesReader, List<Map<String, Integer>> wordCountList) {
            this.multiFilesReader = multiFilesReader;
            this.wordCountList = wordCountList;
        }

        @Override
        public void run() {
            String line = null;
            Map<String, Integer> result = new HashMap<>();
            while (true) {
                try {
                    if ((line = multiFilesReader.readLine()) == null) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert line != null;
                String[] words = line.split(" ");

                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            wordCountList.add(result);
        }

    }

    public static class MultiFilesReader {
        Queue<File> files = new LinkedList<>();
        private BufferedReader reader;

        public MultiFilesReader(List<File> files) throws FileNotFoundException {
            this.files.addAll(files);
            pollReader();
        }

        private synchronized void pollReader() throws FileNotFoundException {
            File file = files.poll();
            if (file == null) {
                reader = null;
            } else {
                reader = new BufferedReader(new FileReader(file));
            }
        }

        public synchronized String readLine() throws IOException {
            if (reader == null) {
                return null;
            }
            String line = reader.readLine();
            if (line == null) {
                pollReader();
                return readLine();
            }
            return line;
        }
    }
}

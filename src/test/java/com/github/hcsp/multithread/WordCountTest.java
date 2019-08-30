package com.github.hcsp.multithread;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WordCountTest {
    @Test
    public void test() throws Exception {
        List<String> aToZ =
                IntStream.range(0, 26)
                        .mapToObj(i -> String.valueOf((char) ('a' + i)))
                        .collect(Collectors.toList());
        Integer count = new Random().nextInt(10) + 100;
        List<String> randomStrings = new ArrayList<>();
        for (String letter : aToZ) {
            randomStrings.addAll(Collections.nCopies(count, letter));
        }

        Collections.shuffle(randomStrings);

        File file = writeToTempFile(count, randomStrings);

        WordCount wordCount = new WordCount(10);
        Map<String, Integer> countResult = wordCount.count(file);
        System.out.println(countResult);
        Assertions.assertTrue(countResult.values().stream().allMatch(count::equals));
    }

    private File writeToTempFile(int count, List<String> randomStrings) throws IOException {
        File file = File.createTempFile("tmp", "");
        List<String> lines =
                Lists.partition(randomStrings, 10).stream()
                        .map(list -> String.join(" ", list))
                        .collect(Collectors.toList());
        Files.write(file.toPath(), lines);
        return file;
    }
}

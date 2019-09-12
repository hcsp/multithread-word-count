package com.github.hcsp.multithread;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WordCountTest {
    private static final int REPEAT = 100;

    @Test
    public void test() throws Exception {
        // 2600 lines of random aligned strings
        List<String> randomStrings =
                IntStream.range(0, 26)
                        .mapToObj(i -> String.valueOf((char) ('a' + i)))
                        .flatMap(letter -> Collections.nCopies(REPEAT, letter).stream())
                        .collect(Collectors.toList());

        Collections.shuffle(randomStrings);

        // Write to 10 files
        List<File> files =
                Lists.partition(randomStrings, 26 * REPEAT / 10).stream()
                        .map(this::writeToTempFile)
                        .collect(Collectors.toList());
        WordCount wordCount = new WordCount(10);
        Map<String, Integer> countResult = wordCount.count(files);
        System.out.println(countResult);
        Assertions.assertTrue(countResult.values().stream().allMatch(n -> n == REPEAT));
    }

    private File writeToTempFile(List<String> oneCharacterLines) {
        try {
            File file = File.createTempFile("tmp", "");

            List<String> lines =
                    Lists.partition(oneCharacterLines, 10).stream()
                            .map(list -> String.join(" ", list))
                            .collect(Collectors.toList());
            Files.write(file.toPath(), lines);
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

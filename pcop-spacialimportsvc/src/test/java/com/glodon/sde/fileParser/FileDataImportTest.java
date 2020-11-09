package com.glodon.sde.fileParser;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class FileDataImportTest {

//    @Test
    public void createDirAndFile() throws IOException {

        String tempPath = "./testPath/abc.xml";

        Path path = Files.createDirectories(Paths.get(tempPath));

//        Files.createFile(path);
        System.out.println(path.toAbsolutePath());


        System.out.println(path.resolve("aa.txt").toAbsolutePath());

    }

}
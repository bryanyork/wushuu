package com.wushuu;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;

public class Demo {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello World");
        Path dir = Paths.get("/opt/wolftau");
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException
            {
                System.out.println(file);
            return FileVisitResult.CONTINUE;
            }
            });
    }
}

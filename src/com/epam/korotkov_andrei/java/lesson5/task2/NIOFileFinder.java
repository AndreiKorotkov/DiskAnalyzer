package com.epam.korotkov_andrei.java.lesson5.task2;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class NIOFileFinder extends SimpleFileVisitor<Path> implements Serializable {

    static Scanner scanner = new Scanner(System.in);
    public int count = 0;
    public int dirCount = 0;

    public static int getInt() {
        System.out.println("Enter number 1 to search for 5 biggest files, 2 to count average file size, 3 to show numbers " +
                "of files and directories by first letter");

        int num;
        if (scanner.hasNextInt()) {
            num = scanner.nextInt();
        } else {
            System.out.println("Error. Please enter a number");
            scanner.next();
            num = getInt();
        }
        return num;
    }

    public static String firstLetter() {
        System.out.println("Enter a letter :");
        char soughtLetter;
        String Letter = null;
        if (scanner.hasNext()) {
            soughtLetter = scanner.next().charAt(0);
            Letter = "" + soughtLetter + "";
        }
        return Letter;
    }

    public void searchBiggestFiles(String directoryPath) {
        Path pathSource = Paths.get(directoryPath);
        try (java.util.stream.Stream<Path> streamPath = Files.walk(pathSource)) {
            streamPath
                    .sorted(Comparator.comparingLong((Path path) -> path.toFile().length())
                            .reversed())
                    .limit(5)
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (java.util.stream.Stream<Path> streamPath = Files.walk(pathSource)) {
            List<Object> biggestFileList = streamPath
                    .sorted(Comparator.comparingLong((Path path) -> path.toFile().length())
                            .reversed())
                    .limit(5)
                    .collect(Collectors.toList());
            ByteArrayOutputStream biggestFilesOutStream = new ByteArrayOutputStream();
            FileOutputStream biggestFilesFOS = new FileOutputStream("biggestFiles.txt");
            biggestFilesOutStream.close();
            ObjectOutputStream listOos = new ObjectOutputStream(biggestFilesFOS);
            for (int i = 0; i < biggestFileList.size(); i++) {
                listOos.writeObject(biggestFileList.get(i).toString());
            }
            listOos.flush();
            listOos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void countAverageFileSize(String directoryPath) {
        Path pathSource = Paths.get(directoryPath);
        final AtomicLong size = new AtomicLong(0);
        long averageSize = 0;
        try {
            Files.walkFileTree(pathSource, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    size.addAndGet(attrs.size());
                    count++;
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {

                    return FileVisitResult.SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

                    return FileVisitResult.CONTINUE;
                }
            });
            averageSize = (size.get() / count);
            System.out.println(averageSize);
            ByteArrayOutputStream averageSizeOutStream = new ByteArrayOutputStream();
            FileOutputStream averageSizeFOS = new FileOutputStream("averageSize.txt");
            averageSizeOutStream.close();
            ObjectOutputStream listOos = new ObjectOutputStream(averageSizeFOS);
            listOos.writeObject(averageSize);
            listOos.flush();
            listOos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sortByNames(String directoryPath) {
        Path pathSource = Paths.get(directoryPath);
        String letter = firstLetter();
        try {
            Files.walkFileTree(pathSource, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.getFileName().toString().startsWith(letter)) {
                        count++;
                    }
                    if (file.getParent().getFileName().toString().startsWith(letter)) {
                        dirCount++;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
            String fileCount = "Number of files starting with " + letter + " is " + count;
            String directoryCount = "Number of directories starting with " + letter + " is " + dirCount;
            System.out.println(fileCount);
            System.out.println(directoryCount);
            ByteArrayOutputStream sortByLettesOutStream = new ByteArrayOutputStream();
            FileOutputStream sortByLetterFOS = new FileOutputStream("filesAndDirectoriesByFirstLettercount.txt");
            sortByLettesOutStream.close();
            ObjectOutputStream listOos = new ObjectOutputStream(sortByLetterFOS);
            listOos.writeObject(fileCount);
            listOos.writeObject(directoryCount);
            listOos.flush();
            listOos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        System.out.println("Input directory path: ");
        Scanner directoryScanner = new Scanner(System.in);
        String directoryPath = directoryScanner.nextLine();
        File file = new File(directoryPath);
        boolean exists = file.exists();

        NIOFileFinder task2 = new NIOFileFinder();

        if (exists) {
            int num1 = getInt();
            switch (num1) {
                case 1:
                    task2.searchBiggestFiles(directoryPath);
                    break;
                case 2:
                    task2.countAverageFileSize(directoryPath);
                    break;
                case 3:
                    task2.sortByNames(directoryPath);
                    break;
            }
        } else {
            System.out.println("Wrong directory path");
        }
    }
}

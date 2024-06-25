package com.kai.common.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ImageUtils {
    public static int SIZE_SM = 400;
    public static int SIZE_MD = 800;
    public static int SIZE_LARGE = 1200;

    public static void generateSmThumbnail(String sourceImagePath, String targeImagePath) {
        generateThumnail(sourceImagePath, targeImagePath, SIZE_SM, SIZE_SM);
    }

    public static void generateMdThumbnail(String sourceImagePath, String targeImagePath) {
        generateThumnail(sourceImagePath, targeImagePath, SIZE_MD, SIZE_MD);
    }

    public static void generateThumnail(String sourceImagePath, String targeImagePath, int width, int height) {
        File outputFile = new File(targeImagePath);
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        try {
            Thumbnails.of(new File(sourceImagePath))
                    .size(width, height)
                    .toFile(new File(targeImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        String path = "D:\\Desktop\\server\\static\\image";
        List<File> fileList = Arrays.asList(new File(path).listFiles());
        Thumbnails.fromFiles(fileList).size(SIZE_SM, SIZE_SM).toFiles(Rename.SUFFIX_HYPHEN_THUMBNAIL);
    }
}

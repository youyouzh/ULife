package com.uusama.common.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author zhaohai
 */
public class FileUtil {

    public static Optional<List<String>> readLastNLine(String path, int lineCount) {
        File file = new File(path);
        if (!file.exists() || !file.canRead()) {
            return Optional.empty();
        }

        // 随机读取
        List<String> lineContents = new ArrayList<>();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            long fileLength = randomAccessFile.length();
            if (fileLength == 0) {
                return Optional.empty();
            }
            // 从最后一行开始逐行读取文件内容
            long pos = fileLength - 1;
            while (pos >= 0) {
                pos--;
                randomAccessFile.seek(pos);
                if (randomAccessFile.readByte() == '\n') {
                    String lineContent = randomAccessFile.readLine();
                    // 编码转换
                    lineContents.add(new String(lineContent.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                    if (lineContents.size() >= lineCount) {
                        break;
                    }
                }
            }
            return Optional.of(lineContents);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * 解压zip文件到同目录下，目录去掉.zip后缀
     * @param path zip文件路径
     * @param destFolderPath zip文件解压路径
     * @param charset 字符
     * @throws IOException IO异常
     */
    public static void unzip(Path path, Path destFolderPath, Charset charset) throws IOException {
        try (ZipFile zipFile = new ZipFile(path.toFile(), ZipFile.OPEN_READ, charset)){
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path entryPath = destFolderPath.resolve(entry.getName());
                if (entryPath.normalize().startsWith(destFolderPath.normalize())){
                    if (entry.isDirectory()) {
                        Files.createDirectories(entryPath);
                    } else {
                        Files.createDirectories(entryPath.getParent());
                        try (InputStream in = zipFile.getInputStream(entry)){
                            try (OutputStream out = Files.newOutputStream(entryPath.toFile().toPath())){
                                IOUtils.copy(in, out);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void mkdirIfNeed(String dir) {
        try {
            Files.createDirectories(Paths.get(dir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateSysFileName(String fileExtension) {
        return System.currentTimeMillis() + RandomUtils.nextInt(1, 1000) + "." + fileExtension;
    }

    public static String generateFileUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取解压文件夹路径
     * @param zipFilePath 压缩文件路径
     * @return unzip dir
     */
    public static Path getUnzipPathRoot(Path zipFilePath) {
        String fileBaseName = FilenameUtils.getBaseName(zipFilePath.getFileName().toString());
        return Paths.get(zipFilePath.getParent().toString(), fileBaseName);
    }
}

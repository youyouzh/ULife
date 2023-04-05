package com.uusama.common.io;

import com.uusama.common.lang.Assert;
import com.uusama.common.util.StrUtil;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * NIO相关工具封装，主要针对Channel读写、拷贝等封装
 *
 * @author uusama
 */
public class NioUtil {

    /**
     * 默认缓存大小 8192
     */
    public static final int DEFAULT_BUFFER_SIZE = 2 << 12;
    /**
     * 默认中等缓存大小 16384
     */
    public static final int DEFAULT_MIDDLE_BUFFER_SIZE = 2 << 13;
    /**
     * 默认大缓存大小 32768
     */
    public static final int DEFAULT_LARGE_BUFFER_SIZE = 2 << 14;

    /**
     * 数据流末尾
     */
    public static final int EOF = -1;

    /**
     * 拷贝文件Channel，使用NIO，拷贝后不会关闭channel
     *
     * @param inChannel  {@link FileChannel}
     * @param outChannel {@link FileChannel}
     * @return 拷贝的字节数
     * @throws IOException IO异常
     * @since 5.5.3
     */
    public static long copy(FileChannel inChannel, FileChannel outChannel) throws IOException {
        Assert.notNull(inChannel, "In channel is null!");
        Assert.notNull(outChannel, "Out channel is null!");

        return copySafely(inChannel, outChannel);
    }

    /**
     * 文件拷贝实现
     *
     * <pre>
     * FileChannel#transferTo 或 FileChannel#transferFrom 的实现是平台相关的，需要确保低版本平台的兼容性
     * 例如 android 7以下平台在使用 ZipInputStream 解压文件的过程中，
     * 通过 FileChannel#transferFrom 传输到文件时，其返回值可能小于 totalBytes，不处理将导致文件内容缺失
     *
     * // 错误写法，dstChannel.transferFrom 返回值小于 zipEntry.getSize()，导致解压后文件内容缺失
     * try (InputStream srcStream = zipFile.getInputStream(zipEntry);
     * 		ReadableByteChannel srcChannel = Channels.newChannel(srcStream);
     * 		FileOutputStream fos = new FileOutputStream(saveFile);
     * 		FileChannel dstChannel = fos.getChannel()) {
     * 		dstChannel.transferFrom(srcChannel, 0, zipEntry.getSize());
     *  }
     * </pre>
     *
     * @param inChannel  输入通道
     * @param outChannel 输出通道
     * @return 输入通道的字节数
     * @throws IOException 发生IO错误
     * @link http://androidxref.com/6.0.1_r10/xref/libcore/luni/src/main/java/java/nio/FileChannelImpl.java
     * @link http://androidxref.com/7.0.0_r1/xref/libcore/ojluni/src/main/java/sun/nio/ch/FileChannelImpl.java
     * @link http://androidxref.com/7.0.0_r1/xref/libcore/ojluni/src/main/native/FileChannelImpl.c
     * @author z8g
     * @since 5.7.21
     */
    private static long copySafely(FileChannel inChannel, FileChannel outChannel) throws IOException {
        final long totalBytes = inChannel.size();
        for (long pos = 0, remaining = totalBytes; remaining > 0; ) { // 确保文件内容不会缺失
            final long writeBytes = inChannel.transferTo(pos, remaining, outChannel); // 实际传输的字节数
            pos += writeBytes;
            remaining -= writeBytes;
        }
        return totalBytes;
    }

    /**
     * 从FileChannel中读取UTF-8编码内容
     *
     * @param fileChannel 文件管道
     * @return 内容
     * @throws IOException IO异常
     */
    public static String readUtf8(FileChannel fileChannel) throws IOException {
        return read(fileChannel, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 从FileChannel中读取内容，读取完毕后并不关闭Channel
     *
     * @param fileChannel 文件管道
     * @param charsetName 字符集
     * @return 内容
     * @throws IOException IO异常
     */
    public static String read(FileChannel fileChannel, String charsetName) throws IOException {
        return read(fileChannel, CharsetUtil.charset(charsetName));
    }

    /**
     * 从FileChannel中读取内容
     *
     * @param fileChannel 文件管道
     * @param charset     字符集
     * @return 内容
     * @throws IOException IO异常
     */
    public static String read(FileChannel fileChannel, Charset charset) throws IOException {
        MappedByteBuffer buffer;
        buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size()).load();
        return StrUtil.str(buffer, charset);
    }

    /**
     * 关闭<br>
     * 关闭失败不会抛出异常
     *
     * @param closeable 被关闭的对象
     */
    public static void close(AutoCloseable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
            }
        }
    }
}

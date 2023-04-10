package com.uusama.common.io;

import com.uusama.common.lang.Assert;
import com.uusama.common.util.StrUtil;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * IO工具类<br>
 * IO工具类只是辅助流的读写，并不负责关闭流。原因是流可能被多次读写，读写关闭后容易造成问题。
 *
 * @author uusama
 */
public class IoUtil extends NioUtil {

    // -------------------------------------------------------------------------------------- Copy start

    /**
     * 执行拷贝，如果限制最大长度，则按照最大长度读取，否则一直读取直到遇到-1
     *
     * @param source           {@link InputStream}
     * @param target           {@link OutputStream}
     * @param bufferSize       缓存大小，< 0 表示默认IoUtil.DEFAULT_BUFFER_SIZE
     * @param count            拷贝总数
     * @param flushEveryBuffer 是否每次写出一个buffer内容就执行flush
     * @return 拷贝总长度
     * @throws IOException IO异常
     */
    public static long copy(InputStream source, OutputStream target, int bufferSize, long count, boolean flushEveryBuffer) throws IOException {
        Assert.notNull(source, "InputStream is null !");
        Assert.notNull(target, "OutputStream is null !");

        bufferSize = bufferSize > 0 ? bufferSize : DEFAULT_BUFFER_SIZE;

        long numToRead = count > 0 ? count : Long.MAX_VALUE;
        long total = 0;
        byte[] buffer = new byte[(int) Math.min(bufferSize, count)];

        int read;
        while (numToRead > 0) {
            read = source.read(buffer, 0, (int) Math.min(bufferSize, numToRead));
            if (read < 0) {
                // 提前读取到末尾
                break;
            }
            target.write(buffer, 0, read);
            if (flushEveryBuffer) {
                target.flush();
            }

            numToRead -= read;
            total += read;
        }

        return total;
    }

    /**
     * 拷贝流，使用默认Buffer大小，拷贝后不关闭流
     *
     * @param in  输入流
     * @param out 输出流
     * @return 传输的byte数
     * @throws IOException IO异常
     */
    public static long copy(InputStream in, OutputStream out) throws IOException {
        return copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in         输入流
     * @param out        输出流
     * @param bufferSize 缓存大小
     * @return 传输的byte数
     * @throws IOException IO异常
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
        return copy(in, out, bufferSize, -1);
    }

    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in         输入流
     * @param out        输出流
     * @param bufferSize 缓存大小
     * @param count      总拷贝长度
     * @return 传输的byte数
     * @throws IOException IO异常
     * @since 5.7.8
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize, long count) throws IOException {
        return copy(in, out, bufferSize, count, false);
    }

    /**
     * 拷贝文件流，使用NIO
     *
     * @param in  输入
     * @param out 输出
     * @return 拷贝的字节数
     * @throws IOException IO异常
     */
    public static long copy(FileInputStream in, FileOutputStream out) throws IOException {
        Assert.notNull(in, "FileInputStream is null!");
        Assert.notNull(out, "FileOutputStream is null!");

        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = in.getChannel();
            outChannel = out.getChannel();
            return copy(inChannel, outChannel);
        } finally {
            close(outChannel);
            close(inChannel);
        }
    }

    // -------------------------------------------------------------------------------------- Copy end

    // -------------------------------------------------------------------------------------- getReader and getWriter start

    /**
     * 获得一个文件读取器，默认使用UTF-8编码
     *
     * @param in 输入流
     * @return BufferedReader对象
     * @since 5.1.6
     */
    public static BufferedReader getUtf8Reader(InputStream in) {
        return getReader(in, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 获得一个文件读取器
     *
     * @param in          输入流
     * @param charsetName 字符集名称
     * @return BufferedReader对象
     * @deprecated 请使用 {@link #getReader(InputStream, Charset)}
     */
    @Deprecated
    public static BufferedReader getReader(InputStream in, String charsetName) {
        return getReader(in, Charset.forName(charsetName));
    }

    /**
     * 获得一个Reader
     *
     * @param in      输入流
     * @param charset 字符集
     * @return BufferedReader对象
     */
    public static BufferedReader getReader(InputStream in, Charset charset) {
        if (null == in) {
            return null;
        }

        InputStreamReader reader;
        if (null == charset) {
            reader = new InputStreamReader(in);
        } else {
            reader = new InputStreamReader(in, charset);
        }

        return new BufferedReader(reader);
    }

    /**
     * 获得{@link BufferedReader}<br>
     * 如果是{@link BufferedReader}强转返回，否则新建。如果提供的Reader为null返回null
     *
     * @param reader 普通Reader，如果为null返回null
     * @return {@link BufferedReader} or null
     * @since 3.0.9
     */
    public static BufferedReader getReader(Reader reader) {
        if (null == reader) {
            return null;
        }

        return (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
    }

    /**
     * 获得一个Writer，默认编码UTF-8
     *
     * @param out 输入流
     * @return OutputStreamWriter对象
     * @since 5.1.6
     */
    public static OutputStreamWriter getUtf8Writer(OutputStream out) {
        return getWriter(out, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 获得一个Writer
     *
     * @param out         输入流
     * @param charsetName 字符集
     * @return OutputStreamWriter对象
     * @deprecated 请使用 {@link #getWriter(OutputStream, Charset)}
     */
    @Deprecated
    public static OutputStreamWriter getWriter(OutputStream out, String charsetName) {
        return getWriter(out, Charset.forName(charsetName));
    }

    /**
     * 获得一个Writer
     *
     * @param out     输入流
     * @param charset 字符集
     * @return OutputStreamWriter对象
     */
    public static OutputStreamWriter getWriter(OutputStream out, Charset charset) {
        if (null == out) {
            return null;
        }

        if (null == charset) {
            return new OutputStreamWriter(out);
        } else {
            return new OutputStreamWriter(out, charset);
        }
    }
    // -------------------------------------------------------------------------------------- getReader and getWriter end

    // -------------------------------------------------------------------------------------- read start

    /**
     * 从流中读取UTF8编码的内容
     *
     * @param in 输入流
     * @return 内容
     * @throws IOException IO异常
     * @since 5.4.4
     */
    public static String readUtf8(InputStream in) throws IOException {
        return read(in, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 从流中读取内容，读取完成后关闭流
     *
     * @param in          输入流
     * @param charsetName 字符集
     * @return 内容
     * @throws IOException IO异常
     * @deprecated 请使用 {@link #read(InputStream, Charset)}
     */
    @Deprecated
    public static String read(InputStream in, String charsetName) throws IOException {
        final FastByteArrayOutputStream out = read(in);
        return StrUtil.isBlank(charsetName) ? out.toString() : out.toString(charsetName);
    }

    /**
     * 从流中读取内容，读取完毕后关闭流
     *
     * @param in      输入流，读取完毕后并不关闭流
     * @param charset 字符集
     * @return 内容
     * @throws IOException IO异常
     */
    public static String read(InputStream in, Charset charset) throws IOException {
        return StrUtil.str(readBytes(in), charset);
    }

    /**
     * 从流中读取内容，读到输出流中，读取完毕后关闭流
     *
     * @param in 输入流
     * @return 输出流
     * @throws IOException IO异常
     */
    public static FastByteArrayOutputStream read(InputStream in) throws IOException {
        return read(in, true);
    }

    /**
     * 从流中读取内容，读到输出流中，读取完毕后可选是否关闭流
     *
     * @param in      输入流
     * @param isClose 读取完毕后是否关闭流
     * @return 输出流
     * @throws IOException IO异常
     * @since 5.5.3
     */
    public static FastByteArrayOutputStream read(InputStream in, boolean isClose) throws IOException {
        final FastByteArrayOutputStream out;
        if (in instanceof FileInputStream) {
            // 文件流的长度是可预见的，此时直接读取效率更高
            try {
                out = new FastByteArrayOutputStream(in.available());
            } catch (IOException e) {
                throw new IOException(e);
            }
        } else {
            out = new FastByteArrayOutputStream();
        }
        try {
            copy(in, out);
        } finally {
            if (isClose) {
                close(in);
            }
        }
        return out;
    }

    /**
     * 从Reader中读取String，读取完毕后关闭Reader
     *
     * @param reader Reader
     * @return String
     * @throws IOException IO异常
     */
    public static String read(Reader reader) throws IOException {
        return read(reader, true);
    }

    /**
     * 从{@link Reader}中读取String
     *
     * @param reader  {@link Reader}
     * @param isClose 是否关闭{@link Reader}
     * @return String
     * @throws IOException IO异常
     */
    public static String read(Reader reader, boolean isClose) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final CharBuffer buffer = CharBuffer.allocate(DEFAULT_BUFFER_SIZE);
        try {
            while (-1 != reader.read(buffer)) {
                builder.append(buffer.flip());
            }
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (isClose) {
                IoUtil.close(reader);
            }
        }
        return builder.toString();
    }

    /**
     * 从流中读取bytes，读取完毕后关闭流
     *
     * @param in {@link InputStream}
     * @return bytes
     * @throws IOException IO异常
     */
    public static byte[] readBytes(InputStream in) throws IOException {
        return readBytes(in, true);
    }

    /**
     * 从流中读取bytes
     *
     * @param in      {@link InputStream}
     * @param isClose 是否关闭输入流
     * @return bytes
     * @throws IOException IO异常
     * @since 5.0.4
     */
    public static byte[] readBytes(InputStream in, boolean isClose) throws IOException {
        if (in instanceof FileInputStream) {
            // 文件流的长度是可预见的，此时直接读取效率更高
            final byte[] result;
            try {
                final int available = in.available();
                result = new byte[available];
                final int readLength = in.read(result);
                if (readLength != available) {
                    throw new IOException(StrUtil.format("File length is [{}] but read [{}]!", available, readLength));
                }
            } catch (IOException e) {
                throw new IOException(e);
            } finally {
                if (isClose) {
                    close(in);
                }
            }
            return result;
        }

        // 未知bytes总量的流
        return read(in, isClose).toByteArray();
    }

    /**
     * 读取指定长度的byte数组，不关闭流
     *
     * @param in     {@link InputStream}，为{@code null}返回{@code null}
     * @param length 长度，小于等于0返回空byte数组
     * @return bytes
     * @throws IOException IO异常
     */
    public static byte[] readBytes(InputStream in, int length) throws IOException {
        if (null == in) {
            return null;
        }
        if (length <= 0) {
            return new byte[0];
        }

        final FastByteArrayOutputStream out = new FastByteArrayOutputStream(length);
        copy(in, out, DEFAULT_BUFFER_SIZE, length);
        return out.toByteArray();
    }

    // -------------------------------------------------------------------------------------- read end

    /**
     * 将byte[]写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param content    写入的内容
     * @throws IOException IO异常
     */
    public static void write(OutputStream out, boolean isCloseOut, byte[] content) throws IOException {
        try {
            out.write(content);
        } finally {
            if (isCloseOut) {
                close(out);
            }
        }
    }

    /**
     * 将多部分内容写到流中，自动转换为UTF-8字符串
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents   写入的内容，调用toString()方法，不包括不会自动换行
     * @throws IOException IO异常
     * @since 3.1.1
     */
    public static void writeUtf8(OutputStream out, boolean isCloseOut, Object... contents) throws IOException {
        write(out, CharsetUtil.CHARSET_UTF_8, isCloseOut, contents);
    }

    /**
     * 将多部分内容写到流中，自动转换为字符串
     *
     * @param out         输出流
     * @param charsetName 写出的内容的字符集
     * @param isCloseOut  写入完毕是否关闭输出流
     * @param contents    写入的内容，调用toString()方法，不包括不会自动换行
     * @throws IOException IO异常
     * @deprecated 请使用 {@link #write(OutputStream, Charset, boolean, Object...)}
     */
    @Deprecated
    public static void write(OutputStream out, String charsetName, boolean isCloseOut, Object... contents) throws IOException {
        write(out, CharsetUtil.charset(charsetName), isCloseOut, contents);
    }

    /**
     * 将多部分内容写到流中，自动转换为字符串
     *
     * @param out        输出流
     * @param charset    写出的内容的字符集
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents   写入的内容，调用toString()方法，不包括不会自动换行
     * @throws IOException IO异常
     * @since 3.0.9
     */
    public static void write(OutputStream out, Charset charset, boolean isCloseOut, Object... contents) throws IOException {
        OutputStreamWriter osw = null;
        try {
            osw = getWriter(out, charset);
            for (Object content : contents) {
                if (content != null) {
                    osw.write(content.toString());
                }
            }
            osw.flush();
        } finally {
            if (isCloseOut) {
                close(osw);
            }
        }
    }

    /**
     * 将多部分内容写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param obj        写入的对象内容
     * @throws IOException IO异常
     * @since 5.3.3
     */
    public static void writeObj(OutputStream out, boolean isCloseOut, Serializable obj) throws IOException {
        writeObjects(out, isCloseOut, obj);
    }

    /**
     * 将多部分内容写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents   写入的内容
     * @throws IOException IO异常
     */
    public static void writeObjects(OutputStream out, boolean isCloseOut, Serializable... contents) throws IOException {
        ObjectOutputStream osw = null;
        try {
            osw = out instanceof ObjectOutputStream ? (ObjectOutputStream) out : new ObjectOutputStream(out);
            for (Object content : contents) {
                if (content != null) {
                    osw.writeObject(content);
                }
            }
            osw.flush();
        } finally {
            if (isCloseOut) {
                close(osw);
            }
        }
    }

    /**
     * 从缓存中刷出数据
     *
     * @param flushable {@link Flushable}
     * @since 4.2.2
     */
    public static void flush(Flushable flushable) {
        if (null != flushable) {
            try {
                flushable.flush();
            } catch (Exception e) {
                // 静默刷出
            }
        }
    }

    /**
     * 关闭<br>
     * 关闭失败不会抛出异常
     *
     * @param closeable 被关闭的对象
     */
    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
            }
        }
    }

    /**
     * 尝试关闭指定对象<br>
     * 判断对象如果实现了{@link AutoCloseable}，则调用之
     *
     * @param obj 可关闭对象
     * @since 4.3.2
     */
    public static void closeIfPossible(Object obj) {
        if (obj instanceof AutoCloseable) {
            close((AutoCloseable) obj);
        }
    }

}

package com.uusama.common.lang;

import com.uusama.common.util.StrUtil;

import java.util.function.Supplier;

/**
 * 断言<br>
 * 断言某些对象或值是否符合规定，否则抛出异常。经常用于做变量检查
 *
 * @author uusama
 */
public class Assert {

    /**
     * 断言是否为真，如果为 {@code false} 抛出给定的异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, IllegalArgumentException::new);
     * </pre>
     *
     * @param <X>        异常类型
     * @param expression 布尔值
     * @param supplier   指定断言不通过时抛出的异常
     * @throws X if expression is {@code false}
     */
    public static <X extends Throwable> void isTrue(boolean expression, Supplier<? extends X> supplier) throws X {
        if (!expression) {
            throw supplier.get();
        }
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, "The value must be greater than zero");
     * </pre>
     *
     * @param expression       布尔值
     * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
     * @param params           参数列表
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue(boolean expression, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        isTrue(expression, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0);
     * </pre>
     *
     * @param expression 布尔值
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue(boolean expression) throws IllegalArgumentException {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出指定类型异常<br>
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     *  Assert.isFalse(i &gt; 0, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>           异常类型
     * @param expression    布尔值
     * @param errorSupplier 指定断言不通过时抛出的异常
     * @throws X if expression is {@code false}
     * @since 5.4.5
     */
    public static <X extends Throwable> void isFalse(boolean expression, Supplier<X> errorSupplier) throws X {
        if (expression) {
            throw errorSupplier.get();
        }
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isFalse(i &lt; 0, "The value must not be negative");
     * </pre>
     *
     * @param expression       布尔值
     * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
     * @param params           参数列表
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isFalse(boolean expression, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        isFalse(expression, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isFalse(i &lt; 0);
     * </pre>
     *
     * @param expression 布尔值
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isFalse(boolean expression) throws IllegalArgumentException {
        isFalse(expression, "[Assertion failed] - this expression must be false");
    }

    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出指定类型异常
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.isNull(value, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>           异常类型
     * @param object        被检查的对象
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @throws X if the object is not {@code null}
     * @since 5.4.5
     */
    public static <X extends Throwable> void isNull(Object object, Supplier<X> errorSupplier) throws X {
        if (null != object) {
            throw errorSupplier.get();
        }
    }

    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.isNull(value, "The value must be null");
     * </pre>
     *
     * @param object           被检查的对象
     * @param errorMsgTemplate 消息模板，变量使用{}表示
     * @param params           参数列表
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull(Object object, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        isNull(object, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.isNull(value);
     * </pre>
     *
     * @param object 被检查对象
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull(Object object) throws IllegalArgumentException {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    // ----------------------------------------------------------------------------------------------------------- Check not null

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出指定类型异常
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.notNull(clazz, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <T>           被检查对象泛型类型
     * @param <X>           异常类型
     * @param object        被检查对象
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 被检查后的对象
     * @throws X if the object is {@code null}
     * @since 5.4.5
     */
    public static <T, X extends Throwable> T notNull(T object, Supplier<X> errorSupplier) throws X {
        if (null == object) {
            throw errorSupplier.get();
        }
        return object;
    }

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link IllegalArgumentException} 异常 Assert that an object is not {@code null} .
     *
     * <pre class="code">
     * Assert.notNull(clazz, "The class must not be null");
     * </pre>
     *
     * @param <T>              被检查对象泛型类型
     * @param object           被检查对象
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 被检查后的对象
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static <T> T notNull(T object, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        return notNull(object, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.notNull(clazz);
     * </pre>
     *
     * @param <T>    被检查对象类型
     * @param object 被检查对象
     * @return 非空对象
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static <T> T notNull(T object) throws IllegalArgumentException {
        return notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    // ----------------------------------------------------------------------------------------------------------- Check empty

    /**
     * 检查给定字符串是否为空，为空抛出自定义异常，并使用指定的函数获取错误信息返回。
     * <pre class="code">
     * Assert.notEmpty(name, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>           异常类型
     * @param <T>           字符串类型
     * @param text          被检查字符串
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 非空字符串
     * @throws X 被检查字符串为空抛出此异常
     * @see StrUtil#isNotEmpty(CharSequence)
     * @since 5.4.5
     */
    public static <T extends CharSequence, X extends Throwable> T notEmpty(T text, Supplier<X> errorSupplier) throws X {
        if (StrUtil.isEmpty(text)) {
            throw errorSupplier.get();
        }
        return text;
    }

    /**
     * 检查给定字符串是否为空，为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notEmpty(name, "Name must not be empty");
     * </pre>
     *
     * @param <T>              字符串类型
     * @param text             被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 非空字符串
     * @throws IllegalArgumentException 被检查字符串为空
     * @see StrUtil#isNotEmpty(CharSequence)
     */
    public static <T extends CharSequence> T notEmpty(T text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        return notEmpty(text, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 检查给定字符串是否为空，为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notEmpty(name);
     * </pre>
     *
     * @param <T>  字符串类型
     * @param text 被检查字符串
     * @return 被检查的字符串
     * @throws IllegalArgumentException 被检查字符串为空
     * @see StrUtil#isNotEmpty(CharSequence)
     */
    public static <T extends CharSequence> T notEmpty(T text) throws IllegalArgumentException {
        return notEmpty(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出自定义异常。
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.notBlank(name, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>              异常类型
     * @param <T>              字符串类型
     * @param text             被检查字符串
     * @param errorMsgSupplier 错误抛出异常附带的消息生产接口
     * @return 非空字符串
     * @throws X 被检查字符串为空白
     * @see StrUtil#isNotBlank(CharSequence)
     */
    public static <T extends CharSequence, X extends Throwable> T notBlank(T text, Supplier<X> errorMsgSupplier) throws X {
        if (StrUtil.isBlank(text)) {
            throw errorMsgSupplier.get();
        }
        return text;
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notBlank(name, "Name must not be blank");
     * </pre>
     *
     * @param <T>              字符串类型
     * @param text             被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 非空字符串
     * @throws IllegalArgumentException 被检查字符串为空白
     * @see StrUtil#isNotBlank(CharSequence)
     */
    public static <T extends CharSequence> T notBlank(T text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        return notBlank(text, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notBlank(name);
     * </pre>
     *
     * @param <T>  字符串类型
     * @param text 被检查字符串
     * @return 非空字符串
     * @throws IllegalArgumentException 被检查字符串为空白
     * @see StrUtil#isNotBlank(CharSequence)
     */
    public static <T extends CharSequence> T notBlank(T text) throws IllegalArgumentException {
        return notBlank(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    /**
     * 断言给定字符串是否不被另一个字符串包含（即是否为子串）
     * 并使用指定的函数获取错误信息返回
     * <pre class="code">
     * Assert.notContain(name, "rod", ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return ");
     *  });
     * </pre>
     *
     * @param <T>           字符串类型
     * @param <X>           异常类型
     * @param textToSearch  被搜索的字符串
     * @param substring     被检查的子串
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 被检查的子串
     * @throws X 非子串抛出异常
     * @see StrUtil#contains(CharSequence, CharSequence)
     * @since 5.4.5
     */
    public static <T extends CharSequence, X extends Throwable> T notContain(CharSequence textToSearch, T substring, Supplier<X> errorSupplier) throws X {
        if (StrUtil.contains(textToSearch, substring)) {
            throw errorSupplier.get();
        }
        return substring;
    }

    /**
     * 断言给定字符串是否不被另一个字符串包含（即是否为子串）
     *
     * <pre class="code">
     * Assert.notContain(name, "rod", "Name must not contain 'rod'");
     * </pre>
     *
     * @param textToSearch     被搜索的字符串
     * @param substring        被检查的子串
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的子串
     * @throws IllegalArgumentException 非子串抛出异常
     */
    public static String notContain(String textToSearch, String substring, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        return notContain(textToSearch, substring, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    /**
     * 断言给定字符串是否不被另一个字符串包含（即是否为子串）
     *
     * <pre class="code">
     * Assert.notContain(name, "rod");
     * </pre>
     *
     * @param textToSearch 被搜索的字符串
     * @param substring    被检查的子串
     * @return 被检查的子串
     * @throws IllegalArgumentException 非子串抛出异常
     */
    public static String notContain(String textToSearch, String substring) throws IllegalArgumentException {
        return notContain(textToSearch, substring, "[Assertion failed] - this String argument must not contain the substring [{}]", substring);
    }

}

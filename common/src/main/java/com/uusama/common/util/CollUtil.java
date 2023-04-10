package com.uusama.common.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 扩展集合相关操作
 * @author uusama
 */
public class CollUtil {

    // ---------------------------------------------------------------------- isEmpty

    /**
     * 集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 如果给定集合为空，返回默认集合
     *
     * @param <T>               集合类型
     * @param <E>               集合元素类型
     * @param collection        集合
     * @param defaultCollection 默认数组
     * @return 非空（empty）的原集合或默认集合
     * @since 4.6.9
     */
    public static <T extends Collection<E>, E> T defaultIfEmpty(T collection, T defaultCollection) {
        return isEmpty(collection) ? defaultCollection : collection;
    }

    /**
     * 如果给定集合为空，返回默认集合
     *
     * @param <T>        集合类型
     * @param <E>        集合元素类型
     * @param collection 集合
     * @param supplier   默认值懒加载函数
     * @return 非空（empty）的原集合或默认集合
     * @since 5.7.15
     */
    public static <T extends Collection<E>, E> T defaultIfEmpty(T collection, Supplier<? extends T> supplier) {
        return isEmpty(collection) ? supplier.get() : collection;
    }

    /**
     * Iterator是否为空
     *
     * @param iterator iterator
     * @return 是否为空
     */
    public static boolean isEmpty(Iterator<?> iterator) {
        return iterator == null || !iterator.hasNext();
    }

    /**
     * Enumeration是否为空
     *
     * @param enumeration {@link Enumeration}
     * @return 是否为空
     */
    public static boolean isEmpty(Enumeration<?> enumeration) {
        return null == enumeration || false == enumeration.hasMoreElements();
    }

    /**
     * Map是否为空
     *
     * @param map 集合
     * @return 是否为空
     * @see MapUtils#isEmpty(Map)
     * @since 5.7.4
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return MapUtils.isEmpty(map);
    }

    // ---------------------------------------------------------------------- isNotEmpty

    /**
     * 集合是否为非空
     *
     * @param collection 集合
     * @return 是否为非空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return false == isEmpty(collection);
    }

    /**
     * Iterator是否为空
     *
     * @param iterator iterator
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterator<?> iterator) {
        return !isEmpty(iterator);
    }

    /**
     * Enumeration是否为空
     *
     * @param enumeration {@link Enumeration}
     * @return 是否为空
     */
    public static boolean isNotEmpty(Enumeration<?> enumeration) {
        return null != enumeration && enumeration.hasMoreElements();
    }

    /**
     * Map是否为非空
     *
     * @param map 集合
     * @return 是否为非空
     * @see MapUtils#isNotEmpty(Map)
     * @since 5.7.4
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return MapUtils.isNotEmpty(map);
    }

    /**
     * 如果提供的集合为{@code null}，返回一个不可变的默认空集合，否则返回原集合<br>
     * 空集合使用{@link Collections#emptySet()}
     *
     * @param <T> 集合元素类型
     * @param set 提供的集合，可能为null
     * @return 原集合，若为null返回空集合
     * @since 4.6.3
     */
    public static <T> Set<T> emptyIfNull(Set<T> set) {
        return (null == set) ? Collections.emptySet() : set;
    }

    /**
     * 如果提供的集合为{@code null}，返回一个不可变的默认空集合，否则返回原集合<br>
     * 空集合使用{@link Collections#emptyList()}
     *
     * @param <T>  集合元素类型
     * @param list 提供的集合，可能为null
     * @return 原集合，若为null返回空集合
     * @since 4.6.3
     */
    public static <T> List<T> emptyIfNull(List<T> list) {
        return (null == list) ? Collections.emptyList() : list;
    }

    public static boolean containsAny(Object source, Object... targets) {
        return Arrays.asList(targets).contains(source);
    }

    public static boolean isAnyEmpty(Collection<?>... collections) {
        return Arrays.stream(collections).anyMatch(CollectionUtils::isEmpty);
    }

    public static <T> List<T> filterList(Collection<T> from, Predicate<T> predicate) {
        if (CollectionUtils.isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper) {
        if (CollectionUtils.isEmpty(from)) {
            return new ArrayList<>();
        }
        return distinct(from, keyMapper, (t1, t2) -> t1);
    }

    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
        if (CollectionUtils.isEmpty(from)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
        if (CollectionUtils.isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (CollectionUtils.isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func) {
        if (CollectionUtils.isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (CollectionUtils.isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollectionUtils.isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, Function.identity());
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
        if (CollectionUtils.isEmpty(from)) {
            return supplier.get();
        }
        return convertMap(from, keyFunc, Function.identity(), supplier);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollectionUtils.isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
        if (CollectionUtils.isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
        if (CollectionUtils.isEmpty(from)) {
            return supplier.get();
        }
        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
        if (CollectionUtils.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction, supplier));
    }

    public static <T, K> Map<K, List<T>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollectionUtils.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(t -> t, Collectors.toList())));
    }

    public static <T, K, V> Map<K, List<V>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollectionUtils.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream()
            .collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toList())));
    }

    // 暂时没想好名字，先以 2 结尾噶
    public static <T, K, V> Map<K, Set<V>> convertMultiMap2(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollectionUtils.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toSet())));
    }

    public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
        return CollectionUtils.containsAny(source, candidates);
    }

    public static <T> T getFirst(List<T> from) {
        return !CollectionUtils.isEmpty(from) ? from.get(0) : null;
    }

    public static <T> T findFirst(List<T> from, Predicate<T> predicate) {
        if (CollectionUtils.isEmpty(from)) {
            return null;
        }
        return from.stream().filter(predicate).findFirst().orElse(null);
    }

    public static <T, V extends Comparable<? super V>> V getMaxValue(List<T> from, Function<T, V> valueFunc) {
        if (CollectionUtils.isEmpty(from)) {
            return null;
        }
        assert from.size() > 0; // 断言，避免告警
        T t = from.stream().max(Comparator.comparing(valueFunc)).get();
        return valueFunc.apply(t);
    }

    public static <T, V extends Comparable<? super V>> V getMinValue(List<T> from, Function<T, V> valueFunc) {
        if (CollectionUtils.isEmpty(from)) {
            return null;
        }
        assert from.size() > 0; // 断言，避免告警
        T t = from.stream().min(Comparator.comparing(valueFunc)).get();
        return valueFunc.apply(t);
    }

    public static <T, V extends Comparable<? super V>> V getSumValue(List<T> from, Function<T, V> valueFunc, BinaryOperator<V> accumulator) {
        if (CollectionUtils.isEmpty(from)) {
            return null;
        }
        assert from.size() > 0; // 断言，避免告警
        return from.stream().map(valueFunc).reduce(accumulator).get();
    }

    public static <T> void addIfNotNull(Collection<T> coll, T item) {
        if (item == null) {
            return;
        }
        coll.add(item);
    }

    public static <T> Collection<T> singleton(T deptId) {
        return deptId == null ? Collections.emptyList() : Collections.singleton(deptId);
    }
}

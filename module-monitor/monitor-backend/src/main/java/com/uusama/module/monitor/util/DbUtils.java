package com.uusama.module.monitor.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.experimental.UtilityClass;

/**
 * 数据库相关工具类
 * @author zhaohai
 */
@UtilityClass
public class DbUtils {

    public static <T> Page<T> pageWrapper(int page, int perPage) {
        Page<T> pageWrapper = new Page<>(page, perPage);
        pageWrapper.setSearchCount(true);
        return pageWrapper;
    }

    public static <T> LambdaQueryWrapper<T> queryWrapper() {
        return new LambdaQueryWrapper<>();
    }
}

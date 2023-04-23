package com.uusama.module.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础实体类
 * @author zhaohai
 */
@Data
public class BaseDO {

    @TableId(type = IdType.AUTO)
    protected Integer id;

    private LocalDateTime createdAt;

    @JsonIgnore
    public LambdaQueryWrapper<? extends BaseDO> getUniqueQuery() {
        return new LambdaQueryWrapper<BaseDO>().eq(true, BaseDO::getId, getId());
    }
}

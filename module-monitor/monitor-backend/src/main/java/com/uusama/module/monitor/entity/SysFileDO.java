package com.uusama.module.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uusama.common.util.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.LocalDateTime;

/**
 * @author zhaohai
 * 系统文件实体，上传文件下载文件处理
 */
@TableName("sys_file")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysFileDO extends BaseDO {

    /**
     * 文件uuid
     */
    private String uuid;

    /**
     * 文件原始名称，上传的原名称
     */
    private String originName;

    /**
     * 系统生成的文件名
     */
    private String sysName;

    /**
     * 文件所属业务类型
     */
    private String bizType;

    private String url;

    /**
     * 文件类型
     */
    private String extension;

    /**
     * 文件大小，单位字节
     */
    private float size;

    /**
     * 文件的md5值
     */
    private String md5;

    @Override
    public LambdaQueryWrapper<SysFileDO> getUniqueQuery() {
        return new LambdaQueryWrapper<SysFileDO>()
            .eq(StringUtils.isNotBlank(this.getMd5()), SysFileDO::getMd5, this.getMd5());
    }

    public String generateSaveDir() {
        return bizType + File.separator + DateTimeUtil.formatToDate(LocalDateTime.now());
    }

    public String generateUrl() {
        return generateSaveDir() + File.separator + sysName;
    }
}

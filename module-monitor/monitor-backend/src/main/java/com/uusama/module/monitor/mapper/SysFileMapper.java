package com.uusama.module.monitor.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uusama.module.monitor.entity.SysFileDO;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhaohai
 */
public interface SysFileMapper extends BaseMapper<SysFileDO> {

    /**
     * 根据url查询
     * @param url url
     * @return SysFile
     */
    default SysFileDO selectByUrl(String url) {
        return this.selectOne(new LambdaQueryWrapper<SysFileDO>().eq(StringUtils.isNotBlank(url), SysFileDO::getUrl, url));
    }
}

package com.uusama.module.monitor.pojo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * AMIS前端CRUD组件返回数据封装
 * @author zhaohai
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AmisCrudData<T> {
    @Schema(description = "表格数据列表")
    private List<T> rows;

    @Schema(description = "动态列名称，如果后台没有返回该字段，则需要在前端页面声明")
    private List<Column> columns;

    @Schema(description = "数据总行数，如果为空，则表示动态数据无法计算总数，根据是否有下一行来判断")
    private Long total;

    @Schema(description = "是否有下一行")
    private Boolean hasNext;

    @Schema(description = "控制当前在第几页")
    private Long page;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Column {
        @Schema(description = "列名称")
        private String label;

        @Schema(description = "列字段名")
        private String name;

        @Schema(description = "是否支持排序")
        private boolean sortable;
    }

    public static <T> AmisCrudData<T> empty() {
        return AmisCrudData.of(new ArrayList<>(), 0);
    }

    public static <T> AmisCrudData<T> of(List<T> data) {
        return AmisCrudData.of(data, data.size());
    }

    public static <T> AmisCrudData<T> of(Page<T> pageResult) {
        return AmisCrudData.of(pageResult.getRecords(), pageResult.getTotal());
    }

    public static <T> AmisCrudData<T> of(List<T> data, long total) {
        return AmisCrudData.<T>builder().rows(data).total(total).build();
    }

    public static <T> AmisCrudData<T> of(List<T> data, List<Column> columns, long total) {
        return AmisCrudData.<T>builder().rows(data).total(total).columns(columns).build();
    }
}

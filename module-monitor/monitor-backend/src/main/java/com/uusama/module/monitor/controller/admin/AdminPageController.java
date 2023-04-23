package com.uusama.module.monitor.controller.admin;

import com.uusama.framework.api.pojo.CommonResult;
import com.uusama.framework.web.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author zhaohai
 * 控制前端页面展示和返回
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminPageController {

    @Operation(summary = "前端首页")
    @GetMapping({"index", "/", "/admin/index"})
    public String indexPage() {
        // 多tab应用
//        return "redirect:/admin/

        // 使用AMIS默认模板，单页应用
        return "app";
    }

    /**
     * 展示前端页面
     * @param path 路径
     * @return 首页，并携带路径参数
     */
    @SneakyThrows
    @GetMapping("/admin/page/frontend/{path}")
    public String showFrontendPage(@PathVariable String path) {
        return "redirect:/admin/index?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8.name());
    }

    @Operation(summary = "获取amis页面json")
    @GetMapping("/admin/page/json/{jsonPath}")
    @ResponseBody
    public CommonResult<Object> getJsonPage(@PathVariable String jsonPath) {
        return CommonResult.success(getResourceFileJson("/static/page/" + jsonPath));
    }

    @Operation(summary = "获取amis页面jsonp格式")
    @GetMapping("/admin/page/jsonp/{jsPath}")
    @ResponseBody
    @SneakyThrows
    public String getJsonpPage(@PathVariable String jsPath) {
        return IOUtils.resourceToString("/static/page/" + jsPath, StandardCharsets.UTF_8);
    }

    @Operation(summary = "获取登录用户菜单等信息")
    @GetMapping("/admin/user-sessions")
    @ResponseBody
    public CommonResult<Object> getUserSession() {
        return CommonResult.success(getResourceFileJson("/static/page/menu.json"));
    }

    @SneakyThrows
    private Object getResourceFileJson(String resourceUrl) {
        return JsonUtils.deserialize(IOUtils.resourceToString(resourceUrl, StandardCharsets.UTF_8), HashMap.class);
    }
}

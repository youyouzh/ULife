package com.uusama.module.monitor.controller.agent;

import com.uusama.framework.api.pojo.CommonResult;
import com.uusama.module.monitor.entity.SysFileDO;
import com.uusama.module.monitor.service.SysFileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhaohai
 * 文件处理控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SysFileController {
    private final SysFileService sysFileService;

    @Operation(summary = "系统文件上传")
    @PostMapping("/sys/files/upload")
    public CommonResult<Object> uploadFile(@RequestPart("file") MultipartFile multipartFile, @RequestParam("bizType") String bizType) {
        try {
            SysFileDO sysFile = sysFileService.uploadFile(multipartFile, bizType);
            return CommonResult.success(sysFile);
        } catch (IOException e) {
            return CommonResult.error(e.getMessage());
        }
    }

    @Operation(summary = "系统文件下载")
    @GetMapping({"/sys/files/download", "/api/v1/files/download"})
    public void downloadFile(HttpServletResponse response, @RequestParam("url") String url) {
        sysFileService.downloadFile(response, url);
    }
}

package com.uusama.ulife.controller;

import com.uusama.framework.api.pojo.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author uusama
 */
@RestController
public class DefaultController {

    @GetMapping("/test")
    public CommonResult<String> test() {

        return CommonResult.success("test");
    }
}

package com.uusama.module.monitor.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.uusama.framework.api.pojo.CommonResult;
import com.uusama.module.monitor.entity.DeployAgentDO;
import com.uusama.module.monitor.entity.DeployAppDO;
import com.uusama.module.monitor.entity.DeployProjectDO;
import com.uusama.module.monitor.entity.DeployProjectVersionDO;
import com.uusama.module.monitor.mapper.DeployAgentMapper;
import com.uusama.module.monitor.mapper.DeployAppMapper;
import com.uusama.module.monitor.mapper.DeployProjectMapper;
import com.uusama.module.monitor.mapper.DeployProjectVersionMapper;
import com.uusama.module.monitor.service.DeployResourceService;
import com.uusama.module.monitor.util.DbUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaohai
 * 前端资源管理数据交互
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminResourceController {
    private final DeployAppMapper deployAppDao;
    private final DeployAgentMapper deployAgentDao;
    private final DeployProjectMapper deployProjectDao;
    private final DeployProjectVersionMapper deployProjectVersionDao;
    private final DeployResourceService deployResourceService;

    @Operation(summary = "获取应用列表")
    @GetMapping("/apps")
    public CommonResult<Object> getApps(@RequestParam(required = false) String keyword,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int perPage) {
        return CommonResult.success(deployResourceService.queryApps(keyword, DbUtils.pageWrapper(page, perPage)));
    }

    @Operation(summary = "新增应用")
    @PostMapping("/apps")
    public CommonResult<Object> insertApps(@RequestBody DeployAppDO deployApp) {
        if (deployAppDao.exists(new LambdaQueryWrapper<DeployAppDO>()
                                    .eq(StringUtils.isNotBlank(deployApp.getCode()), DeployAppDO::getCode, deployApp.getCode()))) {
            log.info("The code is used. deployApp: {}", deployApp);
            return CommonResult.error("该编码已经被使用");
        }
        deployAppDao.insert(deployApp);
        return CommonResult.success();
    }

    @Operation(summary = "修改应用")
    @PutMapping("/apps")
    public CommonResult<Object> updateApp(@RequestBody DeployAppDO deployApp) {
        deployAppDao.update(null, new LambdaUpdateWrapper<DeployAppDO>()
            .eq(DeployAppDO::getId, deployApp.getId())
            .set(DeployAppDO::getName, deployApp.getName())
            .set(DeployAppDO::getDescription, deployApp.getDescription()));
        return CommonResult.success();
    }

    @Operation(summary = "根据ID查找应用")
    @DeleteMapping("/apps/{id}")
    public CommonResult<Object> updateApps(@PathVariable int id) {
        deployAppDao.deleteById(id);
        return CommonResult.success();
    }

    @Operation(summary = "获取Agent列表")
    @GetMapping("/agents")
    public CommonResult<Object> getAgents(@RequestParam(required = false) String keyword,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int perPage) {
        return CommonResult.success(deployResourceService.queryAgents(keyword, DbUtils.pageWrapper(page, perPage)));
    }

    @Operation(summary = "新增Agent")
    @PostMapping("/agents")
    public CommonResult<Object> insertAgents(@RequestBody DeployAgentDO deployAgent) {
        if (deployAgentDao.exists(new LambdaQueryWrapper<DeployAgentDO>()
                                    .eq(StringUtils.isNotBlank(deployAgent.getIp()), DeployAgentDO::getIp, deployAgent.getIp()))) {
            log.info("The Ip is used. deployAgent: {}", deployAgent);
            return CommonResult.error("该IP地址已经被使用");
        }
        deployAgentDao.insert(deployAgent);
        return CommonResult.success();
    }

    @Operation(summary = "修改Agent信息")
    @PutMapping("/agents")
    public CommonResult<Object> updateAgent(@RequestBody DeployAgentDO deployAgent) {
        deployAgentDao.update(null, new LambdaUpdateWrapper<DeployAgentDO>()
            .eq(DeployAgentDO::getId, deployAgent.getId())
            .set(DeployAgentDO::getName, deployAgent.getName())
            .set(DeployAgentDO::getDescription, deployAgent.getDescription()));
        return CommonResult.success();
    }

    @Operation(summary = "根据id获取Agent")
    @DeleteMapping("/agents/{id}")
    public CommonResult<Object> updateAgent(@PathVariable int id) {
        deployAgentDao.deleteById(id);
        return CommonResult.success();
    }

    @Operation(summary = "获取项目列表")
    @GetMapping("/projects")
    public CommonResult<Object> getProjects(@RequestParam(required = false) String keyword,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int perPage) {
        return CommonResult.success(deployResourceService.queryProjects(keyword, DbUtils.pageWrapper(page, perPage)));
    }

    @Operation(summary = "根据id获取项目信息")
    @GetMapping("/projects/{projectId}")
    public CommonResult<DeployProjectDO> getProject(@PathVariable("projectId") int projectId) {
        return CommonResult.success(deployProjectDao.selectById(projectId));
    }

    @Operation(summary = "新增部署项目")
    @PostMapping("/projects")
    public CommonResult<Object> insertProjects(@RequestBody DeployProjectDO deployProject) {
        if (deployProjectDao.exists(new LambdaQueryWrapper<DeployProjectDO>()
                                      .eq(StringUtils.isNotBlank(deployProject.getCode()), DeployProjectDO::getCode, deployProject.getCode()))) {
            log.info("The Ip is used. deployProject: {}", deployProject);
            return CommonResult.error("该项目编码已被使用");
        }
        deployProject.setProjectState(DeployProjectDO.ProjectState.ONLINE);
        deployProjectDao.insert(deployProject);
        return CommonResult.success();
    }

    @Operation(summary = "修改部署项目信息")
    @PutMapping("/projects")
    public CommonResult<Object> updateProject(@RequestBody DeployProjectDO deployProject) {
        deployProjectDao.updateById(deployProject);
        return CommonResult.success();
    }

    @Operation(summary = "删除部署项目")
    @DeleteMapping("/projects/{id}")
    public CommonResult<Object> updateProject(@PathVariable int id) {
        deployProjectDao.deleteById(id);
        return CommonResult.success();
    }

    @Operation(summary = "获取服务运行和部署信息")
    @GetMapping("/projects/{$projectId}/agents")
    public CommonResult<Object> getProjectAgents(@PathVariable int projectId) {
        return CommonResult.success();
    }

    @Operation(summary = "获取部署项目版本列表")
    @GetMapping("/project-versions")
    public CommonResult<Object> getProjectVersions(@RequestParam(required = false) Integer projectId,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int perPage) {
        return CommonResult.success(deployResourceService.queryProjectVersions(projectId, DbUtils.pageWrapper(page, perPage)));
    }

    @Operation(summary = "新增部署项目版本")
    @PostMapping("/project-versions")
    public CommonResult<Object> insertProjectVersions(@RequestBody DeployProjectVersionDO deployProjectVersion) {
        if (deployProjectVersionDao.exists(new LambdaQueryWrapper<DeployProjectVersionDO>()
               .eq(true, DeployProjectVersionDO::getProjectId, deployProjectVersion.getProjectId())
               .eq(StringUtils.isNotBlank(deployProjectVersion.getVersion()), DeployProjectVersionDO::getVersion, deployProjectVersion.getVersion()))) {
            log.info("The version is used. deployProjectVersion: {}", deployProjectVersion);
            return CommonResult.error("该版本号已被使用");
        }
        deployProjectVersionDao.insert(deployProjectVersion);
        return CommonResult.success();
    }

    @Operation(summary = "修改部署项目版本信息")
    @PutMapping("/project-versions")
    public CommonResult<Object> updateProjectVersion(@RequestBody DeployProjectVersionDO deployProjectVersion) {
        deployProjectVersionDao.updateById(deployProjectVersion);
        return CommonResult.success();
    }

    @Operation(summary = "删除项目版本")
    @DeleteMapping("/project-versions/{id}")
    public CommonResult<Object> updateProjectVersion(@PathVariable int id) {
        deployProjectVersionDao.deleteById(id);
        return CommonResult.success();
    }
}

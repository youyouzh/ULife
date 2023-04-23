package com.uusama.module.monitor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uusama.framework.web.util.JsonUtils;
import com.uusama.common.util.FileUtil;
import com.uusama.module.monitor.domain.DeployState;
import com.uusama.module.monitor.entity.BatchDeployTaskDO;
import com.uusama.module.monitor.entity.DeployAgentDO;
import com.uusama.module.monitor.entity.DeployProjectDO;
import com.uusama.module.monitor.entity.DeployProjectVersionDO;
import com.uusama.module.monitor.entity.DeployTaskDO;
import com.uusama.module.monitor.entity.DeployTaskRunTime;
import com.uusama.module.monitor.entity.SysFileDO;
import com.uusama.module.monitor.exception.BatchTaskParseException;
import com.uusama.module.monitor.mapper.BatchDeployTaskMapper;
import com.uusama.module.monitor.mapper.DeployAgentMapper;
import com.uusama.module.monitor.mapper.DeployProjectMapper;
import com.uusama.module.monitor.mapper.DeployProjectVersionMapper;
import com.uusama.module.monitor.mapper.DeployTaskMapper;
import com.uusama.module.monitor.mapper.SysFileMapper;
import com.uusama.module.monitor.pojo.AmisCrudData;
import com.uusama.module.monitor.pojo.BatchDeployTaskRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhaohai
 * 部署任务相关service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchDeployTaskService {
    private final BatchDeployTaskMapper batchDeployTaskDao;
    private final DeployTaskMapper deployTaskDao;
    private final DeployProjectMapper deployProjectDao;
    private final DeployProjectVersionMapper deployProjectVersionDao;
    private final SysFileMapper sysFileDao;
    private final DeployAgentMapper deployAgentDao;
    private final DeployTaskService deployTaskService;

    @Value("${cjhx.file.upload.path}")
    private String uploadFileSavePath;

    public AmisCrudData<BatchDeployTaskDO> queryBatchTasks(String title, Page<BatchDeployTaskDO> pageWrapper) {
        LambdaQueryWrapper<BatchDeployTaskDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Objects.nonNull(title), BatchDeployTaskDO::getTitle, title)
            .orderByDesc(BatchDeployTaskDO::getId);

        return AmisCrudData.of(batchDeployTaskDao.selectPage(pageWrapper, queryWrapper));
    }

    public BatchDeployTaskRequest parseBatchTaskFileResult(String zipAbsolutePath) {
        // 上传文件解压
        Path zipFilePath = Paths.get(zipAbsolutePath);
        Path unzipFilePathRoot = FileUtil.getUnzipPathRoot(zipFilePath);
        unzipFile(zipFilePath, unzipFilePathRoot);

        // 解析批量部署描述元数据文件
        BatchDeployTaskRequest parseManifest = parseManifest(unzipFilePathRoot);

        // 检查基本字段是否缺失
        if (CollectionUtils.isEmpty(parseManifest.getDeployProjectWrappers())) {
            log.error("The deployProjectWrappers is empty.");
            throw new BatchTaskParseException("manifest.json中部署项目为空，请检查 deployProjectWrappers 字段");
        }
        return parseManifest;
    }

    private void unzipFile(Path zipFilePath, Path destFolderPath) {
        if (!zipFilePath.toFile().isFile()) {
            log.error("zip file is not exist. path: {}", zipFilePath);
            throw new BatchTaskParseException("批量部署压缩文件丢失： " + zipFilePath);
        }

        try {
            FileUtil.unzip(zipFilePath, destFolderPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("unzip file exception.", e);
            throw new BatchTaskParseException("批量部署压缩文件解压失败");
        }
    }

    private BatchDeployTaskRequest parseManifest(Path unzipFilePathRoot) {
        Path manifestPath = Paths.get(unzipFilePathRoot.toString(), "manifest.json");
        if (!manifestPath.toFile().isFile()) {
            log.error("The manifest.json file is not exist. path: {}", unzipFilePathRoot);
            throw new BatchTaskParseException("manifest.json 文件缺失，请检查上传文件");
        }

        // 解析部署描述文件
        try {
            String manifestContent = IOUtils.toString(manifestPath.toUri(), StandardCharsets.UTF_8);
            log.info("manifest content: {}", manifestContent);
            return JsonUtils.deserialize(manifestContent, BatchDeployTaskRequest.class);
        } catch (RuntimeException | IOException e) {
            log.error("parse manifest.json exception.", e);
            throw new BatchTaskParseException("manifest.json文件格式解析失败");
        }
    }

    public BatchDeployTaskRequest saveBatchDeployTask(BatchDeployTaskRequest request, @Nullable String unzipFilePathRoot) {
        BatchDeployTaskDO batchDeployTask = batchDeployTaskDao.getOrCreate(request.wrapBatchTask());
        List<Integer> deployTaskIds = new ArrayList<>();

        // 依次创建部署项目相关记录
        for (BatchDeployTaskRequest.DeployProjectWrapper deployProjectWrapper : request.getDeployProjectWrappers()) {
            DeployProjectDO deployProject = deployProjectWrapper.getDeployProject();
            deployProject = deployProjectDao.getOrCreate(deployProject);
            deployProjectWrapper.setDeployProject(deployProject);

            // 检查版本是否存在，不存在则新建
            DeployProjectVersionDO deployProjectVersion = checkOrCreateVersion(deployProjectWrapper, unzipFilePathRoot);
            deployProjectWrapper.setDeployProjectVersion(deployProjectVersion);

            // 创建具体的deployTask
            List<DeployAgentDO> saveDeployAgents = new ArrayList<>();
            for (DeployAgentDO deployAgent : deployProjectWrapper.getDeployAgents()) {
                deployAgent = deployAgentDao.getUnique(deployAgent)
                    .orElseThrow(() -> new BatchTaskParseException("未知的Agent，请检查 deployAgents 中的id和ip配置"));
                saveDeployAgents.add(deployAgent);
                DeployTaskDO deployTask = DeployTaskDO.builder()
                    .projectId(deployProject.getId())
                    .projectVersionId(deployProjectVersion.getId())
                    .agentId(deployAgent.getId())
                    .batchTaskId(batchDeployTask.getId())
                    .build();
                deployTask.initDeployStateAndTime(request.getDeployStartTime());
                deployTaskDao.getOrCreate(deployTask);
                deployTaskIds.add(deployTask.getId());
            }
            deployProjectWrapper.setDeployAgents(saveDeployAgents);
        }
        batchDeployTask.setDeployTaskIds(JsonUtils.serialize(deployTaskIds));
        batchDeployTaskDao.updateById(batchDeployTask);
        return request;
    }

    private DeployProjectVersionDO checkOrCreateVersion(BatchDeployTaskRequest.DeployProjectWrapper deployProjectWrapper, String unzipFilePathRoot) {
        DeployProjectVersionDO deployProjectVersion = deployProjectWrapper.getDeployProjectVersion();
        deployProjectVersion.setProjectId(deployProjectWrapper.getDeployProject().getId());
        Optional<DeployProjectVersionDO> existVersion = deployProjectVersionDao.getUnique(deployProjectVersion);
        if (!existVersion.isPresent()) {
            if (Objects.isNull(unzipFilePathRoot)) {
                log.error("The deploy project version is not exist. project config: {}", deployProjectWrapper);
                throw new BatchTaskParseException("未找到该部署版本，请检查配置中 deployProjectVersion. project: " + JsonUtils.serialize(deployProjectWrapper));
            } else {
                // 从解压缩文件夹中找到部署包，复制文件并创建SysFile记录
                Path deployFilePath = Paths.get(unzipFilePathRoot, deployProjectVersion.getDeployFileUrl());
                if (!deployFilePath.toFile().exists()) {
                    log.error("The deployFile is not exist.");
                    throw new BatchTaskParseException("manifest.json中声明的部署包文件： " + deployFilePath + " 未在压缩包中找到。");
                }

                // 创建部署包版本记录
                SysFileDO sysFile = savePackageSysFile(deployFilePath);
                deployProjectVersion.setDeployFileUrl(sysFile.getUrl());
                deployProjectVersionDao.insert(deployProjectVersion);
            }
        } else {
            deployProjectVersion = existVersion.get();
        }
        return deployProjectVersion;
    }

    private SysFileDO savePackageSysFile(Path deployFilePath) {
        // 复制部署包文件和创建SysFile记录
        String deployFileExtension = FilenameUtils.getExtension(deployFilePath.getFileName().toString());
        try (InputStream inputStream = Files.newInputStream(deployFilePath)) {
            SysFileDO sysFile = SysFileDO.builder()
                .uuid(FileUtil.generateFileUUID())
                .originName(deployFilePath.getFileName().toString())
                .sysName(FileUtil.generateSysFileName(deployFileExtension))
                .bizType("package")
                .extension(deployFileExtension)
                .size(Files.size(deployFilePath))
                .md5(DigestUtils.md5Hex(inputStream))
                .build();

            // 复制一份部署包到指定目录
            sysFile.setUrl(sysFile.generateUrl());
            Path packageSaveAbsolutePath = Paths.get(uploadFileSavePath, sysFile.getUrl());
            FileUtil.mkdirIfNeed(packageSaveAbsolutePath.getParent().toString());
            Files.copy(inputStream, packageSaveAbsolutePath);
            sysFile = sysFileDao.getOrCreate(sysFile);
            return sysFile;
        } catch (IOException e) {
            log.error("create sysFile exception. deployFileUrl: {}", deployFilePath, e);
            throw new BatchTaskParseException("处理安装包失败，请检查 deployFileUrl 和安装包路径是否匹配： " + deployFilePath);
        }
    }

    @Async
    public void runBatchDeployTask(BatchDeployTaskDO batchDeployTask) {
        log.info("runBatchDeployTask batchTaskId: {}", batchDeployTask.getId());
        batchDeployTask.setDeployState(DeployState.DEPLOYING);
        batchDeployTask.setRemark("开始向各Agent分派部署任务");
        batchDeployTaskDao.updateById(batchDeployTask);

        // 依次通知Agent进行部署
        List<DeployTaskDO> deployTasks = deployTaskDao.selectList(new LambdaQueryWrapper<DeployTaskDO>()
            .eq(true, DeployTaskDO::getBatchTaskId, batchDeployTask.getId()));
        // 过滤掉已经部署或者部署中的任务，用于继续部署
        deployTasks = deployTasks.stream().filter(DeployTaskRunTime::canReDeploy).collect(Collectors.toList());
        log.info("total deploy task size: {}", deployTasks.size());
        deployTasks.forEach(deployTaskService::runDeployTask);

        batchDeployTask.setRemark("部署任务分派完毕，各Agent开始部署中");
        batchDeployTaskDao.updateById(batchDeployTask);
    }

    /**
     * 定时检查部署任务，对于到部署时间的任务自动执行
     */
    @Scheduled(fixedDelay = 60000)
    public void autoCheckDeployTask() {
        List<BatchDeployTaskDO> batchDeployTasks = batchDeployTaskDao.selectRecentTasks();
        for (BatchDeployTaskDO batchDeployTask : batchDeployTasks) {
            // 超时的任务自动设置为失败
            if (batchDeployTask.isDeployingOvertime()) {
                log.warn("batchDeployTask is overtime. taskId: {}", batchDeployTask.getId());
                batchDeployTask.setDeployState(DeployState.FAILURE);
                batchDeployTask.setRemark("部署超时");
                batchDeployTaskDao.updateById(batchDeployTask);
            }

            // 到达部署时间的任务自动部署
            if (batchDeployTask.attachAppointmentDeployTime()) {
                this.runBatchDeployTask(batchDeployTask);
            }
        }
    }
}

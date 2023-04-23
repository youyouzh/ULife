package com.uusama.module.monitor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.uusama.common.util.FileUtil;
import com.uusama.module.monitor.mapper.SysFileMapper;
import com.uusama.module.monitor.entity.SysFileDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Set;

/**
 * @author zhaohai
 * 系统文件处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileService {
    private static final Set<String> SUPPORT_FILE_EXTENSIONS = SetUtils.hashSet("jar", "zip", "rzr", "war", "xls", "xlsx");

    @Value("${cjhx.file.upload.path}")
    private String uploadFileSavePath;

    private final SysFileMapper sysFileDao;

    public String getFullSaveFilePath(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        return uploadFileSavePath + File.separator + url;
    }

    public SysFileDO uploadFile(MultipartFile multipartFile, String bizType) throws IOException {
        String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (!SUPPORT_FILE_EXTENSIONS.contains(fileExtension)) {
            log.error("unsupported upload file extension: {}", fileExtension);
            throw new IOException("暂不支持该类型文件上传");
        }

        try(InputStream inputStream = multipartFile.getInputStream()) {
            SysFileDO sysFile = SysFileDO.builder()
                .uuid(FileUtil.generateFileUUID())
                .originName(multipartFile.getOriginalFilename())
                .sysName(FileUtil.generateSysFileName(fileExtension))
                .bizType(bizType)
                .extension(fileExtension)
                .size(multipartFile.getSize())
                .md5(DigestUtils.md5Hex(inputStream))
                .build();

            // 检查相同md5的文件是否存在
            SysFileDO existSysFile = sysFileDao.selectOne(new LambdaQueryWrapper<SysFileDO>().eq(true, SysFileDO::getMd5, sysFile.getMd5()));
            if (Objects.nonNull(existSysFile)) {
                log.info("The same md5 file is exist. fileId: {}", existSysFile.getId());
                return existSysFile;
            }

            // 保存路径
            sysFile.setUrl(sysFile.generateUrl());
            String absoluteSavePath = uploadFileSavePath + File.separator + sysFile.generateSaveDir();
            FileUtil.mkdirIfNeed(absoluteSavePath);
            File saveFile = new File(absoluteSavePath, sysFile.getSysName());
            multipartFile.transferTo(saveFile);

            sysFileDao.insert(sysFile);
            return sysFile;
        } catch (IOException e) {
            log.error("get inputStream failed.", e);
            throw new IOException("文件上传损坏，请重新上传");
        }
    }

    public void downloadFile(HttpServletResponse response, String url) {
        response.setContentType("text/html;charset=UTF-8");

        // 检查url是否存在
        File downloadFile = new File(getFullSaveFilePath(url));
        SysFileDO sysFile = sysFileDao.selectOne(new LambdaQueryWrapper<SysFileDO>().eq(true, SysFileDO::getUrl, url));
        if (Objects.isNull(sysFile) || !downloadFile.exists()) {
            log.error("The file is not exist. fileUrl: {}", url);
            response.setStatus(404);
            return;
        }

        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(downloadFile.length()));
        response.setHeader("Content-Disposition", "attachment; filename=" + sysFile.getOriginName());

        try(BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(downloadFile.toPath()));
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (IOException e) {
            log.error("download file exception.", e);
            response.setStatus(404);
        }
    }
}

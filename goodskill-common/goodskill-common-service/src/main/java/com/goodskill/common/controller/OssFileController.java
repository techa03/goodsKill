package com.goodskill.common.controller;

import com.goodskill.common.application.service.FileApplicationService;
import com.goodskill.core.info.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/oss/files")
public class OssFileController {

    private static final Logger logger = LoggerFactory.getLogger(OssFileController.class);

    private final FileApplicationService fileApplicationService;

    public OssFileController(FileApplicationService fileApplicationService) {
        this.fileApplicationService = fileApplicationService;
    }

    /**
     * 处理文件上传请求
     *
     * @param file 用户上传的文件，通过 MultipartFile 类型接收。
     * @return 返回上传结果，包括文件的 URL 和上传状态信息。
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String publicUrl = fileApplicationService.uploadFile(file);
            return Result.ok(publicUrl);
        } catch (Exception e) {
            logger.error("文件上传失败: {}", file.getOriginalFilename(), e);
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 处理文件下载请求
     * 根据请求参数 responseType 的不同，可以选择直接返回文件内容（以二进制形式）或返回文件的下载 URL。
     *
     * @param fileId       查找文件的唯一标识。
     * @param responseType 下载响应类型，默认为 "url"。可选值为 "binary"（二进制数据响应）和 "url"（文件下载 URL 响应）。
     * @return 根据 responseType 的不同，返回文件内容或文件下载 URL。如果文件不存在，返回错误信息。
     */
    @GetMapping("/download/{fileId}")
    public Result<?> downloadFile(@PathVariable String fileId,
                                  @RequestParam(value = "responseType", defaultValue = "url") String responseType) {
        try {
            Object result = fileApplicationService.downloadFile(fileId, responseType);
            return Result.ok(result);
        } catch (Exception e) {
            logger.error("文件下载失败: {}", fileId, e);
            return Result.fail("文件下载失败: " + e.getMessage());
        }
    }
}

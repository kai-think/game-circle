package com.kai.common.controller;

import com.example.demo.common.BaseController;
import com.example.demo.common.service.UploadService;
import com.example.demo.common.throwable.CheckFaildedException;
import com.example.demo.utils.IValidator;
import com.example.demo.utils.httpresult.HttpResult;
import com.example.demo.utils.httpresult.SuccessResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/upload")
public class UploadController extends BaseController {
    @Resource
    private UploadService uploadService;

    @PostMapping("/image")
    public HttpResult<String> uploadImage(MultipartFile file) {
        if (IValidator.empty(file))
            throw new CheckFaildedException("图片为空");
        return new SuccessResult<>(uploadService.uploadImage(file));
    }

    @PostMapping("/imageWithMediumThumbnail")
    public HttpResult<String> uploadImageWithMediumThumbnail(MultipartFile file) {
        if (IValidator.empty(file))
            throw new CheckFaildedException("图片为空");
        return new SuccessResult<>(uploadService.uploadImageWithMdThumbnail(file));
    }

    @PostMapping("/imageWithLargeThumbnail")
    public HttpResult<String> uploadImageWithLargeThumbnail(MultipartFile file) {
        if (IValidator.empty(file))
            throw new CheckFaildedException("图片为空");
        return new SuccessResult<>(uploadService.uploadImageWithLgThumbnail(file));
    }

    @PostMapping("/video")
    public HttpResult<String> uploadVideo(MultipartFile file) {
        if (IValidator.empty(file))
            throw new CheckFaildedException("视频为空");
        return new SuccessResult<>(uploadService.uploadVideo(file));
    }

    @PostMapping("/file")
    public HttpResult<String> uploadFile(MultipartFile file) {
        if (IValidator.empty(file))
            throw new CheckFaildedException("文件为空");
        return new SuccessResult<>(uploadService.uploadFile(file));
    }
}
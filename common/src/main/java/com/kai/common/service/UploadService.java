package com.kai.common.service;

import com.example.demo.common.throwable.ExecutionException;
import com.example.demo.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@Service
public class UploadService {
    @Value("${spring.resources.static-locations}")
    public String StaticLocation;

    public static final String ThumbnailPath = "/thumbnails";
    public static final String ThumbnailSuffix = "-thumbnail";

    public static final String RelativeImagePath = "/image";
    public static final String RelativeVideoPath = "/video";
    public static final String RelativeFilePath = "/file";

    public String uploadImage(MultipartFile file) {
        return upload(file, RelativeImagePath);
    }

    public String uploadImageWithMdThumbnail(MultipartFile file) {
        return upload(file, RelativeImagePath, ImageUtils.SIZE_MD, ImageUtils.SIZE_MD);
    }

    public String uploadImageWithLgThumbnail(MultipartFile file) {
        return upload(file, RelativeImagePath, ImageUtils.SIZE_LARGE, ImageUtils.SIZE_LARGE);
    }

    public String uploadVideo(MultipartFile file) {
        return upload(file, RelativeVideoPath);
    }

    public String uploadFile(MultipartFile file) {
        return upload(file, RelativeFilePath);
    }

    public String upload(MultipartFile file, String relativePath) {
        return upload(file, relativePath, ImageUtils.SIZE_SM, ImageUtils.SIZE_MD);
    }

    public String upload(MultipartFile file, String relativePath, int thumbnailWidth, int thumbnailHeight) {
        if (StaticLocation == null)
        {
            Logger.getGlobal().severe("静态资源路径错误");
            throw new ExecutionException("服务器内部错误");
        }
        String StaticPath = StaticLocation.substring(5);
        int len = StaticPath.length();
        if (StaticPath.charAt(len - 1) == '/' || StaticPath.charAt(len - 1) == '\\')
            StaticPath = StaticPath.substring(0, len - 1);

        //图片名
        String name = file.getOriginalFilename();
        assert name != null;

        File target = new File(StaticPath + relativePath, name).getAbsoluteFile();

        System.out.println("目标文件路径：" + target.getAbsolutePath());

        if (!target.getParentFile().exists())
            target.getParentFile().mkdirs();

        //文件名冲突处理
        if (target.exists())
        {
            //冲突文件大小一样就当做是相同的文件
            if (target.length() == file.getSize())
                return relativePath + "/" + target.getName();
            else
            {
                //重命名上传的文件，即在最后加个时间戳
                int dotIdx = name.lastIndexOf(".");
                String suffix = name.substring(dotIdx);
                name = name.substring(0, dotIdx);
                target = new File(target.getParentFile(),
                        name + " - " + Long.toHexString(System.currentTimeMillis()) + suffix);
            }
        }

        try {
            file.transferTo(target);
        } catch (IOException e) {
            Logger.getGlobal().warning("文件传输中断");
            throw new ExecutionException("文件上传中断");
        }

        //创建缩略图
        String thumbnailPath = StaticPath + ThumbnailPath + relativePath;
        String thumbnailName = target.getName();

        int dotIdx = thumbnailName.lastIndexOf(".");
        thumbnailName = thumbnailName.substring(0, dotIdx) + ThumbnailSuffix + thumbnailName.substring(dotIdx);

        ImageUtils.generateThumnail(target.getAbsolutePath(), thumbnailPath + "/" + thumbnailName, thumbnailWidth, thumbnailHeight);

        //非测试环境直接返回
        return relativePath + "/" + target.getName();
    }
}
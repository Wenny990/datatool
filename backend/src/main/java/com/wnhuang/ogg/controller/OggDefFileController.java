package com.wnhuang.ogg.controller;


import com.wnhuang.common.domain.response.ApiResult;
import com.wnhuang.ogg.domain.request.OggDefFileBaseRequest;
import com.wnhuang.ogg.domain.request.OggDefFileSaveRequest;
import com.wnhuang.ogg.domain.request.OggDefFileUploadRequest;
import com.wnhuang.ogg.service.OggProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author wnhuang
 * @date 2024/10/14 21:56
 */
@Validated
@RestController
@RequestMapping("defFile")
public class OggDefFileController {

    @Autowired
    OggProcessService oggProcessService;

    @GetMapping
    public ApiResult getDefFileParams(@Validated OggDefFileBaseRequest oggDefFileBaseRequest) {
        return ApiResult.success(oggProcessService.getProcessDefParams(oggDefFileBaseRequest));
    }

    @PostMapping
    public ApiResult saveDefFile(@Validated @RequestBody OggDefFileSaveRequest oggDefFileSaveRequest) {
        return ApiResult.success(oggProcessService.saveProcessDefParams(oggDefFileSaveRequest));
    }

    @GetMapping("/defgen")
    public ApiResult defgenDefFile(@Validated OggDefFileBaseRequest oggDefFileBaseRequest) {
        return ApiResult.success(oggProcessService.defgenDefsFile(oggDefFileBaseRequest));
    }

    @GetMapping("upload")
    public ApiResult uploadDefFile(@Validated OggDefFileUploadRequest oggDefFileUploadRequest) {
        return ApiResult.success(oggProcessService.uploadDefsFile(oggDefFileUploadRequest));
    }

    @GetMapping("content")
    public ApiResult getDefFileContent(@Validated OggDefFileBaseRequest oggDefFileBaseRequest) {
        return ApiResult.success(oggProcessService.getDefsFileContent(oggDefFileBaseRequest));
    }

}

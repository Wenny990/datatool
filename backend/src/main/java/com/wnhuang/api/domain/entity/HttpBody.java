package com.wnhuang.api.domain.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * HTTP请求体配置
 * @author wnhuang
 * @date 2024/12/01
 */
@Data
public class HttpBody {

    /**
     * 请求体类型：none、form-data、x-www-form-urlencoded、binary、raw
     */
    private String type;

    /**
     * form-data参数列表
     */
    private List<FormDataParam> formData;

    /**
     * x-www-form-urlencoded参数列表
     */
    private List<UrlEncodedParam> urlEncoded;

    /**
     * binary文件信息
     */
    private BinaryData binary;

    /**
     * raw数据
     */
    private RawData raw;

    /**
     * Form-Data参数
     */
    @Data
    public static class FormDataParam {
        /**
         * 参数名称
         */
        private String key;

        /**
         * 参数值
         */
        private String value;

        /**
         * 参数类型：text、file
         */
        private String type;

        /**
         * 是否启用
         */
        private Boolean enabled = true;

        /**
         * 描述
         */
        private String description;

        public FormDataParam() {}

        public FormDataParam(String key, String value, String type) {
            this.key = key;
            this.value = value;
            this.type = type;
            this.enabled = true;
        }
    }

    /**
     * URL编码参数
     */
    @Data
    public static class UrlEncodedParam {
        /**
         * 参数名称
         */
        private String key;

        /**
         * 参数值
         */
        private String value;

        /**
         * 是否启用
         */
        private Boolean enabled = true;

        /**
         * 描述
         */
        private String description;

        public UrlEncodedParam() {}

        public UrlEncodedParam(String key, String value) {
            this.key = key;
            this.value = value;
            this.enabled = true;
        }
    }

    /**
     * 二进制数据
     */
    @Data
    public static class BinaryData {
        /**
         * 文件名
         */
        private String fileName;

        /**
         * 文件路径或Base64编码数据
         */
        private String fileData;

        /**
         * 文件类型
         */
        private String contentType;
    }

    /**
     * 原始数据
     */
    @Data
    public static class RawData {
        /**
         * 数据内容
         */
        private String content;

        /**
         * 数据类型：JSON、XML、TEXT、HTML、JavaScript
         */
        private String contentType;

        public RawData() {}

        public RawData(String content, String contentType) {
            this.content = content;
            this.contentType = contentType;
        }
    }
}
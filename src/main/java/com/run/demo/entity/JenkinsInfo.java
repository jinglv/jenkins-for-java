package com.run.demo.entity;

import lombok.Data;

import java.util.Map;

/**
 * @author jinglv
 * @date 2023/4/13 14:17
 */
@Data
public class JenkinsInfo {

    /**
     * Jenkins基础地址
     */
    private String jenkinsBaseUrl;

    /**
     * Jenkins登录名
     */
    private String username;

    /**
     * Jenkins登录密码
     */
    private String password;

    /**
     * Jenkins Job名称
     */
    private String jenkinsJobName;

    /**
     * 执行命令
     */
    private Map<String, String> command;

    /**
     * Jenkins基础模版xml文件名称
     */
    private String jenkinsBaseXml;
}

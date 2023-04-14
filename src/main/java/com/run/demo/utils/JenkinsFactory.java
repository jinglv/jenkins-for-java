package com.run.demo.utils;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 获取Jenkins服务工厂类
 *
 * @author jinglv
 * @date 2023/4/13 14:30
 */
@Slf4j
public class JenkinsFactory {

    /**
     * 获取Jenkins的Java客户端的JenkinsHttpClient
     *
     * @param jenkinsBaseUrl Jenkins基础URL
     * @param username       Jenkins登录用户名
     * @param password       Jenkins登录密码
     * @return JenkinsHttpClient
     */
    public JenkinsHttpClient getJenkinsHttpClient(String jenkinsBaseUrl, String username, String password) {
        JenkinsHttpClient jenkinsHttpClient;
        try {
            jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsBaseUrl), username, password);
        } catch (URISyntaxException e) {
            String tips = "获取Jenkins服务异常" + e.getMessage();
            log.error(tips, e);
            throw new RuntimeException(e);
        }
        return jenkinsHttpClient;
    }

    /**
     * 获取Jenkins的java客户端的JenkinsServer
     *
     * @return JenkinsServer
     */
    public JenkinsServer getJenkinsServer(JenkinsHttpClient jenkinsHttpClient) {
        return new JenkinsServer(jenkinsHttpClient);
    }
}

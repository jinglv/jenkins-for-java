package com.run.demo;

import cn.hutool.core.io.file.FileReader;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Job;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jinglv
 * @date ${DATE} ${TIME}
 */
public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
        String jenkinsBaseUrl = "http://8.140.112.109:8080/jenkins/";
        String username = "admin";
        String password = "admin";
        String newJobName = "Test_Job";

        // 创建Jenkins初始化
        JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsBaseUrl), username, password);
        // 创建jenkins服务
        JenkinsServer jenkinsServer = new JenkinsServer(jenkinsHttpClient);
        // 获取当前所有的Jenkins Job
        Map<String, Job> jobs = jenkinsServer.getJobs();
        // 获取新建的Job
        Job job = jobs.get(newJobName);
        // 读取Jenkins配置文件，FileReader-hutool中的方法
        FileReader fileReader = new FileReader(new File("src/main/resources/jenkins/base_jenkins.xml"));
        String jobXml = fileReader.readString();
        // 更新已存在的Job，参数1：job的名称 参数2：配置文件，参数3：表示是需要登录权限校验，否则会抛出异常：status code: 403, reason phrase: Forbidden
        jenkinsServer.updateJob(newJobName, jobXml, true);
        // 设置Job的参数
        Map<String, String> params = new HashMap<>();
        params.put("userId", "20");
        params.put("remark", "用户Id变为了20");
        params.put("testCommand", "pwd");
        // 执行Job
        job.build(params, true);
    }
}
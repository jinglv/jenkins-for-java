package com.run.demo.utils;

import com.run.demo.entity.JenkinsInfo;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jinglv
 * @date 2023/4/13 15:22
 */
class JenkinsClientTest {

    @Test
    void operateJenkinsJob() {
        JenkinsInfo jenkinsInfo = new JenkinsInfo();
        jenkinsInfo.setJenkinsBaseUrl("http://8.140.112.109:8080/jenkins/");
        jenkinsInfo.setUsername("admin");
        jenkinsInfo.setPassword("admin");
        jenkinsInfo.setJenkinsJobName("Test_Job");
        jenkinsInfo.setJenkinsBaseXml("base_jenkins");
        Map<String, String> params = new HashMap<>();
        params.put("userId", "20");
        params.put("remark", "用户Id变为了20");
        params.put("testCommand", "pwd");
        jenkinsInfo.setCommand(params);

        JenkinsClient jenkinsClient = new JenkinsClient();
        jenkinsClient.operateJenkinsJob(jenkinsInfo);
    }
}
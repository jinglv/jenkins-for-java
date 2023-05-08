package com.run.demo.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.run.demo.entity.JenkinsInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author jinglv
 * @date 2023/4/13 15:22
 */
class JenkinsClientTest {

    @Test
    void operateJenkinsJob() throws IOException {
        JenkinsInfo jenkinsInfo = new JenkinsInfo();
        jenkinsInfo.setJenkinsBaseUrl("http://8.140.112.109:8080/jenkins/");
        jenkinsInfo.setUsername("admin");
        jenkinsInfo.setPassword("admin");
        jenkinsInfo.setJenkinsJobName("Test_Job");
        jenkinsInfo.setJenkinsBaseXml("base_jenkins");
        // Json字符串转成Map<String, String>的方式
        String command = "{\"userId\":20,\"remark\":\"用户Id变为了20\",\"testCommand\":\"pwd\"}";
        Map<String, String> params = new ObjectMapper().readValue(command, new TypeReference<Map<String, String>>() {
        });
        System.out.println(params);
//        Map<String, String> params = new HashMap<>();
//        params.put("userId", "20");
//        params.put("remark", "用户Id变为了20");
//        params.put("testCommand", "pwd");
        jenkinsInfo.setCommand(params);

        JenkinsClient jenkinsClient = new JenkinsClient();
        jenkinsClient.operateJenkinsJob(jenkinsInfo);
    }
}
package com.run.demo.utils;

import cn.hutool.core.io.file.FileReader;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.QueueReference;
import com.run.demo.entity.JenkinsInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @author jinglv
 * @date 2023/4/11 15:17
 */
@Slf4j
public class JenkinsClient {

    /**
     * 操作Jenkins Job
     *
     * @param jenkinsInfo Jenkins信息
     */
    public void operateJenkinsJob(JenkinsInfo jenkinsInfo) {
        JenkinsFactory jenkinsFactory = new JenkinsFactory();
        if (Objects.isNull(jenkinsInfo.getJenkinsBaseXml())) {
            log.error("Jenkins模板名称不能为空！");
        }
        // 读取Jenkins配置文件，FileReader-hutool中的方法
        FileReader fileReader = new FileReader(new File("src/main/resources/jenkins/" + jenkinsInfo.getJenkinsBaseXml() + ".xml"));
        String jobXml = fileReader.readString();

        if (Objects.isNull(jobXml)) {
            log.error("Jenkins模板信息不能为空！");
        }
        // 获取基础信息
        String jenkinsBaseUrl = jenkinsInfo.getJenkinsBaseUrl();
        String username = jenkinsInfo.getUsername();
        String password = jenkinsInfo.getPassword();
        String jenkinsJobName = jenkinsInfo.getJenkinsJobName();
        Map<String, String> command = jenkinsInfo.getCommand();
        if (Objects.isNull(command)) {
            log.error("Jenkins Job请求参数不能为空！");
        }
        // 获取JenkinsHttpClient
        JenkinsHttpClient jenkinsHttpClient = jenkinsFactory.getJenkinsHttpClient(jenkinsBaseUrl, password, username);
        // 创建或者更新Jenkins Job
        try {
            createOrUpdateJobAndRun(jenkinsHttpClient, jenkinsJobName, jobXml, command);
        } catch (IOException e) {
            log.error("Jenkins执行异常");
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建或更新Jenkins的Job并且执行
     *
     * @param jenkinsClient jenkinsClient
     * @param jobName       Jenkins job名称
     * @param jobXml        Jenkins job基础模板
     * @param params        Jenkins Job执行的参数
     * @throws IOException io异常
     */
    private void createOrUpdateJobAndRun(JenkinsHttpClient jenkinsClient, String jobName, String jobXml, Map<String, String> params) throws IOException {
        JenkinsFactory jenkinsFactory = new JenkinsFactory();
        JenkinsServer jenkinsServer = jenkinsFactory.getJenkinsServer(jenkinsClient);
        Job job;
        try {
            Map<String, Job> jobs = jenkinsServer.getJobs();
            job = jobs.get(jobName);
        } catch (IOException e) {
            log.error("获取Jenkins当前Job失败！");
            throw new RuntimeException(e);
        }
        if (Objects.isNull(job)) {
            // 获取job为空则进行新建
            try {
                jenkinsServer.createJob(jobName, jobXml, true);
            } catch (IOException e) {
                log.error("Jenkins Job新建失败！");
                throw new RuntimeException(e);
            }
        } else {
            // 获取job存在则进行新更新
            try {
                jenkinsServer.updateJob(jobName, jobXml, true);
            } catch (IOException e) {
                log.error("Jenkins Job更新失败！");
                throw new RuntimeException(e);
            }
        }
        build(job, params);
    }

    /**
     * 构建无参Jenkins Job
     *
     * @param job Jenkins Job
     * @return QueueReference
     * @throws IOException 抛出io异常
     */
    private QueueReference build(Job job) throws IOException {
        return build(job, null);
    }

    /**
     * 构建带参Jenkins Job
     *
     * @param job    Jenkins Job
     * @param params Jenkins构建传入的参数
     * @return QueueReference
     * @throws IOException 抛出io异常
     */
    private QueueReference build(Job job, Map<String, String> params) throws IOException {
        QueueReference queueReference;
        if (Objects.isNull(params)) {
            queueReference = job.build(true);
        } else {
            queueReference = job.build(params, true);
        }
        return queueReference;
    }
}

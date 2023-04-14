# Jenkins调用

## 创建工程

创建maven项目，添加依赖

- 调用Jenkins

```xml

<dependency>
    <groupId>com.offbytwo.jenkins</groupId>
    <artifactId>jenkins-client</artifactId>
    <version>0.3.8</version>
</dependency>
```

- hutool工具类，进行文件读取、数据转换等

```xml

<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.15</version>
</dependency>
```

## 常用类 - JenkinsHttpClient

封装了调用JenkinsAPI底层方法：

- JenkinsHttpClient(URI uri, String username, String password)
- get(String path)
- getFile(URI path)
- post(String path, boolean crumbFlag)
- post(String path, D data, Class<R> cls)
- post_xml(String path, String xml_data, boolean crumbFlag)
- ......

![image-20220916155214138](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209161552213.png)

## 常用类 - JenkinsServer

封装了调用JenkinsAPI的语义级别的方法：

- JenkinsServer(JenkinsHttpClient client)
- getJob(String jobName)
- createJob(String jobName, String jobXml, BooleanCrumbFlag)
- updateJob(String jobName, String jobXml, BooleanCrumbFlag)
- getJobXml(String jobName)
- deleteJob(FolderJob folder, String jobName, boolean crumbFlag)
- ......

![image-20220916155232045](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209161552082.png)

## 常用类-Job

Jenkins中job对应实体类，有很多实用的语义级别的方法

- Job(String name, String url)
- build(Job job)
- build(Job job, Map<String, String> params)
- getFileFromWorkspace(String fileName)
- setClient(JenkinsHttpConnection client)
- ......

![image-20220916155258133](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209161552220.png)

## 查看Jenkins Api

![image-20220916155146258](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209161551404.png)

Jenkins接口地址：http://{{JENKINS_BASE_URL}}/api/

## 示例

根据Jenkins Job的模板，创建新的Job并执行

### 获取Jenkins Job模板内容

1. 创建一个Jenkins Job
    - 新建一个自由风格的Jenkins Job，并新增一些参数

      ![image-20230412111937123](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202304121119256.png)

2. 获取Jenkins Job的配置数据并保存

    - 到Jenkins服务器上，进入到jobs中查看config.xml

      <img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202304121128375.png" alt="image-20230412112851255" style="zoom:50%;" />

    - 或者请求地址：http://{{JENKINS_BASE_URL}}/job/{{JOB_NAME}}/config.xml

      ![image-20230412112155289](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202304121121321.png)

将获取到的配置内容，拷贝粘贴到我们指定的xml文件。



### 编写代码，操作Jenkins

#### 创建新的Job

```java
    public static void main(String[] args) throws URISyntaxException, IOException {
        String jenkinsBaseUrl = "http://8.140.112.109:8080/jenkins/";
        String username = "admin";
        String password = "admin";
        String newJobName = "Test_Job";

        // 创建Jenkins初始化
        JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsBaseUrl), username, password);
        // 创建jenkins服务
        JenkinsServer jenkinsServer = new JenkinsServer(jenkinsHttpClient);
        // 读取Jenkins配置文件，FileReader-hutool中的方法
        FileReader fileReader = new FileReader(new File("src/main/resources/jenkins/base_jenkins.xml"));
        String jobXml = fileReader.readString();
        // 创建新的Job，参数1：job的名称 参数2：配置文件，参数3：表示是需要登录权限校验，否则会抛出异常：status code: 403, reason phrase: Forbidden
        jenkinsServer.createJob(newJobName, jobXml, true);
    }
```

代码执行后，到Jenkins看就会发现，多一个名为Test_Job的Jenkins Job

![image-20230413100501288](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202304131005408.png)

点进Test_Job后，再点击【配置】，我们看到，配置内容，与指定的模板一致。



#### 执行Job

已经创建了Job，接下来，就要执行这个Job

```java
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
        // 设置Job的参数
        Map<String, String> params = new HashMap<>();
        params.put("userId", "20");
        params.put("remark", "用户Id变为了20");
        // 执行Job
        job.build(params, true);
    }
```

代码执行后，Jenkins中，到Test_Job里，看到已经有执行的build

![image-20230413103807556](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202304131038694.png)

可进入到#1里面查看构建的参数

![image-20230413103856764](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202304131038807.png)

可以看到，与我们设置的参数一致。



#### Jenkins命令执行操作

1. Jenkins配置文件新增参数，并配置执行命令

   ```xml
   <project>
       <description>Jenkins基础模版</description>
       <keepDependencies>false</keepDependencies>
       <properties>
           <hudson.model.ParametersDefinitionProperty>
               <parameterDefinitions>
                   <hudson.model.StringParameterDefinition>
                       <name>userId</name>
                       <description>用户id</description>
                       <defaultValue>12</defaultValue>
                       <trim>false</trim>
                   </hudson.model.StringParameterDefinition>
                   <hudson.model.TextParameterDefinition>
                       <name>remark</name>
                       <description/>
                       <defaultValue>Jenkins调用</defaultValue>
                       <trim>false</trim>
                   </hudson.model.TextParameterDefinition>
                   <hudson.model.TextParameterDefinition>
                       <name>testCommand</name>
                       <description>测试命令</description>
                       <defaultValue>测试命令演示</defaultValue>
                       <trim>false</trim>
                   </hudson.model.TextParameterDefinition>
               </parameterDefinitions>
           </hudson.model.ParametersDefinitionProperty>
       </properties>
       <scm class="hudson.scm.NullSCM"/>
       <canRoam>true</canRoam>
       <disabled>false</disabled>
       <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
       <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
       <triggers/>
       <concurrentBuild>false</concurrentBuild>
       <builders>
           <hudson.tasks.Shell>
               <command>
                   eval "${testCommand}"
               </command>
           </hudson.tasks.Shell>
       </builders>
       <publishers/>
       <buildWrappers/>
   </project>
   ```

   

2. 编写代码，更新已存在Job，传入命令执行

   ```java
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
   ```

   

3. 代码执行成功，再到Jenkins查看构建结果

   ![image-20230413140923759](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202304131409822.png)

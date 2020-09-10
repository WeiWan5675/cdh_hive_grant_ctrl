# CDH集群HiveAdmin权限实现


## 基于AbstractSemanticAnalyzerHook
    AbstractSemanticAnalyzerHook 是Hive提供的客户端钩子,可以在客户端执行sql命令时,实现一些自定义的操作,如权限校验,SQL补全等。

## 使用
- 打包

```mvn clean install -DSpikTest```
- 依赖复制到lib目录

```shell
sudo cp /home/easylife/cdh_hive_grant_ctrl-1.0-SNAPSHOT.jar /opt/cloudera/parcels/CDH/lib/hive/lib
```

- 分发依赖

```shell
for i in `grep -o test-hadoop-[2-9] /etc/hosts`; 
do 
scp -P65508 cdh_hive_grant_ctrl-1.0-SNAPSHOT.jar $i:/tmp;
ssh -p65508 $i 
"sudo mv /tmp/cdh_hive_grant_ctrl-1.0-SNAPSHOT.jar /opt/cloudera/parcels/CDH/lib/hive/lib"; 
done
```


- 修改Hive配置

修改hive-site.xml 的 HiveServer2 高级配置代码段（安全阀） 配置文件 添加如下
```xml
<property>
    <name>hive.semantic.analyzer.hook</name>
    <value>com.hopson.hive.grant.GrantController</value>
</property>
```


- 重启Hive
    验证相关权限
    
## 相关依赖

如果是CDH版本的,需要使用CDH依赖

```xml
    <repositories>
        <repository>
            <id>cloudera</id>
            <url>https://repository.cloudera.com/artifactory/cloudera-repos/</url>
        </repository>
    </repositories>

    <dependencies>
		<!-- <dependency>-->
		<!-- <groupId>org.apache.hive</groupId>-->
		<!-- <artifactId>hive-exec</artifactId>-->
		<!-- <version>1.1.0-cdh5.16.2</version>-->
		<!-- </dependency> -->
        
        <dependency>
            <groupId>org.apache.hive</groupId>
            <artifactId>hive-exec</artifactId>
            <version>1.1.0</version>
        </dependency>

        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>2.6.0</version>
        </dependency>
    </dependencies>
```

## 权限CODE

由于CDH版本的Hive和Apache原生的HiveTokenType不一致，简单整理了常用的CODE:

```
case 799: //REVOKE SELECT on database easylife_ods from user easylife_analysis;
case 825: //show roles
case 652: //create role test;
case 681: //drop role test;
case 824: //SHOW GRANT;
case 826: //SHOW ROLE GRANT user easylife;
case 698: //GRANT select on database easylife_ods to user easylife_analysis;
```
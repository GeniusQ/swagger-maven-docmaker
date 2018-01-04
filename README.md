# Swagger Maven Docmaker 
[![Build Status](https://travis-ci.org/GeniusQ/swagger-maven-docmaker.png)](https://travis-ci.org/GeniusQ/swagger-maven-docmaker)

Swagger Maven Docmaker用于java开发文档自动生成，它将提取您项目中的Swagger注解并生成swagger.json和md文件。


# 产品特性

* 支持非Resful Api类的文档生成，但无法调试；
* Supports [Swagger Spec 2.0](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md)
* Supports [SpringMvc](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) & [JAX-RS](https://jax-rs-spec.java.net/)
* Quickly generates *[swagger.json](https://github.com/kongchen/swagger-maven-example/blob/master/generated/swagger-ui/swagger.json)* and [static document](http://htmlpreview.github.io/?https://raw.github.com/kongchen/swagger-maven-example/master/generated/document.html) by `mvn compile`
* Use [Handlebars](http://handlebarsjs.com/) as template to customize the static document.

# 版本日志
- [1.0.0](https://github.com/geniusq/swagger-maven-docmaker/) 支持非Resful Api类的文档生成 (**ACTIVE!**)
- [3.1.0](https://github.com/kongchen/swagger-maven-plugin/) supports Swagger Spec [2.0](https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md), support JAX-RS & SpingMVC. (**ACTIVE!**)

# 使用说明

* 使用swagger注解来编写java代码的注释，如下

```
// Controller Api 注释范例
@Api("住院相关API")
public class InHospitalApi extends BaseApi {
    @ApiOperation(value="4.3.1 住院前获取病人信息", 
        notes="医保方面信息",response = GetInfoResp.class)
    public GetInfoResp getInfo(
      @ApiParam(value="参数说明",name="参数名") @RequestBody GetInfoReq req)
    {
      return null;
    }
}

// DTO 注释范例
@ApiModel("日志对象")
public class Log
{
    @ApiModelProperty("模块名称")
    private String moduleName;
    @ApiModelProperty("操作/事件名称")
    private String eventName;
    @ApiModelProperty("异常描述")
    private String description;
}
```

* 如果@Api不存在，需要在maven引入swagger注解

```xml
<!-- https://mvnrepository.com/artifact/io.swagger/swagger-annotations -->
<dependency>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-annotations</artifactId>
    <version>1.5.17</version>
</dependency>
```

* 下载Swagger Templates，放入项目的 `templates` 目录中

[模版下载地址 https://github.com/GeniusQ/swagger-maven-docmaker/tree/master/src/main/resources/templates](https://github.com/GeniusQ/swagger-maven-docmaker/tree/master/src/main/resources/templates)

* 在pom.xml的 `plugins` 节点中加入: 

```xml
<plugin>
    <groupId>com.github.geniusq</groupId>
    <artifactId>swagger-maven-docmaker</artifactId>
    <version>${swagger-maven-docmaker-version}</version>
    <configuration>
        <apiSources>
            <apiSource>
                <springmvc>false</springmvc>
                <locations>
                    <location>扫描的包名</location>
                </locations>
                <schemes>
                    <scheme>http</scheme>
                </schemes>
                <!-- api服务器地址和端口，非ResfulApi项目随便填 -->
                <host>127.0.0.1:8080</host>
                 <!-- api路径，非ResfulApi项目随便填 -->
                <basePath>/xxx-api/webapi</basePath>
                <info>
                    <title>${artifactId}-${version}</title>
                    <version>v1</version>
                    <description>项目说明</description>
                </info>
                <templatePath>${basedir}/templates/hymd.hbs</templatePath>
                <outputPath>${basedir}/doc/${artifactId}-${version}.md</outputPath>
                <swaggerDirectory>${basedir}/doc/${artifactId}/${version}/</swaggerDirectory>
            </apiSource>
        </apiSources>
    </configuration>
</plugin>
```

# 打开maven project面板，plugins->swaggerdocmaker，双击generate生成json和md文件

# 暂时没有了

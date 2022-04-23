package com.example.istar.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @author tian
 */
public class MyBatisGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3309/istar", "root", "123456")
                .globalConfig(builder -> {
                    builder.author("tian") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("/Users/tian/projects/java/istar/src/main/java/"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.example")
                            .moduleName("istar")// 设置父包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "/Users/tian/projects/java/istar/src/main/resources/mappers")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok() // 开启lombok
                            .mapperBuilder().enableMapperAnnotation().build(); // 开启注解
//                    builder.controllerBuilder().enableHyphenStyle().enableRestStyle(); // 开启驼峰转连字符
                    builder.addInclude("t_user","t_video")// 设置需要生成的表名
                            .addTablePrefix("t_"); // 设置过滤表前缀
                })
                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}

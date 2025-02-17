package com.Heypon.maker.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public class ScriptGenerator {
    /**
     * 生成运行脚本文件
     * @param outputPath 生成文件的路径
     * @param jarPath jar包的路径
     */
    public static void doGenerate(String outputPath, String jarPath) {
        // linux 脚本
        StringBuffer stringBash = new StringBuffer();
        // #!/bin/bash
        // java -jar target/Heypon-CodeGenerator-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"
        stringBash.append("#!/bin/bash").append("\n");
        stringBash.append(String.format("java -jar %s \"$@\"", jarPath)).append("\n");
        FileUtil.writeBytes(stringBash.toString().getBytes(StandardCharsets.UTF_8), outputPath);

        // 添加可执行权限
        try{
            Set<PosixFilePermission> permissionSet = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(Paths.get(outputPath), permissionSet);
        }catch (Exception e){
        }

        // windows 脚本
        // @echo off
        // java -jar target/Heypon-CodeGenerator-1.0-SNAPSHOT-jar-with-dependencies.jar "%*"
        stringBash = new StringBuffer();
        stringBash.append("@echo off").append("\n");
        stringBash.append(String.format("java -jar %s %%*", jarPath)).append("\n");
        FileUtil.writeBytes(stringBash.toString().getBytes(StandardCharsets.UTF_8), outputPath + ".bat");
    }
}

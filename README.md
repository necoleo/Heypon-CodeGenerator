# Heypon 代码生成器项目

## 一、项目概述
Heypon 代码生成器是一个能依据配置文件动态生成 Java 项目代码的工具。它支持静态文件复制和动态模板生成功能，还可将生成的项目打包成可执行的 JAR 文件，并生成对应的运行脚本，有助于开发者快速搭建和定制项目。


## 二、目录结构描述
```plaintext
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── Heypon
│   │   │           └── maker
│   │   │               ├── cli
│   │   │               │   ├── command
│   │   │               │   │   ├── ConfigCommand.java    // 查看配置文件信息的命令类
│   │   │               │   │   ├── GenerateCommand.java  // 生成代码的命令类
│   │   │               │   │   └── ListCommand.java      // 查看文件列表的命令类
│   │   │               │   └── CommandExecutor.java      // 命令执行器类
│   │   │               ├── generator
│   │   │               │   ├── file
│   │   │               │   │   ├── DynamicFileGenerator.java  // 动态文件生成器类
│   │   │               │   │   ├── FileGenerator.java         // 主代码生成器类
│   │   │               │   │   └── StaticFileGenerator.java   // 静态文件生成器类
│   │   │               │   ├── JarGenerator.java          // JAR 包生成器类
│   │   │               │   ├── MainGenerator.java         // 项目入口类
│   │   │               │   └── ScriptGenerator.java       // 脚本生成器类
│   │   │               ├── meta
│   │   │               │   ├── Meta.java                  // 配置文件数据模型类
│   │   │               │   └── MetaManager.java           // 配置文件管理类
│   │   │               └── model
│   │   │                   └── DataModel.java             // 数据模型类
│   │   └── resources
│   │       ├── templates                                  //ftl模板目录
│   │       │   ├── java
│   │       │   │   ├── cli
│   │       │   │   │   ├── command
│   │       │   │   │   │   ├── ConfigCommand.java.ftl
│   │       │   │   │   │   ├── GenerateCommand.java.ftl
│   │       │   │   │   │   └── ListCommand.java.ftl
│   │       │   │   │   └── CommandExecutor.java.ftl
│   │       │   │   ├── generator
│   │       │   │   │   └── file
│   │       │   │   │       ├── DynamicFileGenerator.java.ftl
│   │       │   │   │       ├── FileGenerator.java.ftl
│   │       │   │   │       └── StaticFileGenerator.java.ftl
│   │       │   │   ├── model
│   │       │   │   │   └── DataModel.java.ftl
│   │       │   │   └── Main.java.ftl
│   │       │   └── pom.xml.ftl
│   │       └── meta.json              // 项目配置文件
│   └── test
│       └── java
│           └── （待添加测试类）
├── target                          // 生成的 JAR 包和脚本存放目录
└── README.md                       // 项目帮助文档
```

## 三、使用步骤

### 1. 配置 `meta.json` 文件
根据项目需求，修改 `meta.json` 文件中的配置信息，包括项目基本信息、文件输入输出路径和模型配置等内容。示例配置如下：
```json
{
    "name": "Heypon-CodeGenerator",
    "description": "A code generator for Java projects",
    "basePackage": "com.Heypon.maker",
    "version": "1.0-SNAPSHOT",
    "author": "Heypon",
    "createTime": "2025-02-17",
    "fileConfig": {
        "inputRootPath": "path/to/input",
        "outputRootPath": "path/to/output",
        "type": "file",
        "files": [
            {
                "inputPath": "",
                "outputPath": "",
                "type": "java",
                "generateType": "dynamic"
            }
        ]
    },
    "modelConfig": {
        "models": [
            {
                "fieldName": "author",
                "type": "String",
                "description": "The author of the code",
                "defaultValue": "Heypon",
                "abbr": "a"
            }
        ]
    }
}
```

### 2. 运行 `MainGenerator` 类
在 IntelliJ IDEA 中，找到 `MainGenerator` 类，右键点击并选择 `Run 'MainGenerator.main()'` 来执行该类的 `main` 方法。程序会根据 `meta.json` 中的配置信息生成代码、打包成 JAR 文件并生成运行脚本。

### 3. 使用命令行工具
生成的 JAR 文件和运行脚本位于 `target` 目录下。你可以使用以下命令执行不同操作：
- **查看配置信息**：
    - 在 Linux/Mac 系统下：`./generator config`
    - 在 Windows 系统下：`generator.bat config`
- **生成代码**：
    - 在 Linux/Mac 系统下：`./generator generate --author=John --outputText=Result`
    - 在 Windows 系统下：`generator.bat generate --author=John --outputText=Result`
- **查看文件列表**：
    - 在 Linux/Mac 系统下：`./generator list`
    - 在 Windows 系统下：`generator.bat list`

## 五、注意事项
- `meta.json` 文件中的路径配置需根据实际情况修改，保证输入和输出路径的正确性，避免文件找不到或生成位置错误。
- 使用 `GenerateCommand` 生成代码时，输入的参数要与 `meta.json` 中的模型配置一致，否则可能引发参数不匹配的错误。

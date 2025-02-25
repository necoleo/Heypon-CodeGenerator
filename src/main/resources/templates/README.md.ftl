# ${name}

> ${description}
>
> 作者: ${author}
>
> 基于 https://github.com/necoleo/Heypon-CodeGenerator , 感谢您的使用！

可以通过命令行交互输入的方式动态生成想要的项目代码

## 使用说明

执行项目根目录的 generator 脚本文件:
```
generator <命令> <选项参数>
```
示例命令 :

```
<#list modelConfig.models as modelInfo>
<#if modelInfo.abbr??>
    generator generate -${modelInfo.abbr}
</#if>
</#list>
```

<#list modelConfig.models as modelInfo>
<#if modelInfo.fieldName??>
${modelInfo?index + 1} ${modelInfo.fieldName}

类型: ${modelInfo.type}

描述: ${modelInfo.description}

默认值: ${modelInfo.defaultValue?c}

<#if modelInfo.abbr??>
    缩写: -${modelInfo.abbr}
</#if>
</#if>

</#list>
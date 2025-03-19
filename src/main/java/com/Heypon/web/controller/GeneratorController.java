package com.Heypon.web.controller;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.Heypon.maker.generator.main.GenerateTemplate;
import com.Heypon.maker.generator.main.ZipGenerator;
import com.Heypon.maker.meta.Meta;
import com.Heypon.web.annotation.AuthCheck;
import com.Heypon.web.common.BaseResponse;
import com.Heypon.web.common.DeleteRequest;
import com.Heypon.web.common.ErrorCode;
import com.Heypon.web.common.ResultUtils;
import com.Heypon.web.constant.UserConstant;
import com.Heypon.web.exception.BusinessException;
import com.Heypon.web.exception.ThrowUtils;
import com.Heypon.web.manager.CosManager;
import com.Heypon.web.model.dto.generator.*;
import com.Heypon.web.model.entity.Generator;
import com.Heypon.web.model.entity.User;
import com.Heypon.web.model.vo.GeneratorVO;
import com.Heypon.web.service.GeneratorService;
import com.Heypon.web.service.UserService;
import com.Heypon.maker.meta.MetaValidator;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.BindException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 代码生成器接口
 */
@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;


    // region 增删改查

    /**
     * 创建
     *
     * @param generatorAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest generatorAddRequest, HttpServletRequest request) {
        if (generatorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorAddRequest, generator);
        List<String> tags = generatorAddRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorAddRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorAddRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));

        // 参数校验
        generatorService.validGenerator(generator, true);
        User loginUser = userService.getLoginUser(request);
        generator.setUserId(loginUser.getId());
        generator.setStatus(0);

        boolean result = generatorService.save(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newGeneratorId = generator.getId();
        return ResultUtils.success(newGeneratorId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = generatorService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param generatorUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateGenerator(@RequestBody GeneratorUpdateRequest generatorUpdateRequest) {
        if (generatorUpdateRequest == null || generatorUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorUpdateRequest, generator);
        List<String> tags = generatorUpdateRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorUpdateRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorUpdateRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        // 参数校验
        generatorService.validGenerator(generator, false);
        long id = generatorUpdateRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<GeneratorVO> getGeneratorVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(generatorService.getGeneratorVO(generator, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param generatorQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Generator>> listGeneratorByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                 HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listMyGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                   HttpServletRequest request) {
        if (generatorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        generatorQueryRequest.setUserId(loginUser.getId());
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param generatorEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editGenerator(@RequestBody GeneratorEditRequest generatorEditRequest, HttpServletRequest request) {
        if (generatorEditRequest == null || generatorEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorEditRequest, generator);
        List<String> tags = generatorEditRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorEditRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorEditRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        // 参数校验
        generatorService.validGenerator(generator, false);
        User loginUser = userService.getLoginUser(request);
        long id = generatorEditRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldGenerator.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }


    /**
     * 制作代码生成器
     * 接收从客户端传来的 项目模板压缩包，从 COS对象存储 下载项目原文件，利用代码生成器制作工具，生成定制化项目文件，打包发送给客户端
     * @param generatorMakeRequest
     * @param request
     */
    @PostMapping("/make")
    public void makeGenerator(@RequestBody GeneratorMakeRequest generatorMakeRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 输入参数
        Meta meta = generatorMakeRequest.getMeta();
        String zipFilePath = generatorMakeRequest.getZipFilePath();

        // 用户登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId = {} 在线制作生成器", loginUser.getId());

        // 创建独立的工作空间，下载压缩包到本地
        String projectPath = System.getProperty("user.dir");
        String id = IdUtil.getSnowflakeNextId() + RandomUtil.randomString(6);
        String tempDirPath = String.format("%s/.temp/make/%s", projectPath, id);
        String localZipFilePath = tempDirPath + File.separator + "project.zip";

        if (!FileUtil.exist(localZipFilePath)) {
            FileUtil.touch(localZipFilePath);
        }

        // 下载文件
        // COS存储的是 模板项目 的压缩包
        try{
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            cosManager.download(zipFilePath, localZipFilePath);
            stopWatch.stop();
            System.out.println(String.format("下载文件：%s 毫秒", stopWatch.getTotalTimeMillis()));
        }catch(Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩包下载失败");
        }

        // 解压，得到项目模板文件
        File unzipDistDir = ZipUtil.unzip(localZipFilePath);
        // 构造 meta 对象和生成器的输出路径
        String sourceRootPath = unzipDistDir.getAbsolutePath();
        meta.getFileConfig().setSourceRootPath(sourceRootPath);

        // 检验和处理默认值
        MetaValidator.doValidAndFill(meta);
        String outputPath = tempDirPath + File.separator + "generated" + File.separator + meta.getName();

        // 调用 maker 方法制作生成器
        GenerateTemplate generateTemplate = new ZipGenerator();
        try{
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            generateTemplate.doGenerate(meta, outputPath);
            stopWatch.stop();
            System.out.println(String.format("制作耗时：%s 毫秒", stopWatch.getTotalTimeMillis()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"制作失败");
        }

        // 下载制作好的生成器压缩包
        String suffix = "-dist.zip";
        String zipFileName = meta.getName() + suffix;
        // 生成器压缩包的绝对路径
        String distZipFilePath = outputPath + suffix;

        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + zipFileName);
        Files.copy(Paths.get(distZipFilePath), response.getOutputStream());

        // 清理工作空间的文件
        CompletableFuture.runAsync(() -> {
            FileUtil.del(tempDirPath);
        });
    }

    /**
     *
     * @param id
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/download")
    public void downloadGeneratorById(long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Generator generator = generatorService.getById(id);
        if (generator == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String filePath = generator.getDistPath();
        if(StrUtil.isBlank(filePath)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        // 追踪事件
        log.info("用户 {} 下载了 {}", loginUser, filePath);

        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + filePath);

        COSObjectInputStream cosObjectInput = null;
        try{
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            COSObject cosObject = cosManager.getObject(filePath);
            cosObjectInput = cosObject.getObjectContent();

            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);

            stopWatch.stop();
            System.out.println("下载耗时: " + stopWatch.getTotalTimeMillis());

            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        }catch (Exception e){
            log.error("file download error, filePath = " + filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"下载失败");
        }finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }

    /**
     * 使用代码生成器
     *
     * @param generatorUseRequest
     * @param request
     * @param response
     * @throws IOException
     */
    @PostMapping("/use")
    public void useGenerator(@RequestBody GeneratorUseRequest generatorUseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取用户输入的请求
        Long id = generatorUseRequest.getId();
        Map<String, Object> dataModel = generatorUseRequest.getDataModel();

        // 需要用户登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId = {} 使用了生成器 id = {}", loginUser.getId(), id);

        Generator generator = generatorService.getById(id);
        if (generator == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 生成器的存储路径
        String distPath = generator.getDistPath();
        if(StrUtil.isBlank(distPath)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        // 从对象存储下载生成器的压缩包
        // 定义独立的工作空间
        String projectPath = System.getProperty("user.dir");
        // 必须要用 userId 区分，否则可能导致输入参数文件冲突
        String tempDirPath = String.format("%s/.temp/use/%s/%s", projectPath, id, loginUser.getId());
        String zipFilePath = tempDirPath + File.separator + "dist.zip";
        // 目录不存在则创建
        if (!FileUtil.exist(tempDirPath)){
            FileUtil.mkdir(tempDirPath);
        }

        if (!FileUtil.exist(zipFilePath)){
            FileUtil.touch(zipFilePath);
            try{
                cosManager.download(distPath,zipFilePath);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"生成器下载失败");
            }
        }

        // 解压压缩包，得到脚本文件
        File unzipDistDir = ZipUtil.unzip(zipFilePath);

        // 将用户输入的参数写到 json 文件中
        String dataModelFilePath = tempDirPath + File.separator + "dataModel.json";
        String jsonStr = JSONUtil.toJsonStr(dataModel);
        FileUtil.writeUtf8String(jsonStr, dataModelFilePath);

        // 执行脚本
        // 找到脚本文件所在路径
        // 注意区分 WINDOWS 还是 其他系统
        File scriptFile = FileUtil.loopFiles(unzipDistDir, 2 ,null)
                .stream()
                .filter(file -> file.isFile()
                        && "generator".equals(file.getName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        // 添加可执行权限
        try{
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(scriptFile.toPath(), permissions);
        }catch (Exception e){

        }

        // 构造命令
        File scriptDir = scriptFile.getParentFile();
        String scriptAbsolutePath = scriptFile.getAbsolutePath();
        String[] command;
        // 区分操作系统
        if(System.getProperty("os.name").toLowerCase().contains("windows")){
            scriptAbsolutePath = scriptFile.getAbsolutePath().replace("\\", "/") + ".bat";
        }

        command = new String[]{scriptAbsolutePath, "json-generate", "--file=" + dataModelFilePath};

        // 这里一定要拆分
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(scriptDir);

        try{
            Process process = processBuilder.start();
            // 读取命令的输出
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            System.out.println("命令执行结束，退出码：" + exitCode);

        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "执行生成器脚本错误: " + e.getMessage());
        }

        // 压缩生成结果，返回给前端
        String generatedPath = scriptDir.getAbsolutePath() + File.separator + "generated";
        String resultPath = tempDirPath + File.separator + "result.zip";
        File resultFile = ZipUtil.zip(generatedPath, resultPath);

        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + resultFile.getName());
        Files.copy(resultFile.toPath(), response.getOutputStream());

        // 清理文件
        CompletableFuture.runAsync(() -> {
            FileUtil.del(tempDirPath);
        });
    }


}

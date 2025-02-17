package ${basePackage}.generator.file;

import cn.hutool.core.io.FileUtil;
import ${basePackage}.model.DataModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class DynamicFileGenerator {
    /**
     * 动态生成
     * 根据 model 文件夹里的数据模型配置生成可变参数的模板文件
     * @param inputPath 输入路径，即ftl模板文件路径
     * @param outputPath 输出路径，即文件保存路径
     * @param model 数据模型
     */
    public static void doDynamicGenreate(String inputPath, String outputPath, Object model) throws IOException, TemplateException {

        File inputFile = new File(inputPath).getCanonicalFile();

        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        // template 文件的目录
        File templateDir = new File(inputFile.getParent());

        // 指定模板文件所在的路径
        configuration.setDirectoryForTemplateLoading(templateDir);

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        // 官方文档http://freemarker.foofun.cn/app_faq.html#faq_number_grouping
        // 设置数字的格式
        configuration.setNumberFormat("0.######");

        // 创建数据模型
        String templateName = FileUtil.getName(inputFile);
        Template template = configuration.getTemplate(templateName);

        // 文件不存在则新建文件
        if (!FileUtil.exist(outputPath)){
            FileUtil.touch(outputPath);
        }

        Writer outputFile = new FileWriter(outputPath);

        template.process(model, outputFile);

        outputFile.close();
    }
}

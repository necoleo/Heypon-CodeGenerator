package ${basePackage}.cli;

import ${basePackage}.cli.command.ConfigCommand;
import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.command.ListCommand;
import picocli.CommandLine;

/**
 *  命令执行器
 *  关联 ConfigCommand、ListCommand、GenerateCommand
 */
@CommandLine.Command(name = "${name}", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;
    {
        commandLine = new CommandLine(this)
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ListCommand());

    }

    /**
     * 在一开始运行时会打印提示
     */
    @Override
    public void run(){
        //不输入子命令时，给出友好提示
        System.out.println("请输入具体命令，如需帮助请输入--help");
    }

    /**
     * 执行命令
     * @param args 接受命令框输入的字符串
     * @return
     */
    public Integer doExecute(String[] args) {
        return commandLine.execute(args);
    }
}
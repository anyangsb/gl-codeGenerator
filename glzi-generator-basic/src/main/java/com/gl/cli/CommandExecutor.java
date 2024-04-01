package com.gl.cli;

import com.gl.cli.command.ConfigCommand;
import com.gl.cli.command.GenerateCommand;
import com.gl.cli.command.ListCommand;
import picocli.CommandLine;

@CommandLine.Command(name = "glzi",mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable{

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ListCommand())
                .addSubcommand(new ConfigCommand());
    }


    @Override
    public void run() {
        System.out.println("请输入具体命令，或输入--help 查看命令提示");
    }

    public Integer doExecute(String[] args){
        return commandLine.execute(args);
    }
}

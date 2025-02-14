package com.Heypon;

import com.Heypon.cli.CommandExcutor;

public class Main {
    public static void main(String[] args) {
//        args = new String[]{"--help"};
        args = new String[]{"generate", "-a", "-o", "-l"};
//        args = new String[]{"config"};
//        args = new String[]{"list"};
        CommandExcutor commandExcutor = new CommandExcutor();
        commandExcutor.doExecute(args);
    }
}
package brainfuck;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import java.io.File;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

@Command(name = "brainfuck", mixinStandardHelpOptions = true, version = "brainfuck 1.0", description = "This challenge is to build your own brainfuck implementation. In console mode, press enter to close")
public class Brainfuck implements Callable<Result> {

    public static void main(String[] args) {
        var brainfuck = new Brainfuck();
        var cmd = new CommandLine(brainfuck);
        var exitCode = cmd.execute(args);
        Result result = cmd.getExecutionResult();
        if (result != null && result.toString() != null) {
            System.exit(exitCode);
        }
    }

    @Parameters(arity = "0..1", description = "parameter file to execute")
    File file;

    @Option(names= "-c", arity = "0..1", description = "executes the command passed")
    String command;

    @Option(names = "-v", description = "verbose model level 1")
    boolean verbose = false;

    @Option(names = "-vv", description = "verbose model level 2")
    boolean verbose2 = false;

    @Override
    public Result call() throws Exception {
        if (file != null) {
            if (file.exists()) {
                new BrainfuckCommandline().run(file);
            } else {
                System.out.println("Error: file "+file.getName()+" does not exist.");
            }
        } else {
            if (command == null) {
                new BrainfuckCommandline().run();
            } else {
                new BrainfuckCommandline().run(command);
            }
        }
        return new Result();
    }
}
package brainf_ck;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "brainf_ck", mixinStandardHelpOptions = true, version = "brainfuck 1.0", description = "This challenge is to build your own brainfuck implementation. In console mode, press enter to close")
public class Brainf_ck implements Callable<Result> {

    public static int _verbose = 0;
    public static int _timeout = 100;
    public static boolean _clearScreen = false;
    public static boolean _interpret = false;
    public static int _bufferSize = 50;
    private static boolean _clearAfterInput = false;

    public static boolean verbose() {
        return _verbose >= 1;
    }
    public static boolean verbose2() {
        return _verbose >= 2;
    }

    public static boolean clearScreen() {
        return _clearScreen;
    }

    public static boolean interpret() {
        return _interpret;
    }
    public static int bufferSize() {
        return _bufferSize;
    }

    public static boolean clearAfterInput() {
        return _clearAfterInput;
    }

    public static void main(String[] args) {
        var brainfuck = new Brainf_ck();
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

    @Option(names= "-t", arity = "0..1", description = "timeout in ms")
    int timeout;

    @Option(names = "-v", description = "verbose model level 1")
    boolean verbose = false;

    @Option(names = "-vv", description = "verbose model level 2")
    boolean verbose2 = false;

    @Option(names = "-clear", description = "clear screen at every step")
    boolean clear = false;

    @Option(names = "-i", description = "use interpreter only instead of byte code")
    boolean interpret = false;

    @Option(names = "-ci", description = "clear the screen after input")
    boolean clearAfterInput = false;

    @Option(names = "-b", description = "size of the outputbuffer = default 50. e.g for each use -b 1")
    int bufferSize = 50;

    @Override
    public Result call() throws Exception {
        if (this.verbose) _verbose = 1;
        if (this.verbose2) _verbose = 2;
        if (this.timeout > 0) {
            _timeout = timeout;
            _verbose = 2;
        }
        if (this.clear) _clearScreen = true;
        if (this.interpret) _interpret = true;
        if (this.clearAfterInput) _clearAfterInput = true;
        _bufferSize = this.bufferSize;
        if (file != null) {
            if (file.exists()) {
                new Brainf_ckCommandline().run(file);
            } else {
                System.out.println("Error: file "+file.getName()+" does not exist.");
            }
        } else {
            if (command == null) {
                new Brainf_ckCommandline().run();
            } else {
                new Brainf_ckCommandline().run(command);
            }
        }
        return new Result();
    }
}
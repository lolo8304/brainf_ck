/*
 * This Java source file was generated by the Gradle 'init' task.
 */


import brainf_ck.Brain;
import brainf_ck.Brainf_ck;
import brainf_ck.Brainf_ckCommandline;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

class Brainf_ckTest {

    private Reader reader;

    Reader ReadReader(String testfile) throws FileNotFoundException, URISyntaxException {
        URL resource = Brainf_ckTest.class.getResource("tests/"+testfile);
        File file = Paths.get(resource.toURI()).toFile();
        reader = new FileReader(file);
        return reader;
    }

    @AfterEach
    void CloseReader() throws IOException {
        Brainf_ck._verbose = 0;
        if (reader != null) {
            reader.close();
        }
    }

    @Test void run_fromfile_ok() throws URISyntaxException, IOException {
        // Arrange
        var bf = ReadReader("step2.bf");

        // Action
        new Brainf_ckCommandline();
    }

    @Test void interpret_dataInc_ok() throws IOException {
        // Arrange
        Brainf_ck._verbose = 2;
        var context = new Brain("++", 10);
        var mem = context.memory();

        // Action
        context.interpret();
        context.interpretUsingVM();

        // Assert
        Assertions.assertEquals(2, mem[0]);
    }


    @Test void interpret_nextInc_ok() throws IOException {
        // Arrange
        Brainf_ck._verbose = 2;
        var context = new Brain(">+++", 10);
        var mem = context.memory();

        // Action
        context.interpret();
        context.interpretUsingVM();

        // Assert
        Assertions.assertEquals(0, mem[0]);
        Assertions.assertEquals(3, mem[1]);
    }

    @Test void interpret_nextDecInc_ok() throws IOException {
        // Arrange
        Brainf_ck._verbose = 2;
        var context = new Brain(">+<++", 10);
        var mem = context.memory();

        // Action
        context.interpret();
        context.interpretUsingVM();

        // Assert
        Assertions.assertEquals(2, mem[0]);
        Assertions.assertEquals(1, mem[1]);
    }
    @Test void interpret_loop1_ok() throws IOException {
        // Arrange
        Brainf_ck._verbose = 2;
        var context = new Brain("[ loop ]>+", 10);
        var mem = context.memory();

        // Action
        context.interpret();
        context.interpretUsingVM();

        // Assert
        Assertions.assertEquals(0, mem[0]);
        Assertions.assertEquals(1, mem[1]);
    }

    @Test void interpret_loop2_ok() throws IOException {
        // Arrange
        Brainf_ck._verbose = 2;
        var context = new Brain("[ [loop] ]>+", 10);
        var mem = context.memory();

        // Action
        context.interpret();
        context.interpretUsingVM();

        // Assert
        Assertions.assertEquals(0, mem[0]);
        Assertions.assertEquals(1, mem[1]);
    }

    @Test void interpret_iterate_loop1_ok() throws IOException {
        // Arrange
        Brainf_ck._verbose = 2;
        var context = new Brain("++[-]>+", 10);
        var mem = context.memory();

        // Action
        context.interpret();
        context.interpretUsingVM();

        // Assert
        Assertions.assertEquals(0, mem[0]);
        Assertions.assertEquals(1, mem[1]);
    }

    @Test void compile_simple_ok() throws IOException {
        // Arrange
        var context = new Brain("+>+-<-", 10);

        // Action
        var bytecode = context.compile();

        // Assert
        Assertions.assertEquals(6, bytecode.length());
        Assertions.assertEquals(Brain.BYTECODE_INC, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_NEXT, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_INC, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_DEC, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_PREV, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_DEC, bytecode.next());
    }

    @Test void compile_iterate_loop1_ok() throws IOException {
        // Arrange
        var context = new Brain("[]>+", 10);

        // Action
        var bytecode = context.compile();

        // Assert
        Assertions.assertEquals(12, bytecode.length());
        Assertions.assertEquals(Brain.BYTECODE_START_LOOP, bytecode.next());
        Assertions.assertEquals(5, bytecode.nextInt());
        Assertions.assertEquals(Brain.BYTECODE_END_LOOP, bytecode.next());
        Assertions.assertEquals(5, bytecode.nextInt());
        Assertions.assertEquals(Brain.BYTECODE_NEXT, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_INC, bytecode.next());
    }
    @Test void compile_iterate_longer_ok() throws IOException {
        // Arrange
        var context = new Brain("+[[]-]>+", 10);

        // Action
        var bytecode = context.compile();

        // Assert
        Assertions.assertEquals(24, bytecode.length());
        var b=0;
        Assertions.assertEquals(Brain.BYTECODE_INC, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_START_LOOP, bytecode.next());
        Assertions.assertEquals(16, bytecode.nextInt());
        Assertions.assertEquals(Brain.BYTECODE_START_LOOP, bytecode.next());
        Assertions.assertEquals(5, bytecode.nextInt());
        Assertions.assertEquals(Brain.BYTECODE_END_LOOP, bytecode.next());
        Assertions.assertEquals(5, bytecode.nextInt());
        Assertions.assertEquals(Brain.BYTECODE_DEC, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_END_LOOP, bytecode.next());
        Assertions.assertEquals(16, bytecode.nextInt());
        Assertions.assertEquals(Brain.BYTECODE_NEXT, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_INC, bytecode.next());
    }

    @Test void interpret_hello_ok() throws IOException {
        // Arrange
        var context = new Brain("++++++++++[>+>+++>+++++++>++++++++++<<<<-]>>>++.>+.+++++++..+++.", 10);

        // Action
        var bytecode = context.compile();
        context.interpretUsingVM();

        // Assert
        Assertions.assertEquals(68, bytecode.length());


    }

    @Test void compile_setto0_ok() throws IOException {
        // Arrange
        var context = new Brain("+[-]", 10);

        // Action
        var bytecode = context.compile();

        // Assert
        Assertions.assertEquals(2, bytecode.length());
        Assertions.assertEquals(Brain.BYTECODE_INC, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_SET_TO_0, bytecode.next());
    }

    @Test void compile_setto0andmove_ok() throws IOException {
        // Arrange
        var context = new Brain("+[-]>+", 10);

        // Action
        var bytecode = context.compile();
        context.interpretUsingVM();
        var memory = context.memory();

        // Assert
        Assertions.assertEquals(3, bytecode.length());
        Assertions.assertEquals(Brain.BYTECODE_INC, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_SET_TO_0_AND_MOVE, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_INC, bytecode.next());
    }

    @Test void executeVm_setto0andmove_ok() throws IOException {
        // Arrange
        var context = new Brain("+[-]>+", 10);

        // Action
        context.interpretUsingVM();
        var bytecode = context.byteCode();
        var memory = context.memory();

        // Assert
        Assertions.assertEquals(3, bytecode.length());
        Assertions.assertEquals(Brain.BYTECODE_INC, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_SET_TO_0_AND_MOVE, bytecode.next());
        Assertions.assertEquals(Brain.BYTECODE_INC, bytecode.next());
    }
}

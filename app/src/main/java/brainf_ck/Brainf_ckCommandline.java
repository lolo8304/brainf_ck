package brainf_ck;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import java.util.Scanner;

public class Brainf_ckCommandline {

    public void run(String command) throws IOException {
            var validInput = !command.isBlank();
            if (validInput) {
                this.executeCommand(command);
            } else {
                System.out.println("Command line is empty");
            }
    }

    public void run() throws IOException {
        var validInput = true;
        do {
            var input = this.readFromConsole();
            validInput = input.isPresent() && !input.get().isBlank();
            if (validInput) {
                this.executeCommand(input.get());
            }
        } while (validInput);
        System.out.println("bye");
    }

    public void run(Reader reader) throws IOException {
        String content = "";
        try (Scanner scanner = new Scanner(reader)) {
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                content = scanner.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        this.run(content);
    }

    public void run(File file) throws IOException {
        this.run(new FileReader(file));
    }

    public void executeCommand(String command) throws IOException {
        if (Brainf_ck.interpret()) {
            new Brain(command).interpret();
        } else {
            new Brain(command).interpretUsingVM();
        }
    }

    private Optional<String> readFromConsole() throws IOException {
        System.out.print("brainfuck>> ");
        Scanner scanner = new Scanner(System.in);
        // Read the string until the end of the line
        if (scanner.hasNextLine()) {
            return Optional.of(scanner.nextLine());
        } else {
            return Optional.empty();
        }
    }
}

package brainfuck;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public class BrainfuckCommandline {

    public void run(String command) {
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

    public void run(File file) {
        String content = "";
        try (Scanner scanner = new Scanner(file)) {
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

    public void executeCommand(String command) {
        System.out.println(command);
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

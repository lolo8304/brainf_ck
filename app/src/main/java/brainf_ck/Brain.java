package brainf_ck;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.Set;

public class Brain {
    private final int memorySize;
    private final byte[] memory;
    private final String program;
    private final int programSize;
    private final Set<Character> tokens;
    private final StringBuilder result;
    private int pointer;
    private int pc;
    private long statCountOps;
    private long statCountMemoryRead;
    private long statCountMemoryWrite;
    private long startTime;

    public Brain(String program) {
        this(program,30000);
    }
    public Brain(String program, int memorySize) {
        this.program = program;
        this.memorySize = memorySize;
        this.memory = new byte[this.memorySize];
        this.pc = 0;
        this.programSize = this.program.length();
        this.pointer = 0;
        this.tokens = Set.of('+', '-', '{', '}', '[', ']', '.', ',');
        this.result = new StringBuilder();
        this.statCountOps = 0L;
        this.statCountMemoryRead = 0L;
        this.statCountMemoryWrite = 0L;
    }

    public byte[] memory() {
        return this.memory;
    }
    public byte memoryValue(int pos) {
        this.statCountMemoryRead++;
        return this.memory[pos];
    }
    public void memoryValue(int pos, byte value) {
        this.statCountMemoryWrite++;
        this.memory[pos] = value;
    }

    public char programChar(int pos) {
        this.statCountOps++;
        return this.program.charAt(pos);
    }

    public void compile() throws IOException {
        this.logStart();
        while (this.pc < this.programSize) {
            if (Brainf_ck.verbose()) this.log();
            var ch = this.programChar(this.pc);
            switch (ch) {
                case '>':
                    this.pointer++;
                    if (this.pointer == this.memorySize) this.pointer = 0;
                    break;
                case '<':
                    if (this.pointer != 0) {
                        this.pointer--;
                    } else {
                        this.pointer = this.memorySize - 1;
                    }
                    break;
                case '+':
                    this.memory[this.pointer]++;
                    this.statCountMemoryWrite++;
                    break;
                case '-':
                    this.memory[this.pointer]--;
                    this.statCountMemoryWrite++;
                    break;
                case '.':
                    if (Brainf_ck.verbose()) {
                        this.result.append((char) this.memoryValue(this.pointer));
                    } else {
                        System.out.print((char) this.memoryValue(this.pointer));
                    }
                    break;
                case ',':
                    this.memoryValue(this.pointer, (byte) System.in.read());
                    break;
                case '[':
                    if (this.memoryValue(this.pointer) == 0) {
                        var loopCounter = 1;
                        ch = this.programChar(++this.pc);
                        while (loopCounter > 0) {
                            switch (ch) {
                                case ']': loopCounter--; break;
                                case '[': loopCounter++; break;
                            }
                            ch = this.programChar(++this.pc);
                        }
                        this.pc--; // go back to increase later again
                    }
                    break;
                case ']':
                    if (this.memoryValue(this.pointer) != 0) {
                        var loopCounter = 1;
                        ch = this.programChar(--this.pc);
                        while (loopCounter > 0) {
                            switch (ch) {
                                case ']': loopCounter++; break;
                                case '[': loopCounter--; break;
                            }
                            ch = this.programChar(--this.pc);
                        }
                    }
                    break;
                case '#':
                    System.in.read();
                    break;
                default:
                    // skip - comment
            }
            this.pc++;
        }
        if (Brainf_ck.verbose()) this.log();
        System.out.println();
        this.logSummary();
    }


    public int mod(int value, int mod) {
        return (value % mod + mod) % mod;
    }

    public void logStart() {
        this.startTime = System.currentTimeMillis();
    }

    public void log() {
        if (!Brainf_ck.verbose()) return;
        if (Brainf_ck._timeout > 0) {
            if (this.pc < this.programSize && this.tokens.contains(this.programChar(this.pc))) {
                try {
                    Thread.sleep(Duration.ofMillis(Brainf_ck._timeout));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (Brainf_ck.clearScreen()) {
            try {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(">"+this.result+"<");
        System.out.print("pc=" + this.pc + " (max=" + this.programSize + "), dc=" + this.pointer+ " ("+this.memorySize+"), ");
        System.out.print("pc-ops="+NumberFormat.getInstance().format(this.statCountOps)+", read="+NumberFormat.getInstance().format(this.statCountMemoryRead)+", write="+NumberFormat.getInstance().format(statCountMemoryWrite));
        System.out.println();
        if (Brainf_ck.verbose2()) {
            var logPointer = this.mod(this.pointer - 4, this.memorySize);
            var logPointerMax = this.mod(this.pointer + 5, this.memorySize);
            var logPc = this.pc - 4;
            var debugLine1 = new StringBuilder();
            var debugLine2 = new StringBuilder();
            var debugLine3 = new StringBuilder().append("        '");
            var debugLine4 = new StringBuilder().append("         ");
            while (logPointer != logPointerMax) {
                debugLine1.append(String.format("%7d", this.memory[logPointer])).append("  ");
                debugLine2.append(String.format("%7d", logPointer)).append(" ").append(logPointer == this.pointer ? '^': ' ');
                debugLine3.append(logPc >= 0 && logPc < this.programSize ? this.programChar(logPc) : " ");
                debugLine4.append(logPc == this.pc ? '^' : ' ');
                logPointer = this.mod(++logPointer, this.memorySize);
                ++logPc;
            }
            var lineMax = Math.max(Math.max(debugLine1.length(), debugLine2.length()), debugLine3.length());
            System.out.println("-".repeat(lineMax));
            System.out.println(debugLine1);
            System.out.println(debugLine2);
            System.out.println("-".repeat(lineMax));
            System.out.println(debugLine3.append('\''));
            System.out.println(debugLine4);
            System.out.println("-".repeat(lineMax));
        }
    }

    public void logSummary() {
        var time = System.currentTimeMillis() - this.startTime;
        System.out.println("#Time="+NumberFormat.getInstance().format(time)+"ms, #ops=" + NumberFormat.getInstance().format(this.statCountOps)+", memory read="+NumberFormat.getInstance().format(this.statCountMemoryRead)+", memory writes="+NumberFormat.getInstance().format(this.statCountMemoryWrite));
    }

}

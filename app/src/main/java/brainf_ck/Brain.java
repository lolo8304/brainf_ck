package brainf_ck;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.Stack;

public class Brain {
    public static final byte BYTECODE_NEXT = 0x01;
    public static final byte BYTECODE_PREV = 0x02;
    public static final byte BYTECODE_INC = 0x03;
    public static final byte BYTECODE_DEC = 0x04;
    public static final byte BYTECODE_SET_TO_0 = 0x05;
    public static final byte BYTECODE_SET_TO_0_AND_MOVE = 0x06;

    public static final byte BYTECODE_PRINT = 0x0A;
    public static final byte BYTECODE_READ_FROM_INPUT = 0x0B;
    public static final byte BYTECODE_DEBUGGER = 0xC;
    public static final byte BYTECODE_START_LOOP = 0x0D;
    public static final byte BYTECODE_END_LOOP = 0x0E;

    public static final byte BYTECODE_NEXT_MULTI = 0x11;
    public static final byte BYTECODE_PREV_MULTI = 0x12;
    public static final byte BYTECODE_INC_MULTI = 0x13;
    public static final byte BYTECODE_DEC_MULTI = 0x14;
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
    private ByteCode bytecode;

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
        this.bytecode = new ByteCode(this.programSize);
    }

    public byte[] memory() {
        return this.memory;
    }
    public ByteCode byteCode() {
        return this.bytecode;
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

    private void initExecution() {
        this.pc = 0;
        this.pointer = 0;
        this.statCountOps = 0;
        for (int i = 0; i < this.memorySize; i++) {
            this.memory[i] = 0;
        }
    }
    public void interpret() throws IOException {
        this.logStart();
        this.initExecution();
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
                        //System.out.print((char) this.memoryValue(this.pointer));
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

    private void addAndPrint(char ch) {
        this.result.append(ch);
        if (result.length() >= Brainf_ck.bufferSize()) {
            System.out.print(result);
            result.setLength(0);
        }
    }

    public void executeVM() throws IOException {
        if (this.bytecode.length() == 0) throw new IOException("Program not compiled");
        this.logStart();
        this.initExecution();
        while (bytecode.hasNext()) {
            var b = this.bytecode.next(); this.statCountOps++;
            switch (b) {
                case BYTECODE_NEXT: // >
                    this.pointer++;
                    if (this.pointer == this.memorySize) this.pointer = 0;
                    break;
                case BYTECODE_NEXT_MULTI: // >
                    var countNext = this.bytecode.nextInt();
                    this.pointer += countNext;
                    if (this.pointer >= this.memorySize) this.pointer = this.pointer - this.memorySize;
                    break;
                case BYTECODE_PREV: // <
                    this.pointer--;
                    if (this.pointer == -1) this.pointer = this.memorySize - 1;
                    break;
                case BYTECODE_PREV_MULTI: // <
                    var countPrev = this.bytecode.nextInt();
                    this.pointer -= countPrev;
                    if (this.pointer < 0) this.pointer = this.memorySize + this.pointer;
                    break;
                case BYTECODE_INC: // +
                    this.memory[this.pointer]++;
                    this.statCountMemoryWrite++;
                    break;
                case BYTECODE_INC_MULTI: // +
                    var countInc = this.bytecode.nextInt();
                    this.memory[this.pointer] += countInc;
                    this.statCountMemoryWrite++;
                    break;
                case BYTECODE_DEC: // -
                    this.memory[this.pointer]--;
                    this.statCountMemoryWrite++;
                    break;
                case BYTECODE_DEC_MULTI: // -
                    var countDec = this.bytecode.nextInt();
                    this.memory[this.pointer] -= countDec;
                    this.statCountMemoryWrite++;
                    break;
                case BYTECODE_PRINT: // .
                    if (Brainf_ck.verbose()) {
                        this.result.append((char) this.memoryValue(this.pointer));
                    } else {
                        this.addAndPrint((char) this.memoryValue(this.pointer));
                    }
                    break;
                case BYTECODE_READ_FROM_INPUT: //,
                    this.memoryValue(this.pointer, (byte) System.in.read());
                    if (Brainf_ck.clearAfterInput()) {
                        this.clearScreen();
                    }
                    break;
                case BYTECODE_DEBUGGER: // #
                    System.in.read();
                    break;
                case BYTECODE_START_LOOP: // [
                    if (this.memoryValue(this.pointer) == 0) {
                        var endLoopDiff = bytecode.nextInt();
                        bytecode.move(endLoopDiff);
                    } else {
                        bytecode.move(4);
                    }
                    break;
                case BYTECODE_END_LOOP: // ]
                    if (this.memoryValue(this.pointer) != 0) {
                        var startLoopDiff = bytecode.nextInt();
                        bytecode.move(- (startLoopDiff + 5)); // 4 bytes + 1 pos
                    } else {
                        bytecode.move(4);
                    }
                    break;
                case BYTECODE_SET_TO_0:
                    this.memoryValue(this.pointer, (byte)0);
                case BYTECODE_SET_TO_0_AND_MOVE:
                    this.memoryValue(this.pointer, (byte)0);
                    this.pointer++;
                    if (this.pointer == this.memorySize) this.pointer = 0;
                default:
                    // skip - comment
            }
        }
        System.out.println();
        this.logSummary();
    }

    /*
    bytecode:
        >: 1, <: 2, +: 3, -: 4, [: 5 + jumpCount, ]: 6 + jumpCountBack
        .: A, ,: B, #: C
    * */
    public ByteCode compile() throws IOException {
        this.pc = 0;
        while (this.pc < this.programSize) {
            var ch = this.programChar(this.pc);
            switch (ch) {
                case '>':
                    bytecode.writeNext();
                    break;
                case '<':
                    bytecode.writePrev();
                    break;
                case '+':
                    bytecode.writeInc();
                    break;
                case '-':
                    bytecode.writeDec();
                    break;
                case '.':
                    bytecode.writePrint();
                    break;
                case ',':
                    bytecode.writeFromInput();
                    break;
                case '[':
                    if (this.nextIs("[-]>111111")) {
                        bytecode.writeSetTo0AndMove();
                    } else if (this.nextIs("[-]1111111")) {
                        bytecode.writeSetTo0();
                    } else {
                        bytecode.writeStartLoop();
                    }
                    break;
                case ']':
                    bytecode.writeEndLoop();
                    break;
                case '#':
                    bytecode.writeDebugger();
                    break;
                default:
                    // skip - comment
            }
            this.pc++;
        }
        bytecode.finish();
        return bytecode;
    }

    private boolean nextIs(String pattern) {
        var index = this.pc;
        var count = pattern.length();
        var i = 0;
        var ch = this.programChar(index++);
        while (i < count && ch == pattern.charAt(i)) {
            i++;
            ch = index < this.programSize ? this.programChar(index++) : 0;
        }
        if (i == count) {
            this.pc += (count - 1);
            return true;
        }
        return false;
    }

    public void interpretUsingVM() throws IOException {
        this.compile();
        this.executeVM();
    }


    private int mod(int value, int mod) {
        return (value % mod + mod) % mod;
    }


    public void logStart() {
        this.startTime = System.currentTimeMillis();
    }

    public void clearScreen() {
        try {
            new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
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
            this.clearScreen();
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
        if (this.result.length() > 0) System.out.println(result);
        System.out.println("#Time="+NumberFormat.getInstance().format(time)+"ms, #ops=" + NumberFormat.getInstance().format(this.statCountOps)+", memory read="+NumberFormat.getInstance().format(this.statCountMemoryRead)+", memory writes="+NumberFormat.getInstance().format(this.statCountMemoryWrite));
    }

    public static class ByteCode {
        private int pc;
        private int lastWasInc;
        private int lastWasDec;
        private int lastWasNext;
        private int lastWasPrev;
        private byte[] byteCode;
        private final Stack<Integer> bracketsBytePos = new Stack<Integer>();

        public ByteCode(Integer size) {
            this.byteCode = new byte[size * 5];
            this.pc = 0;
            this.lastWasInc = 0;
            this.lastWasDec = 0;
            this.lastWasNext = 0;
            this.lastWasPrev = 0;
        }
        public void writeInc() {
            if (this.lastWasInc == 1) {
                this.write(this.pc - 1, BYTECODE_INC_MULTI);
                this.pushInt(2);
                this.lastWasInc++;
            } else if (this.lastWasInc > 1) {
                var oldValue = this.readInt(this.pc - 4);
                this.writeInt(this.pc - 4, oldValue + 1);
            } else {
                this.lastWasInc = 1; this.lastWasDec = 0; this.lastWasNext = 0; this.lastWasPrev = 0;
                push(BYTECODE_INC);
            }
        }
        public void writeDec() {
            if (this.lastWasDec == 1) {
                this.write(this.pc - 1, BYTECODE_DEC_MULTI);
                this.pushInt(2);
                this.lastWasDec++;
            } else if (this.lastWasDec > 1) {
                var oldValue = this.readInt(this.pc - 4);
                this.writeInt(this.pc - 4, oldValue + 1);
            } else {
                this.lastWasInc = 0; this.lastWasDec = 1; this.lastWasNext = 0; this.lastWasPrev = 0;
                push(BYTECODE_DEC);
            }
        }
        public void writeNext() {
            if (this.lastWasNext == 1) {
                this.write(this.pc - 1, BYTECODE_NEXT_MULTI);
                this.pushInt(2);
                this.lastWasNext++;
            } else if (this.lastWasNext > 1) {
                var oldValue = this.readInt(this.pc - 4);
                this.writeInt(this.pc - 4, oldValue + 1);
            } else {
                this.lastWasInc = 0; this.lastWasDec = 0; this.lastWasNext = 1; this.lastWasPrev = 0;
                push(BYTECODE_NEXT);
            }
        }
        public void writePrev() {
            if (this.lastWasPrev == 1) {
                this.write(this.pc - 1, BYTECODE_PREV_MULTI);
                this.pushInt(2);
                this.lastWasPrev++;
            } else if (this.lastWasPrev > 1) {
                var oldValue = this.readInt(this.pc - 4);
                this.writeInt(this.pc - 4, oldValue + 1);
            } else {
                this.lastWasInc = 0; this.lastWasDec = 0; this.lastWasNext = 0; this.lastWasPrev = 1;
                push(BYTECODE_PREV);
            }
        }

        public byte push(byte b) {
            this.byteCode[this.pc++] = b;
            return b;
        }
        public int pushInt(int i) {
            this.writeInt(this.pc, i);
            this.pc += 4;
            return i;
        }

        public byte pop() {
            return this.byteCode[--this.pc];
        }

        public int popInt() {
            this.pc -= 4;
            return this.readInt(this.pc);
        }
        public int nextInt() {
            var i = this.readInt(this.pc);
            this.pc += 4;
            return i;
        }
        public byte next() {
            return this.read(this.pc++);
        }

        public byte write(int index, byte b) {
            this.byteCode[index] = b;
            return b;
        }

        public int writeInt(int index, int i) {
            var offset = index;
            var bytes = intToBytes(i);
            this.byteCode[offset++] = bytes[0];
            this.byteCode[offset++] = bytes[1];
            this.byteCode[offset++] = bytes[2];
            this.byteCode[offset] = bytes[3];
            return i;
        }

        public byte read(int index) {
            return this.byteCode[index];
        }

        public int readInt(int index) {
            var bytes = new byte[4];
            var offset = index;
            bytes[0] = this.byteCode[offset++];
            bytes[1] = this.byteCode[offset++];
            bytes[2] = this.byteCode[offset++];
            bytes[3] = this.byteCode[offset];
            return bytesToInt(bytes);
        }

        public static byte[] intToBytes(int number) {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) (number >> 24); // Extract the highest 8 bits
            bytes[1] = (byte) (number >> 16); // Extract the next 8 bits
            bytes[2] = (byte) (number >> 8);  // Extract the next 8 bits
            bytes[3] = (byte) (number);       // Extract the lowest 8 bits
            return bytes;
        }

        public static int bytesToInt(byte[] bytes) {
            if (bytes.length != 4) {
                throw new IllegalArgumentException("Byte array must be exactly 4 bytes long.");
            }

            return ((bytes[0] & 0xFF) << 24) | // Most significant byte
                    ((bytes[1] & 0xFF) << 16) | // Second byte
                    ((bytes[2] & 0xFF) << 8)  | // Third byte
                    (bytes[3] & 0xFF);         // Least significant byte
        }

        public void writePrint() {
            this.lastWasInc = 0; this.lastWasDec = 0; this.lastWasNext = 0; this.lastWasPrev = 0;
            this.push(BYTECODE_PRINT);
        }

        public void writeFromInput() {
            this.lastWasInc = 0; this.lastWasDec = 0; this.lastWasNext = 0; this.lastWasPrev = 0;
            this.push(BYTECODE_READ_FROM_INPUT);
        }

        public void writeStartLoop() {
            this.lastWasInc = 0; this.lastWasDec = 0; this.lastWasNext = 0; this.lastWasPrev = 0;
            bracketsBytePos.push(this.pc);
            this.push(BYTECODE_START_LOOP);
            this.pushInt(0);
        }

        public void writeEndLoop() {
            this.lastWasInc = 0; this.lastWasDec = 0; this.lastWasNext = 0; this.lastWasPrev = 0;
            var endBracketPos = this.pc;
            this.push(BYTECODE_END_LOOP);
            var lastBracketPos = bracketsBytePos.pop();
            var distance = endBracketPos - lastBracketPos;
            this.pushInt(distance);
            this.writeInt(lastBracketPos+1, distance);
        }

        public void writeDebugger() {
            this.lastWasInc = 0; this.lastWasDec = 0; this.lastWasNext = 0; this.lastWasPrev = 0;
            this.push(BYTECODE_DEBUGGER);
        }

        public void finish() {
            this.byteCode = Arrays.copyOf(this.byteCode, this.pc);
            this.lastWasInc = 0; this.lastWasDec = 0; this.lastWasNext = 0; this.lastWasPrev = 0;
            this.pc = 0;
        }

        public int length() {
            return this.byteCode.length;
        }

        public boolean hasNext() {
            return this.pc < this.length();
        }

        public void move(int i) {
            this.pc += i;
        }

        public void writeSetTo0() {
            this.lastWasInc = 0; this.lastWasDec = 0; this.lastWasNext = 0; this.lastWasPrev = 0;
            this.push(BYTECODE_SET_TO_0);
        }
        public void writeSetTo0AndMove() {
            this.lastWasInc = 0; this.lastWasDec = 0; this.lastWasNext = 0; this.lastWasPrev = 0;
            this.push(BYTECODE_SET_TO_0_AND_MOVE);
        }
    }

}

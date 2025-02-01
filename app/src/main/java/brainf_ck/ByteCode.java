package brainf_ck;

import java.util.Arrays;
import java.util.Stack;

public class ByteCode {
    private int pc;
    private byte lastWas;
    private int lastWasCount;
    private byte[] byteCode;
    private final Stack<Integer> bracketsBytePos = new Stack<Integer>();

    public ByteCode(Integer size) {
        this.byteCode = new byte[size * 5];
        this.pc = 0;
        resetLast();

    }

    private int wasLastAndInc(byte code) {
        if (Brain.BYTECODES_SET_1_BYTE_ONLY.contains(code)) {
            this.lastWas = code;
            this.lastWasCount = 1;
            return this.lastWasCount;
        }
        if (code == this.lastWas) {
            this.lastWasCount++;
        } else {
            this.lastWas = code;
            this.lastWasCount = 1;
        }
        return this.lastWasCount;
    }

    public void writeSingle(byte single) {
        this.writeSingleAndMulti(single, (byte)0);
    }

    public void writeSingleAndMulti(byte single, byte multi) {
        var count = this.wasLastAndInc(single);
        if (count == 1) {
            push(single);
        } else if (count == 2) {
            this.write(this.pc - 1, multi);
            this.pushInt(2);
        } else if (count > 2) {
            var oldValue = this.readInt(this.pc - 4);
            this.writeInt(this.pc - 4, oldValue + 1);
        }
    }

    public void writeInc() {
        writeSingleAndMulti(Brain.BYTECODE_INC, Brain.BYTECODE_INC_MULTI);
    }

    public void writeDec() {
        writeSingleAndMulti(Brain.BYTECODE_DEC, Brain.BYTECODE_DEC_MULTI);
    }

    public void writeNext() {
        writeSingleAndMulti(Brain.BYTECODE_NEXT, Brain.BYTECODE_NEXT_MULTI);
    }

    public void writePrev() {
        writeSingleAndMulti(Brain.BYTECODE_PREV, Brain.BYTECODE_PREV_MULTI);
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
        return this.pc != this.byteCode.length ? this.read(this.pc++) : 0;
    }
    public byte nextNoIndexCheck() {
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
        var b0 = this.byteCode[index++];
        var b1 = this.byteCode[index++];
        var b2 = this.byteCode[index++];
        var b3 = this.byteCode[index];
        if (b2 == 0x00 && b1 ==  0x00 && b0 ==  0x00) return b3 & 0xFF;
        return ((b0 & 0xFF) << 24) | // Most significant byte
                ((b1 & 0xFF) << 16) | // Second byte
                ((b2 & 0xFF) << 8) | // Third byte
                (b3 & 0xFF);         // Least significant byte

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
                ((bytes[2] & 0xFF) << 8) | // Third byte
                (bytes[3] & 0xFF);         // Least significant byte
    }

    public void writePrint() {
        this.writeSingle(Brain.BYTECODE_PRINT);
    }

    public void writeFromInput() {
        this.writeSingle(Brain.BYTECODE_READ_FROM_INPUT);
    }

    public void writeStartLoop() {
        resetLast();
        bracketsBytePos.push(this.pc);
        this.push(Brain.BYTECODE_START_LOOP);
        this.pushInt(0);
    }

    public void writeEndLoop() {
        resetLast();
        var endBracketPos = this.pc;
        this.push(Brain.BYTECODE_END_LOOP);
        var lastBracketPos = bracketsBytePos.pop();
        var distance = endBracketPos - lastBracketPos;
        this.pushInt(distance+5);
        this.writeInt(lastBracketPos + 1, distance);
    }

    public void writeDebugger() {
        this.writeSingle(Brain.BYTECODE_DEBUGGER);
    }

    public void finish() {
        this.byteCode = Arrays.copyOf(this.byteCode, this.pc);
        this.reset();
    }

    public int length() {
        return this.byteCode.length;
    }

    public boolean hasNext() {
        return this.pc != this.length();
    }

    public void moveBack(int i) {
        this.pc -= i;
    }

    public void move(int i) {
        this.pc += i;
    }

    public void writeSetTo0() {
        this.writeSingle(Brain.BYTECODE_SET_TO_0);
    }

    public void writeSetTo0AndMove() {
        this.writeSingleAndMulti(Brain.BYTECODE_SET_TO_0_AND_MOVE, Brain.BYTECODE_SET_TO_0_AND_MOVE_MULTI);
    }

    public void reset() {
        resetLast();
        this.pc = 0;
    }

    private void resetLast() {
        this.lastWas = 0;
        this.lastWasCount = 0;
    }

}

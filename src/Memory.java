import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Memory {
    private static Memory memory = null;
    private static final int RAM_SIZE = 4096;
    private static final int LOAD_ADDRESS = 0x200;
    private static byte[] ram = new byte[RAM_SIZE];
    private static byte[] vregisters = new byte[16];
    private static short iregister;
    private static short programCounter = (short)LOAD_ADDRESS;
    private static byte stackPointer;
    private static byte delayTimer;
    private static byte soundTimer;
    private static final byte[] digits = {
            (byte) 0xF0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xF0, // hexadecimal digit 0
            (byte) 0x20, (byte) 0x60, (byte) 0x20, (byte) 0x20, (byte) 0x70, // 1
            (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // 2
            (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 3
            (byte) 0x90, (byte) 0x90, (byte) 0xF0, (byte) 0x01, (byte) 0x01, // 4
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 5
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 6
            (byte) 0xF0, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x40, // 7
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 8
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 9
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0x90, // 10
            (byte) 0xE0, (byte) 0x90, (byte) 0xE0, (byte) 0x90, (byte) 0xE0, // 11
            (byte) 0xF0, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xF0, // 12
            (byte) 0xE0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xE0, // 13
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // 14
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0x80  // 15
            };

    private Memory() {
        System.arraycopy(digits, 0, ram, 0, 16);
    }

    public static Memory getMemory(){
        if(memory == null){
            memory = new Memory();
        }
        return memory;
    }

    public void loadRom(String romName) {
        try {
            String file = "./roms/" + romName; // Filepath of the rom to be loaded
            Path romPath = Paths.get(file); // Create Path Object of the rom
            byte[] romData = Files.readAllBytes(romPath); // Load the rom data as Byte data
            System.arraycopy(romData, 0, ram, LOAD_ADDRESS, romData.length);
        }catch(IOException e){
            System.out.println("Exceptional Error Encountered: " + e.getMessage());
        }
    }

    public Opcode getNextInstruction(){
        return new Opcode(ram[programCounter], ram[programCounter+1]);
    }

    public static byte getVregister(int index){
        return vregisters[index];
    }

    public static void setVregister(byte data, int index){
        vregisters[index] = data;
    }


    public static short getIregister() {
        return iregister;
    }

    public static void setIregister(short iregister) {
        Memory.iregister = iregister;
    }

    public void incrementProgramCounter(){
        Memory.programCounter += 2;
    }

    public static short getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(short programCounter) {
        Memory.programCounter = programCounter;
    }

    public static byte getStackPointer() {
        return stackPointer;
    }

    public static void setStackPointer(byte stackPointer) {
        Memory.stackPointer = stackPointer;
    }

    public static byte getDelayTimer() {
        return delayTimer;
    }

    public static void setDelayTimer(byte delayTimer) {
        Memory.delayTimer = delayTimer;
    }

    public static byte getSoundTimer() {
        return soundTimer;
    }

    public static void setSoundTimer(byte soundTimer) {
        Memory.soundTimer = soundTimer;
    }
}

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class Memory {
    private static Memory memory = null;
    private static final Display d = Display.getDisplay();
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

    /* Chip8 pseudo-assembler cycle */

    public void cycle(){
        int a = 1;
        Opcode opcode;
        for(;;) {
            opcode = getNextInstruction();
            System.out.printf( a + " : %02x\n", opcode.getOpcode());
            a++;
            if(opcode.getOpcode() == 0x0000){
                break;
            }
            incrementProgramCounter();
            decode(opcode);
        }
    }

    /* IMPLEMENTATION OF CPU */

    public void decode(Opcode opcode){
        switch(opcode.mostSignificantByte){
            case 0x0000:
                System.out.println("0x0000");
                break;
            case 0x1000:
                // maze makes this loops at the end infinitely.......sigh
                // thats why it doesnt output since its an infinite loop
                setProgramCounter(opcode.nnn);
                break;
            case 0x2000:
                // Call subroutine at nnn.
                setProgramCounter((short)(LOAD_ADDRESS + opcode.nnn));
                break;
            case 0x3000:
                // Skip next instruction if Vx = kk.
                if(vregisters[opcode.x] == opcode.kk){
                    incrementProgramCounter();
                }
                break;
            case 0x6000:
                // The interpreter puts the value kk into register Vx.
                vregisters[opcode.x] = opcode.kk;
                break;
            case 0x7000:
                vregisters[opcode.x] = (byte)(vregisters[opcode.x] + opcode.kk);
                break;
            case 0x8000:
                // Stores the value of register Vy in register Vx.
                vregisters[opcode.x] = vregisters[opcode.y];
                break;
            case 0xa000:
                // The value of register I is set to nnn.
                iregister = opcode.nnn;
                break;
            case 0xc000:
                // Set Vx = random byte AND kk.
                Random rand = new Random();
                byte randomByte = (byte)rand.nextInt(256); // return random byte value
                byte randomKK = (byte)(opcode.kk & randomByte); // AND random value with kk according to specifications
                vregisters[opcode.x] = randomKK; // store result in v register array
                break;
            case 0xd000:
                // Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
                byte[] sprite = new byte[opcode.n];
                System.arraycopy(ram, iregister, sprite, 0, opcode.n);
                vregisters[0xF] = d.updateGrid(sprite, vregisters[opcode.x], vregisters[opcode.y]);
                break;
            case 0xe000:
                switch(opcode.kk){
                    case (byte)0x9e:
                    case (byte)0xa1:
                    default:
                        // execution should not reach here
                        System.out.printf("Error - Missing opcode: %02x\n", opcode.getOpcode());
                }
                break;
            case 0xf000:
                switch(opcode.kk){
                    case 0x07:
                    case 0x0a:
                    case 0x15:
                    case 0x18:
                    case 0x1e:
                    case 0x29:
                    case 0x33:
                    case 0x55:
                        System.arraycopy(vregisters, 0, ram, iregister, opcode.x);
                        iregister = (short)(iregister + opcode.x + 1);
                        break;
                    case 0x65:
                        System.arraycopy(ram, iregister, vregisters, 0, opcode.x);
                        iregister = (short)(iregister + opcode.x + 1);
                        break;
                    default:
                        // execution should not reach here
                        System.out.printf("Error - Missing opcode: %02x\n", opcode.getOpcode());
                }
                break;
            default:
                System.out.printf("Error - Missing opcode: %02x\n", opcode.getOpcode());
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

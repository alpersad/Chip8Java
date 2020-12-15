import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class Chip8 {
    static int RAM_SIZE = 4096;
    static int LOAD_ADDRESS = 0x200;

    // RAM
    static byte[] ram = new byte[RAM_SIZE];

    // CPU
    static byte[] vregisters = new byte[16];
    static short iregister;
    static short programCounter = (short)LOAD_ADDRESS;
    static byte stackPointer;
    static byte delayTimer;
    static byte soundTimer;

    // DISPLAY
//    static boolean[][] display = new boolean[64][32];
    static Display d = Display.getDisplay();

    public static void main(String[] args){
        try {
            loadRom("MAZE");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        cycle();
        System.out.printf("I - register: %02x\n", iregister);
        System.out.printf("V[1] - register: %02x\n", vregisters[1]);
        System.out.printf("sprite?: %02x\n", ram[iregister]);
//        drawDisplay();
    }

/*    *//* Draw display *//*
    public static void drawDisplay(){
        for(int i = 0; i < 32; i++){
            for(int j = 0; j < 64; j++){
                System.out.print(display[j][i]? "*" : " ");
            }
            System.out.println();
        }
    }

    public static void updateDisplay(byte[] sprite, int x, int y){
        boolean bit;
        boolean collision;
        int x_wrap;
        for(byte b : sprite) {
            for (int i = 7, j = 0; i >= 0; i--, j++) {
                bit = ((b >> i) & 0x01) == 1;
                x_wrap = x + j;
                x_wrap = x_wrap < 64 ? x_wrap : x_wrap - 64;
                collision = display[x_wrap][y];
                display[x_wrap][y] = display[x_wrap][y] ^ bit;
                if(collision){
                    vregisters[0xF] = !display[x_wrap][y] ? (byte)1 : 0;
                }
            }
            y++;
        }
    }*/

    /* Loading ROM into memory */

    public static void loadRom(String romName) throws IOException {
        String file = "./roms/" + romName; // Filepath of the rom to be loaded
        Path romPath = Paths.get(file); // Create Path Object of the rom
        byte[] romData = Files.readAllBytes(romPath); // Load the rom data as Byte data
        System.arraycopy(romData, 0, ram, LOAD_ADDRESS, romData.length);
    }

    /* Chip8 pseudo-assembler cycle */

    public static void cycle(){
        int a = 0;
        //short opcode = 0;
        Opcode opcode;
        for(;;) {
            a++;
            opcode = new Opcode(ram[programCounter], ram[programCounter+1]);
            if(opcode.getOpcode() == 0x0000 || a > 2048){
                break;
            }
            decode(opcode);
        }
    }

    /* Program Counter */

    public static void incrementPC(){
        programCounter = (short)(programCounter + 0x2);
    }

    public static void setPC(short pc){
        programCounter = pc;
    }

    /* IMPLEMENTATION OF CPU */

    public static void decode(Opcode opcode){
        incrementPC();
        switch(opcode.mostSignificantByte){
            case 0x0000:
                System.out.println("0x0000");
                break;
            case 0x1000:
                // maze makes this loops at the end infinitely.......sigh
                // thats why it doesnt output since its an infinite loop
                setPC(opcode.nnn);
                break;
            case 0x2000:
                // Call subroutine at nnn.
                setPC((short)(LOAD_ADDRESS + opcode.nnn));
                break;
            case 0x3000:
                // Skip next instruction if Vx = kk.
                if(vregisters[opcode.x] == opcode.kk){
                    incrementPC();
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
            default:
                System.out.printf("Error - Missing opcode: %02x\n", opcode.getOpcode());
        }
    }

}

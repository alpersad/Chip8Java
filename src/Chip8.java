public class Chip8 {
    private static final Memory memory = Memory.getMemory();

    public static void main(String[] args){
        memory.loadRom("TICTAC");
        memory.cycle();
    }

}

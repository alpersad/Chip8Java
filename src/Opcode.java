public class Opcode {
    short opcode;
    short nnn;
    byte kk;
    int x;
    int y;
    int n;
    int mostSignificantByte;

    public Opcode(byte a, byte b){
        this.opcode = (short)((a << 8) + b);
        this.mostSignificantByte = this.mostSignificantByte();
        this.nnn = this.nnn();
        this.kk = this.kk();
        this.x = this.x();
        this.y = this.y();
        this.n = this.n();
    }

    /*
    A 4-bit value, the upper 4 bits of the upper byte of the instruction
     */
    public int mostSignificantByte(){
        return this.opcode & 0xF000;
    }

    /*
    nnn - A 12-bit value, the lowest 12 bits of the instruction
    */
    public short nnn(){
        return (short)(this.opcode & 0x0FFF);
    }

    /*
    kk or byte - An 8-bit value, the lowest 8 bits of the instruction
     */
    public byte kk(){
        return (byte)(this.opcode & 0x00FF);
    }

    /*
    x - A 4-bit value, the lower 4 bits of the high byte of the instruction
     */
    public int x(){
        return (this.opcode & 0x0F00) >> 8;
    }

    /*
    y - A 4-bit value, the upper 4 bits of the low byte of the instruction
     */
    public int y(){
        return (this.opcode & 0x00F0) >> 4;
    }

    /*
    n or nibble - A 4-bit value, the lowest 4 bits of the instruction
     */
    public int n(){
        return this.opcode & 0x000F;
    }

    /*
    Returns the 16-bit instruction as a short
     */
    public short getOpcode(){
        return opcode;
    }
}

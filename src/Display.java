import javax.swing.*;
import java.awt.*;

public class Display extends JComponent{
    private static Display display = null;
    private static int DISPLAY_WIDTH = 655;
    private static int DISPLAY_HEIGHT = 360;
    private static boolean[][] grid = new boolean[64][32];

    private Display() {
        JFrame emulator = new JFrame("Chip 8 Emulator");
        emulator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        emulator.setSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        emulator.setContentPane(this);
        emulator.setVisible(true);
    }

    public static Display getDisplay(){
        if(display == null){
            display = new Display();
        }
        return display;
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        g2.fillRect(0,0,DISPLAY_WIDTH,DISPLAY_HEIGHT);
        g2.setColor(Color.WHITE);
        for(int i = 0; i < 32; i++){
            for(int j = 0; j < 64; j++){
                if(grid[j][i]){
                    g2.fillRect(j*10,i*10,10,10);
                }
            }
        }
    }

    public byte updateGrid(byte[] sprite, int x, int y){
        boolean spriteBit; // individual bits of the sprite byte data
        boolean preCollision; // stores the previous pixel value before pixel is updated
        byte collision = 0; // returns 1 if a pixel is erased, otherwise returns 0
        int x_wrap; // check if sprite is being drawn offscreen
        for(byte b : sprite) {
            for (int i = 7, j = 0; i >= 0; i--, j++) {
                spriteBit = ((b >> i) & 0x01) == 1;
                x_wrap = x + j;
                x_wrap = x_wrap < 64 ? x_wrap : x_wrap - 64;
                preCollision = grid[x_wrap][y];
                grid[x_wrap][y] = grid[x_wrap][y] ^ spriteBit;
                if(preCollision){
                    collision = !grid[x_wrap][y] ? (byte)1 : 0;
                }
            }
            y++;
        }
        return collision;
    }
}

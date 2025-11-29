package enity;

import java.awt.image.BufferedImage;

public class Enity {
    
    public int x, y;
    public int speed;
    public BufferedImage up1, down1, left1, right1, up2, down2, left2, right2;
    public String direction;
    public BufferedImage[] swimFrames;
    public BufferedImage[] idleFrames;
    public BufferedImage[] turnFrames;
    public String state = "idle";

    public static void main(String[] args) {}

    public int spriteCounter = 0;
    public int spriteNum = 1;
}
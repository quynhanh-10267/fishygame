package enity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {
    
    public int x, y;
    public int speed;
    public BufferedImage up1, down1, left1, right1, up2, down2, left2, right2;
    public String direction;
    public BufferedImage[] swimFrames;
    public BufferedImage[] idleFrames;
    public BufferedImage[] turnFrames;
    public String state = "idle";

    public Rectangle solidArea;

    // collision properties
    public int width = 64;
    public int height = 64;
    public boolean collisionOn = false;

    public int spriteCounter = 0;
    public int spriteNum = 1;
    public String name;
}
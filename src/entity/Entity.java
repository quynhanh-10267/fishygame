package entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {
    
    public int x, y;
    public int speed;
    public int width = 64, height = 64;
    
    // State
    public String direction = "left"; 
    public String state = "swim";     
    
    // Frames
    public BufferedImage[] swimFrames;
    public BufferedImage[] eatFrames;
    public BufferedImage[] turnFrames;
    public BufferedImage[] idleFrames;

    // Logic
    public Rectangle solidArea;
    public boolean collisionOn = false;
    public int spriteCounter = 0;
    public int spriteNum = 0;
    public String name;
    public int scoreValue;
    
    // >> ĐÃ XÓA: dy, actionLockCounter (Vì đã chuyển sang Enemy.java)
    
    // Helpers
    public void startEating() {
        if (eatFrames != null && eatFrames.length > 0 && !state.equals("turn")) {
            state = "eat"; spriteNum = 0; spriteCounter = 0;
        }
    }

    public void startTurning() {
        if (turnFrames != null && turnFrames.length > 0 && state.equals("swim")) {
            state = "turn"; spriteNum = 0; spriteCounter = 0;
        } else {
            flipDirection();
        }
    }
    
    public void flipDirection() {
        direction = direction.equals("left") ? "right" : "left";
    }
}
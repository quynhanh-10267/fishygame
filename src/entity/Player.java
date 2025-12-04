package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import main.GamePanel;
import input.MouseHandler;

public class Player extends Entity {
    GamePanel gp;
    MouseHandler mouseH;
    
    // --- 1. CONSTANTS ---
    final int EAT_FRAMES = 6;
    final int IDLE_FRAMES = 6;
    final int SWIM_FRAMES = 15;
    final int TURN_FRAMES = 5;
    
    // >> CẬP NHẬT KÍCH THƯỚC GỐC THEO ẢNH
    final int BASE_WIDTH = 125; 
    final int BASE_HEIGHT = 105; 
    
    // --- 2. ASSETS ---
    public BufferedImage[] eatFrames;
    public BufferedImage[] idleFrames;
    public BufferedImage[] swimFrames;
    public BufferedImage[] turnFrames;
    public BufferedImage upBubble; 
    
    // --- 3. MOVEMENT & LOGIC VARIABLES ---
    private double exactX, exactY;
    private double easing = 0.05;
    private String currentFacing = "right"; 
    
    // --- 4. LEVEL & EFFECT STATE ---
    private int currentLevel = 1; 
    private int effectCounter = 0; 
    private boolean showEffect = false;
    
    public Player(GamePanel gp, MouseHandler mouseH) {
        this.gp = gp;
        this.mouseH = mouseH;
        
        eatFrames = new BufferedImage[EAT_FRAMES];
        idleFrames = new BufferedImage[IDLE_FRAMES];
        swimFrames = new BufferedImage[SWIM_FRAMES];
        turnFrames = new BufferedImage[TURN_FRAMES];
        
        setDefaultValues();
        getPlayerImageByLoop(); 
    }

    public void setDefaultValues() {
        currentLevel = 1;
        updateSize(1.0); // Bắt đầu với tỷ lệ 1.0
        
        x = gp.worldWidth / 2 - width / 2;
        y = gp.worldHeight / 2 - height / 2;
        
        exactX = x;
        exactY = y;

        speed = 5; 
        state = "idle";
        direction = "right";
        currentFacing = "right";
        solidArea = new Rectangle((int)x, (int)y, width, height);
    }
    
    private void updateSize(double scale) {
        // Tính toán kích thước mới dựa trên tỷ lệ
        this.width = (int)(BASE_WIDTH * scale);
        this.height = (int)(BASE_HEIGHT * scale);
        
        if (solidArea != null) {
            solidArea.width = this.width;
            solidArea.height = this.height;
        }
    }

    public void getPlayerImageByLoop() {
        try {
            for (int i = 0; i < EAT_FRAMES; i++) 
                eatFrames[i] = ImageIO.read(getClass().getResourceAsStream("/res/angelfish/angelfisheat" + (i + 1) + ".png"));
            for (int i = 0; i < IDLE_FRAMES; i++) 
                idleFrames[i] = ImageIO.read(getClass().getResourceAsStream("/res/angelfish/angelfishidle" + (i + 1) + ".png"));
            for (int i = 0; i < SWIM_FRAMES; i++) 
                swimFrames[i] = ImageIO.read(getClass().getResourceAsStream("/res/angelfish/angelfishswim" + (i + 1) + ".png"));
            for (int i = 0; i < TURN_FRAMES; i++) 
                turnFrames[i] = ImageIO.read(getClass().getResourceAsStream("/res/angelfish/angelfishturn" + (i + 1) + ".png"));
            
            upBubble = ImageIO.read(getClass().getResourceAsStream("/res/animation/up.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        checkLevelUp();

        // Movement Logic
        double centerX = exactX + width / 2.0;
        double centerY = exactY + height / 2.0;
        double mouseWorldX = mouseH.mouseX + gp.cameraX;
        double mouseWorldY = mouseH.mouseY + gp.cameraY;

        double dx = mouseWorldX - centerX;
        double dy = mouseWorldY - centerY;
        
        exactX += dx * easing;
        exactY += dy * easing;

        // Boundary Check
        if (exactX < 0) exactX = 0;
        if (exactX > gp.worldWidth - width) exactX = gp.worldWidth - width;
        if (exactY < 0) exactY = 0;
        if (exactY > gp.worldHeight - height) exactY = gp.worldHeight - height;

        x = (int) exactX;
        y = (int) exactY;

        // Facing Logic
        if (!state.equals("eat") && !state.equals("turn")) {
            if (Math.abs(dx) > 1.0) {
                String newFacing = (dx > 0) ? "right" : "left";
                if (!newFacing.equals(currentFacing)) {
                    state = "turn";
                    currentFacing = newFacing;
                    spriteNum = 0; 
                }
            }
        }

        // State Update
        if (!state.equals("turn") && !state.equals("eat")) {
            double velocity = Math.sqrt(dx * easing * dx * easing + dy * easing * dy * easing);
            state = (velocity > 0.5) ? "swim" : "idle";
        }

        solidArea.x = x;
        solidArea.y = y;

        // Animation Counter
        spriteCounter++;
        if (spriteCounter > 3) {
            spriteNum++;
            spriteCounter = 0;
            
            if (state.equals("eat")) {
                if (spriteNum >= EAT_FRAMES) { state = "swim"; spriteNum = 0; }
            } else if (state.equals("turn")) {
                if (spriteNum >= TURN_FRAMES) { state = "swim"; spriteNum = 0; }
            } else if (state.equals("swim")) {
                if (spriteNum >= SWIM_FRAMES) spriteNum = 0;
            } else { // idle
                if (spriteNum >= IDLE_FRAMES) spriteNum = 0;
            }
        }
        
        if (showEffect) {
            effectCounter--;
            if (effectCounter <= 0) showEffect = false;
        }
    }
    
    private void checkLevelUp() {
        int newLevel = currentLevel;
        double scale = 1.0;

        // >> LOGIC SCALE MỚI (Dựa trên tính toán diện tích)
        if (gp.score >= 900) {
            newLevel = 3;
            scale = 1.6; // Size: 200x168 (Area 33,600 > Lionfish 28,640)
        } else if (gp.score >= 300) {
            newLevel = 2;
            scale = 1.2; // Size: 150x126 (Area 18,900 > Surgeonfish 17,850)
        } else {
            newLevel = 1;
            scale = 1.0; // Size: 125x105 (Area 13,125 > Minnow 3,120)
        }

        if (newLevel > currentLevel) {
            currentLevel = newLevel;
            updateSize(scale); 
            showEffect = true;
            effectCounter = 60; 
            System.out.println("LEVEL UP! Scale: " + scale);
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage currentFrame = null;
        
        if (state.equals("eat") && spriteNum < EAT_FRAMES) currentFrame = eatFrames[spriteNum];
        else if (state.equals("turn") && spriteNum < TURN_FRAMES) currentFrame = turnFrames[spriteNum];
        else if (state.equals("swim") && spriteNum < SWIM_FRAMES) currentFrame = swimFrames[spriteNum];
        else if (spriteNum < IDLE_FRAMES) currentFrame = idleFrames[spriteNum];

        if (currentFrame != null) {
            // Vẽ theo kích thước thật đã được scale
            int drawWidth = this.width;
            int drawHeight = this.height;
            
            AffineTransform oldTransform = g2.getTransform();
            
            int screenX = x - gp.cameraX;
            int screenY = y - gp.cameraY;
            
            g2.translate(screenX, screenY);

            if (currentFacing.equals("right") && !state.equals("turn")) {
                g2.transform(AffineTransform.getScaleInstance(-1, 1));
                g2.translate(-drawWidth, 0);
            }

            g2.drawImage(currentFrame, 0, 0, drawWidth, drawHeight, null);
            g2.setTransform(oldTransform);
            
            if (showEffect && upBubble != null) {
            // 1. Cấu hình kích thước mong muốn
            int bubbleSize = 64; 
            
            // 2. Tính toán vị trí X để bong bóng nằm GIỮA đầu Player
            // Công thức: (Tâm Player) - (Một nửa kích thước bong bóng) - (Camera)
            int bubbleX = (int)(x + width/2 - bubbleSize/2) - gp.cameraX;
            
            // 3. Tính toán vị trí Y (Bay lên)
            int bubbleY = (int)(y - 20) - gp.cameraY;
            int floatOffset = (60 - effectCounter); 
            
            // 4. Vẽ với kích thước mới (bubbleSize, bubbleSize)
            g2.drawImage(upBubble, 
                bubbleX, 
                bubbleY - floatOffset, 
                bubbleSize, // Chiều rộng mới: 64
                bubbleSize, // Chiều cao mới: 64
                null
            );
        }
        }
    }
    
    public void eating() {
        state = "eat";
        spriteNum = 0;
        spriteCounter = 0;
    }
}
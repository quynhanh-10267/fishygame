package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import main.GamePanel;

public class Banner {
    GamePanel gp;
    private BufferedImage[] images;
    private double[] letterY;
    private double targetY;
    private double riseSpeed = 8.0; 
    
    private boolean active = false;
    private int visibleCounter = 0; 
    private int maxVisibleTime = -1; 
    private int tick = 0; 
    
    private final int IMG_WIDTH = 80;
    private final int IMG_HEIGHT = 80;
    // >> SỬA: Tăng khoảng cách giữa các chữ (10 -> 20)
    private final int SPACING = 20; 
    private final int LETTER_DELAY = 5; 

    public Banner(GamePanel gp) {
        this.gp = gp;
    }

    public void show(String text, int duration) {
        this.maxVisibleTime = duration;
        this.visibleCounter = 0;
        this.tick = 0;
        loadImages(text);
        
        // Vị trí đích: 1/3 màn hình
        this.targetY = gp.screenHeight / 3.0;
        
        if (images != null) {
            letterY = new double[images.length];
            for (int i = 0; i < letterY.length; i++) {
                letterY[i] = gp.screenHeight + 100; 
            }
        }
        this.active = true;
    }

    public boolean isActive() {
        return active;
    }

    private void loadImages(String text) {
        text = text.toUpperCase();
        int len = text.length();
        images = new BufferedImage[len];
        try {
            for (int i = 0; i < len; i++) {
                char c = text.charAt(i);
                if (c == ' ') { images[i] = null; continue; }
                
                // Hỗ trợ cả chữ cái và số
                String path = "/res/text/" + c + ".png";
                if (getClass().getResourceAsStream(path) != null) {
                    images[i] = ImageIO.read(getClass().getResourceAsStream(path));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void update() {
        if (!active || letterY == null) return;
        tick++;
        for (int i = 0; i < letterY.length; i++) {
            int startFrame = i * LETTER_DELAY;
            if (tick >= startFrame) {
                if (letterY[i] > targetY) {
                    letterY[i] -= riseSpeed;
                    double distance = letterY[i] - targetY;
                    if (distance < 150) {
                        letterY[i] -= (riseSpeed * 0.15); 
                        if (letterY[i] - targetY < 2) letterY[i] = targetY;
                    }
                } else {
                    double floatOffset = Math.sin((tick * 0.05) + i) * 2;
                    letterY[i] = targetY + floatOffset;
                }
            }
        }
        if (maxVisibleTime != -1) {
            if (tick > letterY.length * LETTER_DELAY + 30) { 
                visibleCounter++;
                if (visibleCounter > maxVisibleTime) {
                    active = false;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        if (!active || images == null) return;
        int totalWidth = (images.length * IMG_WIDTH) + ((images.length - 1) * SPACING);
        int startX = (gp.screenWidth - totalWidth) / 2;
        for (int i = 0; i < images.length; i++) {
            int drawX = startX + i * (IMG_WIDTH + SPACING);
            int drawY = (int) letterY[i];
            if (images[i] != null) {
                g2.drawImage(images[i], drawX, drawY, IMG_WIDTH, IMG_HEIGHT, null);
            }
        }
    }
}
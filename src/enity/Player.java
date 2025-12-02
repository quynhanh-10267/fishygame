package enity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;
    // >> SỐ LƯỢNG FRAME CHO MỖI TRẠNG THÁI
    final int SWIM_IDLE_FRAMES = 12;
    final int TURN_FRAMES = 6;
    
    private String currentFacing = "right"; // Mặc định cá hướng sang phải

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        // Khởi tạo mảng frame
        idleFrames = new BufferedImage[SWIM_IDLE_FRAMES];
        swimFrames = new BufferedImage[SWIM_IDLE_FRAMES];
        turnFrames = new BufferedImage[TURN_FRAMES];
        setDefaultValues();
        
        getPlayerImageByLoop();
    }

    public void setDefaultValues() {
        x = gp.screenWidth / 2 - gp.tileSize / 2;
        y = gp.screenHeight / 2 - gp.tileSize / 2;
        speed = 7;
        state = "idle";
        spriteNum=0;
        currentFacing = "left";
        direction = "down";
        solidArea = new Rectangle(x, y, gp.tileSize, gp.tileSize);
    }
    
    
    public void getPlayerImageByLoop() {
        try {
            // >> TẢI IDLE FRAMES (idle1.png -> idle10.png)
            for (int i = 0; i < SWIM_IDLE_FRAMES; i++) {
                String fileName = "/res/idle" + (i + 1) + ".png";
                idleFrames[i] = ImageIO.read(getClass().getResourceAsStream(fileName));
            }

           
            for (int i = 0; i < SWIM_IDLE_FRAMES; i++) {
                String fileName = "/res/swim" + (i + 1) + ".png";
                swimFrames[i] = ImageIO.read(getClass().getResourceAsStream(fileName));
            }

          
            for (int i = 0; i < TURN_FRAMES; i++) {
                String fileName = "/res/turn" + (i + 1) + ".png";
                turnFrames[i] = ImageIO.read(getClass().getResourceAsStream(fileName));
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi tải frame. Kiểm tra tên file và đường dẫn trong /res/");
            e.printStackTrace();
        }
    }
    public void collisionChecker(Aquarium aq){
        // Update solidArea position
        solidArea.x = x;
        solidArea.y = y;

        // Assume no collision at start of check
        collisionOn = false;

        for (int i = 0; i < aq.entities.size(); i++) {
            Entity e = aq.entities.get(i);
            if (e == null) continue;

            // Check intersection using helper method
            if (solidArea.intersects(e.solidArea)) {
                collisionOn = true;
                // Basic behaviour: remove the entity (eaten/collected)
                aq.entities.remove(i);
                i--; // adjust index after removal
            }
        }
    }
    public void update() {
        
        boolean isMoving = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;
        String newDirection = direction;
       
        boolean shouldTurn = false;
        if (isMoving){
            if (keyH.upPressed == true) {
                newDirection = "up";
                y = Math.max(0, y - speed);
            }
            else if (keyH.downPressed == true) {
                newDirection = "down";
                y = Math.min(gp.screenHeight - gp.tileSize, y + speed);
            }
            else if (keyH.leftPressed == true) {
                newDirection = "left";
                x -= speed;
                currentFacing = "left"; 
                if (!state.equals("turn")) { state = "swim"; }
            }
            else if (keyH.rightPressed == true) {
                newDirection = "right";
                x+= speed;
                // Kiểm tra quay đầu: Đang hướng trái, nhấn phải
                if (currentFacing.equals("left")) { shouldTurn = true; } 
                currentFacing = "right"; // Cập nhật hướng mặt
            }
            
            
            if (newDirection.equals("left") && currentFacing.equals("right")) {
                currentFacing = "left";
                shouldTurn = true;
            } else if (newDirection.equals("right") && currentFacing.equals("left")) {
                currentFacing = "right";
                shouldTurn = true;
            }
            
            if (shouldTurn) {
                state = "turn";
                spriteNum = 0; // Bắt đầu hoạt hình quay đầu
            } else if (!state.equals("turn")) {
                 // Nếu không quay và không phải đang quay, thì bơi
                 state = "swim";
            }
            direction = newDirection; // Cập nhật hướng di chuyển
        } else {
            // Không nhấn phím nào
            if (!state.equals("turn")) { // Trừ khi đang trong quá trình quay
                state = "idle";
            }
        }
        // GIỚI HẠN DI 
        x = Math.max(0, Math.min(gp.screenWidth - gp.tileSize, x));
        y = Math.max(0, Math.min(gp.screenHeight - gp.tileSize, y));
        
        spriteCounter++; // vẽ lại hướng đi mỗi 10 frame
        if (spriteCounter >10) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;   
        }
        int animationSpeed = 4; 

        if (spriteCounter > animationSpeed) {
            spriteCounter = 0;
            spriteNum++; 
            
            if (state.equals("turn")) {
                // Hoạt hình Quay Đầu (Turn)
                if (spriteNum >= TURN_FRAMES) {
                    spriteNum = 0;
                    state = "idle"; // Quay đầu xong, chuyển về đứng yên
                }
            } else if (state.equals("swim")) {
                // Hoạt hình Bơi (Swim)
                if (spriteNum >= SWIM_IDLE_FRAMES) {
                    spriteNum = 0; // Lặp lại hoạt hình bơi
                }
            } else if (state.equals("idle")) {
                // Hoạt hình Đứng Yên (Idle)
                 if (spriteNum >= SWIM_IDLE_FRAMES) {
                    spriteNum = 0; // Lặp lại hoạt hình đứng yên
                }
            }
        }
    }
    public void draw(Graphics2D g2) {
        BufferedImage currentFrame = null;
        
        // 1. CHỌN FRAME DỰA TRÊN TRẠNG THÁI VÀ spriteNum
        // Luôn kiểm tra bounds để tránh lỗi IndexOutOfBoundsException
        if (state.equals("turn") && spriteNum < TURN_FRAMES) {
            currentFrame = turnFrames[spriteNum];
        } else if (state.equals("swim") && spriteNum < SWIM_IDLE_FRAMES) {
            currentFrame = swimFrames[spriteNum];
        } else if (state.equals("idle") && spriteNum < SWIM_IDLE_FRAMES) {
            currentFrame = idleFrames[spriteNum];
        }

        if (currentFrame != null) {
            
            int targetWidth = gp.tileSize;
            int targetHeight = gp.tileSize;
            
            AffineTransform oldTransform = g2.getTransform();

            // >> BƯỚC 2: DỊCH CHUYỂN G2D ĐẾN VỊ TRÍ CỦA CÁ
            g2.translate(x, y);
            // 2. XỬ LÝ LẬT HÌNH (FLIP) CHO HƯỚNG DI CHUYỂN TRÁI/PHẢI
           if (currentFacing.equals("right") && !state.equals("turn")) {
            // Lật hình ngang qua trục Y và dịch chuyển ngược lại bằng targetWidth
            g2.transform(AffineTransform.getScaleInstance(-1, 1));
            g2.translate(-targetWidth, 0); 
            }
            // Lưu ý: Nếu trạng thái là "turn", ta không lật.

           
            g2.drawImage(currentFrame, 0, 0, targetWidth, targetHeight, null);
            g2.setTransform(oldTransform);
            }
    }
}


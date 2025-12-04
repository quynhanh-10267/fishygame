package entity;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import main.GamePanel;

public class Feature {
    
    public static class MonsterType {
        String name;
        String folderPath; // Thư mục chứa ảnh (VD: /res/minnow/)
        int speed;
        int width, height;
        int scoreValue;
        
        // Mảng chứa animation frames
        public BufferedImage[] swimFrames;
        public BufferedImage[] turnFrames;
        public BufferedImage[] eatFrames;
        public BufferedImage[] idleFrames;

        public MonsterType(String name, String folder, int speed, int w, int h, int score, 
                           int swimCount, int turnCount, int eatCount, int idleCount) {
            this.name = name;
            this.folderPath = folder;
            this.speed = speed;
            this.width = w;
            this.height = h;
            this.scoreValue = score;
            
            // Load ảnh động
            this.swimFrames = loadFrames(name + "swim", swimCount);
            this.turnFrames = loadFrames(name + "turn", turnCount);
            
            if (eatCount > 0) this.eatFrames = loadFrames(name + "eat", eatCount);
            if (idleCount > 0) this.idleFrames = loadFrames(name + "idle", idleCount);
        }

        private BufferedImage[] loadFrames(String prefix, int count) {
            BufferedImage[] frames = new BufferedImage[count];
            try {
                for (int i = 0; i < count; i++) {
                    // Path: /res/folder/prefix + index + .png
                    String path = folderPath + prefix + (i + 1) + ".png";
                    frames[i] = ImageIO.read(getClass().getResourceAsStream(path));
                }
            } catch (Exception e) {
                System.err.println("Error loading: " + prefix);
            }
            return frames;
        }
    }
    
    // Hàm factory tạo Entity từ Type
    public Enemy createMonster(MonsterType type, GamePanel gp) {
        // >> KHỞI TẠO ENEMY
        Enemy monster = new Enemy(gp); 
        
        monster.name = type.name;
        monster.speed = type.speed;
        monster.width = type.width;
        monster.height = type.height;
        monster.scoreValue = type.scoreValue;
        
        // Copy references
        monster.swimFrames = type.swimFrames;
        monster.turnFrames = type.turnFrames;
        monster.eatFrames = type.eatFrames;   
        monster.idleFrames = type.idleFrames; 
        
        // Setup hitbox
        monster.solidArea = new java.awt.Rectangle(0, 0, type.width, type.height);
        return monster;
    }
}
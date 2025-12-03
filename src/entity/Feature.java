package entity;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Feature {
    
    // Định nghĩa các loại cá (Blueprint)
    public static class MonsterType {
        String name;
        int speed;
        int width, height; // Kích thước hitbox (liên quan đến size để ăn)
        BufferedImage imageLeft, imageRight;
        
        public MonsterType(String name, int speed, int w, int h) {
            this.name = name;
            this.speed = speed;
            this.width = w;
            this.height = h;
            loadImages();
        }

        private void loadImages() {
            try {
                imageLeft = ImageIO.read(getClass().getResourceAsStream("/res/" + name + "swim1.png"));
                imageRight = ImageIO.read(getClass().getResourceAsStream("/res/" + name + "swim2.png"));
            } catch (IOException | IllegalArgumentException e) {
                System.err.println("Không tìm thấy ảnh cho quái vật: " + name);
                // Tạo ảnh placeholder nếu lỗi để tránh crash
                imageLeft = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                imageRight = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }
        }
    }

    // Danh sách các loại quái vật (Pre-loaded)
    public MonsterType oyster, jellyPink, john, lion, puffShark;

    public Feature() {
        setupMonsters();
    }

    public void setupMonsters() {
        // Cấu hình thông số: Tên, Tốc độ, Rộng, Cao
        // Logic: Size càng to thì càng khó ăn
        oyster = new MonsterType("oyster", 0, 40, 40);      // Đứng im hoặc rất chậm
        jellyPink = new MonsterType("jelly", 3, 50, 50);    
        john = new MonsterType("john", 4, 60, 45);
        lion = new MonsterType("lion", 5, 80, 80);
        puffShark = new MonsterType("puff", 7, 120, 100);   // Boss nhỏ
    }
    
    // Hàm factory để tạo Entity dựa trên Type
    public Entity createMonster(MonsterType type) {
        Entity monster = new Entity();
        monster.name = type.name;
        monster.speed = type.speed;
        monster.width = type.width;
        monster.height = type.height;
        monster.up1 = type.imageLeft;  // Gán ảnh tạm
        monster.up2 = type.imageRight; // Có thể mở rộng class Entity để chứa imageLeft/Right riêng
        
        // Cập nhật hitbox
        monster.solidArea = new java.awt.Rectangle(0, 0, type.width, type.height);
        return monster;
    }
}
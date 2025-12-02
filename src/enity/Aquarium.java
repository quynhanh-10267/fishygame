package enity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import main.GamePanel;

public class Aquarium extends Entity {
    GamePanel gp;
    Random rand = new Random();
    
    // Danh sách chứa tất cả cá (trừ người chơi)
    public ArrayList<Entity> entities = new ArrayList<>();
    
    // Bộ đếm thời gian
    int spawnCounter = 0;

    public Aquarium(GamePanel gp) {
        this.gp = gp;
    }

    // Hàm sinh ngẫu nhiên 1 con cá
    public void spawnEntity() {
        Entity obj = new Entity();

        // 1. Random vị trí (trong phạm vi screenHeight)
        obj.x = rand.nextInt(0,2);
        obj.direction = "right";
        obj.y = rand.nextInt(gp.screenHeight - gp.tileSize); // Vị trí y ngẫu nhiên
        if(obj.x == 1) {
            obj.x = gp.screenWidth; // Bên ngoài
            obj.direction = "left";
        }
        
        obj.solidArea = new Rectangle(obj.x, obj.y, gp.tileSize, gp.tileSize);
        
        obj.name = "mer";
        obj.speed = 5;
        if(obj.direction == "left") {
            try {
                obj.up1 = ImageIO.read(getClass().getResourceAsStream("/res/"+ obj.name +"1.png"));
            } catch (IOException e) {
                System.out.println("Lỗi khi tải ảnh thức ăn!");
                e.printStackTrace();
            }
        } else
        {
            try {
                obj.up1 = ImageIO.read(getClass().getResourceAsStream("/res/"+ obj.name +"2.png"));
            } catch (IOException e) {
                System.out.println("Lỗi khi tải ảnh thức ăn!");
                e.printStackTrace();
            }
        }
        // obj.image = ... (Load ảnh cá bé ở đây hoặc trong class Entity)

        entities.add(obj); // Thêm vào danh sách
    }

    // Hàm cập nhật (Gọi trong GamePanel.update)
    public void update() {
        // --- LOGIC SINH CÁ ---
        spawnCounter++;
        if (spawnCounter > 60) { // Cứ 60 khung hình (khoảng 1s) thì sinh 1 con
            spawnEntity();
            spawnCounter = 0;
        }

        // --- CẬP NHẬT VỊ TRÍ TỪNG CON ---
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e != null) {
                if(e.direction == "left")
                {
                    e.x -= e.speed; // Di chuyển sang trái
                    e.solidArea.x = e.x;
                } else {
                    e.x += e.speed; // Di chuyển sang phải
                    e.solidArea.x = e.x;
                }
            }
            // (Tùy chọn) Xóa cá nếu bơi ra xa quá hoặc danh sách quá dài
        }
    }

    // Hàm vẽ (Gọi trong GamePanel.paintComponent)
    public void draw(Graphics2D g2) {
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e != null) { 
                g2.drawImage(e.up1, e.x, e.y, e.up1.getWidth(), e.up1.getHeight(), null);
            }
        }
    }
}

package entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import main.GamePanel;

public class Aquarium {
    GamePanel gp;
    Random rand = new Random();
    
    // Danh sách chứa Enemy
    public ArrayList<Enemy> entities = new ArrayList<>();
    
    // Các biến đếm nội bộ (Internal State)
    private int spawnCounter = 0;
    private int moveTick = 0; 
    final int SLOW_DOWN_FACTOR = 2; 

    public Aquarium(GamePanel gp) {
        this.gp = gp;
    }

    // >> PHƯƠNG THỨC MỚI: Dùng để Reset game sạch sẽ
    public void reset() {
        entities.clear();
        spawnCounter = 0;
        moveTick = 0;
    }

    public void spawnEntity() {
        // Lấy danh sách quái từ Level hiện tại
        ArrayList<Feature.MonsterType> types = gp.currentLevel.monsterTypes;
        if (types == null || types.isEmpty()) return;

        // --- Logic Spawn có trọng số (Weighted Random) ---
        int index = 0;
        int dice = rand.nextInt(100);

        // Giả sử Level 1 có 3 loại: Minnow, Surgeonfish, Lionfish
        if (types.size() >= 3) {
            if (dice < 60) index = 0;      // 60% ra Minnow
            else if (dice < 90) index = 1; // 30% ra Surgeonfish
            else index = 2;                // 10% ra Lionfish
        } else {
            index = rand.nextInt(types.size());
        }

        Feature.MonsterType selectedType = types.get(index);
        
        // Giảm độ khó đầu game: Nếu điểm thấp mà ra Boss -> Đổi thành Minnow
        if (gp.score < 300 && selectedType.name.equals("lionfish")) {
            if (rand.nextInt(100) < 90) selectedType = types.get(0);
        }

        Enemy monster = gp.feature.createMonster(selectedType, gp);

        // Random vị trí & hướng
        boolean isRight = rand.nextBoolean();
        monster.direction = isRight ? "right" : "left";
        monster.y = rand.nextInt(gp.worldHeight - monster.height);
        
        if (isRight) monster.x = -monster.width;
        else monster.x = gp.worldWidth;

        monster.dy = rand.nextInt(3) - 1; 
        monster.actionLockCounter = 0;
        
        entities.add(monster);
    }

    public void update() {
        spawnCounter++;
        // Tốc độ spawn nhanh hơn một chút (50 frames)
        if (spawnCounter > 50) { 
            spawnEntity();
            spawnCounter = 0;
        }

        moveTick++;
        boolean allowMove = (moveTick % SLOW_DOWN_FACTOR == 0);

        for (int i = 0; i < entities.size(); i++) {
            Enemy e = entities.get(i);
            if (e != null) {
                // Update AI & Position
                e.update(allowMove);
                
                // Garbage Collection
                if (e.x < -200 || e.x > gp.worldWidth + 200) {
                    entities.remove(i);
                    i--; 
                }
            }
        }
        
        if (moveTick > 1000) moveTick = 0;
        checkPredatorCollision();
    }

    private void checkPredatorCollision() {
        for (int i = 0; i < entities.size(); i++) {
            Enemy predator = entities.get(i);
            for (int j = 0; j < entities.size(); j++) {
                if (i == j) continue; 
                Enemy prey = entities.get(j);
                
                if (predator.solidArea.intersects(prey.solidArea)) {
                    int sizeA = predator.width * predator.height;
                    int sizeB = prey.width * prey.height;

                    if (sizeA > sizeB * 1.2) {
                        predator.startEating();
                        entities.remove(j);
                        if (j < i) i--; 
                        j--; 
                    }
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        for (Enemy e : entities) {
            e.draw(g2);
        }
    }
}
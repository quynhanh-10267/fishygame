package main;

import entity.MonsterType;
import java.util.ArrayList;

public class Level {
    
    public int levelNum;
    public int winScore;
    public ArrayList<MonsterType> monsterTypes = new ArrayList<>();
    
    public Level(int levelNum) {
        this.levelNum = levelNum;
        setupLevel();
    }
    
    private void setupLevel() {
        if (levelNum == 1) {
            this.winScore = 2000; 
            
            // 1. MINNOW (40x30) - 30 điểm
            // Player Lv1 (50x40) LỚN HƠN -> Ăn được
            monsterTypes.add(new MonsterType(
                "minnow", "/res/minnow/", 2, 40, 30, 30, 
                15, 7, 0, 0 
            ));
            // 2. SURGEONFISH (75x55) - 60 điểm
            // Player Lv2 (75x63) LỚN HƠN -> Ăn được
            monsterTypes.add(new MonsterType(
                "surgeonfish", "/res/surgeonfish/", 3, 75, 55, 60, 
                14, 5, 5, 0 
            ));
            // 3. LIONFISH (90x60) - 90 điểm
            // Player Lv3 (100x80) LỚN HƠN -> Ăn được
            monsterTypes.add(new MonsterType(
                "lionfish", "/res/lionfish/", 4, 90, 60, 150, 
                14, 5, 6, 6 
            ));
        }
    }
}
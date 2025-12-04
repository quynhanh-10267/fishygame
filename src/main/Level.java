package main;

import entity.Feature.MonsterType;
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
            
            // 1. MINNOW (65x48) - 30 điểm
            // Player gốc (125x105) ăn được
            monsterTypes.add(new MonsterType(
                "minnow", "/res/minnow/", 2, 65, 48, 30, 
                15, 7, 0, 0 
            ));
            
            // 2. SURGEONFISH (170x105) - 60 điểm
            // Player gốc (125x105) NHỎ HƠN -> Không ăn được
            // Player Lv2 (150x126) có Diện tích lớn hơn -> Ăn được
            monsterTypes.add(new MonsterType(
                "surgeonfish", "/res/surgeonfish/", 3, 170, 105, 60, 
                14, 5, 5, 0 
            ));
            
            // 3. LIONFISH (179x160) - 150 điểm
            // Player Lv2 (150x126) NHỎ HƠN -> Không ăn được
            // Player Lv3 (200x168) LỚN HƠN -> Ăn được
            monsterTypes.add(new MonsterType(
                "lionfish", "/res/lionfish/", 4, 179, 160, 150, 
                14, 5, 6, 6 
            ));
        }
    }
}
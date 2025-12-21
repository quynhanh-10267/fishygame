package main;

import entity.Enemy;
import entity.Player;
import java.util.ArrayList;

public class CollisionChecker {
    
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkPlayerVsEnemies(Player player, ArrayList<Enemy> enemies) {
        int pPad = 10;
        player.solidArea.setBounds(player.x + pPad, player.y + pPad, player.width - 2*pPad, player.height - 2*pPad);

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy == null) continue;


            int paddingX = (int)(enemy.width * 0.2); 
            int paddingY = (int)(enemy.height * 0.2); 

            enemy.solidArea.setBounds(
                enemy.x + paddingX, 
                enemy.y + paddingY, 
                enemy.width - 2 * paddingX, 
                enemy.height - 2 * paddingY
            );

            if (player.solidArea.intersects(enemy.solidArea)) {
                processCollision(player, enemy, i);
            }
        }
    }

    public void processCollision(Player player, Enemy enemy, int index) {
        int playerSize = player.width * player.height;
        int enemySize = enemy.width * enemy.height;

        if (playerSize > enemySize) {
            // ĂN
            gp.aquarium.entities.remove(index);
            gp.score += enemy.scoreValue;
            player.eating();
        } else {
            // BỊ ĂN
            gp.lives--;
            gp.aquarium.entities.remove(index); 
            
            System.out.println("Ouch! Lives left: " + gp.lives);
            
            if (gp.lives > 0) {
                // >> TRƯỜNG HỢP 1: CÒN MẠNG -> PAUSE GAME + HIỆN SORRY
                gp.gameState = gp.respawnState; // Dừng game
                gp.banner.show("SORRY", 180); // Hiện chữ SORRY trong 2 giây (120 frames)
                
            } else {
                // >> TRƯỜNG HỢP 2: HẾT MẠNG -> GAME OVER
                gp.gameState = gp.gameOverState;
                gp.banner.show("YOU LOSE", -1); 
                System.out.println("GAME OVER");
            }
        }
    }
}
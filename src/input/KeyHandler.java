package input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import main.GamePanel;

public class KeyHandler extends KeyAdapter {
    
    private GamePanel gp;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        // Xử lý phím M (Menu/Pause)
        if (code == KeyEvent.VK_M) {
            if (gp.gameState == gp.playState) {
                gp.gameState = gp.pauseState; // Đang chơi -> Pause
            } else if (gp.gameState == gp.pauseState) {
                gp.gameState = gp.playState; // Đang Pause -> Chơi tiếp
            }
        }
    }
}
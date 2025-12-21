package input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import main.GamePanel;

public class MouseHandler extends MouseAdapter {
    
    private GamePanel gp;
    public int mouseX, mouseY; 

    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    // --- XỬ LÝ DI CHUYỂN ---
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        
        // >> LOGIC CHECK HOVER CHO MENU
        if (gp.gameState == gp.pauseState) {
            // Kiểm tra New Game
            if (gp.newGameRect != null && gp.newGameRect.contains(mouseX, mouseY)) {
                gp.commandNum = 0; // Đang chọn New Game
            } 
            // Kiểm tra Exit
            else if (gp.exitRect != null && gp.exitRect.contains(mouseX, mouseY)) {
                gp.commandNum = 1; // Đang chọn Exit
            } 
            else if (gp.gameOptionRect != null && gp.gameOptionRect.contains(mouseX, mouseY)) {
                gp.commandNum = 2; // Đang chọn Gameoptions
            }
            // Không chọn gì
            else {
                gp.commandNum = -1;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    // --- XỬ LÝ CLICK ---
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gp.gameState == gp.pauseState) {
            int mx = e.getX();
            int my = e.getY();
            
            if (gp.newGameRect != null && gp.newGameRect.contains(mx, my)) {
                gp.resetGame();
            }
            else if (gp.exitRect != null && gp.exitRect.contains(mx, my)) {
                System.exit(0);
            }
        
        }
    }
}
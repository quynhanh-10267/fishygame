package main;
import javax.swing.JFrame;

public class FishyGame  {
    public static void main(String[] args) {
        JFrame window = new JFrame("Fishy Game");
        GamePanel gamePanel = new GamePanel();// bảng vẽ và xử lý logic
        
        UI menu = new UI(window, gamePanel);
        window.add(menu);
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);// hiển thị cửa sổ ở giữa màn hình
        
        window.setVisible(true);
        
    }
}
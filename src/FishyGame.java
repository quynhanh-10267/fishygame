import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class FishyGame extends JPanel implements KeyListener {
    private BufferedImage background;
    private int playerX = 100, playerY = 100; // Initial position
    private final int PLAYER_SIZE = 40;
    private final int MOVE_AMOUNT = 10;

    public FishyGame() {
        try {
            // Load your PNG background image.
            background = ImageIO.read(new File("background.png"));
        } catch (IOException e) {
            System.out.println("Background could not be loaded!");
        }
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null)
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        // Draw a "player" as a red rectangle
        g.setColor(Color.RED);
        g.fillRect(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                playerX = Math.max(playerX - MOVE_AMOUNT, 0);
                break;
            case KeyEvent.VK_RIGHT:
                playerX = Math.min(playerX + MOVE_AMOUNT, getWidth() - PLAYER_SIZE);
                break;
            case KeyEvent.VK_UP:
                playerY = Math.max(playerY - MOVE_AMOUNT, 0);
                break;
            case KeyEvent.VK_DOWN:
                playerY = Math.min(playerY + MOVE_AMOUNT, getHeight() - PLAYER_SIZE);
                break;
        }
        repaint();
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fishy Game");
        FishyGame game = new FishyGame();
        frame.add(game);
        frame.setSize(800, 600); // Adjust as needed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
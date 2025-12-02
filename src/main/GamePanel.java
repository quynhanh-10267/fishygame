package main;

import java.awt.Dimension;
import java.awt.Color;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import enity.Player;
import enity.Aquarium;
import java.awt.Graphics;

public class GamePanel extends JPanel implements Runnable {
    //screen settings
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 2;
    public final int tileSize = originalTileSize * scale; // 64x64 tile
    public final int screenWidth = 960; // 640 pixels
    public final int screenHeight = 730; // 480 pixels
    public BufferedImage background;
    
    int FPS =60;
    
    KeyHandler keyH = new KeyHandler();
    Player player = new Player(this, keyH);
    Aquarium aquarium = new Aquarium(this);
    Thread gameThread; 

    public GamePanel () {
        
        try {
            // Load your PNG background image from the classpath (resources folder)
            background = ImageIO.read(getClass().getResourceAsStream("/res/background.png"));
        } catch (IOException e) {
            System.out.println("Background could not be loaded!");
            e.printStackTrace();
        }

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void startGameThread() {
        if(gameThread == null){
            gameThread = new Thread(this);
            gameThread.start();// gọi run
        }
        
    }

    @Override
    public void run() {

        double drawInterval = 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {

            update();
            repaint(); //paintComponent gọi lại
            
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime/1000000; // vì hàm sleep nhận ms còn ở đây là nanos
                if (remainingTime > 0) {
                    Thread.sleep((long) remainingTime);
                }
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            nextDrawTime += drawInterval;
        }
    }
    public void update() {
        
        player.update();
        aquarium.update();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //background
        if (background != null)
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        //player
        Graphics2D g2 = (Graphics2D)g;
        player.collisionChecker(aquarium);
        player.draw(g2);
        aquarium.draw(g2);
        g2.dispose();// dừng tài nguyên
    }
}

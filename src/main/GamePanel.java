package main;

import entity.Aquarium;
import entity.Banner;
import entity.Feature;
import entity.Player;
import input.KeyHandler;
import input.MouseHandler;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {
    
    // --- 1. SCREEN & WORLD SETTINGS ---
    public final int screenWidth = 1000;
    public final int screenHeight = 750;
    public final int worldWidth = 1280;
    public final int worldHeight = 960;
    public final int originalTileSize = 16;
    public final int scale = 2;
    public final int tileSize = originalTileSize * scale; 

    // --- 2. CAMERA ---
    public int cameraX = 0;
    public int cameraY = 0;

    // --- 3. GAME STATE ---
    public int gameState;
    public final int playState = 1;
    public final int gameOverState = 2;
    public final int winState = 3;
    public final int pauseState = 4;
    
    // --- 4. DATA ---
    public Level currentLevel;
    public boolean startBannerShown = false; 
    public int score = 0;
    public int lives = 3;

    // --- 5. MENU ASSETS & LOGIC ---
    private BufferedImage menuBg, btnNewGame, btnExit;
    private int menuX, menuY;
    
    // Public để MouseHandler truy cập check click
    public Rectangle newGameRect, exitRect; 
    
    // Biến điều khiển Animation Menu
    public int commandNum = -1; // -1: None, 0: NewGame, 1: Exit
    private int menuTick = 0;   // Đếm thời gian để tạo sóng

    // --- 6. SYSTEM ---
    int FPS = 60;
    public BufferedImage background;
    
    // Input Handlers
    public MouseHandler mouseH;
    public KeyHandler keyH;
    
    public CollisionChecker cChecker = new CollisionChecker(this);
    public Feature feature = new Feature();
    public Banner banner; 
    Thread gameThread;
    
    // Cursor Management
    private Cursor blankCursor;   
    private Cursor defaultCursor; 
    
    // --- 7. ENTITIES ---
    public Player player;
    public Aquarium aquarium;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        
        // Init Input Handlers
        mouseH = new MouseHandler(this);
        keyH = new KeyHandler(this);
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseH);
        this.addKeyListener(keyH);
        
        currentLevel = new Level(1);
        gameState = playState;

        // Init Cursors
        defaultCursor = Cursor.getDefaultCursor();
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        this.setCursor(blankCursor); // Mặc định ẩn

        // Load & Setup
        loadResources();
        setupMenuPositions();
        
        // Init Entities
        player = new Player(this, mouseH);
        aquarium = new Aquarium(this);
        
        banner = new Banner(this);
        banner.show("LEVEL 1", 180);
    }

    private void loadResources() {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/res/background.png"));
            menuBg = ImageIO.read(getClass().getResourceAsStream("/res/screen/menuu.png"));
            btnNewGame = ImageIO.read(getClass().getResourceAsStream("/res/screen/newgame.png"));
            btnExit = ImageIO.read(getClass().getResourceAsStream("/res/screen/exit.png"));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void setupMenuPositions() {
        int bgW = 558; int bgH = 448;
        menuX = (screenWidth - bgW) / 2;
        menuY = (screenHeight - bgH) / 2;
        
        int centerY = menuY + bgH / 2;
        int ngW = 132; int ngH = 132;
        int exW = 89;  int exH = 89;
        int gap = 60;
        
        int totalBtnWidth = ngW + gap + exW;
        int startX = menuX + (bgW - totalBtnWidth) / 2;
        
        newGameRect = new Rectangle(startX, centerY - ngH/2, ngW, ngH);
        exitRect = new Rectangle(startX + ngW + gap, centerY - exH/2, exW, exH);
    }

    public void resetGame() {
        score = 0;
        lives = 3;
        currentLevel = new Level(1);
        aquarium.reset(); 
        player.setDefaultValues();
        banner.show("LEVEL 1", 180);
        startBannerShown = false;
        gameState = playState;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        while (gameThread != null) {
            update();
            repaint();
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public void update() {
        // --- LOGIC CURSOR & MENU ANIMATION ---
        if (gameState == pauseState) {
            // Hiện chuột khi ở Menu
            if (this.getCursor() != defaultCursor) this.setCursor(defaultCursor);
            
            // Tăng biến đếm để tạo sóng cho nút
            menuTick++; 
            
            // Cập nhật banner nền (nếu muốn chữ Sorry vẫn nổi lên khi pause)
            banner.update();
            return; // Dừng logic game
        } 
        else {
            // Ẩn chuột khi chơi
            if (this.getCursor() != blankCursor) this.setCursor(blankCursor);
        }

        banner.update();

        if (gameState == playState) {
            if (!startBannerShown) {
                if (!banner.isActive()) startBannerShown = true; 
            }

            player.update();
            
            // Camera Edge Pushing
            int marginX = 150; int marginY = 100;
            int playerScreenX = player.x - cameraX;
            int playerScreenY = player.y - cameraY;

            if (playerScreenX < marginX) cameraX = player.x - marginX;
            else if (playerScreenX + player.width > screenWidth - marginX) cameraX = (player.x + player.width) - (screenWidth - marginX);

            if (playerScreenY < marginY) cameraY = player.y - marginY;
            else if (playerScreenY + player.height > screenHeight - marginY) cameraY = (player.y + player.height) - (screenHeight - marginY);

            if (cameraX < 0) cameraX = 0;
            if (cameraY < 0) cameraY = 0;
            if (cameraX > worldWidth - screenWidth) cameraX = worldWidth - screenWidth;
            if (cameraY > worldHeight - screenHeight) cameraY = worldHeight - screenHeight;

            aquarium.update();
            cChecker.checkPlayerVsEnemies(player, aquarium.entities);
            
            if (score >= currentLevel.winScore) {
                gameState = winState;
                banner.show("VICTORY", -1);
            }
        }
        else if (gameState == pauseState) {
            // Logic thoát khỏi Pause nếu banner SORRY hết giờ (dành cho trường hợp mất mạng)
            if (!banner.isActive() && lives > 0) gameState = playState;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 1. Draw Background
        if (background != null) {
            int sx1 = cameraX; int sy1 = cameraY;
            int sx2 = cameraX + screenWidth; int sy2 = cameraY + screenHeight;
            if (sx2 > worldWidth) sx2 = worldWidth;
            if (sy2 > worldHeight) sy2 = worldHeight;
            g2.drawImage(background, 0, 0, screenWidth, screenHeight, sx1, sy1, sx2, sy2, null);
        } else {
            g2.setColor(new Color(0, 100, 200)); 
            g2.fillRect(0, 0, screenWidth, screenHeight);
        }

        // 2. Draw Entities
        aquarium.draw(g2);
        player.draw(g2);
        drawGameUI(g2);

        // 3. Draw Overlay (Win/Lose)
        if (gameState == winState || gameState == gameOverState) {
            g2.setColor(new Color(0, 0, 0, 150)); 
            g2.fillRect(0, 0, screenWidth, screenHeight);
            if (gameState == winState) {
                g2.setFont(new Font("Arial", Font.BOLD, 30));
                g2.setColor(Color.WHITE);
                String subText = "Level Completed!";
                int subLength = (int)g2.getFontMetrics().getStringBounds(subText, g2).getWidth();
                g2.drawString(subText, screenWidth / 2 - subLength / 2, screenHeight/2 + 100);
            }
        }
        
        // 4. Draw Pause Menu
        if (gameState == pauseState) {
            drawPauseScreen(g2);
        }

        // 5. Draw Banner
        banner.draw(g2);
        
        g2.dispose();
    }
    
    private void drawPauseScreen(Graphics2D g2) {
        // Lớp nền tối
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        // Vẽ khung Menu
        if (menuBg != null) g2.drawImage(menuBg, menuX, menuY, null);
        
        // --- TÍNH TOÁN HIỆU ỨNG SÓNG (BOBBING EFFECT) ---
        // Biên độ 5px, tốc độ 0.1
        int waveOffset = (int)(Math.sin(menuTick * 0.1) * 5); 

        // Vẽ nút New Game
        if (btnNewGame != null) {
            int y = newGameRect.y;
            // Nếu đang hover (commandNum == 0), cộng thêm sóng
            if (commandNum == 0) y += waveOffset;
            g2.drawImage(btnNewGame, newGameRect.x, y, newGameRect.width, newGameRect.height, null);
        }
        
        // Vẽ nút Exit
        if (btnExit != null) {
            int y = exitRect.y;
            // Nếu đang hover (commandNum == 1), cộng thêm sóng
            if (commandNum == 1) y += waveOffset;
            g2.drawImage(btnExit, exitRect.x, y, exitRect.width, exitRect.height, null);
        }
    }
    
    private void drawGameUI(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Level: " + currentLevel.levelNum, 20, 40);
        g2.drawString("Score: " + score + " / " + currentLevel.winScore, 20, 70);
        g2.drawString("Lives: " + lives, 20, 100);
        
        if (gameState == playState) {
            g2.setFont(new Font("Arial", Font.ITALIC, 14));
            g2.drawString("[M] Menu", screenWidth - 100, 40);
        }
    }
}
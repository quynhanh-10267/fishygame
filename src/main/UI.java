package main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UI extends JPanel {
    JFrame window;
    GamePanel gamePanel;
    public Image background;
    public Image bambooFrame;
    public Image titleLogo;
    public Image seashellButton1;
    public Image seashellButton2;
    public boolean isHovered = false;
    public Image andyFish;
    public int btnX, btnY, btnW, btnH;
    public final Color TEXT_COLOR_CREAM = new Color(255, 253, 240, 190 ); // mau trang kem
    // xanh la dam
    public final Color BTN_TEXT_COLOR = new Color(0, 100, 0);
    public Font titleFont = new Font("Serif", Font.PLAIN, 42); // Font mỏng hơn chút
    public Font bodyFont = new Font("Serif", Font.PLAIN, 25);
    private Font buttonFont = new Font("Brush Script MT", Font.BOLD, 32);
    public final String[] introLines = {
        "Drag  the  mouse  to  control,",
        "avoid  bigger  fish,  try  to",
        "eat  smaller  fish  to  make",
        "lead  grew  rapidly.",
        "", 
        "Press  the  left  mouse  button,",
        "can  accelerate  to  catch  small  fish",
        "",
        "Continuous  eat  smaller  fish,  can  make",
        "the  score  into  times  increase.."
    };


    public UI(JFrame window, GamePanel gamePanel){
        this.setPreferredSize(new Dimension(gamePanel.screenWidth, gamePanel.screenHeight));
        this.window = window;
        this.gamePanel = gamePanel;

        try {
            background = ImageIO.read(getClass().getResourceAsStream("/res/screen/background.png"));
            bambooFrame = ImageIO.read(getClass().getResourceAsStream("/res/screen/menuu.png"));
            titleLogo = ImageIO.read(getClass().getResourceAsStream("/res/screen/gametitle.png"));
            seashellButton1 = ImageIO.read(getClass().getResourceAsStream("/res/screen/sanho1.png"));
            seashellButton2 = ImageIO.read(getClass().getResourceAsStream("/res/screen/sanho2.png"));
            andyFish = ImageIO.read(getClass().getResourceAsStream("/res/angelfish/angelfishidle1.png"));
    

        } catch (IOException e) { e.printStackTrace(); }
    
       
        // xu ly click chuot de chuyen man hinh
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                if(isHovered){
                    // code chuyen man hinh
                    startGame();
                }
            }
        });
        // xu ly di chuyen chuot(tao hieu ung sang)
        this.addMouseMotionListener((new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e){
                // Kiểm tra xem tọa độ chuột có nằm trong hình chữ nhật của nút Play không
                boolean isInside = (e.getX() >= btnX && e.getX() <= btnX + btnW &&
                                    e.getY() >= btnY && e.getY() <= btnY + btnH);

                // Nếu trạng thái thay đổi (đang không chạm -> chạm, hoặc ngược lại)
                if (isInside != isHovered) {
                    isHovered = isInside; // Cập nhật trạng thái
                    repaint(); // Vẽ lại màn hình ngay lập tức để đổi ảnh
                }
            }            
        }));
    }
    

    // THÊM HÀM NÀY ĐỂ CHUYỂN MÀN HÌNH
    public void startGame() {
        // 1. Xóa Menu hiện tại khỏi cửa sổ
        window.remove(this);
        // 2. Thêm GamePanel vào cửa sổ
        window.add(gamePanel);
        // 3. Cập nhật lại giao diện
        window.revalidate();
        window.repaint();
        // 4. Quan trọng: Yêu cầu GamePanel chạy game và nhận bàn phím
        gamePanel.startGameThread(); // Hàm này bạn phải viết bên GamePanel
        gamePanel.requestFocusInWindow(); 
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
       
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = gamePanel.screenWidth;
        int height = gamePanel.screenHeight;

        // 1. Vẽ Nền
        if (background != null) g2d.drawImage(background, 0, 0, width, height, null);

        // 2. Vẽ Khung tre
        int frameMargin = (int)(width * 0.1);
        int frameW = width - frameMargin * 2;
        int frameH = (int)(height - frameMargin * 1);
        int frameY = (int)(height*0.07);
        if (bambooFrame != null) g2d.drawImage(bambooFrame, frameMargin, frameY, frameW, frameH, null);

        // 3. Vẽ Logo Title
        int logoW = (int)(width * 0.38);
        int logoH = (int)(logoW * 0.4); 
        if (titleLogo != null) g2d.drawImage(titleLogo, (width - logoW) / 2, frameY - (int)(logoH * 0.1), logoW, logoH, null);

        // 4. Vẽ Chữ "GameIntro"
        g2d.setColor(TEXT_COLOR_CREAM);
        g2d.setFont(titleFont); // Dùng font Serif cho giống style cổ điển
        String title = "GameIntro";
        FontMetrics fmTitle = g2d.getFontMetrics();
        int titleY = (int)(height*0.3);
        g2d.drawString(title, (width - fmTitle.stringWidth(title)) / 2, titleY);

        // 5. Vẽ nội dung hướng dẫn
        // --- QUAN TRỌNG: TẠO HIỆU ỨNG KÉO DÃN FONT ---
        AffineTransform stretch = new AffineTransform();
        // Số 1.2 nghĩa là kéo dãn chiều ngang ra 120%
        // Số 1.0 nghĩa là giữ nguyên chiều cao
        stretch.scale(1.25, 1.0); 
        // Tạo ra một font mới từ font cũ nhưng đã bị kéo dãn
        g2d.setFont(bodyFont.deriveFont(stretch));
       
        FontMetrics fmBody = g2d.getFontMetrics();
        int textLineHeight = fmBody.getHeight();
      
        // Lùi vào từ lề trái khung tre khoảng 10% chiều rộng khung
        int textStartX = frameMargin + (int)(frameW * 0.1); 
        // Bắt đầu vẽ từ dưới tiêu đề "GameIntro"
        int textStartY = (int)(titleY + textLineHeight * 1 + 15 );
        // Vòng lặp vẽ từng dòng chữ
        for (int i = 0; i < introLines.length; i++) {
            int lineY = textStartY + i * (int)(textLineHeight * 0.9);
            // Vẽ dòng chữ tại tọa độ tính toán, mỗi dòng cách nhau 1.2 lần chiều cao chữ
            //g2d.drawString(introLines[i], textStartX, textStartY + i * (int)(textLineHeight * 0.9));
            // Vẽ bóng đen mờ cho từng dòng chữ (Tạo cảm giác có chiều sâu)
            g2d.setColor(new Color(0, 0, 0, 80)); // Đen mờ
            g2d.drawString(introLines[i], textStartX + 1, lineY + 1 ); // Lệch 1 pixel

            // Vẽ chữ chính lên trên
            g2d.setColor(TEXT_COLOR_CREAM);
            g2d.drawString(introLines[i], textStartX, lineY);
        }

        // 6. VẼ CÁ VÀ NHÃN "Andy"
        g2d.setFont(bodyFont); // Trả về font bình thường
        if (andyFish != null) {
            int fishW = (int)(frameW * 0.11); // Cá rộng khoảng 18% khung
            int fishH = (int)(fishW * 0.9);   // Tỷ lệ chiều cao
            
            int fishX = textStartX + (int)(frameW * 0.63); 
            // Nằm ngang hàng với đoạn văn bản thứ 2
            int fishY = textStartY + textLineHeight * 2 - 10; 

            // --- VẼ HÌNH TRÒN ĐEN SAU LƯNG CÁ ---
            int circleSize = (int)(fishW * 1.3); // Hình tròn to hơn con cá 20%
            int circleX = fishX + (fishW - circleSize) / 2; // Căn giữa cá
            int circleY = fishY + (fishH - circleSize) / 2;

            // Vẽ hình tròn đen đặc (Background)
            g2d.setColor(new Color(0, 0, 50)); // Màu xanh đen đậm (gần như đen)
            g2d.fillOval(circleX, circleY, circleSize, circleSize);

            // Vẽ viền đen đậm cho hình tròn
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(3)); // Độ dày viền = 3
            g2d.drawOval(circleX, circleY, circleSize, circleSize);

            // Vẽ chữ "Andy" nhỏ trên đầu cá
            g2d.setColor(Color.white);
            g2d.setFont(bodyFont.deriveFont(20f)); // Font nhỏ hơn xíu
            String fishLabel = "Andy";
            int labelW = g2d.getFontMetrics().stringWidth(fishLabel);
            g2d.drawString(fishLabel, circleX + (circleSize - labelW)/2, circleY + 5);
            
            // Vẽ con cá
            g2d.drawImage(andyFish, fishX, fishY, fishW, fishH, null);
        }
        // --- 7. VẼ NÚT PLAY (Logic quan trọng nhất) ---
        
        // Tính toán vị trí nút (Cố định hoặc theo tỷ lệ)
        btnW = (int)(width * 0.22); // Nút nhỏ lại một chút
        btnH = (int)(btnW * 0.38);
        btnX = (width - btnW) / 2;
        // <--- SỬA LẠI: Đặt nút cao hơn một chút để đè lên cạnh dưới khung tre
        btnY = frameY + frameH - (int)(btnH * 1.2);

       
        Image currentBtnImage = isHovered ? seashellButton2 : seashellButton1;

        // Vẽ cái vỏ sò (nền nút)
        if (currentBtnImage != null) {
            g2d.drawImage(currentBtnImage, btnX, btnY, btnW, btnH, null);
        } 
        // VẼ CHỮ "Play" ĐÈ LÊN VỎ SÒ
        g2d.setFont(buttonFont); 
        g2d.setColor(BTN_TEXT_COLOR); // Màu chữ xanh lá cây đậm (như game gốc)
        
        String btnText = "Play";
        FontMetrics fmBtn = g2d.getFontMetrics();
        
        // Tính tọa độ để chữ nằm CHÍNH GIỮA cái vỏ sò
        int textX = btnX + (btnW - fmBtn.stringWidth(btnText)) / 2;
        int textY = btnY + (btnH - fmBtn.getHeight()) / 2 + fmBtn.getAscent();
         g2d.drawString(btnText, textX, textY);

         //TITLE STATE

    }
    
}
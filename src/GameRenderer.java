import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GameRenderer {
    Graphics2D g;
    BufferStrategy bs;
    int cellSize, width, height;

    BufferedImage appleImg, gameImage;
    GameWindow window;
    
    public GameRenderer(BufferedImage gameImage, GameWindow window, GameManager gameManager) {
        this.gameImage = gameImage;
        this.window = window;
    	bs = window.getBufferStrategy();
    	
        g = (Graphics2D) gameImage.getGraphics();//(Graphics2D) bs.getDrawGraphics();
        
        this.width = gameManager.winWidth;
        this.height = gameManager.winHeight;
        this.cellSize = gameManager.cellSize;
        appleImg = getAppleImage();
    }

    public void renderSnakes(HashMap<String, Snake> snakes) {
    	for (String key : snakes.keySet()) {

    		Snake currSnake = snakes.get(key);
    		g.setColor(currSnake.getColor());
    		ArrayList<Integer> snakeXs = currSnake.getSnakeXs();
    		ArrayList<Integer> snakeYs = currSnake.getSnakeYs();
    		
    		for (int j = 0; j < snakeXs.size(); j++) {
    			int x = snakeXs.get(j);
    			int y = snakeYs.get(j);

    			g.drawRect(x, y, cellSize, cellSize);
    		}
    	}
    }

    public void clear() {
    	g.clearRect(0, 0, width, height);
    }

    public void renderFPS(int fps) {
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + fps, 5, 15);
    }

    public void renderScores(HashMap<String, Snake> snakes) {
    	ArrayList<Snake> snakesList = new ArrayList<Snake>();
    	for (String key : snakes.keySet())
        	snakesList.add(snakes.get(key));
    	
    	Collections.sort(snakesList);
    	
        int y = 10;
        for (Snake snake : snakesList) {
        	g.setColor(snake.getColor());
        	g.drawString(snake.getName() + ": " + (snake.getLength() - 1), 10, y += 15);
        }
    }

    public void renderCells() {
        g.setColor(new Color(100, 100, 100));
        for (int y = 0; y < width; y += cellSize) {
            g.drawLine(0, y, height, y);
        }
        for (int x = 0; x < width; x += cellSize) {
            g.drawLine(x, 0, x, height);
        }
    }

    public void renderApple(Apple apple) {
        g.setColor(Color.RED);
        int appleX = apple.getX();
        int appleY = apple.getY();
        g.drawImage(appleImg, appleX, appleY, null);
    }
    
    public void show() {
    	//Resize gameImage
    	if (window.isFullscreen) {
    		bs.getDrawGraphics().drawImage(gameImage, window.getWidth()/4, 0, window.getWidth()/2, window.getHeight(), null);
    	}
    	else {
    		bs.getDrawGraphics().drawImage(gameImage, 0, 0, window.getWidth(), window.getHeight(), null);
    	}
    	
    	bs.show();
    }

    /*A note to anyone who reads this method
    This method creates an apple image without reading any files
    I would never do this in a serious project, I just thought it would
    be cool if I could actually do it this way, It draws an image pixel by pixel
    */
    public BufferedImage getAppleImage() {
        BufferedImage bi = new BufferedImage(30, 30, BufferedImage.TYPE_4BYTE_ABGR);
        bi.setRGB(0, 0, 0);
        bi.setRGB(0, 1, 0);
        bi.setRGB(0, 2, 0);
        bi.setRGB(0, 3, 0);
        bi.setRGB(0, 4, 0);
        bi.setRGB(0, 5, 0);
        bi.setRGB(0, 6, 0);
        bi.setRGB(0, 7, 0);
        bi.setRGB(0, 8, 0);
        bi.setRGB(0, 9, 0);
        bi.setRGB(0, 10, 0);
        bi.setRGB(0, 11, 0);
        bi.setRGB(0, 12, 0);
        bi.setRGB(0, 13, 0);
        bi.setRGB(0, 14, 0);
        bi.setRGB(0, 15, 0);
        bi.setRGB(0, 16, 0);
        bi.setRGB(0, 17, 0);
        bi.setRGB(0, 18, 0);
        bi.setRGB(0, 19, 0);
        bi.setRGB(1, 0, 0);
        bi.setRGB(1, 1, 0);
        bi.setRGB(1, 2, 0);
        bi.setRGB(1, 3, 0);
        bi.setRGB(1, 4, 0);
        bi.setRGB(1, 5, 0);
        bi.setRGB(1, 6, 0);
        bi.setRGB(1, 7, 282005520);
        bi.setRGB(1, 8, 1472861975);
        bi.setRGB(1, 9, 1925520150);
        bi.setRGB(1, 10, 1958681878);
        bi.setRGB(1, 11, 1673076501);
        bi.setRGB(1, 12, 750002961);
        bi.setRGB(1, 13, 97268480);
        bi.setRGB(1, 14, 0);
        bi.setRGB(1, 15, 0);
        bi.setRGB(1, 16, 0);
        bi.setRGB(1, 17, 0);
        bi.setRGB(1, 18, 0);
        bi.setRGB(1, 19, 0);
        bi.setRGB(2, 0, 0);
        bi.setRGB(2, 1, 0);
        bi.setRGB(2, 2, 0);
        bi.setRGB(2, 3, 0);
        bi.setRGB(2, 4, 0);
        bi.setRGB(2, 5, 50266112);
        bi.setRGB(2, 6, 1875973399);
        bi.setRGB(2, 7, -405924329);
        bi.setRGB(2, 8, -3663850);
        bi.setRGB(2, 9, -3925482);
        bi.setRGB(2, 10, -4317675);
        bi.setRGB(2, 11, -4644843);
        bi.setRGB(2, 12, -5037292);
        bi.setRGB(2, 13, -475060972);
        bi.setRGB(2, 14, 2108237586);
        bi.setRGB(2, 15, 799222550);
        bi.setRGB(2, 16, 0);
        bi.setRGB(2, 17, 0);
        bi.setRGB(2, 18, 0);
        bi.setRGB(2, 19, 0);
        bi.setRGB(3, 0, 0);
        bi.setRGB(3, 1, 219459860);
        bi.setRGB(3, 2, 16842496);
        bi.setRGB(3, 3, 0);
        bi.setRGB(3, 4, 0);
        bi.setRGB(3, 5, 819269653);
        bi.setRGB(3, 6, -271444969);
        bi.setRGB(3, 7, -3336681);
        bi.setRGB(3, 8, -3729130);
        bi.setRGB(3, 9, -3990762);
        bi.setRGB(3, 10, -4383211);
        bi.setRGB(3, 11, -4710379);
        bi.setRGB(3, 12, -5102828);
        bi.setRGB(3, 13, -5364460);
        bi.setRGB(3, 14, -72865773);
        bi.setRGB(3, 15, -677238253);
        bi.setRGB(3, 16, 1402809874);
        bi.setRGB(3, 17, 93926144);
        bi.setRGB(3, 18, 0);
        bi.setRGB(3, 19, 0);
        bi.setRGB(4, 0, 337290778);
        bi.setRGB(4, 1, 1897964825);
        bi.setRGB(4, 2, 1394581529);
        bi.setRGB(4, 3, 103524907);
        bi.setRGB(4, 4, 0);
        bi.setRGB(4, 5, -1898573289);
        bi.setRGB(4, 6, -3075049);
        bi.setRGB(4, 7, -3402218);
        bi.setRGB(4, 8, -3794666);
        bi.setRGB(4, 9, -4121579);
        bi.setRGB(4, 10, -4514027);
        bi.setRGB(4, 11, -4775660);
        bi.setRGB(4, 12, -5168108);
        bi.setRGB(4, 13, -5495277);
        bi.setRGB(4, 14, -5887725);
        bi.setRGB(4, 15, -6149357);
        bi.setRGB(4, 16, -140759534);
        bi.setRGB(4, 17, -1818676975);
        bi.setRGB(4, 18, 109062912);
        bi.setRGB(4, 19, 0);
        bi.setRGB(5, 0, 673226522);
        bi.setRGB(5, 1, -1742364899);
        bi.setRGB(5, 2, -1725589220);
        bi.setRGB(5, 3, 673618208);
        bi.setRGB(5, 4, 148840480);
        bi.setRGB(5, 5, -1328278761);
        bi.setRGB(5, 6, -3140329);
        bi.setRGB(5, 7, -3467498);
        bi.setRGB(5, 8, -3859946);
        bi.setRGB(5, 9, -4121579);
        bi.setRGB(5, 10, -4514027);
        bi.setRGB(5, 11, -4841196);
        bi.setRGB(5, 12, -5233644);
        bi.setRGB(5, 13, -5495277);
        bi.setRGB(5, 14, -5953261);
        bi.setRGB(5, 15, -6214894);
        bi.setRGB(5, 16, -6607342);
        bi.setRGB(5, 17, -107597807);
        bi.setRGB(5, 18, 1569797136);
        bi.setRGB(5, 19, 33488896);
        bi.setRGB(6, 0, 824874783);
        bi.setRGB(6, 1, -1272212191);
        bi.setRGB(6, 2, -852651486);
        bi.setRGB(6, 3, 1915718948);
        bi.setRGB(6, 4, 50266112);
        bi.setRGB(6, 5, -1697508841);
        bi.setRGB(6, 6, -3271401);
        bi.setRGB(6, 7, -3533034);
        bi.setRGB(6, 8, -3925482);
        bi.setRGB(6, 9, -4252651);
        bi.setRGB(6, 10, -4645099);
        bi.setRGB(6, 11, -4906732);
        bi.setRGB(6, 12, -5298924);
        bi.setRGB(6, 13, -5626093);
        bi.setRGB(6, 14, -6018541);
        bi.setRGB(6, 15, -6280174);
        bi.setRGB(6, 16, -6672622);
        bi.setRGB(6, 17, -6999791);
        bi.setRGB(6, 18, -762236143);
        bi.setRGB(6, 19, 680276755);
        bi.setRGB(7, 0, 657362983);
        bi.setRGB(7, 1, -1188066011);
        bi.setRGB(7, 2, -416118234);
        bi.setRGB(7, 3, -1154185688);
        bi.setRGB(7, 4, 0);
        bi.setRGB(7, 5, 1758467094);
        bi.setRGB(7, 6, -3271145);
        bi.setRGB(7, 7, -3598314);
        bi.setRGB(7, 8, -3990762);
        bi.setRGB(7, 9, -4252395);
        bi.setRGB(7, 10, -4710379);
        bi.setRGB(7, 11, -4972012);
        bi.setRGB(7, 12, -5364460);
        bi.setRGB(7, 13, -5691629);
        bi.setRGB(7, 14, -6084077);
        bi.setRGB(7, 15, -6345710);
        bi.setRGB(7, 16, -6738158);
        bi.setRGB(7, 17, -7065327);
        bi.setRGB(7, 18, -175229935);
        bi.setRGB(7, 19, 1267414545);
        bi.setRGB(8, 0, 87267635);
        bi.setRGB(8, 1, 2100659497);
        bi.setRGB(8, 2, -46628053);
        bi.setRGB(8, 3, -164068565);
        bi.setRGB(8, 4, 87267635);
        bi.setRGB(8, 5, 1053955353);
        bi.setRGB(8, 6, -3402217);
        bi.setRGB(8, 7, -3663850);
        bi.setRGB(8, 8, -4056042);
        bi.setRGB(8, 9, -4383211);
        bi.setRGB(8, 10, -4775659);
        bi.setRGB(8, 11, -5037292);
        bi.setRGB(8, 12, -5429740);
        bi.setRGB(8, 13, -5756909);
        bi.setRGB(8, 14, -6149357);
        bi.setRGB(8, 15, -6410990);
        bi.setRGB(8, 16, -6803438);
        bi.setRGB(8, 17, -7130607);
        bi.setRGB(8, 18, -41077487);
        bi.setRGB(8, 19, 1468871954);
        bi.setRGB(9, 0, 0);
        bi.setRGB(9, 1, 338919462);
        bi.setRGB(9, 2, -1304919509);
        bi.setRGB(9, 3, -96959701);
        bi.setRGB(9, 4, 440759837);
        bi.setRGB(9, 5, 1571760400);
        bi.setRGB(9, 6, -3467497);
        bi.setRGB(9, 7, -3729130);
        bi.setRGB(9, 8, -4121578);
        bi.setRGB(9, 9, -4448747);
        bi.setRGB(9, 10, -4841195);
        bi.setRGB(9, 11, -5102828);
        bi.setRGB(9, 12, -5495276);
        bi.setRGB(9, 13, -5822445);
        bi.setRGB(9, 14, -6214893);
        bi.setRGB(9, 15, -6476526);
        bi.setRGB(9, 16, -6868974);
        bi.setRGB(9, 17, -7196143);
        bi.setRGB(9, 18, -343067375);
        bi.setRGB(9, 19, 1099773712);
        bi.setRGB(10, 0, 208284416);
        bi.setRGB(10, 1, 946086912);
        bi.setRGB(10, 2, 929443333);
        bi.setRGB(10, 3, 2018989845);
        bi.setRGB(10, 4, -2089994236);
        bi.setRGB(10, 5, -1734921971);
        bi.setRGB(10, 6, -3533034);
        bi.setRGB(10, 7, -3794666);
        bi.setRGB(10, 8, -4187115);
        bi.setRGB(10, 9, -4514027);
        bi.setRGB(10, 10, -4906476);
        bi.setRGB(10, 11, -5168108);
        bi.setRGB(10, 12, -5560557);
        bi.setRGB(10, 13, -5887725);
        bi.setRGB(10, 14, -6280173);
        bi.setRGB(10, 15, -6607342);
        bi.setRGB(10, 16, -6999790);
        bi.setRGB(10, 17, -7261423);
        bi.setRGB(10, 18, -477219311);
        bi.setRGB(10, 19, 965424658);
        bi.setRGB(11, 0, 476324864);
        bi.setRGB(11, 1, -1167842816);
        bi.setRGB(11, 2, 1768302336);
        bi.setRGB(11, 3, 795291909);
        bi.setRGB(11, 4, 242038528);
        bi.setRGB(11, 5, 1305286423);
        bi.setRGB(11, 6, -3598314);
        bi.setRGB(11, 7, -3859946);
        bi.setRGB(11, 8, -4252395);
        bi.setRGB(11, 9, -4579563);
        bi.setRGB(11, 10, -4972012);
        bi.setRGB(11, 11, -5233644);
        bi.setRGB(11, 12, -5626093);
        bi.setRGB(11, 13, -5953261);
        bi.setRGB(11, 14, -6345710);
        bi.setRGB(11, 15, -6607342);
        bi.setRGB(11, 16, -6999791);
        bi.setRGB(11, 17, -7326959);
        bi.setRGB(11, 18, -74697199);
        bi.setRGB(11, 19, 1619866896);
        bi.setRGB(12, 0, 0);
        bi.setRGB(12, 1, 711009280);
        bi.setRGB(12, 2, 55902208);
        bi.setRGB(12, 3, 0);
        bi.setRGB(12, 4, 0);
        bi.setRGB(12, 5, 2043417879);
        bi.setRGB(12, 6, -3663850);
        bi.setRGB(12, 7, -3925482);
        bi.setRGB(12, 8, -4317675);
        bi.setRGB(12, 9, -4644843);
        bi.setRGB(12, 10, -5037292);
        bi.setRGB(12, 11, -5364460);
        bi.setRGB(12, 12, -5756909);
        bi.setRGB(12, 13, -6018541);
        bi.setRGB(12, 14, -6410990);
        bi.setRGB(12, 15, -6738158);
        bi.setRGB(12, 16, -7130607);
        bi.setRGB(12, 17, -7392239);
        bi.setRGB(12, 18, -24365551);
        bi.setRGB(12, 19, 1821062673);
        bi.setRGB(13, 0, 0);
        bi.setRGB(13, 1, 0);
        bi.setRGB(13, 2, 0);
        bi.setRGB(13, 3, 0);
        bi.setRGB(13, 4, 114633515);
        bi.setRGB(13, 5, -1463085289);
        bi.setRGB(13, 6, -3729130);
        bi.setRGB(13, 7, -3990762);
        bi.setRGB(13, 8, -4383211);
        bi.setRGB(13, 9, -4710379);
        bi.setRGB(13, 10, -5102828);
        bi.setRGB(13, 11, -5364460);
        bi.setRGB(13, 12, -5822445);
        bi.setRGB(13, 13, -6084077);
        bi.setRGB(13, 14, -6476526);
        bi.setRGB(13, 15, -6738158);
        bi.setRGB(13, 16, -7196143);
        bi.setRGB(13, 17, -7457775);
        bi.setRGB(13, 18, -259246575);
        bi.setRGB(13, 19, 1250768657);
        bi.setRGB(14, 0, 0);
        bi.setRGB(14, 1, 0);
        bi.setRGB(14, 2, 0);
        bi.setRGB(14, 3, 0);
        bi.setRGB(14, 4, 215291157);
        bi.setRGB(14, 5, -1228204522);
        bi.setRGB(14, 6, -3794410);
        bi.setRGB(14, 7, -4121579);
        bi.setRGB(14, 8, -4514027);
        bi.setRGB(14, 9, -4775660);
        bi.setRGB(14, 10, -5168108);
        bi.setRGB(14, 11, -5495277);
        bi.setRGB(14, 12, -5887725);
        bi.setRGB(14, 13, -6149357);
        bi.setRGB(14, 14, -6541806);
        bi.setRGB(14, 15, -6868975);
        bi.setRGB(14, 16, -7261423);
        bi.setRGB(14, 17, -7523055);
        bi.setRGB(14, 18, -1433651695);
        bi.setRGB(14, 19, 327562765);
        bi.setRGB(15, 0, 0);
        bi.setRGB(15, 1, 0);
        bi.setRGB(15, 2, 0);
        bi.setRGB(15, 3, 0);
        bi.setRGB(15, 4, 61472768);
        bi.setRGB(15, 5, -1765205994);
        bi.setRGB(15, 6, -3859946);
        bi.setRGB(15, 7, -4121579);
        bi.setRGB(15, 8, -4514027);
        bi.setRGB(15, 9, -4841196);
        bi.setRGB(15, 10, -5233644);
        bi.setRGB(15, 11, -5560813);
        bi.setRGB(15, 12, -5953261);
        bi.setRGB(15, 13, -6214894);
        bi.setRGB(15, 14, -6607342);
        bi.setRGB(15, 15, -6934511);
        bi.setRGB(15, 16, -7326959);
        bi.setRGB(15, 17, -326355695);
        bi.setRGB(15, 18, 814363920);
        bi.setRGB(15, 19, 0);
        bi.setRGB(16, 0, 0);
        bi.setRGB(16, 1, 0);
        bi.setRGB(16, 2, 0);
        bi.setRGB(16, 3, 0);
        bi.setRGB(16, 4, 0);
        bi.setRGB(16, 5, 818551573);
        bi.setRGB(16, 6, -272360938);
        bi.setRGB(16, 7, -4252651);
        bi.setRGB(16, 8, -4645099);
        bi.setRGB(16, 9, -4906476);
        bi.setRGB(16, 10, -5298924);
        bi.setRGB(16, 11, -5626093);
        bi.setRGB(16, 12, -6018541);
        bi.setRGB(16, 13, -6280174);
        bi.setRGB(16, 14, -6672622);
        bi.setRGB(16, 15, -107728623);
        bi.setRGB(16, 16, -1349504239);
        bi.setRGB(16, 17, 814691600);
        bi.setRGB(16, 18, 0);
        bi.setRGB(16, 19, 0);
        bi.setRGB(17, 0, 0);
        bi.setRGB(17, 1, 0);
        bi.setRGB(17, 2, 0);
        bi.setRGB(17, 3, 0);
        bi.setRGB(17, 4, 0);
        bi.setRGB(17, 5, 33488896);
        bi.setRGB(17, 6, 1740708630);
        bi.setRGB(17, 7, -490857195);
        bi.setRGB(17, 8, -4710379);
        bi.setRGB(17, 9, -4972012);
        bi.setRGB(17, 10, -5364460);
        bi.setRGB(17, 11, -5691629);
        bi.setRGB(17, 12, -6084077);
        bi.setRGB(17, 13, -6345710);
        bi.setRGB(17, 14, -845598959);
        bi.setRGB(17, 15, 1872048144);
        bi.setRGB(17, 16, 412431637);
        bi.setRGB(17, 17, 0);
        bi.setRGB(17, 18, 0);
        bi.setRGB(17, 19, 0);
        bi.setRGB(18, 0, 0);
        bi.setRGB(18, 1, 0);
        bi.setRGB(18, 2, 0);
        bi.setRGB(18, 3, 0);
        bi.setRGB(18, 4, 0);
        bi.setRGB(18, 5, 0);
        bi.setRGB(18, 6, 0);
        bi.setRGB(18, 7, 280961040);
        bi.setRGB(18, 8, 1605836821);
        bi.setRGB(18, 9, -2018302955);
        bi.setRGB(18, 10, -1850988781);
        bi.setRGB(18, 11, -1918359532);
        bi.setRGB(18, 12, 1705257748);
        bi.setRGB(18, 13, 882715668);
        bi.setRGB(18, 14, 0);
        bi.setRGB(18, 15, 0);
        bi.setRGB(18, 16, 0);
        bi.setRGB(18, 17, 0);
        bi.setRGB(18, 18, 0);
        bi.setRGB(18, 19, 0);
        bi.setRGB(19, 0, 0);
        bi.setRGB(19, 1, 0);
        bi.setRGB(19, 2, 0);
        bi.setRGB(19, 3, 0);
        bi.setRGB(19, 4, 0);
        bi.setRGB(19, 5, 0);
        bi.setRGB(19, 6, 0);
        bi.setRGB(19, 7, 0);
        bi.setRGB(19, 8, 0);
        bi.setRGB(19, 9, 0);
        bi.setRGB(19, 10, 0);
        bi.setRGB(19, 11, 0);
        bi.setRGB(19, 12, 0);
        bi.setRGB(19, 13, 0);
        bi.setRGB(19, 14, 0);
        bi.setRGB(19, 15, 0);
        bi.setRGB(19, 16, 0);
        bi.setRGB(19, 17, 0);
        bi.setRGB(19, 18, 0);
        bi.setRGB(19, 19, 0);
        return bi;
    }
}

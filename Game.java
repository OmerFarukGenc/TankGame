import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;

public class Game extends JFrame implements KeyListener{

	private int step;
	private int[][] mat = null;
	private int row, col;
	private int refreshRate;

	
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>(0);	
	private ArrayList<Tank> tanks = new ArrayList<Tank>(0);

	private Tank p1 = null;
	private Tank p2 = null;
	private java.util.Timer timer =null;
	

	public Game(int step, int refreshRate){
	
		this.refreshRate = refreshRate;
		File f = null;
		Scanner k = null;
		
		try{
			f = new File("Stage.txt");
			k = new Scanner(f);
		}catch(FileNotFoundException e){
			System.out.println("File not found");
			System.exit(0);
		}
	
		this.row = k.nextInt();
		this.col = k.nextInt();
	
		mat = new int[row][col];
		
				
		for(int i = 0;i < row;i++)
			for(int j = 0;j < col;j++)
				mat[i][j] = k.nextInt();
			/*	if(i == 0 || j == 0 || i == row - 1 || j == col - 1)
					mat[i][j] = 1;
				else 
					mat[i][j] = 0;*/
		
		p1 = new Tank(k.nextInt(), k.nextInt(), 'u');;
		
		
		int tempRow,tempCol;
		
		int tankCount = k.nextInt();
		
		for(int i = 0;i < tankCount;i++){
			tempRow = k.nextInt();
			tempCol = k.nextInt();
			tanks.add(new Tank(tempRow, tempCol, 'u'));
		}
	
		this.step = step;
		this.setSize(col * step, row * step);
		this.setResizable(false);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addKeyListener(this);
		
		
		java.util.Timer t = new java.util.Timer();
		t.schedule(new Render(), 0, 1000 / this.refreshRate);
		t.schedule(new Engine(), 0, 1000 / this.refreshRate);
		
	}
	
	public ArrayList<Tank> getTanks(){
		return tanks;
	
	}
/////// PAINT
	public void paintBG(Graphics g){

		for(int i = 0;i < row; i++)
			for(int j = 0;j < col; j++)
				if(mat[i][j] == 1){
					g.setColor(Color.BLACK);
					g.fillRect(j * step, i * step, step, step);
				}else if(mat[i][j] == 2){
					g.setColor(Color.RED);
					g.fillRect(j * step, i * step, step, step);
				}else{
					g.setColor(Color.WHITE);
					g.fillRect(j * step, i * step, step, step);
				}
	
	}
	
	public void paintBullets(Graphics g){
		g.setColor(Color.ORANGE);
		
		for(Bullet b:bullets)
			if(b.isAlive())
				g.fillRect(b.getCol() * step + step / 3, b.getRow() * step + step / 3, step / 3, step / 3);
	
	}
	
	public void paintTanks(Graphics g){
		
		paintTank(g,p1);
		
		for(Tank t:tanks)	
			if(t.isAlive())
				paintTank(g,t);

	}
	
	public void paintTank(Graphics g, Tank t){
		char dir;
		int tankCol, tankRow;
		
		if(t == p1)
			g.setColor(Color.GREEN);
		else if(t == p2)
			g.setColor(Color.YELLOW);
		else
			g.setColor(Color.GRAY);
		dir = t.getDir();
		tankCol = t.getCol();
		tankRow = t.getRow();
			
		g.fillRect((tankCol - 1) * step, (tankRow - 1) * step, step * 3, step * 3);
		
		g.setColor(Color.WHITE);
	
		if(dir == 'u'){
			g.fillRect((tankCol - 1) * step, (tankRow - 1) * step, step, step);
			g.fillRect((tankCol + 1) * step, (tankRow - 1) * step, step, step);
		}else if(dir == 'd'){
			g.fillRect((tankCol - 1) * step, (tankRow + 1) * step, step, step);
			g.fillRect((tankCol + 1) * step, (tankRow + 1) * step, step, step);
		}else if(dir == 'r'){
			g.fillRect((tankCol + 1) * step, (tankRow - 1) * step, step, step);
			g.fillRect((tankCol + 1) * step, (tankRow + 1) * step, step, step);
		}else if(dir == 'l'){
			g.fillRect((tankCol - 1) * step, (tankRow + 1) * step, step, step);
			g.fillRect((tankCol - 1) * step, (tankRow - 1) * step, step, step);
		}
	
	
	}

	private class Render extends TimerTask{
		public void run(){
			repaint();
			return;
		}
	}
	
	public void paint(Graphics g){
		Graphics offgc;
		Image offscreen = null;
		offscreen = createImage(col * step, row * step);
		offgc = offscreen.getGraphics();
		
		
		paintBG(offgc);
		paintBullets(offgc);
		paintTanks(offgc);		
		
		g.drawImage(offscreen, 0, 0, this);
			
	}
	
////////ENGINE

	private class Engine extends TimerTask{
		public void run(){
		//	aiengine.simulate(this ,aispeed);
			simulate();
			return;
		}
	}	
	
	

	
	public void bulletMovement(){
	
		for(Bullet b:bullets)
			if(b.isAlive())
				b.move();
	}
	
	public void bulletCollision(){
		tanks.add(p1);
		int[] p = {-1, 0, 1};
		int tankRow, tankCol;
		int bulletRow, bulletCol;
		char bulletDir;
		int c = 0,r = 0;
		boolean red = false;
		
		
		for(Bullet b:bullets){
			red = false;
			bulletRow = b.getRow();
			bulletCol = b.getCol();
			bulletDir = b.getDir();
			
			if(bulletDir == 'u' || bulletDir == 'd'){
				r = 0;
				c = 1;
			}else if(bulletDir == 'l' || bulletDir == 'r'){
				r = 1;
				c = 0;
			}
			
			if(mat[bulletRow][bulletCol] == 1){
				b.kill();
				b.getOwner().setFire(true);
				continue;
			}
			
			
			for(int i:p)
				for(int j:p)
					if(mat[bulletRow + i * r][bulletCol + j * c] == 2 && (mat[bulletRow + i * r][bulletCol + j * c] = 0) == 0 && (red = true));
					
			if(red){
				b.kill();
				b.getOwner().setFire(true);
				continue;
			}
			for(Tank t:tanks){
				if(b.getOwner() != t){
				
					tankRow = t.getRow();
					tankCol = t.getCol();
				
					for(int i:p)
						for(int j:p)
							if(bulletRow + i == tankRow && bulletCol + j == tankCol){
								b.kill();
								b.getOwner().setFire(true);
								t.kill();
							}
						
				}
			}
			
		}
		tanks.remove(p1);
	
	}
	
	public void clearDead(){
		boolean key = true;
		Tank tempTank = null;
		Bullet tempBullet = null;
		
		while(key){
			key = false;
			
			for(Tank t: tanks)
				if(!t.isAlive()){
					key = true;
					tempTank = t;
				}
				
			tanks.remove(tempTank);
		}
		
		key = true;
	
		while(key){
			key = false;
			
			for(Bullet b: bullets)
				if(!b.isAlive()){
					key = true;
					tempBullet = b;
				}
				
			bullets.remove(tempBullet);
		}
	
	}
	
	public void simulate(){
		clearDead();
		bulletMovement();
		bulletCollision();
	}	
	
	
	public boolean tankMovementCheck(Tank t, char dir){
		int r = 0, c = 0;
		int tankRow = t.getRow(), tankCol = t.getCol();
		boolean result = false;	
		int[] p = {-1,0,1};
	
		for(Tank tank: tanks)
			if(tank != t)
				for(int i:p)
					for(int j:p)
						mat[tank.getRow() + i][tank.getCol() + j] = 9;

		
		if(dir == 'u' && mat[tankRow - 2][tankCol] == 0 && mat[tankRow - 2][tankCol - 1] == 0 && mat[tankRow - 2][tankCol + 1] == 0){
			System.out.println(c + " " + r);
			result = true;}
		else if(dir == 'd' && mat[tankRow + 2][tankCol] == 0 && mat[tankRow + 2][tankCol - 1] == 0 && mat[tankRow + 2][tankCol + 1] == 0){
			System.out.println(c + " " + r);
			result = true;}
		else if(dir == 'r' && mat[tankRow][tankCol + 2] == 0 && mat[tankRow - 1][tankCol + 2] == 0 && mat[tankRow + 1][tankCol + 2] == 0){
			System.out.println(c + " " + r);
			result = true;}
		else if(dir == 'l' && mat[tankRow][tankCol - 2] == 0 && mat[tankRow - 1][tankCol - 2] == 0 && mat[tankRow + 1][tankCol - 2] == 0){
			System.out.println(c + " " + r);
			result = true;}
		
		
		for(Tank tank: tanks)
			if(tank != t)
				for(int i:p)
					for(int j:p)
						mat[tank.getRow() + i][tank.getCol() + j] = 0;
			
		return result;
	}

	public void fire(Tank t){
		if(!t.canFire())
			return;
			
			
		char dir = t.getDir();
		
		
		
		if(dir == 'u')
			bullets.add(new Bullet(t, t.getRow() - 1, t.getCol(), 'u'));
		else if(dir == 'd')
			bullets.add(new Bullet(t, t.getRow() + 1, t.getCol(), 'd'));
		else if(dir == 'l')
			bullets.add(new Bullet(t, t.getRow(), t.getCol() - 1, 'l'));
		else if(dir == 'r')
			bullets.add(new Bullet(t, t.getRow(), t.getCol() + 1, 'r'));

		t.setFire(false);		
	
	}

	public void keyPressed(KeyEvent e){
	
		
	}
	
	public void keyReleased(KeyEvent e){
		char c = e.getKeyChar();
		if(c == '-')
			return;
			
		char dir = 'a';
		Tank t = p1;
		
		if(c == 'w')
			dir = 'u';
		else if(c == 's')
			dir = 'd';
		else if(c == 'a')
			dir = 'l';
		else if(c == 'd')
			dir = 'r';
		else if(c == 'x'){
			fire(p1);
			return;	
		}
		else 
			return;
			
		t.setDir(dir);	
		
		if(tankMovementCheck(t, dir))
			t.move(dir);		
		
	}

	public void keyTyped(KeyEvent e){
		
	}

}

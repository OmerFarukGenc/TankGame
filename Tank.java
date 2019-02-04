import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Tank{

	private boolean alive = true;
	private boolean fire = true;
	private int step;
	private int row, col;
	private char dir = 'u';
	
	public boolean canFire(){
		return fire;
	}
	
	public void setFire(boolean f){
		this.fire = f;
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public void kill(){
		alive = false;
	}
	
	public void setDir(char dir){
		this.dir = dir;
	}
	
	public void move(char dir){
		this.setDir(dir);
		if(dir == 'u')
			row--;
		else if(dir == 'd')
			row++;
		else if(dir == 'r')
			col++;
		else if(dir == 'l')
			col--;
		
		return;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getCol(){
		return col;
	}
	
	public char getDir(){
		return dir;
	}
	
	public Tank(int row, int col, char dir){
		this.row = row;
		this.col = col;
		this.dir = dir;
	}



	
}

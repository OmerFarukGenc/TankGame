import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Bullet {

	Tank owner = null;

	private int row, col;

	private boolean alive = true;
	
	public boolean isAlive(){
		return this.alive;
	}
	
	public void kill(){
		this.alive = false;
	}	
	
	public Tank getOwner(){
		return this.owner;
	}

	private char dir = 'r';

	public char getDir(){
		return dir;
	}	

	public Bullet(Tank owner, int row, int col, char dir){
		this.owner = owner;
		this.row = row;
		this.col = col;
		this.dir = dir;
	}

	public int getRow(){
		return row;
	}
	
	public int getCol(){
		return col;
	}
	
	

	public void move(){
		if(dir == 'r')
			col++;
		else if(dir == 'l')
			col--;
		else if(dir == 'u')
			row--;
		else if(dir == 'd')
			row++;
		

					
	}

}

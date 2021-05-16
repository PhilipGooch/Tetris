package tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Formatter;
import java.util.Random;
import java.util.Scanner;

public class World extends JPanel implements ActionListener{
	
	int width = 10;
	int height = 27;
	Timer t;
	Random rand;
	Block bl;
	int count = 0;
	int fallspeed;
	int dropspeed;
	int speed;
	int ij[][] = new int[width][height];
	int sij[][] = new int[width][height];
	int rotation = 0;
	int shape;
	int nextshape;
	boolean drop = false;
	int score = 0;
	int scorecount = 0;
	int level = 1;
	Font font;
	int highscore = 0;
	boolean gameover = false;
	Scanner s;
	FileOutputStream to;
	int dropIndex;
	int dropNumber = 0;
	
	public static void main (String args []){
		JFrame j = new JFrame("Tetris");
		World w = new World();
		j.add(w);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setSize(396,598);
		j.setVisible(true);
		j.setResizable(false);
		j.setLocationRelativeTo(null);
	}
	
	public World(){
		font = new Font("Sherif",Font.BOLD, 30);
		rand = new Random();
		nextshape = rand.nextInt(7)+1;
		t = new Timer(10, this);
		addKeyListener(new KL());
		setFocusable(true);
		newBlock();
		fallspeed = 30;
		dropspeed = 4;
		speed = fallspeed;
		String a;
		for(int i = 0; i <width; i++){
			sij[i][0] = 1;
		}
		try{
			s = new Scanner(new File("C:\\test\\highscore.txt"));
			a = s.next();
			highscore = Integer.parseInt(a);
		}
		catch(Exception e){
			System.out.println("no highscore file");
		}
		t.start();
	}

	public void newBlock(){
		for(int i = 0; i <width; i++){
			for(int j = 0; j <height; j++){
				if(sij[i][j]==0&&ij[i][j]>0){
					sij[i][j] = ij[i][j];
				}
			}
		}
		shape = nextshape;
		nextshape = rand.nextInt(7)+1;
		bl = new Block(shape);
		speed = fallspeed;
		rotation = 0;
	}
	
	public void actionPerformed(ActionEvent event) {
		for(int i = width/2; i < (width/2)+2; i++){
			if(sij[i][height-3]>0){
				gameover = true;
			}
		}
		if(!gameover){
			ij = bl.getIJ();
			count++;
			boolean [] fall = new boolean [width]; 
			boolean allfall = true;
			if(count >= speed){					
			int index [] = new int [width];
			for(int i = 0; i < width; i++){
				index[i] = 0;
			}
			for(int i = 0; i < width; i++){
				for(int j = 0; j < height-1; j++){
					if(ij[i][j] > 0 ){
						index[i] = j;
						break;
					}
					else{
						index[i] = height-1;
					}
				}
			}
			for(int i = 0; i < width; i++){
				fall[i] = true;
			}
			for(int i = 0; i < width; i++){
				if(sij[i][index[i]-1] > 0){
					fall[i] = false;
				}
			}
			for(boolean f: fall){
				if(f==false){
					allfall = false;
				}
			}
			clearLine();
			if(drop){
				drop();
			}
			if(allfall){
					bl.fall();
					count = 0;
				}else{
					newBlock();
				}
			}	
		}else{
			t.stop();
			if(score > highscore){
				highscore = score;
				try{
					to = new FileOutputStream("C:\\test\\highscore.txt");
					to.write(new String(""+highscore).getBytes());
					to.flush();
					to.close();
				}catch(Exception e){
					System.out.println("error");
				}				
			}
		}
		repaint();
	}
	
	private class KL extends KeyAdapter{
		public void keyPressed(KeyEvent event){
			int key = event.getKeyCode();
			if(key == KeyEvent.VK_S){
				speed = dropspeed;
			}else{
				speed = fallspeed;
			}
			boolean moveleft = true;
			if(key == KeyEvent.VK_A){
				boolean stop = false;
				for(int i = 0; i < width-1; i++){
					for(int j = 0; j < height; j++){
						if(ij[i+1][j]>0&&sij[i][j]>0){
							moveleft = false;
							stop = true;
							break;
						}
					}
					if(stop){
						break;
					}
				}
				if(moveleft){	
					bl.moveLeft(event);	
				}
			}	
			boolean moveright = true;
			if(key == KeyEvent.VK_D){
				boolean stop = false;
				for(int i = width-1; i >= 0; i--){
					for(int j = height-1; j >= 0 ; j--){
						if(i<width-1&&(ij[i][j]>0&&sij[i+1][j]>0)){	
							moveright = false;
							stop = true;
							break;
						}
					}
					if(stop){
						break;
					}
				}
				if(moveright){
					bl.moveRight(event);
				}
			}
			if(key == KeyEvent.VK_SPACE){
				rotate();
			}
		}
	}
	
	public void clearLine(){				//BUG!!! if you clear line 1, miss line 2 and clear line 3; line 4 doesn't fall
		int sum [] = new int [height];
		for(int j = 1; j < height; j++){
			sum[j] = 0;
		}
		for(int j = 1; j < height; j++){
			for(int i = 0; i < width; i++){
				if(sij[i][j]==0) continue;
				if(sij[i][j]>0){
					sum [j]++;
				}
				if(sum[j]==width){
					for(int ii = 0; ii < width; ii++){
						sij[ii][j] = 0;
					}
					drop = true;
					dropNumber++;
					if(scorecount>5){
						scorecount = 0;
						levelUP();
					}
					if(dropNumber==1){
						dropIndex= j;
					}
				}
			}
		}
		score += dropNumber * dropNumber;
		scorecount = scorecount + (dropNumber*dropNumber);
	}
	
	public void levelUP(){
		level++;
		if(level<=7){
			fallspeed = fallspeed -4;
		}
		else if(level>7&&level<=11){
			fallspeed = fallspeed -3;
		}
		else if(level>11&&level<=14){
			fallspeed = fallspeed -2;
		}
		else if(level>14){
			fallspeed = fallspeed -1;
		}
	}
	
	public void drop(){
		drop = false;
		for(int i = 0; i < width; i++){
			for(int j = dropIndex; j < height; j++){		
				if(j < height-5){
					sij [i][j] = sij [i][j+(dropNumber)];
				}
			}
		}	
		dropNumber = 0;
	}
	
	public void clearIJ(){
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				ij[i][j] = 0;
			}
		}	
	}
	
	public void rotate(){							
		boolean halt = false;
		boolean rotate = true;
		for(int i = 0; i < width; i++){			
			for(int j = 0; j < height; j++){	
				if(ij[i][j]>0){
					clearIJ();						//# #
					if(shape==1){					//# #
						ij[i][j] = 1;
						ij[i][j+1] = 1;			
						ij[i+1][j] = 1;			
						ij[i+1][j+1] = 1;
					}
					if(shape==2){					//# # # #
						if(rotation==0||rotation==2){
							if((i<width-1&&i>=0&&j>2)&&sij[i+1][j+1]==0&&sij[i+1][j]==0&&sij[i+1][j-1]==0&&sij[i+1][j-2]==0){
								ij[i+1][j+1] = 2;	
								ij[i+1][j] = 2;			
								ij[i+1][j-1] = 2;	
								ij[i+1][j-2] = 2;	
								rotate = true;
							}else{
								ij[i][j] = 2;			
								ij[i+1][j] = 2;
								ij[i+2][j] = 2;
								ij[i+3][j] = 2;		//#		
								rotate = false;		//#
							}						//#
						}							//#
						if(rotation==1||rotation==3){
							if((i<width-2&&i>0&&j>0)&&sij[i-1][j+1]==0&&sij[i][j+1]==0&&sij[i+1][j+1]==0&&sij[i+2][j+1]==0){
								ij[i-1][j+1] = 2;	
								ij[i][j+1] = 2;	
								ij[i+1][j+1] = 2;
								ij[i+2][j+1] = 2;
								rotate = true;
							}else{
								ij[i][j] = 2;
								ij[i][j+1] = 2;
								ij[i][j+2] = 2;
								ij[i][j+3] = 2;
								rotate = false;		
							}						
						}
					}	
					if(shape==3){					//  #			
						if(rotation==0){			//# # #
							if((i<width-1&&i>=0&&j>1)&&sij[i+1][j+1]==0&&sij[i+1][j]==0&&sij[i+1][j-1]==0&&sij[i+2][j]==0){
								ij[i+1][j+1] = 3;
								ij[i+1][j] = 3;
								ij[i+1][j-1] = 3;
								ij[i+2][j] = 3;
								rotate = true;
							}else{
								ij[i][j] = 3;
								ij[i][j+1] = 3;
								ij[i][j+2] = 3;
								ij[i+1][j+1] = 3;	
								rotate = false;
							}						//#
						}							//# #
						if(rotation==1){			//#
							if((i<width-1&&i>0&&j>0)&&sij[i-1][j+1]==0&&sij[i][j+1]==0&&sij[i+1][j+1]==0&&sij[i][j]==0){
								ij[i-1][j+1] = 3;
								ij[i][j+1] = 3;
								ij[i+1][j+1] = 3;
								ij[i][j] = 3;
								rotate = true;
							}else{
								ij[i][j] = 3;
								ij[i][j+1] = 3;
								ij[i][j+2] = 3;
								ij[i+1][j+1] = 3;
								rotate = false;
							}
						}							//# # #
						if(rotation==2){			//  #
							if((i<width-1&&i>=0&&j>0)&&sij[i][j]==0&&sij[i+1][j+1]==0&&sij[i+1][j]==0&&sij[i+1][j-1]==0){
								ij[i][j] = 3;
								ij[i+1][j+1] = 3;
								ij[i+1][j] = 3;
								ij[i+1][j-1] = 3;
								rotate = true;
							}else{
								ij[i][j] = 3;
								ij[i+1][j] = 3;
								ij[i+2][j] = 3;
								ij[i+1][j-1] = 3;
								rotate = false;
							}						//  # 	
						}							//# #
						if(rotation==3){			//  #
							if((i<width-2&&i>=0&&j>0)&&sij[i][j]==0&&sij[i+1][j]==0&&sij[i+1][j+1]==0&&sij[i+2][j]==0){
								ij[i][j] = 3;
								ij[i+1][j] = 3;
								ij[i+1][j+1] = 3;
								ij[i+2][j] = 3;
								rotate = true;
							}else{
								ij[i][j] = 3;
								ij[i+1][j-1] = 3;
								ij[i+1][j] = 3;
								ij[i+1][j+1] = 3;
								rotate = false;
							}
						}
					}								//#
					if(shape==4){					//#
						if(rotation==0){			//# #
							if((i<width-2&&i>=0&&j>0)&&sij[i][j+1]==0&&sij[i][j]==0&&sij[i+1][j+1]==0&&sij[i+2][j+1]==0){
								ij[i][j+1] = 4;
								ij[i][j] = 4;
								ij[i+1][j+1] = 4;
								ij[i+2][j+1] = 4;
								rotate = true;
							}else{
								ij[i][j] = 4;
								ij[i][j+1] = 4;
								ij[i][j+2] = 4;
								ij[i+1][j] = 4;
								rotate = false;
							}
						}							//# # # 
						if(rotation==1){			//#
							if((i<width-1&&i>=0&&j>1)&&sij[i][j+1]==0&&sij[i+1][j+1]==0&&sij[i+1][j]==0&&sij[i+1][j-1]==0){
								ij[i][j+1] = 4;
								ij[i+1][j+1] = 4;
								ij[i+1][j] = 4;
								ij[i+1][j-1] = 4;
								rotate = true;
							}else{
								ij[i][j] = 4;
								ij[i][j+1] = 4;
								ij[i+1][j+1] = 4;
								ij[i+2][j+1] = 4;
								rotate = false;
							}						//# #
						}							//  #
						if(rotation==2){			//  #
							if((i<width-1&&i>0&&j>0)&&sij[i-1][j-2]==0&&sij[i][j-2]==0&&sij[i+1][j-2]==0&&sij[i+1][j-1]==0){
								ij[i-1][j-2] = 4;
								ij[i][j-2] = 4;
								ij[i+1][j-2] = 4;
								ij[i+1][j-1] = 4;
								rotate = true;
							}else{
								ij[i][j] = 4;
								ij[i+1][j] = 4;
								ij[i+1][j-1] = 4;
								ij[i+1][j-2] = 4;
								rotate = false;
							}
						}							//	  #		
						if(rotation==3){			//# # # 
							if((i<width-1&&i>=0&&j>0)&&sij[i+1][j+2]==0&&sij[i+1][j+1]==0&&sij[i+1][j]==0&&sij[i+2][j]==0){
								ij[i+1][j+2] = 4;
								ij[i+1][j+1] = 4;
								ij[i+1][j] = 4;
								ij[i+2][j] = 4;
								rotate = true;
							}else{
								ij[i][j] = 4;
								ij[i+1][j] = 4;
								ij[i+2][j] = 4;
								ij[i+2][j+1] = 4;
								rotate = false;
							}
						}
					}								//  #
					if(shape==5){					//  #
						if(rotation==0){			//# #
							if((i<width-2&&i>=0&&j>0)&&sij[i][j]==0&&sij[i][j+1]==0&&sij[i+1][j]==0&&sij[i+2][j]==0){
								ij[i][j] = 5;
								ij[i][j+1] = 5;
								ij[i+1][j] = 5;
								ij[i+2][j] = 5;
								rotate = true;
							}else{
								ij[i][j] = 5;
								ij[i+1][j] = 5;
								ij[i+1][j+1] = 5;
								ij[i+1][j+2] = 5;
								rotate = false;
							}
						}							//#
						if(rotation==1){			//# # #
							if((i<width-1&&i>=0&&j>1)&&sij[i][j-2]==0&&sij[i][j-1]==0&&sij[i][j]==0&&sij[i+1][j]==0){
								ij[i][j-2] = 5;
								ij[i][j-1] = 5;
								ij[i][j] = 5;
								ij[i+1][j] = 5;
								rotate = true;
							}else{
								ij[i][j] = 5;
								ij[i][j+1] = 5;
								ij[i+1][j] = 5;
								ij[i+2][j] = 5;
								rotate = false;
							}						//# #
						}							//#
						if(rotation==2){			//#
							if((i<width-1&&i>0&&j>0)&&sij[i-1][j+2]==0&&sij[i][j+2]==0&&sij[i+1][j+2]==0&&sij[i+1][j+1]==0){
								ij[i-1][j+2] = 5;
								ij[i][j+2] = 5;
								ij[i+1][j+2] = 5;
								ij[i+1][j+1] = 5;
								rotate = true;
							}else{
								ij[i][j] = 5;
								ij[i][j+1] = 5;
								ij[i][j+2] = 5;
								ij[i+1][j+2] = 5;
								rotate = false;
							}
						}							//# # #
						if(rotation==3){			//    #
							if((i<width-1&&i>=0&&j>0)&&sij[i+1][j-1]==0&&sij[i+2][j-1]==0&&sij[i+2][j]==0&&sij[i+2][j+1]==0){
								ij[i+1][j-1] = 5;
								ij[i+2][j-1] = 5;
								ij[i+2][j] = 5;
								ij[i+2][j+1] = 5;
								rotate = true;
							}else{
								ij[i][j] = 5;
								ij[i+1][j] = 5;
								ij[i+2][j] = 5;
								ij[i+2][j-1] = 5;
								rotate = false;
							}
						}
					}								//# #
					if(shape==6){					//  # #
						if(rotation==0||rotation==2){		
							if((i<width-1&&i>=0&&j>0)&&sij[i][j-1]==0&&sij[i][j]==0&&sij[i+1][j]==0&&sij[i+1][j+1]==0){
								ij[i][j-1] = 6;
								ij[i][j] = 6;
								ij[i+1][j] = 6;
								ij[i+1][j+1] = 6;
								rotate = true;
							}else{
								ij[i][j] = 6;
								ij[i+1][j] = 6;
								ij[i+1][j-1] = 6;
								ij[i+2][j-1] = 6;
								rotate = false;		//  #
							}						//# #
						}							//#
						if(rotation==1||rotation==3){
							if((i<width-2&&i>=0&&j>0)&&sij[i][j+1]==0&&sij[i+1][j+1]==0&&sij[i+1][j]==0&&sij[i+2][j]==0){
							ij[i][j+1] = 6;
							ij[i+1][j+1] = 6;
							ij[i+1][j] = 6;
							ij[i+2][j] = 6;
							rotate = true;
							}else{
								ij[i][j] = 6;
								ij[i][j+1] = 6;
								ij[i+1][j+1] = 6;
								ij[i+1][j+2] = 6;
								rotate = false;
							}
						}
					}								//  # #
					if(shape==7){					//# #
						if(rotation==0||rotation==2){		
							if((i<width-1&&i>=0&&j>0)&&sij[i][j+1]==0&&sij[i][j+2]==0&&sij[i+1][j+1]==0&&sij[i+1][j]==0){
								ij[i][j+1] = 7;
								ij[i][j+2] = 7;
								ij[i+1][j+1] = 7;
								ij[i+1][j] = 7;
								rotate = true;
							}else{
								ij[i][j] = 7;
								ij[i+1][j] = 7;
								ij[i+1][j+1] = 7;
								ij[i+2][j+1] = 7;
								rotate = false;		//#
							}						//# #
						}							//  #
						if(rotation==1||rotation==3){
							if((i<width-2&&i>=0&&j>0)&&sij[i][j-1]==0&&sij[i+1][j-1]==0&&sij[i+1][j]==0&&sij[i+2][j]==0){
								ij[i][j-1] = 7;
								ij[i+1][j-1] = 7;
								ij[i+1][j] = 7;
								ij[i+2][j] = 7;
								rotate = true;
							}else{
								ij[i][j] = 7;
								ij[i][j+1] = 7;
								ij[i+1][j] = 7;
								ij[i+1][j-1] = 7;
								rotate = false;
							}
						}
					}
					halt = true;
					break;	
				}	
				if(halt){ 		
					break;
				}
			}
		}
		if(rotate){
			rotation++;
		}	
		if(rotation == 4){
			rotation = 0;
		}
	}
	
	public void paint(Graphics gr){
		super.paint(gr);
		setBackground(Color.WHITE);
		int y = 560;
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(ij[i][j] > 0){
					gr.setColor(mapColor(ij[i][j]));
					gr.fillRect((i+1)*20, y-((j+1)*20), 20, 20);
				}
				if(sij[i][j] > 0){
					gr.setColor(mapColor(sij[i][j]));
					gr.fillRect((i+1)*20, y-((j+1)*20), 20, 20);
				}
			}
		}
		gr.setColor(Color.BLACK);
		gr.fillRect(20, 540, (40+(width*20))+100, 20);
		gr.fillRect(20, 0, (40+(width*20))+100, 20);
		gr.fillRect(0, 0, 20, 560);
		gr.fillRect((40+(width*20))-20, 0, 20, 560);
		gr.fillRect((40+(width*20))+120, 0, 20, 560);
		gr.fillRect((40+(width*20)), 140, 120, 20);	
		gr.setFont(font);
		gr.drawString("Score",260,220);
		if(score<10){
			gr.drawString(""+score,293,270);
		}else{
			gr.drawString(""+score,283,270);
		}
		gr.drawString("Level",260,325);
		if(level<10){	
			gr.drawString(""+level,293,375);
		}else{
			gr.drawString(""+level,283,375);
		}
		gr.drawString("High",267,427);
		gr.drawString("Score",260,460);
		if(highscore<10){
			gr.drawString(""+highscore,293,510);
		}else{
			gr.drawString(""+highscore,283,510);
		}
		gr.setColor(mapColor(nextshape));
		
		if(nextshape==1){
			gr.fillRect((40+(width*20))+40,60,40,40);
		}
		if(nextshape==2){
			gr.fillRect((40+(width*20))+20,70,80,20);
		}
		if(nextshape==3){
			gr.fillRect((40+(width*20))+50,60,20,20);
			gr.fillRect((40+(width*20))+30,80,60,20);
		}
		if(nextshape==4){
			gr.fillRect((40+(width*20))+40,50,20,60);
			gr.fillRect((40+(width*20))+60,90,20,20);
		}
		if(nextshape==5){
			gr.fillRect((40+(width*20))+60,50,20,60);
			gr.fillRect((40+(width*20))+40,90,20,20);
		}
		if(nextshape==6){
			gr.fillRect((40+(width*20))+30,60,40,20);
			gr.fillRect((40+(width*20))+50,80,40,20);
		}
		if(nextshape==7){
			gr.fillRect((40+(width*20))+50,60,40,20);
			gr.fillRect((40+(width*20))+30,80,40,20);
		}
	}
	
	private Color mapColor(int i) {
		switch (i) {
		case 1:
			return Color.GRAY;
		case 2:
			return Color.BLUE;
		case 3:
			return Color.GREEN;
		case 4:
			return Color.YELLOW;
		case 5:
			return Color.PINK;
		case 6:
			return Color.CYAN;
		case 7:
			return Color.ORANGE;
		default:
			return Color.WHITE;
		}
	}
}
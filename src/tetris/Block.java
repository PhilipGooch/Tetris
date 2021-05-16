package tetris;

import java.awt.event.KeyEvent;

public class Block {

	int width = 10;
	int height = 27;
	int ij [] [] = new int [width] [height];
	int shape;
	int x;
	
	public Block(int s){
		shape = s;
		x = width/2;
		if(shape == 1){
			ij[x-1][25] = 1;		//# #
			ij[x][25] = 1;			//# #
			ij[x-1][24] = 1;
			ij[x][24] = 1;
		}	
		if(shape == 2){
			ij[x-1][25] = 2;		//# # # #
			ij[x][25] = 2;
			ij[x+1][25] = 2;
			ij[x+2][25] = 2;
		}
		if(shape == 3){
			ij[x][25] = 3;			//  #
			ij[x-1][24] = 3;		//# # #
			ij[x][24] = 3;
			ij[x+1][24] = 3;
		}
		if(shape == 4){
			ij[x-1][25] = 4;		//#
			ij[x-1][24] = 4;		//#
			ij[x-1][23] = 4;		//# #
			ij[x][23] = 4;
		}
		if(shape == 5){
			ij[x][25] = 5;			//  #
			ij[x][24] = 5;			//  #
			ij[x][23] = 5;			//# #
			ij[x-1][23] = 5;
		}
		if(shape == 6){
			ij[x-1][25] = 6;		//# #
			ij[x][25] = 6;			//  # #
			ij[x][24] = 6;
			ij[x+1][24] = 6;
		}
		if(shape == 7){
			ij[x-1][24] = 7;		//  # #
			ij[x][24] = 7;			//# #
			ij[x][25] = 7;
			ij[x+1][25] = 7;
		}
	}

	public void fall(){
		for(int j = 0; j < height; j++){ 
			if(j==height-1){
				for(int i = 0; i< width; i++){
					ij[i][height-1] = 0;
				}
			}
			else{
				for(int i = 0; i< width; i++){
					ij[i][j] = ij[i][j+1];
				}
			}
		}
	}
	
	public void moveLeft(KeyEvent event){		
		if(event.getKeyCode() == KeyEvent.VK_A){
			int zz = 0;
			for(int z : ij[0]){
				zz = zz + z;
			}
			if(zz == 0){
				for(int i = 0; i< width-1; i++){
					ij[i] = ij[i+1];
				}
				ij[width-1] = new int [height];
			}	
		}
	}	

	public void moveRight(KeyEvent event){
		if(event.getKeyCode() == KeyEvent.VK_D){
			int zz = 0;
			for(int z : ij[width-1]){
				zz = zz + z;
			}
			if(zz == 0){
				for(int i = width-1; i> 0; i--){
					ij[i] = ij[i-1];
				}
				ij[0] = new int [height];
			}	
		}	
	}
	
	public int [][] getIJ(){
		return ij;
	}
}

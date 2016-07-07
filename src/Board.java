import java.util.Arrays;
import java.util.Random;

public class Board {
	private int[][] tiles; //if no number then it is 0
	private boolean[][] combined; //true if the number has already been combined with another number on this move
	private int maxTile; //keeps track of how high you get in the game
	
	public Board(){
		tiles = new int[4][4]; //filled with all zeros
		combined = new boolean[4][4]; //filled with all false
		Random gen = new Random(); //will generate random spaces to put numbers in
		double prob; //if less than .75, then the new number is a 2. otherwise it is a 4.
		while(full()<2){ //put the two starting numbers in the board
			prob = Math.random();
			if(prob<.75) //approximately 75% of time tile is a 2? no clue just guessing
				tiles[gen.nextInt(tiles.length)][gen.nextInt(tiles[0].length)]=2;
			else
				tiles[gen.nextInt(tiles.length)][gen.nextInt(tiles[0].length)]=4;
		}
		maxTile = 0;
	}
	
	public Board(int[][] t){ //constructor used in checkWin()
		tiles = new int[t.length][t[0].length];
		for(int row=0; row<t.length; row++){ //copy t into tiles (so that the array from the original game doesn't change)
			for(int col=0; col<t[0].length; col++){
				tiles[row][col]=  t[row][col];
			}
		}
		combined = new boolean[tiles.length][tiles[0].length];
		maxTile = 0;
	}
	
	private int full(){ //returns number of spaces that have tiles
		int full=0;
		for(int row=0; row<tiles.length; row++){
			for(int col=0; col<tiles[0].length; col++){
				if(tiles[row][col] != 0)
					full++;
			}
		}
		return full;
	}
	
	public boolean moveLeft(){ //returns false if no tiles can move left
		int c;
		boolean changed = false; //true when the board has changed due to the move - only then can you add a tile
		for(int col=0; col<tiles[0].length; col++){ //THIS FOR LOOP IS COLUMNS FIRST FYI (so tiles furthest left move first)
			for(int row=0; row<tiles.length; row++){
				if(tiles[row][col] != 0){
					c=col; //where the tile is going to move to
					while(c>0 && tiles[row][c-1] == 0){ //can move left still
						c--;
					}
					if(c>0 && tiles[row][c-1] == tiles[row][col] && !combined[row][c-1]){ //can combine with tile
						tiles[row][c-1] = 2*tiles[row][c-1]; //tile becomes next power of two
						if(tiles[row][c-1]>maxTile) //reached a new high tile
							maxTile = tiles[row][c-1];
						combined[row][c-1] = true; //a tile can only combine with another tile once in a move
						tiles[row][col] = 0; //tile moved so set original spot to 0
						changed = true;
					}
					else if(c != col){ //tile did move, must move number to new tile and delete (make 0) original tile
						tiles[row][c] = tiles[row][col];
						tiles[row][col] = 0;
						changed = true;
					}
				}
			}
		}
		if(changed)
			return add();
		else
			return false;
	}
	
	public boolean moveRight(){ //retuns false if no tiles can move right
		int c;
		boolean changed = false; //true when the board has changed due to the move - only then can you add a tile
		for(int col=tiles[0].length-1; col>=0; col--){ //THIS FOR LOOP IS COLUMNS FIRST FYI (so tiles furthest right move first)
			for(int row=0; row<tiles.length; row++){
				if(tiles[row][col] != 0){
					c=col; //where the tile is going to move to
					while(c<tiles[0].length-1 && tiles[row][c+1] == 0){ //can move left still
						c++;
					}
					if(c<tiles[0].length-1 && tiles[row][c+1] == tiles[row][col] && !combined[row][c+1]){ //can combine with tile
						tiles[row][c+1] = 2*tiles[row][c+1]; //tile becomes next power of two
						if(tiles[row][c+1]>maxTile) //reached a new high tile
							maxTile = tiles[row][c+1];
						combined[row][c+1] = true; //a tile can only combine with another tile once in a move
						tiles[row][col] = 0; //tile moved so set original spot to 0
						changed = true;
					}
					else if(c != col){ //tile did move, must move number to new tile and delete (make 0) original tile
						tiles[row][c] = tiles[row][col];
						tiles[row][col] = 0;
						changed = true;
					}
				}
			}
		}
		if(changed)
			return add();
		else
			return false;
	}
	
	public boolean moveDown(){ //returns false if no tiles can move down
		int r;
		boolean changed = false; //true when the board has changed due to the move - only then can you add a tile
		for(int row=tiles.length-1; row>=0; row--){ //THIS FOR LOOP IS ROWS BOTTOM UP FYI (so tiles furthest down move first)
			for(int col=0; col<tiles[0].length; col++){
				if(tiles[row][col] != 0){
					r=row; //where the tile is going to move to
					while(r<tiles.length-1 && tiles[r+1][col] == 0){ //can move down still
						r++;
					}
					if(r<tiles.length-1 && tiles[r+1][col] == tiles[row][col] && !combined[r+1][col]){ //can combine with tile
						tiles[r+1][col] = 2*tiles[r+1][col]; //tile becomes next power of two
						if(tiles[r+1][col]>maxTile) //reached a new high tile
							maxTile = tiles[r+1][col];
						combined[r+1][col] = true; //a tile can only combine with another tile once in a move
						tiles[row][col] = 0; //tile moved so set original spot to 0
						changed = true;
					}
					else if(r != row){ //tile did move, must move number to new tile and delete (make 0) original tile
						tiles[r][col] = tiles[row][col];
						tiles[row][col] = 0;
						changed = true;
					}
				}
			}
		}
		if(changed)
			return add();
		else
			return false;
	}
	
	public boolean moveUp(){ //returns false if no tiles can move up
		int r;
		boolean changed = false; //true when the board has changed due to the move - only then can you add a tile
		for(int row=0; row<tiles.length; row++){ //noraml for loop, tiles furthest up move first
			for(int col=0; col<tiles[0].length; col++){
				if(tiles[row][col] != 0){
					r=row; //where the tile is going to move to
					while(r>0 && tiles[r-1][col] == 0){ //can move left still
						r--;
					}
					if(r>0 && tiles[r-1][col] == tiles[row][col] && !combined[r-1][col]){ //can combine with tile
						tiles[r-1][col] = 2*tiles[r-1][col]; //tile becomes next power of two
						if(tiles[r-1][col]>maxTile) //reached a new high tile
							maxTile = tiles[r-1][col];
						combined[r-1][col] = true; //a tile can only combine with another tile once in a move
						tiles[row][col] = 0; //tile moved so set original spot to 0
						changed = true;
					}
					else if(r != row){ //tile did move, must move number to new tile and delete (make 0) original tile
						tiles[r][col] = tiles[row][col];
						tiles[row][col] = 0;
						changed = true;
					}
				}
			}
		}
		if(changed)
			return add();
		else 
			return false;
	}
	
	private boolean add(){ //returns true if tile can be added (and adds a tile)
		reset(); //move is finished so combined is all false again
		if(full() == tiles.length*tiles[0].length) //every space has a tile
			return false; //no tile can be added
		Random gen = new Random(); //random generator for spot new tile comes in at
		double prob = Math.random(); //random generator that decides if new tile is a 2 or a 4
		int spot = gen.nextInt(tiles.length*tiles[0].length-full()); //location of new tile
		int row=-1,col=-1; //if index out of bounds error occurs because of this then for loops below failed
		boolean b = false; //so you get double break out of for loops
		for(int r=0; r<tiles.length; r++){
			for(int c=0; c<tiles[0].length; c++){
				if(spot==0 && tiles[r][c] == 0){ //the new tile is going here
					row=r;
					col=c;
					b = true;
					break;
				}
				else if(tiles[r][c] == 0) //possible tile spot but tile isn't going here
					spot--;
			}
			if(b)
				break;
		}
		if(tiles[row][col] != 0) //new tile replaced old tile - BAD
			System.out.println("NOT OK");
		if(prob<.75)
			tiles[row][col] = 2;
		else
			tiles[row][col]=4;
		return true; //tile added
	}
	
	private void reset(){ //resets combined to all false after every move
		for(int row=0; row<combined.length; row++){
			for(int col=0; col<combined[0].length; col++){
				combined[row][col] = false;
			}
		}
	}
	
	public boolean checkStuck(){ //returns true if no more moves can be made
		Board testing = new Board(tiles); //so that the board doesn't actually change if a move can be made
		if(full() == tiles.length*tiles[0].length && !(testing.moveLeft() || testing.moveRight() 
				|| testing.moveUp() || testing.moveDown())){
			return true;
		}
		return false;
	}
	
	public boolean checkWin(){ //returns true if you have reached the 2048 tile
		for(int row=0; row<tiles.length; row++){
			for(int col=0; col<tiles[0].length; col++){
				if(tiles[row][col] == 2048)
					return true;
			}
		}
		return false;
	}
	
	public int[][] getTiles(){
		return tiles;
	}
	
	public int getMax(){
		return maxTile;
	}
	
	public void print()//print the board nicely
	{
		for (int r=0; r<tiles.length; r++){
			for (int c=0; c<tiles[0].length; c++)
				System.out.print(tiles[r][c]+" ");
			System.out.println();
		}
		System.out.println();
	}
	
	public static void main(String[] args){
		Board a = new Board();
		a.print();
		a.moveLeft();
		a.print();
		a.moveUp();
		a.print();
		a.moveRight();
		a.print();
		a.moveDown();
		a.print();
	}
}

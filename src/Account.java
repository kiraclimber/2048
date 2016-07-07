import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class Account { 
	private String username; //username of account (also file name)
	private String path; //file path
	private String[] text; //read in from file (line 1=username, line2=password, line3=scores (seperated by commas)
	private ArrayList<Integer> scores; //history of that person's scores
	
	public Account(String username) throws IOException{ //for an account that already has been set up
		this.username = username;
		path = "src/textFiles/" + username + ".txt";
		openFile(); //open up the file and read data from it
		getScores(); //get scores from the data (line 3)
	}
	
	public Account(String username, String password) throws IOException{ //new account
		this.username = username;
		path = "src/textFiles/" + this.username + ".txt"; //creates a new .txt file with username as file name
		addInfo(this.username); //adds username to first line
		addInfo(password); //adds password to second line
		scores = new ArrayList<Integer>(); //nothing in scores currently
		addScore(0); //so there is something in that line
		openFile(); //open up the file and read data from it
	}
	
	public void getScores(){ //populates scores ArrayList with scores from previous games
		String stringscores = text[2]; //third (second...) line has scores separated by commas
		String[] arrscores = stringscores.split(","); //separate scores and put into array
		scores = new ArrayList<Integer>(); //initialize scores ArrayList
		for(int i=0; i<arrscores.length; i++){
			scores.add(Integer.parseInt(arrscores[i])); //add scores to ArrayList (and make them integers)
		}
	}
	
	private void addInfo(String info) throws IOException{ //add username and password with this (makes a new line after entering data)
		FileWriter write = new FileWriter(path, true);
		PrintWriter printer = new PrintWriter(write); 
		printer.printf("%s" + "%n", info); //write info and then go to a new line
		printer.close(); //close resource
	}
	
	public void addScore(int score) throws IOException{ //add scores with this (no new line, separated by commas)
		String stringscore = Integer.toString(score) + ","; //formats score
		FileWriter write = new FileWriter(path , true);
		PrintWriter printer = new PrintWriter( write );
		printer.print(stringscore); //write score
		printer.close(); //close resource
		scores.add(score); //add score to scores ArrayList so if you use Play Again button you don't have to read file all over again
	}
	
	public boolean checkPassword(String password){ //checks that input password is the same as password on file
		String correctPassword = text[1];
		if(correctPassword.equals(password))
			return true;
		return false;
	}
	
	public int bestScore(){ //returns high score from file
		int max = 0;
		for(int i=0; i<scores.size(); i++){
			if(scores.get(i)>max)
				max=scores.get(i);
		}
		return max;
	}
	
	public void openFile() throws IOException{ //opens file and reads all content into array of size 3
		FileReader fr = new FileReader(path);
		BufferedReader reader = new BufferedReader(fr);
		text = new String[3]; //username, password, scores
		for(int i=0; i<text.length; i++){
			text[i] = reader.readLine();
		}
		reader.close();
	}
	
	public static void main(String[] args) throws IOException{
	}
}

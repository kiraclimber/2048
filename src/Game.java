import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class Game implements ActionListener, KeyListener{
	//for sign in frame
	private JComboBox choices;
	private JButton next;
	private JTextField unameArea;
	private JPasswordField pwordArea;
	private JPasswordField verifypwordArea;
	private JLabel username;
	private JLabel password;
	private JLabel verify;
	private JPanel signPanel;
	private JFrame signFrame;
	private Account account;
	private static String[] comboBoxChoices = {"guest", "sign in", "create account"};
	private String choice; //saves current combo box choice
	private JLabel errorLabel; //if stuff isn't filled out
	
	//for main game
	private JLabel[][] labels; //for tiles and empty spaces (both are labels, have different borders)
	private JLabel bestScore;
	private JPanel panel; 
	private JFrame frame;
	private Board puzzle;
	private GridBagConstraints gbc;
	
	//for frame that pops up when you lose
	private JButton againButton; //play a new game (also shows up in win frame)
	private JLabel loseLabel;
	private JPanel losePanel;
	private JFrame loseFrame;
	
	//for frame that pops up when you win
	private JButton continueButton; //continue playing after having won
	private JButton endGame; //stop playing
	private JLabel winLabel; 
	private JPanel winPanel;
	private JFrame winFrame;
	
	private boolean alreadyWon; //so that if you want to keep playing it doesn't tell you "you win" every time you make a move
	
	public Game(){ //set up sign in frame
		choice = "guest"; //default
		gbc = new GridBagConstraints();
		signPanel = new JPanel();
		signPanel.setLayout(new GridBagLayout());
		signFrame = new JFrame();
		gbc.fill = GridBagConstraints.BOTH; //makes buttons stretch to fill entire space
		choices = new JComboBox(comboBoxChoices);
		choices.setSelectedIndex(0);
		choices.addActionListener(this);
		gbc.gridx = 1;
		gbc.gridy = 0;
		signPanel.add(choices, gbc);
		next = new JButton("continue");
		next.addActionListener(this);
		gbc.gridx = 2;
		signPanel.add(next, gbc);
		unameArea = new JTextField(); //add to panel with constraints later (once selected)
		pwordArea = new JPasswordField(); //add to panel with constraints later (once selected)
		verifypwordArea = new JPasswordField(); //add to panel with constraints later (once selected)
		username = new JLabel("username: ");
		username.setHorizontalAlignment(JLabel.RIGHT);
		password = new JLabel("password: ");
		password.setHorizontalAlignment(JLabel.RIGHT);
		verify = new JLabel("verify password: ");
		verify.setHorizontalAlignment(JLabel.RIGHT);
		errorLabel = new JLabel();
		errorLabel.setForeground(Color.red); //makes text red
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		signPanel.add(errorLabel, gbc);
		signFrame.setContentPane(signPanel);
		signFrame.setSize(500, 500);
		signFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //what happens when you hit red x
		signFrame.setVisible(true);
	}
	
	public void setUp(){ //sets up game panel/frame to play
		signFrame.dispose();
		alreadyWon = false;
		puzzle = new Board();
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.addKeyListener(this); //so you register arrow keys
		frame = new JFrame();
		labels = new JLabel[puzzle.getTiles().length][puzzle.getTiles()[0].length];
		gbc.fill = GridBagConstraints.BOTH; //makes buttons stretch to fill entire space
		gbc.weightx = gbc.weighty = 1; //rows and columns expand with window
		gbc.gridwidth = 1;
		JLabel l;
		for(int row=0; row<puzzle.getTiles().length; row++){
			for(int col=0; col<puzzle.getTiles()[0].length; col++){
				l=new JLabel(){
					public Dimension getPreferredSize(){ //so that the labels stay the same size throughout game
						return new Dimension(500/8,500/8);
					}
				};
				gbc.gridx = col;
				gbc.gridy = row;
				if(puzzle.getTiles()[row][col] != 0){ //colored tile with number on it
					l.setText(Integer.toString(puzzle.getTiles()[row][col]));
					l.setBorder(BorderFactory.createRaisedBevelBorder());
					setColor(l); //method that sets color based on number on label
				}
				l.setOpaque(true); //so that Label.setBackground changes the actual color of the label
				l.setHorizontalAlignment(JLabel.CENTER);
				l.addKeyListener(this);
				labels[row][col] = l;
				panel.add(l,gbc);		
			}
		}
		bestScore = new JLabel("Best Score: " + account.bestScore()){
			public Dimension getPreferredSize(){ //so that the label stays the same size throughout game
					return new Dimension(250,15); //width, height
			}
		};
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 4;
		bestScore.setHorizontalAlignment(JLabel.CENTER);
		panel.add(bestScore, gbc);
		frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){ //make it so score gets added to file after window closes
                try {
					account.addScore(puzzle.getMax());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
                e.getWindow().dispose();
            }
        });
		frame.setContentPane(panel);
		frame.addKeyListener(this);
		frame.setSize(500, 530); //width, height
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //what happens when you hit red x
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == choices){ //choices of ways to sign in
			choice = (String)choices.getSelectedItem();
			if(!choice.equals("guest")){ //have to add password and username fields
				gbc.gridwidth=1;
				gbc.gridx = 1;
				gbc.gridy = 1;
				signPanel.add(unameArea, gbc);
				gbc.gridy = 2;
				signPanel.add(pwordArea, gbc);
				gbc.gridx = 0;
				gbc.gridy = 1;
				signPanel.add(username, gbc);
				gbc.gridy = 2;
				signPanel.add(password, gbc);
				if(choice.equals("create account")){ //have to add verify password field
					gbc.gridx=1;
					gbc.gridy = 3;
					signPanel.add(verifypwordArea, gbc);
					gbc.gridx = 0;
					signPanel.add(verify, gbc);
				}
				if(choice.equals("sign in")){ //remove verify password field if user first selected create account
					if (verifypwordArea.getParent() == signPanel){ //if verify password field is on panel
						signPanel.remove(verifypwordArea); //text field removed
						signPanel.remove(verify); //label saying "verify password" removed
					}
				}
			}
			else{ //choice is "guest". remove username and password and verify password fields if on panel
				if(pwordArea.getParent() == signPanel){ //password area on panel, means username area also on panel
					signPanel.remove(unameArea); //text field for username removed
					signPanel.remove(username); //label saying "username" removed
					signPanel.remove(pwordArea); //text field for passwrod removed
					signPanel.remove(password); //label saying "password" removed
				}
				if(verifypwordArea.getParent() == signPanel){ //verify password area on panel, must remove
					signPanel.remove(verifypwordArea); //text field for verify password removed
					signPanel.remove(verify); //label saying "verify password" removed
				}
			}
			signFrame.validate();
			signFrame.repaint(); //like have to refresh frame or something so changes shows up
		}
		if(e.getSource() == next){ //continue on to game if everything is filled out
			if(choice.equals("guest")){
				File f = new File("src/textFiles/guest.txt");
				if(!f.exists()){ //guest file does not exist yet
					try{
						account = new Account("guest","guest"); //have to set up guest file
					} catch(IOException i){
						i.printStackTrace();
					}
				}
				else{
					try{
						account = new Account("guest"); //guest file already exists
					} catch(IOException i){
						i.printStackTrace();
					}
				}
				setUp(); //set up board to play
			}
			else if(choice.equals("create account")){ //requires username, password, and verify password
				char[] input = pwordArea.getPassword(); //can't use getText() because its a password field
				char[] vinput = verifypwordArea.getPassword();
				String password = String.valueOf(input);
				String vpassword = String.valueOf(vinput);
				File f = new File("src/textFiles/" + unameArea.getText() + ".txt"); //for first else if statement
				if(unameArea.getText().equals("") || password.equals("")) //missing username or password
					errorLabel.setText("Please enter username and password");
				else if(f.exists() && !f.isDirectory()) { //so two accounts don't have same username
				    errorLabel.setText("Username is already in use");
				}
				else if(!password.equals(vpassword)) //passwords are not the same
					errorLabel.setText("Passwords don't match");
				else{
					try {
						account = new Account(unameArea.getText(), password); //set up a new account and file
					} catch (IOException i) {
						i.printStackTrace();
					}
					setUp(); //set up board to play
				}
			}
			else if(choice.equals("sign in")){ //requires username and password
				char[] input = pwordArea.getPassword();
				String password = String.valueOf(input);
				File f = new File("src/textFiles/" + unameArea.getText() + ".txt");
				if(unameArea.getText().equals("") || password.equals("")) //missing username or password
					errorLabel.setText("Please enter username and password");
				else if(!f.exists()) //trying to sign in with username that does not have file associated with it
					errorLabel.setText("Username does not exist");
				else{
					try{
						account = new Account(unameArea.getText()); //set up "new" account to existing file
					} catch(IOException i){
						i.printStackTrace();
					}
					if(!account.checkPassword(password)) //passwords don't match
						errorLabel.setText("Incorrect password");
					else
						setUp(); //set up board to play
				}
			}
		}
		if(e.getSource() == endGame){ //won, don't want to play anymore, add score to file
			winFrame.dispose();
			frame.dispose();
			try {
				account.addScore(puzzle.getMax());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if(e.getSource() == againButton){ //play another game (button on both win and lose frames)
			frame.dispose(); //closes game frame
			if(loseFrame != null) //button on lose frame
				loseFrame.dispose(); //closes lose frame
			else{ //close win frame and add score (button on win frame)
				winFrame.dispose();
				try {
					account.addScore(puzzle.getMax());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			setUp(); //set up game board for next game
		}
		if(e.getSource() == continueButton){
			winFrame.dispose(); //close win frame and keep playing
		}
	}
	
	@Override
	public void keyTyped(KeyEvent event){
		//inherited from KeyEvent, not used
	}
	

	@Override
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_UP) { //up arrow
			puzzle.moveUp();
			updateBoard();
		}
		else if(event.getKeyCode() == KeyEvent.VK_DOWN){ //down arrow
			puzzle.moveDown();
			updateBoard();
		}
		else if(event.getKeyCode() == KeyEvent.VK_LEFT){ //left arrow
			puzzle.moveLeft();
			updateBoard();
		}
		else if(event.getKeyCode() == KeyEvent.VK_RIGHT){ //right arrow
			puzzle.moveRight();
			updateBoard();
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		//inherited from KeyEvent, not used
	}
	
	private void updateBoard(){ //updates the board to match puzzle.getTiles() after every move
		for(int row=0; row<puzzle.getTiles().length; row++){
			for(int col=0; col<puzzle.getTiles()[0].length; col++){
				if(puzzle.getTiles()[row][col] != 0){ //colored tile with number on it
					labels[row][col].setText(Integer.toString(puzzle.getTiles()[row][col]));
					labels[row][col].setBorder(BorderFactory.createRaisedBevelBorder());
					setColor(labels[row][col]); //set label to appropriate color
				}
				else{ //blank label (no tile)
					labels[row][col].setText("");
					labels[row][col].setBorder(BorderFactory.createEmptyBorder());
					labels[row][col].setBackground(null);
				}	
			}
		}
		if(puzzle.checkStuck()) //no more moves can be made
			gameOver(); //sets up game over frame
		if(!alreadyWon && puzzle.checkWin()) //haven't already won and have reached 2048 tile
			youWin(); //sets up you win frame
	}
	
	private void youWin(){ //you win frame gets created
		alreadyWon = true;
		winFrame = new JFrame();
		winPanel = new JPanel();
		winLabel = new JLabel("You Win");
		winPanel.add(winLabel);
		againButton = new JButton("Play Again");
		againButton.addActionListener(this);
		winPanel.add(againButton);
		endGame = new JButton("Quit");
		endGame.addActionListener(this);
		winPanel.add(endGame);
		continueButton = new JButton("Keep Playing");
		continueButton.addActionListener(this);
		winPanel.add(continueButton);
		winFrame.setContentPane(winPanel);
		winFrame.setSize(100,100);
		winFrame.setVisible(true);
		winFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		winFrame.pack();
	}
	
	private void gameOver(){ //game over frame gets created (player stuck)
		loseFrame = new JFrame();
		losePanel = new JPanel();
		loseLabel = new JLabel("You Lose");
		losePanel.add(loseLabel);
		againButton = new JButton("Play Again");
		againButton.addActionListener(this);
		losePanel.add(againButton);
		loseFrame.setContentPane(losePanel);
		loseFrame.setSize(100, 100);
		loseFrame.setVisible(true);
		loseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loseFrame.pack();
		try {
			account.addScore(puzzle.getMax());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setColor(JLabel l){ //sets labels to be correct color based on number on label
		int num=Integer.parseInt(l.getText()); //number on the label
		Color[] colors = {new Color(150,230,230), new Color(230,150,230), new Color(230,230,150), 
				new Color(50,230,230), new Color(230,50,230), new Color(230,230,50),
				new Color(250,0,0), new Color(0,250,0), new Color(0,0,250),
				new Color(250,250,0), new Color(250,0,250), new Color(0,250,250),
				new Color(200,230,230), new Color(230,200,230), new Color(230,230,200)}; 
		Double d = Math.log(num)/Math.log(2); //so that numbers on tiles turn into arith. seq.
		if(d<=colors.length){
			l.setBackground(colors[d.intValue()-1]);
			l.setForeground(Color.black); //set text color back to black if it had been white
		}
		else{ //numbers got too high for colors
			l.setBackground(Color.black); 
			l.setForeground(Color.white); //text color
		}
	}
	
	private void setColorGradient(JLabel l){ //NOT CURRENTLY USED - OLD IDEA, MAY USE STILL
		int num=Integer.parseInt(l.getText()); //number on the label
		//Color[] colors = {Color.blue, Color.cyan, Color.pink, Color.green, Color.magenta, Color.orange, 
		//		Color.red, Color.yellow, Color.lightGray};		
		Double d = Math.log(num)/Math.log(2);
		if(d<16){
			if(d<6)
				l.setBackground(new Color(50*d.intValue(),230,230)); //set background to appropriate color from array
			else if(d<11)
				l.setBackground(new Color(230,50*(d.intValue()-5),230));
			else
				l.setBackground(new Color(50,50,50*(d.intValue()-10)));
			l.setForeground(Color.black); //set text color back to black if it had been white
		}
		else{ //numbers got too high for colors
			l.setBackground(Color.black); 
			l.setForeground(Color.white); //text color
		}
	}
	
	
	public static void main(String[] args){
		Game g = new Game();
	}

}

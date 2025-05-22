package App;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import javax.swing.Timer;


public class SnakeGame extends JPanel implements ActionListener, KeyListener{
	
	private class Tile{
		int x;
		int y;
		Tile(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
	
	//Snake
	Tile snakeHead;
	ArrayList<Tile> snakeBody;
	
	//Apple
	Tile food;
	Random random;
	
	int boardWidth;
	int boardHeight;
	int tileSize = 25;
	
	//Logic
	Timer gameLoop;
	int velocityX;
	int velocityY;
	boolean gameOver = false;
	
	SnakeGame(int boardWidth, int boardHeight){
		this.boardHeight = boardHeight;
		this.boardWidth = boardWidth;
		setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
		setBackground(Color.black);
		addKeyListener(this);
		setFocusable(true);
		Sound();
		
		snakeHead = new Tile(5,5);
		snakeBody = new ArrayList<Tile>();
		
		food = new Tile(10,10);
		random = new Random();
		placeFood();
		
		velocityY = 0;
		velocityX = 0;
		
		gameLoop = new Timer(100, this);
		gameLoop.start();
	}

	private void placeFood() {
		food.x = random.nextInt(boardWidth/tileSize);
		food.y = random.nextInt(boardHeight/tileSize);
		
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	private void Sound() {
	    try {
	        AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File("src/Sound/game.wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInput);
	        clip.loop(10);
	    } catch(Exception ex) {
	        System.out.println("Error: " + ex.getMessage());
	    }
	}
	
	private void EatSound() {
	    try {
	        AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File("src/Sound/nhac.wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInput);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error: " + ex.getMessage());
	    }
	}
	
	private void draw(Graphics g) {
		
		for(int i = 0; i< boardWidth/tileSize; i++) {
			g.drawLine(i * tileSize, 0, i*tileSize, boardHeight);
			g.drawLine(0, i*tileSize, boardWidth, i*tileSize);
		}
		
		// Apple:
		g.setColor(Color.red);
		g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);
		
		//Snake head
		g.setColor(Color.white);
		g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);
		
		//Snake Body
		for (int i = 0; i< snakeBody.size(); i++) {
			Tile snakePart = snakeBody.get(i);
			g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
		}
		
		//Score
		g.setFont(new Font("Arial", Font.PLAIN, 16));
		if (gameOver) {
			g.setColor(Color.red);
			g.drawString("Game Over: "+ String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
		}
		else {
			g.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
		}
	}
	
	public boolean collision(Tile tile1, Tile tile2) {
		return tile1.x == tile2.x && tile1.y == tile2.y;
	}

	
	public void move() {
		//Food
		if (collision(snakeHead, food)) {
			snakeBody.add(new Tile(food.x, food.y));
			EatSound();
			placeFood();
		}
		
		// Body
		for (int i = snakeBody.size() - 1; i >= 0; i--) {
			Tile snakePart = snakeBody.get(i);
			if (i == 0) {
				snakePart.x = snakeHead.x;
				snakePart.y = snakeHead.y;
			}
			else {
				Tile prevSnakePart = snakeBody.get(i-1);
				snakePart.x = prevSnakePart.x;
				snakePart.y = prevSnakePart.y;
			}
		}
		//Head
		snakeHead.x += velocityX;
		snakeHead.y += velocityY;
		
		//Wrap horizontal
		if (snakeHead.x < 0) {
		    snakeHead.x = boardWidth / tileSize - 1;
		} else if (snakeHead.x >= boardWidth / tileSize) {
		    snakeHead.x = 0;
		}
		
		//Wrap vertical
		if (snakeHead.y < 0) {
		    snakeHead.y = boardHeight / tileSize - 1;
		} else if (snakeHead.y >= boardHeight / tileSize) {
		    snakeHead.y = 0;
		}
		
		//game over
		for (int i = 0; i <snakeBody.size(); i++) {
			Tile snakePart = snakeBody.get(i);
			
			//Collide with the head
			if(collision(snakeHead, snakePart)) {
				gameOver = true;
			}
		}
		if(snakeHead.x*tileSize < 0 || snakeHead.x*tileSize > boardWidth || snakeHead.y*tileSize < 0 || snakeHead.y*tileSize > boardHeight) {
			gameOver = true;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		move();
		repaint();
		if(gameOver) {
			gameLoop.stop();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
			velocityX = 0;
			velocityY = -1;
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
			velocityX = 0;
			velocityY = 1;
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
			velocityX = -1;
			velocityY = 0;
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
			velocityX = 1;
			velocityY = 0;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}
}

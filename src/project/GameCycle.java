package project;

import java.awt.*;
import java.awt.event.*;
import static java.lang.Math.*;
import static java.lang.String.format;
import java.util.*;
import javax.swing.*;
import static project.Board.*;

enum Direct {
    right(1, 0), down(0, 1), left(-1, 0);

    Direct(int x, int y) {
        this.x = x;
        this.y = y;
    }
    final int x, y;
};

public class GameCycle extends JPanel implements Runnable {
   public static final int empty = -1; 
   public static final int border = -2;

   Shape fallingShape; //current shape
   Shape nextShape; //next shape
   
   int fallingShapeRow; //position
   int fallingShapeColumn; //position

   final int[][] board = new int[countRows][countColumns]; // coordinates for game board (imported from Board class)

   Thread fallingThread;
   static final Scoreboard scoreboard = new Scoreboard();
   static final Random random = new Random();

   public GameCycle() {
       setPreferredSize(dimension); // size of game screen
       setBackground(background);
       setFocusable(true);
       initialBoard();
       selectShape(); //gets a tetrominoe to initial position

       addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) { 
        	// we set the game in Game Over mode from starting. So, its not only when we finish a game, it can be used when a game is never started either.
        	   if (scoreboard.gameOver()) {
                   newGame();
                   repaint();
               }
           }
       });

       addKeyListener(new KeyAdapter() {
    	   boolean dropDown;
    	   
    	   @Override
           public void keyReleased(KeyEvent event) {
               dropDown = false;
           }
    	   
           @Override
           public void keyPressed(KeyEvent event) {
               switch (event.getKeyCode()) {
                   case KeyEvent.VK_UP:
                           rotate(fallingShape); // rotates the shape clockwise
                       break;

                   case KeyEvent.VK_LEFT:
                       if (canMove(fallingShape, Direct.left)) { // one column left
                           move(Direct.left);
                       }
                       break;

                   case KeyEvent.VK_RIGHT:
                       if (canMove(fallingShape, Direct.right)) { // one column right
                           move(Direct.right);
                       }
                       break;
                       
                   case KeyEvent.VK_DOWN:
                	   if(canMove(fallingShape, Direct.down)) { // one row down
                		   move(Direct.down);
                	   }
                	   break;
                	   
                   case KeyEvent.VK_SPACE:
                       if (!dropDown) {
                           dropDown = true;
                           while (canMove(fallingShape, Direct.down)) { // if tetrominoe has a empty space to move down, it goes down. Else, it stops.
                               move(Direct.down);
                               repaint();
                           }
                           shapeHasLanded();
                       }
                       break;
               }
               if (scoreboard.gameOver()) {
                   return;
               }
               repaint();   
           }
       });
   }

   void selectShape() { // gets new initial shape (tetrominoe)
       fallingShapeRow = 1; // row coordinates for initial position
       fallingShapeColumn = 6; // column coordinates for initial position
       fallingShape = nextShape; // sets the falling shape as next
       Shape[] shapes = Shape.values();
       nextShape = shapes[random.nextInt(shapes.length)]; //gets a random shape and sets it as the next shape
      
       if (fallingShape != null)
    	   fallingShape.reset();
   }

   void addShape(Shape shape) {
       for (int[] pst : shape.position) {
           board[fallingShapeRow + pst[1]][fallingShapeColumn + pst[0]] = shape.ordinal();
       }
   }
   
   void shapeHasLanded() {
       addShape(fallingShape);
       if (fallingShapeRow < 2) { // if there is lest than 2 remaining rows, it sets the game as over, sets the high score and ends it.
           scoreboard.setGameOver();
           scoreboard.setHighScore();
           end();
       } else { // if there is any lines removes do the remove operation and adds point according to the removed line number
           scoreboard.addLines(removedLines());
       }
       selectShape();
   }
   
   void newGame() {
       end(); //stops the current game (ends)
       initialBoard(); // gets a clean game board
       selectShape(); // gets a new initial tetrominoe
       scoreboard.restartGame(); // restarts the game
       (fallingThread = new Thread(this)).start();
   }

   void end() {
       if (fallingThread != null) { // to end the game, we interrupt the falling command and chage it.
           Thread temp = fallingThread;
           fallingThread = null;
           temp.interrupt();
       }
   }

   void initialBoard() { // game's starting board
       for (int row = 0; row < countRows; row++) { // for scanning the rows
           Arrays.fill(board[row], empty);
           for (int column = 0; column < countColumns; column++) { //for scanning columns
               if (column == 0 || column == countColumns - 1 || row == countRows - 1) { //for specifying the borders
                   board[row][column] = border;
               }
           }
       }
   }

   @Override
   public void run() {

       while (Thread.currentThread() == fallingThread) { // while game still continues
          
    	   try {
               Thread.sleep(scoreboard.getSpeed()); // for stating the current tetrominoe's speed
           } 
    	   catch (InterruptedException e) {
               return;
           }

           if (!scoreboard.gameOver()) { // if the game is not ended
               if (canMove(fallingShape, Direct.down)) { //tetrominoes keep going down until get interrupted by other tetrominoes
                   move(Direct.down);
               } else { // if tetrominoes get interrupted they should be landed and this part of the code makes it happen
                   shapeHasLanded();
               }
               repaint();
           }
       }
   }

   void drawStartScreen(Graphics2D g) { // draws the game board and full game screen

       g.setColor(titleBackground);
       g.fill(title);
       g.fill(clickGuidance);
       g.setColor(text);
       
       g.setFont(mainFont);
       g.drawString("TETRIS", titleX, titleY);
      
       g.setFont(guidanceFont);
       g.drawString("click anywhere to start", clickX, clickY);
   }

   void drawSquare(Graphics2D g, int colors, int row, int column) {
       g.setColor(color[colors]);
       g.fillRect(leftMargin + column * blockSize, topMargin + row * blockSize,
               blockSize, blockSize); // it fills the tetrominoes with colors
      
       g.setColor(tetrominoeBorder);
       g.drawRect(leftMargin + column * blockSize, topMargin + row * blockSize,
               blockSize, blockSize); // it borders the tetrominoes with white
   }

   void drawUI(Graphics2D g) {
       // board background
       g.setColor(boardColor);
       g.fill(main);

       // for the blocks dropped in the game board
       for (int row = 0; row < countRows; row++) {
           for (int column = 0; column < countColumns; column++) {
               int idx = board[row][column];
               if (idx > empty)
                   drawSquare(g, idx, row, column);
           }
       }

       // the borders of the board, and the preview panel
       g.setColor(boardBorder);
       g.draw(main);
       g.draw(mainn);
       g.draw(preview);
       g.draw(scoreBoard);
       g.draw(scoreBoardd);
       g.draw(previeww);
       
       // positions, font, color, and text for scoreboard
       int scorex = scoreX;
       int scorey = scoreY;
       g.setColor(text);
       g.setFont(scoreFont);
       g.drawString(format("Level:     %6d", scoreboard.getLevel()), scorex, scorey);
       g.drawString(format("Lines:     %6d", scoreboard.getLines()), scorex, scorey + 30);
       g.drawString(format("Score:     %6d", scoreboard.getScore()), scorex, scorey + 60);
       g.drawString(format("High Score:%6d", scoreboard.getHighScore()), scorex, scorey + 90);
       
       // position for group members' names
       int namesx = namesX;
       int namesy = namesY;
       g.drawString(format("#RuntimeTerror         %6d", scoreboard.getLevel()), namesx, namesy);
       g.drawString(format("Ekin Çoşkun    		   %6d", scoreboard.getLevel()), namesx, namesy + 40);
       g.drawString(format("Egehan Daştan    	   %6d", scoreboard.getLevel()), namesx, namesy + 80);
       g.drawString(format("Berke Koçyiğitoğlu     %6d", scoreboard.getLevel()), namesx, namesy + 120);
       
       // positions(coordinates) for preview
       int minX = 0, minY = 0, maxX = 0, maxY = 0;
       for (int[] pst : nextShape.position) {
           minX = min(minX, pst[0]);
           minY = min(minY, pst[1]);
           maxX = max(maxX, pst[0]);
           maxY = max(maxY, pst[1]);
       }
       double px = previewX;
       double py = previewY;

       g.translate(px, py);
       for (int[] p : nextShape.shape) { // for deciding the next shape
           drawSquare(g, nextShape.ordinal(), p[1], p[0]);
       }
       g.translate(-px, -py);
   }

   void drawFallingShape(Graphics2D graphics) {
       int idx = fallingShape.ordinal(); // for drawing the next shape as current shape
       for (int[] p : fallingShape.position)
           drawSquare(graphics, idx, fallingShapeRow + p[1], fallingShapeColumn + p[0]);
   }

   @Override
   public void paintComponent(Graphics g) {
       super.paintComponent(g);
       Graphics2D graphics = (Graphics2D) g;
       graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
               RenderingHints.VALUE_ANTIALIAS_ON);
       drawUI(graphics);

       if (scoreboard.gameOver()) { // if the game is over, restart the game and print back the start screen
           drawStartScreen(graphics);
       } else { // while the game continues print the falling (current) shape on the screen
           drawFallingShape(graphics);
       }
   }

   void rotate(Shape shape) {
       if (shape == Shape.Square) // if the shape is square return as same
           return;

       for (int[] row : shape.position) { // creates a temporary position and then changes the position of the rest
           int tmp = row[1];
           row[1] = row[0];
           row[0] = -tmp;
       }
   }

   void move(Direct direction) {
       fallingShapeRow += direction.y; // for vertical movements (such as falling down)
       fallingShapeColumn += direction.x; // for horizontal movements (such as right - left moves)
   }

   boolean canMove(Shape shape, Direct direction) { 
       for (int[] pst : shape.position) { //for scanning the position of the shape
           int newColumn = fallingShapeColumn + direction.x + pst[0];
           int newRow = fallingShapeRow + direction.y + pst[1];
           
           	if (board[newRow][newColumn] != empty) { //if the tetrominoe is in a blank space it can move, if else it cannot.
               return false;
           }
       }
       return true;
   }
   
   int removedLines() {
       int count = 0; //for finding the number of the rows we removed -- for points
       
       for (int row = 0; row < countRows -1; row++) { // for scanning all of the rows in the game
          
    	   for (int column = 1; column < countColumns - 1; column++) { // for scanning all of the columns in the game
              
        	   //if the specific row's any square is empty, do nothing (do not remove the line)
        	   if (board[row][column] == empty) {
                   break;
               }
        	  
        	   /* we have 12 columns, so if the number of the columns equal to 12 in the selected row,
        	   we remove the line*/
               if (column == countColumns - 2) {
                   count++;
                   removeLine(row);
               }
           }
       }
       return count;
   }

   void removeLine(int line) {
	   
	   //for scanning a specific row (line) to see if it is empty or not
       for (int column = 0; column < countColumns; column++) {
    	   board[line][column] = empty;
       }
      
       // for falling rows down after removing line
       for (int column = 0; column < countColumns; column++) {
           for (int row = line; row > 0; row--) {
               board[row][column] = board[row - 1][column];
           }
       }
   }
}

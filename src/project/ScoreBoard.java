package project;

class Scoreboard {
    static final int MAXLEVEL = 10;

    private int level;
    private int lines;
    private int score;
    private int highScore;
    private boolean gameOver = true;

    void restartGame() {
        level = 0;
        lines = 0;
        score = 0;
        setHighScore();
        gameOver = false;
    }

    void setGameOver() {
        gameOver = true;
    }

    boolean gameOver() {
        return gameOver;
    }

    void setHighScore() {
        if (score > highScore) {
            highScore = score;
        }
    }

    int getSpeed() {
    	// tetrominoes goes faster as the level increases according to this method
    	if (level == 0) {
     	   return 650;
    	}
        else if (level == 1) {
     	   return 600;
        }
        else if (level == 2) {
     	   return 550;
        }
        else if (level == 3) {
     	   return 500;
        }
        else if (level == 4) {
     	   return 400;
        }
        else if (level == 5) {
     	   return 350;
        }
        else if (level == 6) {
     	   return 300;
        }
        else if (level == 7) {
     	   return 250;
        }
        else if (level == 8) {
     	   return 200;
        }
        else if (level == 9) {
     	   return 150;
        }
        else if (level == 10) {
     	   return 100;
        }
        else {
     	   return 100;
        }
    }

    void addLevel() {
        if (level < MAXLEVEL) {
            level++;
        }
    }
    
    int getLevel() {
        return level;
    }

    int getLines() {
        return lines;
    }
    int getScore() {
        return score;
    }

    int getHighScore() {
        return highScore;
    }
    
    void addScore(int score) {
        this.score += score;
    }

    void addLines(int line) {
    	// point of 1 line equals line * 10 = 10, more than 1 line equal line * 10 + line * 10 until 4 lines (max lines possible)
		if(line == 1) {
			//10
			addScore(10);
		}
		if(line == 2) {
			//20 + 20
			addScore(40);
		}
		if(line == 3) {
			//30 + 30
			 addScore(60);
		}
		if(line == 4) {
			//40 + 40
			addScore(80);
		}
 
        lines += line;
        if (lines >= 10) {
            addLevel();
            lines = 0;
        }
    }
}
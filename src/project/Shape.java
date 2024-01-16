package project;

enum Shape {
    IShape(new int[][]{{0, -1}, {0, 0}, {0, 1}, {0, 2}}),
    JShape(new int[][]{{1, -1}, {0, -1}, {0, 0}, {0, 1}}),
    LShape(new int[][]{{-1, -1}, {0, -1}, {0, 0}, {0, 1}}),
    Square(new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}}),
    SShape(new int[][]{{0, -1}, {0, 0}, {1, 0}, {1, 1}}),
    TShape(new int[][]{{-1, 0}, {0, 0}, {1, 0}, {0, 1}}),
    ZShape(new int[][]{{0, -1}, {0, 0}, {-1, 0}, {-1, 1}});
 
	final int[][] position, shape;
	
    private Shape(int[][] shape) {
        this.shape = shape;
        position = new int[4][2];
        reset();
    }
 
    void reset() {
        for (int i = 0; i < position.length; i++) {
            position[i] = shape[i].clone();
        }
    }
}
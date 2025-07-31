import java.util.*;

public class Board{

    // Attributes of the board
    private final int WIDTH;
    private final int HEIGHT;
    private final int MINES;
    private boolean lost = false;
    private boolean won = false;
    private boolean first = true;
    private int flags = 0;

    // Array of the layout of the board
    private ArrayList<ArrayList<Integer>> board = new ArrayList<>();

    // List of what to display on the board
    // 0 means closed, 1 means open, -1 means flagged, -2 means the mine that lost the game
    private ArrayList<ArrayList<Integer>> display = new ArrayList<>(); 

    // Constants for the board display options
    public static final int OPEN = 1;
    public static final int CLOSED = 0;
    public static final int FLAGGED = -1;
    public static final int LOST = -2;
    public static final int NO_MINE = -3;
    public static final int MINE = -1;

    // Constant values for board sizes
    public static final int BEGINNER_HEIGHT = 8;
    public static final int BEGINNER_WIDTH = 8;
    public static final int BEGINNER_MINES = 10;
    public static final int INTERMEDIATE_HEIGHT = 16;
    public static final int INTERMEDIATE_WIDTH = 16;
    public static final int INTERMEDIATE_MINES = 40;
    public static final int EXPERT_HEIGHT = 16;
    public static final int EXPERT_WIDTH = 30;
    public static final int EXPERT_MINES = 99;


    // Getter for cell of the board
    public int get(int row, int column){
        return board.get(row).get(column);
    }

    // Getter for a cell of the display
    public int getDisplay(int row, int column) {
        return display.get(row).get(column);
    }
 
    public ArrayList<ArrayList<Integer>> getDisplay(){
        return display;
    }

    public int getHeight(){
        return HEIGHT;
    }

    public int getWidth(){
        return WIDTH;
    }

    public boolean isWon() {
        return won;
    }

    public boolean isLost() {
        return lost;
    }

    // Constructor to get the values for the board
    public Board(int width, int height, int mines){
        this.WIDTH = width;
        this.HEIGHT = height;
        this.MINES = mines;

        createBoard();
        createDisplay();
        setUpNumbers();
    }

    // Create the board
    public void createBoard(){
        board = new ArrayList<>(); // Make sure the board is empty before creating a new one

        ArrayList<int[]> freeSpaces = new ArrayList<>();
        // Create a list of possible rooms + make all the board safe
        for(int i = 0; i < HEIGHT; i++){
            board.add(new ArrayList<>());
            for(int j = 0; j < WIDTH; j++){
                board.get(i).add(-2);
                freeSpaces.add(new int[]{i,j});
            }
        }

        // Select a random room from that list and place a mine there
        for(int i = 0; i < MINES; i++){
            Random random = new Random();
            int index = random.nextInt(freeSpaces.size());

            // Get the coordinate
            int[] coords = freeSpaces.get(index);
            // Remove the coordinate from the list
            freeSpaces.remove(index);

            // Set the cell to be a mine (-1)
            board.get(coords[0]).set(coords[1], -1);
        }
    }

    // Method to set up the display board
    public void createDisplay(){
        for(int i = 0; i < HEIGHT; i++){
            display.add(new ArrayList<>());

            // Start with all cells hidden
            for(int j = 0; j < WIDTH; j++){
                display.get(i).add(CLOSED);
            }
        }
    }

    // Open all the cells that contain mines when the game ends
    public void openMinesOnEnd() {
        for(int i = 0; i < HEIGHT; i++){
            for(int j = 0; j < WIDTH; j++){
                // If a cell contains a mine open it (unless it is the cell that lost the game)
                if(board.get(i).get(j) == MINE && display.get(i).get(j) == CLOSED){
                    // Set it to be open on the display board
                    display.get(i).set(j, OPEN);
                }
                // If a mine is flagged but does not contain a mine
                if(board.get(i).get(j) != MINE && display.get(i).get(j) == FLAGGED) {
                    display.get(i).set(j, NO_MINE);
                }
            }
        }
    }

    // Method to count the number of -1s surrounding a cell
    public void setUpNumbers(){
        for(int i = 0; i < HEIGHT; i++){
            for(int j = 0; j < WIDTH; j++){
                // If a cell doesnt contain a mine
                if(board.get(i).get(j) != -1){
                    // Get the number of mines surrounding that cell
                    board.get(i).set(j, checkCell(i, j));
                }
            }
        }
    }

    // Method to check the number of mines surrounding a cell
    public int checkCell(int row, int column){
        int mineCount = 0;
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                int[] coords = new int[]{row+i, column+j};
                // If the coords are valid then check if the cell contains a mine
                if(checkValid(coords) && board.get(coords[0]).get(coords[1]) == -1){
                    mineCount++;
                }
            }
        }

        return mineCount;
    }

    // Method to check if the coordinates are valid
    public boolean checkValid(int[] coords){
        // Returns false if the coordinates go off the side of the board
        return coords[0] >= 0 && coords[1] >= 0 && coords[0] < HEIGHT && coords[1] < WIDTH;
    }

    // Method to check if the game has been won
    public boolean checkWon(){
        for(int i = 0; i < HEIGHT; i++){
            for(int j = 0; j < WIDTH; j++){
                // If there is an unopen non-mine cell return false
                if(board.get(i).get(j) != -1 && display.get(i).get(j) == CLOSED){
                    return false;
                }
            }
        }

        // Set won to be true
        won = true;
        return true;
    }

    // Functions to handle all possible forms of interaction

    // Return return value indicates state of the game: lost (-1), won (1), continue (0)
    public int leftClick(int row, int column) {
        // If the cell is open or flagged, or the game is won/lost do nothing
        if (lost || won || display.get(row).get(column) == OPEN || display.get(row).get(column) == FLAGGED) return 0;

        // If the cell is a mine and it is not the first click
        if (board.get(row).get(column) == MINE && !first) {
            // Mark is as the cell that lost
            display.get(row).set(column, LOST);
            // Open all other mine cells
            openMinesOnEnd();
            // Set lost to true
            lost = true;
            // Return -1 to indicate that the game has been lost
            return -1;
        }

        // If it is the first click and the cell is a mine them move the mine
        if(first && board.get(row).get(column) == MINE) moveMine(row, column);
        if(first) first = false; // Set first to be false after the first click

        // Open the cell
        // Mark the cell as open
        display.get(row).set(column, OPEN);
        // If the cell is a zero then cascade the surrounding cells
        // if(board.get(row).get(column) == 0) cascade(row, column);
        if(board.get(row).get(column) == 0) cascadeCell(row, column);
        
        // Check if that game has been won and return 1 if it has been
        if(checkWon()) return 1;
        // Otherwise return 0 to indicate that the game continues on
        return 0;
    }

    public int rightClick(int row, int column) {

        // If the cell is open or the game is won/lost do nothing
        if (lost || won || display.get(row).get(column) == OPEN) return flags;

        // Mark the cell

        // If the cell is marked unmark it
        if(display.get(row).get(column) == FLAGGED) {
            display.get(row).set(column, CLOSED); // Set the cell to be closed
            flags--; // Decement the number of flags
        }
        // If the cell it unmarked mark it
        else if(display.get(row).get(column) == CLOSED) {
            display.get(row).set(column, FLAGGED); // Set the cell to be flagged
            flags++; // Increment the number of flags
        }

        // Return the number of flags total
        return flags;
    }

    // Return return value indicates state of the game: lost (-1), won (1), continue (0)
    public int doubleClick(int row, int column) {
        // If the cell is closed or flagged or the game is won/lost do nothing
        if (lost || won || display.get(row).get(column) == CLOSED || display.get(row).get(column) == FLAGGED) return 0;


        // Count the number of flags in the vicinity of the cell
        int flagCount = countFlags(row, column);

        // If it is not the correct number do nothing (and continue the game)
        if (flagCount != board.get(row).get(column)) return 0;
        // If it is the correct number cascade the cell (and handle if the game was lost when cascading)
        if (!cascadeCell(row, column)) {
            // Open all other mine cells
            openMinesOnEnd();
            // Set lost to true
            lost = true;
            // Return -1 to indicate that the game has been lost
            return -1;
        }

        // Check if that game has been won and return 1 if it has been
        if(checkWon()) return 1;
        // If the game was not lost or won return 0
        return 0;
    }

    public int countFlags(int row, int column) {
        int flagCount = 0;

        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                int[] coords = new int[]{row+i, column+j};
                // If the coords are valid then check if the cell is marked with a flag
                if(checkValid(coords) && display.get(coords[0]).get(coords[1]) == FLAGGED)
                    flagCount++;
                
            }
        }

        return flagCount;
    }

    public boolean cascadeCell(int row, int column) {

        boolean lostGame = false;

        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                // Do not check the centre cell
                int[] coords = new int[]{row+i, column+j};
                if(!(i == 0 && j == 0) && checkValid(coords)){

                    // If the cell is open or flagged then skip it
                    if (display.get(coords[0]).get(coords[1]) == OPEN || display.get(coords[0]).get(coords[1]) == FLAGGED
                        || display.get(coords[0]).get(coords[1]) == LOST) continue;

                    // If the cell contains a mine then mark it as the mine that lost the game
                    if (board.get(coords[0]).get(coords[1]) == MINE) {
                        display.get(coords[0]).set(coords[1], LOST);
                        // Set the game to be lost
                        lostGame = true;
                        // Open the next cell
                        continue;
                    }

                    // Open the cell
                    display.get(coords[0]).set(coords[1], OPEN);

                    // If the cell contains a 0 then cascade the cell as well
                    if(board.get(coords[0]).get(coords[1]) == 0) {
                        boolean res = cascadeCell(coords[0], coords[1]);
                        // If cascading the cell lost the game then update the lost game value
                        if (!res) lostGame = true;
                    }
                }
            }
        }

        // Return whether the game was lost
        return !lostGame;
    }

    // Make sure the first click is not a mine (this can be optomised to only recalculate the affected sqaures)
    public void moveMine(int row, int column){
        boolean placed = false;

        // If the cell is a mine then move the mine to the first non-mine cell
        for(int i = 0; i < HEIGHT; i++){
            for(int j = 0; j < WIDTH; j++){
                // If the cell is not a mine them set it to be a mine
                if(board.get(i).get(j) != -1) {
                    board.get(i).set(j, MINE);
                    placed = true;
                    break; // Exit the loop
                }
            }

            // Exit both loops after moving the mine
            if(placed) break;
        }

        // Set the current cell to not be a mine if the mine was successfully moved
        if(placed) board.get(row).set(column, 0);

        // Recalculate the board
        setUpNumbers();
    }
}
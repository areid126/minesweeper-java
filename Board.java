import java.util.*;
public class Board{

    public static void main(String[] args){
        Board board = new Board(8, 8, 10); // Beginner
        Board board2 = new Board(16, 16, 40); // Intermediate
        Board board3 = new Board(30, 16, 99); // Expert
        board.print();
        board2.print();
        board3.print();
    }

    // Attributes of the board
    private final int WIDTH;
    private final int HEIGHT;
    private final int MINES;
    private boolean lost = false;
    private boolean won = false;
    private ArrayList<ArrayList<Integer>> board = new ArrayList<>();
    private ArrayList<ArrayList<Boolean>> display = new ArrayList<>(); // List of all the open squares

    // Getter for cell of the board
    public int get(int row, int column){
        return board.get(row).get(column);
    }

    public ArrayList<ArrayList<Boolean>> getDisplay(){
        return display;
    }

    public int getHeight(){
        return HEIGHT;
    }

    public int getWidth(){
        return WIDTH;
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

            // Set the square to be a mine (-1)
            board.get(coords[0]).set(coords[1], -1);
        }
    }

    // Method to set up the display board
    public void createDisplay(){
        for(int i = 0; i < HEIGHT; i++){
            display.add(new ArrayList<>());
            for(int j = 0; j < WIDTH; j++){
                display.get(i).add(false);
            }
        }
    }

    // Method to open a specific square of the board
    public void open(int row, int column){
        display.get(row).set(column, true); // Set the clicked square to be open

        // Perform the checks on the number in that square
        int value = board.get(row).get(column);
        if(value == -1){
            // The player has clicked a bomb and so the game ends
            lost = true;
            System.out.println("The game has been lost");
            // need to open the whole game to show the game has been lost
        }
        if(value == 0){
            // Cascade the squares so that everything opens
            cascade(row, column);
        }
        // Does not need to do anything else if any other square is clicked
    }

    // Version of open that takes the coords as an array
    public void open(int[] coords){
        open(coords[0], coords[1]);
    }

    // Method to cascade clicking a 0
    public void cascade(int row, int column){
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                // Do not check the centre cell
                int[] coords = new int[]{row+i, column+j};
                if(!(i == 0 && j == 0) && checkValid(coords)){
                    // If the square contains a 0 then cascade that square as well
                    if(board.get(coords[0]).get(coords[1]) == 0 && !display.get(coords[0]).get(coords[1])){
                        display.get(coords[0]).set(coords[1], true);
                        cascade(coords[0], coords[1]);
                    }
                    display.get(coords[0]).set(coords[1], true);
                }
            }
        }
    }


    // Prints the board to the terminal
    public void print(){
        for(int i = 0; i < HEIGHT; i++){
            for(int j = 0; j < WIDTH; j++){
                System.out.print(board.get(i).get(j) + "    ");
            }
            System.out.println();
        }
        // System.out.println(board);
    }

    // Method to count the number of -1s surrounding a square
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
                // If the coords are valid then check if the cell contains a bomb
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
                // If there is an unopen non-bomb square return false
                if(board.get(i).get(j) != -1 && !display.get(i).get(j)){
                    return false;
                }
            }
        }
        System.out.println("The game has been won");
        return true;
    }

    // Make sure the first click is not a bomb
    public void firstClick(){
        
    }
}
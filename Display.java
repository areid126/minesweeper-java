import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Display extends JFrame {

    public static void main(String[] args){
        new Display();
    }
    
    // Attributes that define the board
    private int width = Board.BEGINNER_WIDTH;
    private int height = Board.BEGINNER_HEIGHT;
    private int mines = Board.BEGINNER_MINES;
    private Board board = new Board(width, height, mines);
    private ArrayList<ArrayList<GridButton>> buttons = new ArrayList<>();

    // Attributes for GUI elements
    private JLabel restart;
    private JLabel mineCount;
    private JPanel gridPanel;
    private JPanel mainPanel;

    // Attributes for managing operation of the game
    private boolean rightClick = false;
    private boolean leftClick = false;
    private int currentRow = 0;
    private int currentColumn = 0;
    private int flags = 0;
    
    // Attributes for the timer
    private Timer timer;
    private long startTime;
    private JLabel timerLabel;

    // Constant values for different boards
    private static final int BEGINNER = 1;
    private static final int INTERMEDIATE = 2;
    private static final int EXPERT = 3;

    // Constant values for use in styling
    private static final BevelBorder INNER_BORDER = new BevelBorder(BevelBorder.LOWERED, Colours.BORDER_OUTER, Colours.BORDER_OUTER, Colours.BORDER_SHADOW, Colours.BORDER_SHADOW);
    private static final BevelBorder OUTER_BORDER = new BevelBorder(BevelBorder.RAISED, Colours.BORDER_OUTER, Colours.BORDER_OUTER, Colours.BORDER_SHADOW, Colours.BORDER_SHADOW);

    // Constructor to set up the display
    public Display() {
        super(); // Call the constructor for the superclass
        setUpFrame(); // Set up the board
    }


    // Method to set up the board
    public void setUpFrame(){

        // Set up the menu bar
        setUpMenuBar();

        // Create the main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Colours.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(OUTER_BORDER, new EmptyBorder(10, 10, 10, 10)));

        // Set up the header
        setUpHeader();
        // Create the grid for the board
        setUpGameGrid();

        // Add the main panel to the frame
        add(mainPanel);

        // Format the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    // Method for setting up the grid of buttons
    public void setUpGameGrid() {
        // BevelBorder innerBorder = new BevelBorder(BevelBorder.LOWERED, Colours.BORDER_OUTER, Colours.BORDER_OUTER, Colours.BORDER_SHADOW, Colours.BORDER_SHADOW);

        // Create the grid for the board
        gridPanel = new JPanel();
        gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.Y_AXIS));
        buttons = new ArrayList<>();
        
        for(int i = 0; i < board.getHeight(); i++){
            buttons.add(new ArrayList<>());
            
            // Create a new row for the buttons
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            gridPanel.add(row);

            // Create all the buttons for the row
            for(int j = 0; j < board.getWidth(); j++){

                GridButton button = setUpGridButton(i, j); // Create the button
                buttons.get(i).add(button); // Add the buttons to the list of buttons
                row.add(button); // Add the button to the row on the display
            }
        }

        // Add the correct border to the panel
        gridPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 0, 0, 0), INNER_BORDER));
        gridPanel.setBackground(Colours.BACKGROUND);
        mainPanel.add(gridPanel);
    }

    // Method to create a gridbutton
    public GridButton setUpGridButton(int row, int column) {
        
        // Create the button
        GridButton button = new GridButton(row, column, board.get(row, column), board);

        // Add an event listener to the button
        button.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e){
                // Do nothing
            }

            @Override
            public void mouseEntered(MouseEvent e){
                // Track what cell the mouse it currently in
                currentRow = button.getRow();
                currentColumn = button.getColumn();
            }

            @Override
            public void mouseExited(MouseEvent e){
                // Do nothing
            }

            @Override
            public void mousePressed(MouseEvent e){
                // Only register click events when the game is not won or lost
                if (board.isLost() || board.isWon()) return;

                setResetIcon("img/shocked.png", "O");

                // If the left click is pressed then log it
                if(e.getButton() == MouseEvent.BUTTON1) {
                    leftClick = true;
                }

                // If the right mouse button is clicked then mark the cell
                if(e.getButton() == MouseEvent.BUTTON3){
                    rightClick = true;
                    
                    board.rightClick(currentRow, currentColumn);
                    // Update the display after clicking a button
                    updateDisplay();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e){
                // Only register click events when the game is not won or lost
                if (board.isLost() || board.isWon()) return;

                setResetIcon("img/smile.png", "S");

                // If the right mouse button is released and the left is not being pressed do nothing
                if(e.getButton() == MouseEvent.BUTTON3 && !leftClick) {
                    rightClick = false;
                    return;
                }

                int res = 0;

                // If the left click is released and the cell is not marked
                if(e.getButton() == MouseEvent.BUTTON1){
                    leftClick = false;

                    // If the right mouse button is also being clicked cascade the cell
                    if (rightClick) res = board.doubleClick(currentRow, currentColumn);
                    

                    // Otherwise open the square normally
                    else {
                        // Start the timer on the first left click
                        if (!timer.isRunning()) {
                            startTime = System.currentTimeMillis();
                            timer.start();
                        }

                        res = board.leftClick(currentRow, currentColumn);
                    }
                } 
                
                // If the right click is released
                if(e.getButton() == MouseEvent.BUTTON3){
                    rightClick = false;

                    // If the left mouse button was also being clicked at the same time cascade the square
                    if (leftClick) res = board.doubleClick(currentRow, currentColumn);
                }

                // Handle if the game was lost
                if (res == -1) handleGameEnd("img/dead.png", "L");
                // Handle if the game was won
                if (res == 1) handleGameEnd("img/sunglasses.png", "W");
                
                // Update the display after clicking a button
                updateDisplay(); 

            }
        });

        // Set the appearance of the button
        button.setAppearance();

        // Return the button
        return button;
    }

    // Method to set up the menu bar
    public void setUpMenuBar() {
        
        // Create a menu for the application
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");

        // Create the beginner board size option
        JMenuItem beginner = new JMenuItem("Beginner");
        // Set up the action listener
        beginner.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Change the size of the board
                changeBoardSize(BEGINNER);
            }
        });
        menu.add(beginner);


        // Create the intermediate size option
        JMenuItem intermediate = new JMenuItem("Intermediate");
        // Set up the action listener
        intermediate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Change the size of the board
                changeBoardSize(INTERMEDIATE);
            }
        });
        menu.add(intermediate);

        // Create the expert size option
        JMenuItem expert = new JMenuItem("Expert");
        // Set up the action listener
        expert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Change the size of the board
                changeBoardSize(EXPERT);
            }
        });
        menu.add(expert);
        
        // Add the menu to the menubar
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    // Method to set up the game header bar
    public void setUpHeader() {
        
        // Create the header panel
        JPanel headerPanel = new JPanel();
        // Format the header panel
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(INNER_BORDER, new EmptyBorder(5, 5, 5, 5)));
        headerPanel.setBackground(Colours.BACKGROUND);
        // Add the header panel to the main panel
        mainPanel.add(headerPanel);

        // Set up the different header fields
        mineCount = setUpCounterFields(mines - flags);
        setUpRestartButton();
        setUpTimer();

        // Add the fields to the header panel
        headerPanel.add(mineCount);
        headerPanel.add(Box.createHorizontalGlue());
        headerPanel.add(restart);
        headerPanel.add(Box.createHorizontalGlue());
        headerPanel.add(timerLabel);
    }

    // Method to set up the restart button
    public void setUpRestartButton() {
        // Layout the restart button
        restart = new JLabel();
        
        // Layout the button
        restart.setBorder(OUTER_BORDER);
        restart.setBackground(Colours.BACKGROUND);
        restart.setOpaque(true);
        restart.setForeground(Colours.YELLOW);
        restart.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        restart.setHorizontalAlignment(SwingConstants.CENTER);
        restart.setVerticalAlignment(SwingConstants.CENTER);
        restart.setPreferredSize(new Dimension(30, 30));
        
        // Add the icon to the button
        setResetIcon("img/smile.png", "S");
        
        // Add a mouse listener to the restart button
        restart.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e){
                // Do nothing
            }

            @Override
            public void mouseEntered(MouseEvent e){
                // Do nothing
            }

            @Override
            public void mouseExited(MouseEvent e){
                // Do nothing
            }

            @Override
            public void mousePressed(MouseEvent e){
                // Do nothing
            }

            @Override
            public void mouseReleased(MouseEvent e){
                // Restart the game
                handleRestart();
            }
        });
    }

    // Method to set up the counter fields in the header
    public JLabel setUpCounterFields(int intialValue) {
        JLabel counter = new JLabel();
        counter.setText(formatInt(intialValue));
        counter.setForeground(Colours.RED);
        counter.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 20));
        counter.setBackground(Color.BLACK);
        counter.setOpaque(true);
        counter.setBorder(INNER_BORDER);
        return counter;
    }

    // Method to set up the timer
    public void setUpTimer() {
        timerLabel = setUpCounterFields(0);

        // Set up the timer that the timer field operates around
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the amount of time the timer has been running for
                long runningTime = System.currentTimeMillis() - startTime;
                
                // Get the number of seconds as an integer
                int seconds = 0;
                try {
                    seconds = Math.toIntExact(runningTime / 1000);
                } catch (ArithmeticException exception) {
                    // If the integer overflows take the maximum display value
                    seconds = 999; 
                }
                
                // Update the display with the number of seconds
                timerLabel.setText(formatInt(seconds));
            }
        });
    }

    // Method to handle the end of the game
    public void handleGameEnd(String imgPath, String altText) {
        // Set the reset button icon
        setResetIcon(imgPath, altText);
        // Stop the timer
        timer.stop();
    }

    // Method to handle restarting the game
    public void handleRestart() {
        // Change the board object
        board = new Board(width, height, mines);

        // Update the board associated with the display icons
        for(int i = 0; i < buttons.size(); i++){
            for(int j = 0; j < buttons.get(i).size(); j++){
                GridButton button = buttons.get(i).get(j);
                // Update the appearance of every button (if it needs to be updated)
                button.setGame(board);
                if(button.checkUpdate()) button.setAppearance();
            }
        }

        // Change the reset icon to be the default icon
        setResetIcon("img/smile.png", "S");

        // Set the header back to the default values
        resetHeader();
        
        // Update the display
        revalidate(); 
    }

    // Method to handle changing board size
    public void changeBoardSize(int size) {

        // Update the size variables and create the new board
        if (size == BEGINNER) { // Set up beginner size board
            width = Board.BEGINNER_WIDTH; height = Board.BEGINNER_HEIGHT; mines = Board.BEGINNER_MINES;
        }
        else if (size == INTERMEDIATE) { // Set up intermediate size board
            width = Board.INTERMEDIATE_WIDTH; height = Board.INTERMEDIATE_HEIGHT; mines = Board.INTERMEDIATE_MINES;
        }
        else if (size == EXPERT) { // Set up expect size board
            width = Board.EXPERT_WIDTH; height = Board.EXPERT_HEIGHT; mines = Board.EXPERT_MINES;
        }
        else return; // Do nothing if the board is not a defined size

        // Create the new board
        board = new Board(width, height, mines);

        // Remove the old panel from the frame
        mainPanel.remove(gridPanel);

        // Set up the panel again, now for the new game
        setUpGameGrid();

        // Set the header back to the default values
        resetHeader();

        // Update the display
        pack();
        updateDisplay();
    }

    // Method to reset the header fields (timer, flag count)
    public void resetHeader() {
        // Clear the timer
        timer.stop();
        timerLabel.setText(formatInt(0)); // Clear the displayed time

        // Clear the mine counter
        flags = 0;
        mineCount.setText(formatInt(mines - flags));
    }

    // Method to set the icon of the reset button
    public void setResetIcon(String icon, String altText) {
        try {
            BufferedImage buttonIcon = ImageIO.read(new File(icon));
            restart.setIcon(new ImageIcon(buttonIcon.getScaledInstance(25, -1, Image.SCALE_DEFAULT))); // Set the icon on the button
            restart.setText("");
        } catch (IOException e) {
            // If there is no icon image then set text instead
            restart.setIcon(null);
            restart.setText(altText);
        }
    }

    // Method to format an integer for string printing for the counters
    public String formatInt(int num) {
        if (num > 999) num = 999; // Never display a number greater than 999
        if (num < -99) num = -99; // Never display a number less than -99

        // If the number is positive format it to have three 0s
        if (num >= 0) return String.format("%03d", num);

        // If the number is negative then change the width of the minus sign and format it to two digits
        else return "–" + String.format("%02d", Math.abs(num));
    }

    // Method to update the display
    public void updateDisplay(){
        // Update the appearance of the grid buttons
        for(int i = 0; i < board.getDisplay().size(); i++){
            for(int j = 0; j < board.getDisplay().get(i).size(); j++){
                // Update the appearance of buttons that need their appearance updated
                if(buttons.get(i).get(j).checkUpdate()) buttons.get(i).get(j).setAppearance();
            }
        }

        // Update the number of mines unflagged
        mineCount.setText(formatInt(mines - flags));

        // Reload the display
        revalidate(); 
    }
}


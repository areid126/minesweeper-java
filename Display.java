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

public class Display {

    public static void main(String[] args){
        Display display = new Display();
        display.setUpFrame();
    }

    // Create the frame to contain the board
    private JFrame frame = new JFrame();
    // Variables that define board size
    private int width = Board.BEGINNER_WIDTH;
    private int height = Board.BEGINNER_HEIGHT;
    private int mines = Board.BEGINNER_MINES;
    private Board board = new Board(width, height, mines);
    private ArrayList<ArrayList<GridButton>> buttons = new ArrayList<>();
    private JLabel restart = new JLabel();
    private JLabel mineCount = new JLabel();
    private JPanel panel = new JPanel();
    private JPanel mainPanel = new JPanel();
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

    // Method to set up the board
    public void setUpFrame(){

        // Create a menu for the application
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");
        JMenuItem beginner = new JMenuItem("Beginner");
        JMenuItem intermediate = new JMenuItem("Intermediate");
        JMenuItem expert = new JMenuItem("Expert");
        menuBar.add(menu);
        menu.add(beginner);
        menu.add(intermediate);
        menu.add(expert);
        frame.setJMenuBar(menuBar);

        // Set up the individual menu options
        beginner.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Change the size of the board
                changeBoardSize(BEGINNER);
            }
        });

        intermediate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Change the size of the board
                changeBoardSize(INTERMEDIATE);
            }
        });

        expert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Change the size of the board
                changeBoardSize(EXPERT);
            }
        });



        // Create the two types of board
        BevelBorder innerBorder = new BevelBorder(BevelBorder.LOWERED, Colours.unopenedTop, Colours.unopenedTop, Colours.unopenedBottom, Colours.unopenedBottom);
        BevelBorder outerBorder = new BevelBorder(BevelBorder.RAISED, Colours.unopenedTop, Colours.unopenedTop, Colours.unopenedBottom, Colours.unopenedBottom);

        // Create the main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Colours.unopened);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, new EmptyBorder(10, 10, 10, 10)));

        // Create the header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(innerBorder, new EmptyBorder(5, 5, 5, 5)));
        headerPanel.setBackground(Colours.unopened);
        mainPanel.add(headerPanel);

        // Add the fields needed for the header
        mineCount.setText(formatInt(mines - flags));
        mineCount.setForeground(Colours.counter);
        // Bitstream Vera Sans Mono
        mineCount.setFont(new Font("monospace", Font.BOLD, 20));
        mineCount.setBackground(Color.BLACK);
        mineCount.setOpaque(true);
        mineCount.setBorder(innerBorder);

        // Layout the restart button
        restart = new JLabel();
        // Layout the button
        restart.setBorder(outerBorder);
        restart.setBackground(Colours.unopened);
        restart.setOpaque(true);
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


        timerLabel = new JLabel(formatInt(0));
        timerLabel.setForeground(Colours.counter);
        timerLabel.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 20));
        timerLabel.setBackground(Color.BLACK);
        timerLabel.setOpaque(true);
        timerLabel.setBorder(innerBorder);

        // Set up the timer
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

        // Add the fields to the header panel
        headerPanel.add(mineCount);
        headerPanel.add(Box.createHorizontalGlue());
        headerPanel.add(restart);
        headerPanel.add(Box.createHorizontalGlue());
        headerPanel.add(timerLabel);

        // Create the grid for the board
        setUpGameGrid();
        frame.add(mainPanel);

        // Format the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // Method for setting up the grid of buttons
    public void setUpGameGrid() {
        BevelBorder innerBorder = new BevelBorder(BevelBorder.LOWERED, Colours.unopenedTop, Colours.unopenedTop, Colours.unopenedBottom, Colours.unopenedBottom);

        // Create the grid for the board
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        buttons = new ArrayList<>();
        
        for(int i = 0; i < board.getHeight(); i++){
            buttons.add(new ArrayList<>());
            // Create a new row for the buttons
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            panel.add(row);
            for(int j = 0; j < board.getWidth(); j++){
                
                GridButton button = new GridButton(i, j, board.get(i, j), board);
                button.setPreferredSize(new Dimension(20, 20));
                button.setMinimumSize(new Dimension(20, 20));
                button.setMaximumSize(new Dimension(20, 20));
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
                        if (res == -1) handleLoss();
                        // Handle if the game was won
                        if (res == 1) handleWin();
                        
                        // Update the display after clicking a button
                        updateDisplay(); 

                    }
                });
                button.setAppearance(); // Set the appearance of the button
                buttons.get(i).add(button); // add the buttons to the list of buttons
                row.add(button);
            }
        }

        // Add the correct border to the panel
        panel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 0, 0, 0), innerBorder));
        panel.setBackground(Colours.unopened);
        mainPanel.add(panel);
    }

    // Method to handle losing the game
    public void handleLoss() {
        // Set the reset button icon
        setResetIcon("img/dead.png", "L");
        // Stop the timer
        timer.stop();
    }

    // Method to handle winning the game
    public void handleWin() {
        // Set the reset button icon
        setResetIcon("img/sunglasses.png", "W");
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
                // Update the appearance of every button
                buttons.get(i).get(j).setGame(board);
                buttons.get(i).get(j).setAppearance();
            }
        }

        // Change the reset icon to be the default icon
        setResetIcon("img/smile.png", "S");
        
        // Reset the timer
        timer.stop();
        timerLabel.setText(formatInt(0)); // Clear the displayed time
        
        // Reset the flag counter
        flags = 0;
        mineCount.setText(formatInt(mines - flags));
        
        // Update the display
        frame.revalidate(); 
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
        mainPanel.remove(panel);

        // Set up the panel again, now for the new game
        setUpGameGrid();

        // Clear the timer
        timer.stop();
        timerLabel.setText(formatInt(0)); // Clear the displayed time

        // Clear the mine counter
        flags = 0;
        mineCount.setText(formatInt(mines - flags));

        // Update the display
        frame.pack();
        updateDisplay();
    }

    // Method to set the icon of the reset button
    public void setResetIcon(String icon, String altText) {
        try {
            BufferedImage buttonIcon = ImageIO.read(new File(icon));
            restart.setIcon(new ImageIcon(buttonIcon.getScaledInstance(25, -1, Image.SCALE_DEFAULT))); // Set the icon on the button

        } catch (IOException e) {
            restart.setHorizontalAlignment(SwingConstants.CENTER);
            restart.setVerticalAlignment(SwingConstants.CENTER);
            restart.setPreferredSize(new Dimension(30, 30));
            restart.setText(altText);
            restart.setForeground(Colours.YELLOW);
            restart.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
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
        frame.revalidate(); 
    }
}


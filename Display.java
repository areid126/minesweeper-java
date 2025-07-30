import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;

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
    private int width = 8;
    private int height = 8;
    private int mines = 10;
    private Board board = new Board(width, height, mines);
    // JPanel panel = new JPanel(new GridLayout(board.getHeight(), board.getWidth()));
    private ArrayList<ArrayList<GridButton>> buttons = new ArrayList<>();
    private JLabel restart = new JLabel();
    private JLabel mineCount = new JLabel();
    private boolean rightClick = false;
    private boolean leftClick = false;
    private int currentRow = 0;
    private int currentColumn = 0;
    private int flags = 0;


    // Method to set up the board
    public void setUpFrame(){

        // Create the two types of board
        BevelBorder innerBorder = new BevelBorder(BevelBorder.LOWERED, Colours.unopenedTop, Colours.unopenedTop, Colours.unopenedBottom, Colours.unopenedBottom);
        BevelBorder outerBorder = new BevelBorder(BevelBorder.RAISED, Colours.unopenedTop, Colours.unopenedTop, Colours.unopenedBottom, Colours.unopenedBottom);

        // Create the main panel
        JPanel mainPanel = new JPanel();
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
                handleRestart();
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
                // Do nothing
            }
        });


        JLabel timer = new JLabel(formatInt(0));
        timer.setForeground(Colours.counter);
        timer.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 20));
        timer.setBackground(Color.BLACK);
        timer.setOpaque(true);
        timer.setBorder(innerBorder);

        // Add the fields to the header panel
        headerPanel.add(mineCount);
        headerPanel.add(Box.createHorizontalGlue());
        headerPanel.add(restart);
        headerPanel.add(Box.createHorizontalGlue());
        headerPanel.add(timer);

        // Create the grid for the board
        JPanel panel = new JPanel(new GridLayout(board.getHeight(), board.getWidth()));
        
        for(int i = 0; i < board.getHeight(); i++){
            buttons.add(new ArrayList<>());
            for(int j = 0; j < board.getWidth(); j++){
                
                GridButton button = new GridButton(i, j, board.get(i, j), board);
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

                        // If the left click is pressed then log it
                        if(e.getButton() == MouseEvent.BUTTON1) {
                            leftClick = true;
                        }

                        // If the right mouse button is clicked then mark the cell
                        if(e.getButton() == MouseEvent.BUTTON3){
                            rightClick = true;
                            // int flags = board.rightClick(button.getRow(), button.getColumn());
                            flags = board.rightClick(currentRow, currentColumn);
                            // Use the number of flags to update the number of unflagged mines

                            // Update the display after clicking a button
                            updateDisplay();
                        }
                    }
        
                    @Override
                    public void mouseReleased(MouseEvent e){

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
                            if (rightClick) {
                                // res = board.doubleClick(button.getRow(), button.getColumn());
                                res = board.doubleClick(currentRow, currentColumn);
                            }

                            // Otherwise open the square normally
                            else {
                                // res = board.leftClick(button.getRow(), button.getColumn());
                                res = board.leftClick(currentRow, currentColumn);
                            }
                        } 
                        
                        // If the right click is released
                        if(e.getButton() == MouseEvent.BUTTON3){
                            rightClick = false;

                            // If the left mouse button was also being clicked at the same time cascade the square
                            if (leftClick) {
                                // res = board.doubleClick(button.getRow(), button.getColumn());
                                res = board.doubleClick(currentRow, currentColumn);
                            }
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
                panel.add(button);
            }
        }

        // Add the correct border to the panel
        panel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 0, 0, 0), innerBorder));
        panel.setBackground(Colours.unopened);
        int size = 20, gridWidth = (board.getWidth()*size) - 3, gridHeight = (board.getHeight()*size) - 2;
        panel.setPreferredSize(new Dimension(gridWidth, gridHeight));
        panel.setMinimumSize(new Dimension(gridWidth, gridHeight));
        panel.setMaximumSize(new Dimension(gridWidth, gridHeight));
        mainPanel.add(panel);
        frame.add(mainPanel);

        // Format the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // Method to handle losing the game
    public void handleLoss() {
        // Set the reset button icon
        setResetIcon("img/dead.png", "L");


        // Stop the timer (for later when the timer is implemented)
    }

    // Method to handle winning the game
    public void handleWin() {
        // Set the reset button icon
        setResetIcon("img/sunglasses.png", "W");

        // Stop the timer (for later when the timer is implemented)
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
        
        // Reset the flag counter
        flags = 0;
        mineCount.setText(formatInt(mines - flags));
        
        // Update the display
        frame.revalidate(); 
    }

    // Method to handle changing board size
    public void changeBoardSize() {

    }

    // Method to set the icon of the reset button
    public void setResetIcon(String icon, String altText) {
        try {
            BufferedImage buttonIcon = ImageIO.read(new File(icon));
            restart.setIcon(new ImageIcon(buttonIcon.getScaledInstance(25, -1, Image.SCALE_DEFAULT))); // Set the icon on the button
            restart.setText("");
        } catch (IOException e) {
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
                // Update the appearance of every button
                buttons.get(i).get(j).setAppearance();
            }
        }

        // Update the number of mines unflagged
        mineCount.setText(formatInt(mines - flags));

        // Reload the display
        frame.revalidate(); 
    }
}


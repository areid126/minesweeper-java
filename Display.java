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
    private Board board = new Board(8, 8, 10);
    // JPanel panel = new JPanel(new GridLayout(board.getHeight(), board.getWidth()));
    private ArrayList<ArrayList<GridButton>> buttons = new ArrayList<>();
    private JLabel restart = new JLabel();


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
        JLabel mineCount = new JLabel("099");
        mineCount.setForeground(Colours.counter);
        mineCount.setFont(new Font("Bitstream Vera Sans Mono", Font.BOLD, 20));
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
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("img/smile.png"));
            restart.setIcon(new ImageIcon(buttonIcon.getScaledInstance(25, -1, Image.SCALE_DEFAULT))); // Set the icon on the button
            restart.setText("");
        } catch (IOException e) {
            restart.setText("S");
        }

        JLabel timer = new JLabel("000");
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
                
                GridButton button = new GridButton(i, j, board.get(i, j));
                button.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e){
                    
                    }
        
                    @Override
                    public void mouseEntered(MouseEvent e){
                       
                    }
        
                    @Override
                    public void mouseExited(MouseEvent e){
                        
                    }
        
                    @Override
                    public void mousePressed(MouseEvent e){
                        
                    }
        
                    @Override
                    public void mouseReleased(MouseEvent e){
                        // Do nothing if the board has already been won or lost
                        if (board.isWon() || board.isLost()) return;

                        // If the left click is pressed
                        if(e.getButton() == MouseEvent.BUTTON1){
                            boolean notLost = board.open(button.getRow(), button.getColumn());

                            // Handle if the game was lost
                            if(!notLost) {
                                // Indicate that this was the button that lost the game
                                button.setLost(true);
                                handleLoss();
                            }

                            updateDisplay(); // Update the display after clicking a button
                        } 
                        
                        // If the right click is pressed
                        if(e.getButton() == MouseEvent.BUTTON3){
                            button.mark();
                        }

                        // Handle if the game has been won
                        boolean won = board.checkWon();
                        if (won) {
                           handleWin();
                        }

                    }
                });
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
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("img/dead.png"));
            restart.setIcon(new ImageIcon(buttonIcon.getScaledInstance(25, -1, Image.SCALE_DEFAULT))); // Set the icon on the button
            restart.setText("");
        } catch (IOException e) {
            restart.setText("L");
        }

        // Stop the timer (for later when the timer is implemented)
    }

    // Method to handle winning the game
    public void handleWin() {
        // Set the reset button icon
        try {
            BufferedImage buttonIcon = ImageIO.read(new File("img/sunglasses.png"));
            restart.setIcon(new ImageIcon(buttonIcon.getScaledInstance(25, -1, Image.SCALE_DEFAULT))); // Set the icon on the button
            restart.setText("");
        } catch (IOException e) {
            restart.setText("W");
        }

        // Stop the timer (for later when the timer is implemented)
    }

    // Method to update the display
    public void updateDisplay(){
        for(int i = 0; i < board.getDisplay().size(); i++){
            for(int j = 0; j < board.getDisplay().get(i).size(); j++){
                if(board.getDisplay().get(i).get(j)){
                    buttons.get(i).get(j).open();
                }
            }
        }
        frame.revalidate(); // Reload the display
    }
}


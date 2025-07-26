import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
public class GridButton extends JLabel{

    // Store the buttons position in the grid and its value
    private final int ROW;
    private final int COLUMN;
    private int value;
    private boolean open = false;
    private boolean marked = false;
    private boolean lost = false; // If it was this button that was clicked to lose the game

    public GridButton(int row, int column, int value){

        this.ROW = row;
        this.COLUMN = column;
        this.value = value;

        int size = 0;

        // setMinimumSize(new Dimension(size, size));
        setPreferredSize(new Dimension(size, size));
        // setMaximumSize(new Dimension(size, size));


        // Set basic layout things
        setBorder(new BevelBorder(BevelBorder.RAISED, Colours.unopenedTop, Colours.unopenedBottom));
        setBackground(Colours.unopened);
        setOpaque(true);
        setFont(new Font(Font.DIALOG, Font.BOLD, 15));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        // setBorderPainted(false);
        // setFocusPainted(false);
        setFocusable(false);
        // setRolloverEnabled(false);
        // setContentAreaFilled(false);
    }

    public int[] getPosition(){
        return new int[]{ROW, COLUMN};
    }

    public int getValue(){
        return value;
    }

    public int getRow(){
        return ROW;
    }

    public int getColumn(){
        return COLUMN;
    }

    public boolean getLost() {
        return lost;
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }

    public void mark(){
        if(!open){
            marked = !marked;
            if(marked){
                setButtonIcon("img/flag.png", "F");
            }
            else{
                setButtonIcon(null, "");
            }  
        }
    }

    // Method to open a button after it has been pressed
    public void open() {
        if(!marked){
            open = true;
            setText(value + "");
            setIcon(null); // Remove the icon when opening the cell
            setBorder(new MatteBorder(1, 1, 0, 0, Colours.unopenedBottom));
            // setRolloverEnabled(false);
            switch(value){
                case(0):
                setText("");
                break;
                case(1):
                setForeground(Colours.ONE);
                break;
                case(2):
                setForeground(Colours.TWO);
                break;
                case(3):
                setForeground(Colours.THREE);
                break;
                case(4):
                setForeground(Colours.FOUR);
                break;
                case(5):
                setForeground(Colours.FIVE);
                break;
                case(6):
                setForeground(Colours.SIX);
                break;
                case(7):
                setForeground(Colours.SEVEN);
                break;
                case(8):
                setForeground(Colours.EIGHT);
                break;
                case(-1):
                setButtonIcon("img/bomb.png", "B");
                // Set the background of the clicked square to be red
                if(lost) setBackground(Colours.counter);
                break;
            }
            
        }
    }

    // Function for getting an icon
    void setButtonIcon(String iconPath, String altText) {

        // Set the icon if there is an icon to set
        if(iconPath != null) {
            try {
                BufferedImage buttonIcon = ImageIO.read(new File(iconPath));
                // setIcon(new ImageIcon(buttonIcon.getScaledInstance(40, -1, Image.SCALE_SMOOTH))); // Set the icon on the button
                setIcon(new ImageIcon(buttonIcon)); // Set the icon on the button
                setText("");
            } catch (IOException e) {
                // If there is an error loading the image then set the text to be the altText instead
                setText(altText);
            }
        }
        // Unset the icon if there is no icon to set
        else {
            setIcon(null);
            setText("");
        }

    }
}

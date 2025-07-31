import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
public class GridButton extends JLabel{

    // Store the buttons position in the grid and its value
    private final int ROW;
    private final int COLUMN;
    private Board game; // The game this button is associated with
    private int currentValue;
    private int currentDisplay;
    private static final int SIZE = 20;

    // Constants for borders
    private final static BevelBorder CLOSED_BORDER = new BevelBorder(BevelBorder.RAISED, Colours.BORDER_OUTER, Colours.BORDER_SHADOW);
    private final static MatteBorder OPEN_BORDER = new MatteBorder(1, 1, 0, 0, Colours.BORDER_SHADOW);

    public GridButton(int row, int column, int value, Board game){

        this.ROW = row;
        this.COLUMN = column;
        this.currentValue = value; // Start with the initial value
        this.currentDisplay = Board.CLOSED; // Start with all cells closed
        this.game = game;

        // Set up the basic appearance of all buttons
        setFont(new Font(Font.DIALOG, Font.BOLD, 15));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setFocusable(false);
        setOpaque(true);
        setForeground(Color.BLACK);
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setMaximumSize(new Dimension(SIZE, SIZE));
    }

    // Change the game associated with the board
    public void setGame(Board game) {
        this.game = game;
    }

    public int getRow(){
        return ROW;
    }

    public int getColumn(){
        return COLUMN;
    }

    // Method that gets whether the appearance of the button needs to be updated
    public boolean checkUpdate() {
        int display = game.getDisplay(ROW, COLUMN);
        int value = game.get(ROW, COLUMN);
        
        // If the current values are the same as the ones in the board do not update
        if (display == currentDisplay && value == currentValue) return false;

        return true; // The display needs to be updated
    }

    // Sets up the appearance of the button based on its display cell
    public void setAppearance() {
        int display = game.getDisplay(ROW, COLUMN);
        currentDisplay = display; // Update the current display
        currentValue = game.get(ROW, COLUMN); // Update the current value

        // If the cell is hidden then hide it
        if (display == Board.CLOSED) setAppearanceHidden(null, null);
        // If the cell is marked then mark it
        else if (display == Board.FLAGGED) setAppearanceHidden("img/flag.png", "F");
        // If the cell is open then open it
        else setAppearanceOpen();
    }


    public void setAppearanceHidden(String imgPath, String altText) {
        // Set the border
        setBorder(CLOSED_BORDER);
        // Set background colour
        setBackground(Colours.BACKGROUND);
        // Set the icon for the button
        setButtonIcon(imgPath, altText);
    }

    public void setAppearanceOpen() {
        int value = game.get(ROW, COLUMN);
        int display = game.getDisplay(ROW, COLUMN);

        // Set the value of the cell
        if (value != -1 && value != 0) setText(value + "");
        // Remove the icon when opening the cell
        if (value != -1) setIcon(null); 
        // Set the border of the cell
        setBorder(OPEN_BORDER);
        // Set background colour
        if (display != Board.LOST) setBackground(Colours.BACKGROUND);
        else setBackground(Colours.RED);
        
        // Set the foreground colour based on the value of the cell
        if (value == 0) setText("");
        else if (value == 1) setForeground(Colours.ONE);
        else if (value == 2) setForeground(Colours.TWO);
        else if (value == 3) setForeground(Colours.THREE);
        else if (value == 4) setForeground(Colours.FOUR);
        else if (value == 5) setForeground(Colours.FIVE);
        else if (value == 6) setForeground(Colours.SIX);
        else if (value == 7) setForeground(Colours.SEVEN);
        else if (value == 8) setForeground(Colours.EIGHT);
        else if (value == -1) setButtonIcon("img/bomb.png", "B");

        // If the display is incorrectly flagged then set the appearance
        if (display == Board.NO_MINE) setButtonIcon("img/incorrectMine.png", "N");
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
                // Set the altText to be red when displaying flags
                if (altText.equals("F")) setForeground(Colours.RED);
                // Set the foreground colour to be black when displaying altText for mines
                else setForeground(Color.BLACK);
            }
        }
        // Unset the icon if there is no icon to set
        else {
            setIcon(null);
            setText("");
        }

    }
}

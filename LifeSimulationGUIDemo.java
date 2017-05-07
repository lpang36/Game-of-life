/* Conway's Game of Life
 * Lawrence Pang
 * Computer Science
 * 01/01/2016
 */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;  // Needed for ActionListener
import javax.swing.event.*;  // Needed for ActionListener
// The "FileIO" class.
import java.awt.*;
import java.io.*;
import java.util.*;

class LifeSimulationGUIDemo extends JFrame
{
    static Colony colony = new Colony (0.5);
    static javax.swing.Timer t;
    JTextField row = new JTextField (5); //select row for populate/eradicate
    JTextField column = new JTextField (5); //select column for populate/eradicate
    JTextField radius = new JTextField (5); //select radius for populate/eradicate
    JTextField file = new JTextField (5); //select file for save
    JComboBox loadBox = new JComboBox(); //select file for load
    JButton simulateBtn;
    ArrayList <String> loadList = new ArrayList<String> (); //list of files

    //======================================================== constructor
    public LifeSimulationGUIDemo ()
    {
        // 1... Create/initialize components
        BtnListener btnListener = new BtnListener (); // listener for all buttons
        //buttons
        simulateBtn = new JButton ("Simulate");
        simulateBtn.addActionListener (btnListener);
        JButton populateBtn = new JButton ("Populate");
        populateBtn.addActionListener (btnListener);
        JButton eradicateBtn = new JButton ("Eradicate");
        eradicateBtn.addActionListener (btnListener);
        JButton saveBtn = new JButton ("Save");
        saveBtn.addActionListener (btnListener);
        JButton loadBtn = new JButton ("Load");
        loadBtn.addActionListener (btnListener);
        //initialize combo box with files
        final File folder = new File("C:\\Users\\L\\Documents\\Computer Science\\School\\sets g11\\Game of Life");
        listFilesForFolder (folder);
        loadBox.setModel (new DefaultComboBoxModel(loadList.toArray()));

        // 2... Create content pane, set layout
        JPanel content = new JPanel ();        // Create a content pane
        content.setLayout (new BorderLayout ()); // Use BorderLayout for panel
        JPanel north = new JPanel ();
        north.setLayout (new FlowLayout ()); // Use FlowLayout for input area
        JPanel south = new JPanel ();
        south.setLayout (new FlowLayout ());

        DrawArea board = new DrawArea (500, 500);

        // 3... Add the components to the input area.

        south.add (simulateBtn);
        north.add (new JLabel ("Row: "));
        north.add (row);
        north.add (new JLabel ("Column: "));
        north.add (column);
        north.add (new JLabel ("Radius: "));
        north.add (radius);
        north.add (populateBtn);
        north.add (eradicateBtn);
        south.add (new JLabel ("Save: "));
        south.add (file);
        south.add (saveBtn);
        south.add (new JLabel ("Load: "));
        south.add (loadBox);
        south.add (loadBtn);

        content.add (north, "South"); // Input area
        content.add (board, "Center"); // Output area
        content.add (south, "North");

        // 4... Set this window's attributes.
        setContentPane (content);
        pack ();
        setTitle ("Life Simulation");
        setSize (550, 630);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null);           // Center window.
    }

    //adds all the appropriate files in the folder
    public void listFilesForFolder (final File folder)
    {
        loadList.clear();
        for (final File fileEntry : folder.listFiles())
        {
            //all files in the .txt format can be loaded, excluding README.txt which is in every BlueJ project
            if (fileEntry.getName().indexOf(".txt")!=-1&&!fileEntry.getName().equals("README.txt"))
                loadList.add(fileEntry.getName().substring (0,fileEntry.getName().length()-4));
        }
    }

    class BtnListener implements ActionListener 
    {
        public void actionPerformed (ActionEvent e)
        {
            //run the simulation
            if (e.getActionCommand ().equals ("Simulate"))
            {
                Movement moveColony = new Movement (); // ActionListener for timer
                t = new javax.swing.Timer (200, moveColony); // set up Movement to run every 200 milliseconds
                t.start (); // start simulation
                simulateBtn.setText("Stop"); //change button text
            }
            //populate at given row, column, radius
            if (e.getActionCommand ().equals ("Populate"))
            {
                int x = 0; int y = 0; int r = 0;
                try {
                    x = Integer.parseInt(row.getText());
                    y = Integer.parseInt(column.getText());
                    r = Integer.parseInt(radius.getText());
                } catch (NumberFormatException f) {}
                colony.populate (x,y,r);
            }
            //eradicate at given row, column, radius
            if (e.getActionCommand ().equals ("Eradicate"))
            {
                int x = 0; int y = 0; int r = 0;
                try {
                    x = Integer.parseInt(row.getText());
                    y = Integer.parseInt(column.getText());
                    r = Integer.parseInt(radius.getText());
                } catch (NumberFormatException f) {}
                colony.eradicate (x,y,r);
            }
            //stop the simulation
            if (e.getActionCommand ().equals ("Stop"))
            {
                t.stop();
                simulateBtn.setText("Simulate"); //change button text
            }
            //save file to folder
            if (e.getActionCommand ().equals ("Save"))
            {
                try {
                    String text = file.getText();
                    if (text.indexOf(".txt")==-1) text = text+".txt"; //adds the .txt ending
                    colony.save(text);
                    //update combo box to include new file
                    final File folder = new File("C:\\Users\\L\\Documents\\Computer Science\\School\\sets g11\\Game of Life");
                    listFilesForFolder (folder);
                    loadBox.removeAllItems();
                    for (String s: loadList)
                        loadBox.addItem(s);
                } catch (IOException f) {}
            }
            //load file
            if (e.getActionCommand ().equals ("Load"))
            {
                try {
                    colony.load(loadBox.getSelectedItem()+".txt");
                } catch (IOException f) {}
            }
            repaint ();            // refresh display of colony
        }
    }

    class DrawArea extends JPanel
    {
        public DrawArea (int width, int height)
        {
            this.setPreferredSize (new Dimension (width, height)); // size
        }

        public void paintComponent (Graphics g)
        {
            colony.show (g); // display current state of colony
        }
    }

    class Movement implements ActionListener
    {
        public void actionPerformed (ActionEvent event)
        {
            colony.advance (); // advance to the next time step
            repaint (); // refresh 
        }
    }

    //======================================================== method main
    public static void main (String[] args)
    {
        LifeSimulationGUIDemo window = new LifeSimulationGUIDemo ();
        window.setVisible (true);
    }
}

class Colony
{
    private boolean grid[] [];

    public Colony (double density)
    {
        grid = new boolean [100] [100];
        for (int row = 0 ; row < grid.length ; row++)
            for (int col = 0 ; col < grid [0].length ; col++)
                grid [row] [col] = Math.random () < density;
    }

    public void show (Graphics g)
    {
        for (int row = 0 ; row < grid.length ; row++)
            for (int col = 0 ; col < grid [0].length ; col++)
            {
                if (grid [row] [col]) // life
                    g.setColor (Color.black);
                else
                    g.setColor (Color.white);
                g.fillRect (col * 5 + 2, row * 5 + 2, 5, 5); // draw life form
            }
    }

    public boolean live (int i, int j)
    {
        int count = 0; //keeps count of living neighbours
        //goes through surrounding cells
        for (int k = -1; k<=1; k++)
        {
            for (int l = -1; l<=1; l++)
            {
                if (k!=0||l!=0) //if the cell is not the input cell, i.e. a neighbour
                {
                    //ensures no error is thrown for edges
                    try {
                        if (grid[i+k][j+l]) count++;
                    } catch (ArrayIndexOutOfBoundsException e) {}
                }
            }
        }
        //conditions for life
        if (grid[i][j]&&count<2) return false;
        else if (grid[i][j]&&count<4) return true;
        else if (grid[i][j]) return false;
        else if (!grid[i][j]&&count==3) return true;
        return false;
    }

    public void advance ()
    {
        boolean[][] gridCopy = new boolean [100][100];
        for (int i = 0; i<grid.length; i++)
        {
            for (int j = 0; j<grid[i].length; j++)
            {
                gridCopy[i][j] = live(i, j);
            }
        }
        grid = gridCopy; //updates all cells simultaneously
    }

    public void populate (int row, int column, int radius)
    {
        for (int i = row-radius ; i<=row+radius ; i++)
            for (int j = column-radius ; j<=column+radius ; j++)
            {
                try {
                    grid [i] [j] = Math.random () < 0.9; //90% of cells populated
                } catch (ArrayIndexOutOfBoundsException e) {}
            }
    }

    public void eradicate (int row, int column, int radius)
    {
        for (int i = row-radius ; i<=row+radius ; i++)
            for (int j = column-radius ; j<=column+radius ; j++)
            {
                try {
                    grid [i] [j] = Math.random () < 0.1; //10% of cells populated
                } catch (ArrayIndexOutOfBoundsException e) {}
            }
    }

    public void load (String fname) throws IOException
    {
        Scanner s = null;
        String str = "";
        grid = new boolean [100][100];

        try {
            s = new Scanner(new BufferedReader(new FileReader(fname)));
            while (s.hasNext())
            {
                str+=s.next(); //adds the entire file to a string
            }
        } finally {
            if (s != null) {
                s.close();
            }
        }

        for (int i = 0; i<str.length(); i++)
        {
            //determines corresponding row and column from a given character in the file
            int row = i/100;
            int column = i%100;
            //O is a living cell, . is a dead cell
            if (str.charAt(i) == 'O')
                grid[row][column] = true;
            else
                grid[row][column] = false;
        }
    }

    public void save (String fname) throws IOException
    {
        FileWriter fw = new FileWriter (fname);
        PrintWriter fileout = new PrintWriter (fw);

        for (int x = 0 ; x < 100 ; x++)
        {
            for (int y = 0; y<100; y++)
            {
                //O is a living cell, . is a dead cell
                if (grid[x][y]) fileout.print ('O'); // write a value to file
                else fileout.print ('.');
            }
            fileout.println(); //new line
        }
        fileout.close (); // close file
    }
}


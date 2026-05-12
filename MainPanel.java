package project;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
//import javax.swing.JFileChooser;
//import java.io.File;

public class MainPanel extends JPanel implements ActionListener
{
    Project project;
    List<Star> allStars;
    
    JTextField startInput;
    JTextField goalInput;
    JButton searchButton;
    JButton flightLogButton;
    JLabel statusLabel;
//    JButton loadButton;
    
    double testRadius = 5.0;
    
    double zoom = 20.0;  // 1 parsec is going to equal 20 pixels as default
    double camX = 0.0;
    double camY = 0.0;
//    int screenWidth, screenHeight;
    
    public MainPanel(Project project, List<Star> allStars)
    {
        this.project = project;
        this.allStars = allStars;
//        this.screenWidth = w;
//        this.screenHeight = h;
        
//        this.setPreferredSize(new Dimension(w,h));
        this.setBackground(Color.black);
        this.setLayout(new BorderLayout());
        
        JPanel controlBar = new JPanel();
        controlBar.setBackground(Color.darkGray);
        
        JLabel startLabel = new JLabel("Start ID: ");
        startLabel.setForeground(Color.white);
        startInput = new JTextField("0", 15); //I am setting the sun as the start node bebcause it is that the origin
        
        JLabel goalLabel = new JLabel("Goal ID: ");
        goalLabel.setForeground(Color.white);
        goalInput = new JTextField("5853498713190525696", 15); 
        //use 66526127137440128 for Pleiades dataset, it is the star Atlas
        //use 5853498713190525696 for closest_stars datasets, it is the star Proxima Centauri
        
        searchButton = new JButton("Calculate Path");
        searchButton.addActionListener(this);
        
        flightLogButton = new JButton("View Star Log");
        flightLogButton.setEnabled(false);
        flightLogButton.addActionListener(this);
        
        statusLabel = new JLabel(" Ready ");
        statusLabel.setForeground(Color.white);
        
//        loadButton = new JButton("Choose Data");
//        loadButton.addActionListener(this);
        
//        controlBar.add(loadButton);
        controlBar.add(startLabel);
        controlBar.add(startInput);
        controlBar.add(goalLabel);
        controlBar.add(goalInput);
        controlBar.add(searchButton);
        controlBar.add(flightLogButton);
        controlBar.add(statusLabel);
        this.add(controlBar, BorderLayout.NORTH);

    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(KEY_ANTIALIASING,VALUE_ANTIALIAS_ON);
        
        int centerX = getWidth()/2;
        int centerY = getHeight()/2;

        graphics.setColor(new Color(150,150,200,150));
        for (Star s : allStars)
        {
            int screenX = (int)((s.x-camX)*zoom)+centerX;
            int screenY = (int)((s.y-camY)*zoom)+centerY;
            int starSize = 2;
            graphics.setColor(Color.lightGray);
            
            if (s.sourceID==0L)
            {
                starSize = 8;
                graphics.setColor(Color.yellow);
            }
            else if (s.start)
            {
                graphics.setColor(Color.green);
                starSize = 6;
            }
            else if (s.goal)
            {
                graphics.setColor(Color.magenta);
                starSize = 6;
            }
            if (screenX >= 0 && screenX <= getWidth() && screenY >= 0 && screenY <= getHeight()) graphics.fillOval(screenX-(starSize/2),screenY-(starSize/2),starSize,starSize); //this line very good for optimization dont delete
        }
        
        if (project.finalRoute != null && project.finalRoute.size() > 1)
        {
            graphics.setStroke(new BasicStroke(2));
            for (int yes=0; yes<project.finalRoute.size()-1; yes++)
            {
                Star current = project.finalRoute.get(yes);
                Star next = project.finalRoute.get(yes+1);
                
                int x1 = (int)((current.x-camX)*zoom)+centerX;
                int y1 = (int)((current.y-camY)*zoom)+centerY;
                int x2 = (int)((next.x-camX)*zoom)+centerX;
                int y2 = (int)((next.y-camY)*zoom)+centerY;
                
                graphics.setColor(Color.lightGray);
                graphics.drawLine(x1,y1,x2,y2);
                
                if (current.start) 
                {
                    if (current.sourceID!=0L) graphics.setColor(Color.green);
                    graphics.setColor(Color.yellow);
                    graphics.fillOval(x1-3,y1-3,8,8);
                }
                else if (current.goal) graphics.setColor(Color.magenta);
                else graphics.setColor(Color.cyan);
                graphics.fillOval(x1-3,y1-3,6,6);
                
                if (next.start) 
                {
                    if (next.sourceID!=0L) graphics.setColor(Color.green);
                    graphics.setColor(Color.yellow);
                    graphics.fillOval(x2-3,y2-3,8,8);
                } 
                else if (current.goal) graphics.setColor(Color.magenta);
                else graphics.setColor(Color.cyan);
                graphics.fillOval(x2-3,y2-3,6,6);
            }
        }
    }
    
    private void showFlightLog()
    {
        if (project.finalRoute == null || project.finalRoute.size() < 2) return;
        StringBuilder sb = new StringBuilder(); //not really sure what this is but apparently is it better for editing Strings so i will try it out
        double PCtoLY = 3.261563777;
        
        sb.append("                          STAR PATH LOG    \n\n");
        for (int yes = project.finalRoute.size()-1;yes>=0;yes--)
        {
            Star s = project.finalRoute.get(yes);
            if (yes == project.finalRoute.size()-1)
            {
                sb.append("START:  Star ID ").append(s.sourceID).append("\n");
                sb.append("        Coordinate: (").append(s.toStringCoordinates()).append(")\n");
            }
            else if (yes==0)
            {
                sb.append("\nGOAL :  Star ID ").append(s.sourceID).append("\n");
                sb.append("        Coordinate: (").append(s.toStringCoordinates()).append(")\n\n");
                sb.append("Total Trip: ").append(s.gCost).append(" parsecs\n");
                sb.append("            ").append(s.gCost*PCtoLY).append(" lightyears\n\n");
                sb.append("Jump Radius: ").append(testRadius).append(" parsecs\n\n");
            }
            else
            {
                int jumpNumber = project.finalRoute.size()-1-yes;
                sb.append("Jump ").append(jumpNumber).append(": Star ID ").append(s.sourceID).append("\n");
                sb.append("        Coordinate: (").append(s.toStringCoordinates()).append(")\n");
            }
            if (yes>0)
            {
                Star nextStar = project.finalRoute.get(yes-1);
                double jumpDistance = Math.sqrt(Math.pow(s.x-nextStar.x,2)+Math.pow(s.y-nextStar.y,2)+Math.pow(s.z-nextStar.z,2));
                sb.append("        Jump Distance: ").append(jumpDistance).append(" parsecs\n");
                sb.append("                       ").append(jumpDistance*PCtoLY).append(" lightyears\n\n");
            }
        }
        sb.append("\n                  ");
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setMargin(new Insets(10,10,10,10));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550,400));
        JOptionPane.showMessageDialog(this,scrollPane, "Gaia Flight Log", JOptionPane.PLAIN_MESSAGE);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae)
    {
//        if (ae.getSource() == loadButton)
//        {
//            JFileChooser fileChooser = new JFileChooser(".");
//            fileChooser.setDialogTitle("Select Star Datasest (.txt)");
//            int userSelection = fileChooser.showOpenDialog(this);
//            if (userSelection == JFileChooser.APPROVE_OPTION)
//            {
//                File fileLoad = fileChooser.getSelectedFile();
//                try 
//                {
//                    statusLabel.setText(" Loading "+fileLoad.getName()+" ");
//                    project.Star ok = new Star(fileLoad.getAbsolutePath());
//                    flightLogButton.setEnabled(false);
//                    startInput.setText("");
//                    goalInput.setText("");
//                    statusLabel.setText(" Loaded "+fileLoad.getName()+" ");
//                    repaint();
//                }
//                catch (Exception e)
//                {
//                    statusLabel.setText(" Error reading file ");
//                }
//            }
//            return;
//        }
        if (ae.getSource() == flightLogButton)
        {
            showFlightLog();
            return;
        }
        if (ae.getSource() == searchButton) 
        {
            try 
            {
                flightLogButton.setEnabled(false);
                //this reads the text boxes and converts to long
                long sID = Long.parseLong(startInput.getText().trim());
                long gID = Long.parseLong(goalInput.getText().trim());
              
                project.setStartStar(sID);
                project.setGoalStar(gID);
                
                double maxDistance = Math.sqrt(Math.pow(project.startStar.x-project.goalStar.x,2)+Math.pow(project.startStar.y-project.goalStar.y,2)+Math.pow(project.startStar.z-project.goalStar.z,2));
                statusLabel.setText(" Searching for smallest jump radius ");
                testRadius = 5.0;
                while (testRadius<=maxDistance+5.0)
                {
                    //this wipes previous search so search can be done again
                    project.resetUniverse();
                    project.openList.clear();
                    project.checkedList.clear();
                    if (project.finalRoute != null) project.finalRoute.clear();
                    project.goalReached = false;
                    project.step=0;
                    
                    //sets up algorithm targets
                    project.setCurrentStar(sID);
                    project.setStartStar(sID);
                    project.setGoalStar(gID);
                    project.radius = testRadius;
                    
                    statusLabel.setText(" Searching ");
                    project.search();
                    if (project.goalReached)
                    {
                        break;
                    }
                    testRadius+=2.0;
                }
                
                if (project.goalReached && project.finalRoute.size() > 0)
                {
                    flightLogButton.setEnabled(true);
                    double totalTrip = project.finalRoute.get(0).gCost;
                    double totalLY = totalTrip*3.261563777;
                    String formatted = String.format("%.5f", totalTrip);
                    String formattedLY = String.format("%.5f",totalLY);
                    statusLabel.setText( "Path Found of "+(project.finalRoute.size()-1)+" Jumps. Total trip: "+formatted+" parsecs, "+formattedLY+" LY");
                    
                    double minX = Double.MAX_VALUE, maxX = -1*Double.MAX_VALUE;
                    double minY = Double.MAX_VALUE, maxY = -1*Double.MAX_VALUE;
                    
                    for (Star s : project.finalRoute)
                    {
                        if (s.x < minX) minX = s.x;
                        if (s.x > maxX) maxX = s.x;
                        if (s.y < minY) minY = s.y;
                        if (s.y >maxY) maxY = s.y;
                    }
                    
                    camX = (minX+maxX)/2.0;
                    camY = (minY+maxY)/2.0;
                    double pathWidth = maxX-minX;
                    double pathHeight = maxY-minY;
                    
                    if (pathWidth < 1.0) pathWidth = 1;
                    if (pathHeight < 1.0) pathHeight = 1;
                    
                    double zoomX = (getWidth()*0.8)/pathWidth; //so it has 10% free space
                    double zoomY = (getHeight()*0.8)/pathHeight; // this too
                    zoom = Math.max(0.5, Math.min(zoomX, zoomY));
                    System.out.println("\n Cam Debug");
                    System.out.println("Zoom Scale "+zoom);
                    System.out.println("Cam X "+camX);
                    System.out.println("Cam Y "+camY);
                }
                else 
                {
                    flightLogButton.setEnabled(false);
                    statusLabel.setText( "Error, no path exists even with a direct jump ");
                }
//                if (project.goalReached) statusLabel.setText(" Path Found\n Jumps: "+(project.finalRoute.size()-1));
//                else statusLabel.setText(" Error, no path exists within jump radius ");
                repaint();
            }
            catch (Exception e)
            {
                statusLabel.setText(" Invalid Star ID ");
            }
        }
    }
    
    
//  {
//      Star ok = new Star("/project/testDataSource.txt"); 
//      System.out.println(ok);
//  }
  
//  public MainPanel(Project proj, List stars, int w, int h)
//  {
//    super.setPreferredSize(new Dimension(w, h));
////    Star ok = new Star("/project/testDataSource.txt"); 
////    System.out.println(ok.toString());
//    
//    
//  }
//  
//  @Override
//  public void paintComponent(Graphics gr)
//  {
//    super.paintComponent(gr);
//    Graphics2D g = (Graphics2D) gr;
//    g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
//    
//    
//  }
}

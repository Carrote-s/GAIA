package project;

//import javax.swing.JDialog;
//import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

public class Project
{
  Star ok = new Star("/project/closest_stars_to_earth_100k.txt");
  /*      closest_stars_to_earth_100k.txt
          pleiades_galactic_neighborhood_59k.txt
          closest_stars_to_earth_1m.txt (I would only use this if I have made a 3D version of the visualization, not using the 2D one)
  */
//    System.out.println(ok.toString());
//  int indexStart = ok.stars.indexOf(418551920284673408L);
//  int indexEnd = ok.stars.indexOf(6875990375999580544L);
//  Star startStar = ok.stars.get(indexStart);
//  Star endStar = ok.stars.get(indexEnd);
  Star startStar, goalStar, currentStar;
  public ArrayList<Star> openList = new ArrayList<>();
  public ArrayList<Star> checkedList = new ArrayList<>();
  public List<Star> finalRoute = new ArrayList<>();
  KDTree starMap;
  int step = 0;
  double radius = 0.0;
  //change search radius
  int stepLimit = 300;
  //change how many steps is max 
  boolean goalReached = false;
  
  public static void main(String[] args)
  {
    /*JFrame.setDefaultLookAndFeelDecorated(true);
    JDialog.setDefaultLookAndFeelDecorated(true);
    JFrame fr = new JFrame("Data Structures & Algorithms Project");
    fr.setContentPane(new MainPanel(1200, 800));
    fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    fr.setLocation(10, 10);
    fr.setResizable(false);
    fr.pack();
    fr.setVisible(true);*/
      
    Project p = new Project();
    List<Star> allStars = new ArrayList<>(p.ok.starDataID.values());
    p.starMap = new KDTree(allStars);
    System.out.println(allStars.size()+" stars loaded");
    
    JFrame frame = new JFrame("Gaia DR3 A* Pathfinder"); 
    MainPanel panel = new MainPanel(p, allStars);
    
//    frame.setContentPane(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(panel);
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//    frame.pack();
//    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
 
  private void getCost(Star node)
  {
    double nodeDistance = Math.sqrt(Math.pow(node.x-startStar.x,2)+Math.pow(node.y-startStar.y,2)+Math.pow(node.z-startStar.z,2));
    node.gCost = nodeDistance;
    
    nodeDistance =  Math.sqrt(Math.pow(node.x-goalStar.x,2)+Math.pow(node.y-goalStar.y,2)+Math.pow(node.z-goalStar.z,2));
    node.hCost = nodeDistance;
        //heuristic cost
    node.fCost = node.gCost+node.fCost;
    //total cost
  }
  
//  public void setStartStar(double xCoord, double yCoord, double zCoord)
//  {
//      startStar = ok.starDataCoord.get(new Coordinate(xCoord, yCoord, zCoord));
//      startStar.setAsStart();
//  }
  public void setStartStar(long id)
  {
      startStar = ok.starDataID.get(id);
      startStar.setAsStart();
  }
  
//  public void setCurrentStar(double xCoord, double yCoord, double zCoord)
//  {
//      currentStar = ok.starDataCoord.get(new Coordinate(xCoord, yCoord, zCoord));
//      currentStar.setAsStart();
//  }
  public void setCurrentStar(long id)
  {
      currentStar = ok.starDataID.get(id);
      currentStar.setAsStart();
  }
      
//  public void setGoalStar(double xCoord, double yCoord, double zCoord)
//  {
//      goalStar = ok.starDataCoord.get(new Coordinate(xCoord,yCoord,zCoord));
//      goalStar.setAsGoal();
//  }
  public void setGoalStar(long id)
  {
      goalStar = ok.starDataID.get(id);
      goalStar.setAsGoal();
  }
  
  public void search()
  {
      currentStar.gCost = 0;
      currentStar.hCost = Math.sqrt(Math.pow(currentStar.x-goalStar.x,2)+Math.pow(currentStar.y-goalStar.y,2)+Math.pow(currentStar.z-goalStar.z,2));
      currentStar.fCost = currentStar.gCost+currentStar.hCost;
      currentStar.setAsOpen();
      openList.add(currentStar);
      while (goalReached == false && step < stepLimit && !openList.isEmpty())
      {
          int bestStarIndex = 0;
          double bestStarfCost = Double.MAX_VALUE;
          for (int yes=0;yes<openList.size();yes++)
          {
              if (openList.get(yes).fCost<bestStarfCost)
              {
                  bestStarIndex = yes;
                  bestStarfCost = openList.get(yes).fCost;
              }
              else if (openList.get(yes).fCost==bestStarfCost)
              {
                  if (openList.get(yes).gCost<openList.get(bestStarIndex).gCost)
                  {
                      bestStarIndex = yes;
                  }
              }
          }
//          double x = currentStar.x;
//          double y = currentStar.y;
//          double z = currentStar.z;
          currentStar = openList.get(bestStarIndex);
          if (currentStar.sourceID == goalStar.sourceID)
          {
              goalReached = true; 
              pathTrack();
              return;
          }
          
          currentStar.setAsChecked();
          checkedList.add(currentStar);
          openList.remove(currentStar);  

          double radiusOfSearch = radius;
          List<Star> neighbors = starMap.radiusSearch(currentStar, radiusOfSearch);
           
          for (int yes=0; yes<neighbors.size(); yes++)
          {
              Star neighbor = neighbors.get(yes);
              double distanceToNeighbor = Math.sqrt(Math.pow(currentStar.x-neighbor.x,2)+Math.pow(currentStar.y-neighbor.y,2)+Math.pow(currentStar.z-neighbor.z,2));
              double tentgCost = currentStar.gCost+distanceToNeighbor;
              if (neighbor.open == false || tentgCost < neighbor.gCost)
              {
                  neighbor.gCost = tentgCost;
                  neighbor.hCost = Math.sqrt(Math.pow(neighbor.x-goalStar.x,2)+Math.pow(neighbor.y-goalStar.y,2)+Math.pow(neighbor.z-goalStar.z,2));
                  neighbor.fCost = neighbor.gCost+neighbor.hCost;
                  neighbor.parent = currentStar;
                  if (neighbor.open == false)
                  {
                      neighbor.setAsOpen();
                      openList.add(neighbor);
                  }
              }
              
              
//              if (neighbors.get(yes).fCost<bestStarfCost)
//              {
//                  bestStarIndex = yes;
//                  bestStarfCost = neighbors.get(yes).fCost;
//              }
//              else if (neighbors.get(yes).fCost==bestStarfCost)
//              {
//                  if (neighbors.get(yes).gCost<neighbors.get(bestStarIndex).gCost)
//                  {
//                      bestStarIndex = yes;
//                  }
//              }
          }
//          currentStar = neighbors.get(bestStarIndex);
//          if (currentStar == goalStar)
//          {
//              goalReached = true;
//              pathTrack();
//          }
          step++;
      }
      if (goalReached == false)
      {
          System.out.println("There is no path possible with radius of "+radius+" in the star map");
      }
  }
  
  private void pathTrack()
  {
      if (goalStar.parent == null && goalStar != startStar) System.out.println("No path can be found. :(");
      finalRoute.clear(); // this just clears any old paths from previous searches
      Star current = goalStar;
      double PCtoLY = 3.261563777;
      while (current != null)
      {
          finalRoute.add(current);
          current = current.parent;
      }
      for (int x = finalRoute.size()-1; x>=0; x--)
      {
          Star s = finalRoute.get(x);
          if (x == finalRoute.size()-1)
          {
              System.out.println("\n\n START: Star ID " + s.sourceID);           
              System.out.println("        Coordinate: ("+s.toStringCoordinates()+")");
          }
          else if (x == 0)
          {
              System.out.println("\nGOAL  : Star ID " + s.sourceID);
              System.out.println("        Coordinate: ("+s.toStringCoordinates()+")");             
              System.out.println("\n\nTotal trip: " + s.gCost + " parsecs");
              System.out.println("            "+ s.gCost*PCtoLY + " lightyears\n\n");
          } //i think its parsecs
          else 
          {
              int jumpNumber = finalRoute.size()-1-x;
              System.out.println("Jump " + jumpNumber + ": Star ID " + s.sourceID);
              System.out.println("        Coordinate: ("+s.toStringCoordinates()+")"); // +" (Distance from last star " + s.hCost+ " parsecs");
          }
          if (x>0) 
          {
              Star nextStar = finalRoute.get(x-1);
              double jumpDistance = Math.sqrt(Math.pow(s.x-nextStar.x,2)+Math.pow(s.y-nextStar.y,2)+Math.pow(s.z-nextStar.z,2));
              System.out.println("        Jump distance: "+jumpDistance+" parsecs");
              System.out.println("                       "+jumpDistance*PCtoLY+" lightyears\n");
          }
      }
  }
  
  public void resetUniverse()
  {
      for (Star s : ok.starDataID.values())
      {
          s.gCost=0;
          s.hCost=0;
          s.fCost=0;
          s.parent = null;
          s.open = false;
          s.checked = false;
          s.start = false;
          s.goal = false;
      }
  }

}

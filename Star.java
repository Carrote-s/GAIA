/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;

/**
 *
 * @author siwonryu
 */
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Star 
{
    long sourceID;
    double x, y, z;

    double gCost, hCost, fCost;
    boolean start, goal, checked, open;
//    List<Star> stars = new ArrayList<>();
    Map<Long,Star> starDataID = new HashMap<>();
//    Map<Coordinate, Star> starDataCoord = new HashMap<>();
//    boolean isPathStar = false;
    public Star parent;
    public Star theSun;
    
    public Star(long id, double xCoord, double yCoord, double zCoord)
    {
        this.sourceID = id;
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord; 
//        Coordinate ok = new Coordinate(xCoord,yCoord,zCoord);
        this.start = false;
        this.goal = false;
    }
    public Star(String fileName)
    {
        Scanner in = new Scanner(Star.class.getResourceAsStream("closest_stars_to_earth_100k.txt"));
        /*       closest_stars_to_earth_100k.txt
                 pleiades_galactic_neighborhood_59k.txt
                 closest_stars_to_earth_1m.txt (I would only use this if I have made a 3D version of the visualization, not using the 2D one)
        */
//        Coordinate sunCoord = new Coordinate(0.0,0.0,0.0);
        theSun = new Star(0L,0.0,0.0,0.0);
        starDataID.put(0L,theSun);
//        starDataCoord.put(sunCoord,theSun);
        while (in.hasNext())
        {
            this.sourceID = in.nextLong();
            this.x = in.nextDouble();
            this.y = in.nextDouble();
            this.z = in.nextDouble();
//            Coordinate ok = new Coordinate(this.x, this.y, this.z);
            Star newStar = new Star(this.sourceID,this.x,this.y,this.z);
            starDataID.put(this.sourceID, newStar);
//            starDataCoord.put(ok,newStar);
        }
        
//        for (int yes=0;y<20;yes++)
//        {
//            this.sourceID = in.nextLong();
//            this.x = in.nextDouble();
//            this.y = in.nextDouble();
//            this.z = in.nextDouble();
//            Coordinate ok = new Coordinate(this.x, this.y, this.z);
//            Star newStar = new Star(this.sourceID,this.x,this.y,this.z);
//            starDataID.put(this.sourceID, newStar);
//            starDataCoord.put(ok,newStar);
//        }
        
//        for (int yes=0; yes<100; yes++)
//        {
//            this.sourceId = in.nextLong();
//            this.x = in.nextDouble();
//            this.y = in.nextDouble();
//            this.z = in.nextDouble();
//            Star newStar = new Star(this.sourceId,this.x,this.y,this.z);
//            stars.add(newStar);
//        }
    }
    
    
    public void setAsStart()
    {
        start=true;
    }
    public void setAsGoal()
    {
        goal=true;
    }
    public void setAsChecked()
    {
        checked = true;
    }
    public void setAsOpen()
    {
        open = true;
    }
//    public void setAsPath()
//    {
//        this.isPathStar = false;
//    }
   
    private Star getStar(long id)
    {
        return starDataID.get(id);
    }
//    private Star getStar(double xCoord, double yCoord, double zCoord)
//    {
//        return starDataCoord.get(new Coordinate(xCoord,yCoord,zCoord));
//    }
//    
    @Override
    public String toString()
    {
        return this.sourceID+" "+this.x+" "+this.y+" "+this.z;
    }
    
    public String toStringCoordinates()
    {
        return this.x+" "+this.y+" "+this.z;
    }
    
    
}

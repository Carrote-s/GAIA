/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 *
 * @author siwonryu
 */
public class KDTree 
{
    int axis;
    private class KDNode
    {
        Star star; 
        KDNode left, right;
        int axis;
        
        KDNode(Star star, int axis)
        {
            this.star = star;
            this.axis = axis;
        }
    }
    private KDNode root;
    public KDTree(List<Star> stars)
    {
        this.root = buildTree(new ArrayList<>(stars),0);
    }
        
    public KDNode buildTree(List<Star> stars, int depth)
    {
        if (stars == null || stars.isEmpty()) return null;
        int axis = depth%3;
        stars.sort(new Comparator<Star>()
        {
            @Override
            public int compare(Star star1, Star star2)
            {
                if (axis==0) return Double.compare(star1.x,star2.x);
                if (axis==1) return Double.compare(star1.y,star2.y);
                return Double.compare(star1.z, star2.z);
            }
                
        });
            
        int medianIndex = stars.size()/2;
        KDNode node = new KDNode(stars.get(medianIndex),axis);
            
        List<Star> leftStars = new ArrayList<>(stars.subList(0,medianIndex));
        List<Star> rightStars = new ArrayList<>(stars.subList(medianIndex+1,stars.size()));
        node.left = buildTree(leftStars,depth+1);
        node.right = buildTree(rightStars,depth+1);
        return node;
            
    }
        
    public List<Star> radiusSearch(Star target, double radius)
    {
        List<Star> neighbors = new ArrayList<>();
        searchNode(root, target, radius, neighbors);
        return neighbors;
    }
        
    private double getDistance(Star ok, Star ok2)
    {
        return Math.sqrt(Math.pow(ok.x-ok2.x,2)+Math.pow(ok.y-ok2.y,2)+Math.pow(ok.z-ok2.z,2));
    }
        
    private void searchNode(KDNode node, Star target, double radius, List<Star> neighbors)
    {
        if (node==null) return;
        double distance = getDistance(node.star, target);
        if (distance <= radius&&node.star.sourceID != target.sourceID) neighbors.add(node.star);
        double axisDistance=0;
            
        //I am trying out what a rule switch is because Apache recommended this instead of many if statements
        switch(node.axis)
        {
            case 0 -> axisDistance = target.x-node.star.x;
            case 1 -> axisDistance = target.y-node.star.y;
            case 2 -> axisDistance = target.z-node.star.z;
        }
//            if (node.axis == 0) axisDistance = target.x-node.star.x;
//            else if (node.axis==1) axisDistance = target.y-node.star.y;
//            else axisDistance = target.z-node.star.z;
        if (axisDistance<0)
        {
            searchNode(node.left,target,radius,neighbors);
            if (Math.abs(axisDistance) <= radius)
            {
                searchNode(node.right,target,radius,neighbors);
            }
        }
        else
        {
            searchNode(node.right,target,radius,neighbors);
            if (Math.abs(axisDistance) <= radius)
            {
                searchNode(node.left,target,radius,neighbors);
            }
        }
            
    }
}

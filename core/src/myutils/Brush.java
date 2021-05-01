package myutils;

public class Brush
{
    public int radius = 50;
    public int points = 100;
    public Brush(){}
    public Brush(int radius, int points)
    {
        this.radius = radius;
        this.points = points;
    }


    public void setRadius(int radius)
    {
        this.radius = radius;
    }

    public int getRadius()
    {
        return radius;
    }

    public void setPoints(int points)
    {
        this.points = points;
    }

    public int getPoints()
    {
        return points;
    }
}

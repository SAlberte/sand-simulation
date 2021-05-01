package myutils;

import com.badlogic.gdx.graphics.g3d.Material;

public class MaterialBrushes
{
    public Brush airBrush = new Brush(25, 250);
    public Brush sandBrush = new Brush(50, 100);
    public Brush waterBrush = new Brush(50, 190);
    public Brush steelBrush = new Brush(25, 200);

    public void setBrushRadius(Materials material, int radius)
    {
        getBrush(material).setRadius(radius);
    }

    public int getBrushRadius(Materials material)
    {
        return getBrush(material).getRadius();
    }

    public void setBrushPoints(Materials material, int pointsNum)
    {
        getBrush(material).setPoints(pointsNum);
    }

    public int getBrushPoints(Materials material)
    {
        return getBrush(material).getPoints();
    }

    private Brush getBrush(Materials material)
    {
        Brush brush;
        switch(material)
        {
            case AIR:
                brush = airBrush;
                break;
            case SAND:
                brush = sandBrush;
                break;
            case WATER:
                brush = waterBrush;
                break;
            case STEEL:
                brush = steelBrush;
                break;
            default:
                brush = new Brush();
        }
        return brush;
    }
}

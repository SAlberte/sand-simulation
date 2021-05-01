package myutils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class Button extends Rectangle
{
    private Color color;

    public Button(float x, float y, float width, float height, Color color)
    {
        super(x, y, width, height);
        this. color = color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public Color getColor()
    {
        return color;
    }
}

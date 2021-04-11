package myutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class ParticleDrawInfo
{
    public int x;
    public int y;
    public int element;
    public Color color;

    public ParticleDrawInfo(int x, int y, int element, Color color)
    {
        this.x = x;
        this.y = y;
        this.element = element;
        this.color = color;
    }

}


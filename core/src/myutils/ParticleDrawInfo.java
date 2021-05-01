package myutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class ParticleDrawInfo
{
    public int x;
    public int y;
    public Materials material;
    public Color color;

    public ParticleDrawInfo(int x, int y, Materials material, Color color)
    {
        this.x = x;
        this.y = y;
        this.material = material;
        this.color = color;
    }

}


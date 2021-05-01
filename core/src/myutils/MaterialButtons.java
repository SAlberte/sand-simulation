package myutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MaterialButtons
{

    public Button sandButton;
    public Button waterButton;
    public Button steelButton;
    public Button airButton;
    private final int topUIEdge;

    public MaterialButtons()
    {
        topUIEdge = (int)
                (0.9f*Gdx.graphics.getHeight()-(Gdx.graphics.getWidth()/10));
        positionAndInitializeButtons();
    }

    public void positionAndInitializeButtons()
    {
        sandButton = new Button(
                (int)(0.1f* Gdx.graphics.getWidth()),
                (int)(0.9f*Gdx.graphics.getHeight()),
                (int)(Gdx.graphics.getWidth()/10f),
                (int)(Gdx.graphics.getWidth()/10f),
                Color.YELLOW);

        waterButton = new Button(
                (int)(0.25f*Gdx.graphics.getWidth()),
                (int)(0.9f*Gdx.graphics.getHeight()),
                (int)(Gdx.graphics.getWidth()/10f),
                (int)(Gdx.graphics.getWidth()/10f),
                Color.BLUE);

        steelButton = new Button(
                (int)(0.4f*Gdx.graphics.getWidth()),
                (int)(0.9f*Gdx.graphics.getHeight()),
                (int)(Gdx.graphics.getWidth()/10f),
                (int)(Gdx.graphics.getWidth()/10f),
                Color.GRAY);

        airButton = new Button(
                (int)(0.55f*Gdx.graphics.getWidth()),
                (int)(0.9f*Gdx.graphics.getHeight()),
                (int)(Gdx.graphics.getWidth()/10f),
                (int)(Gdx.graphics.getWidth()/10f),
                Color.BLACK);
    }

    public int getTopUIEdge()
    {
        return topUIEdge;
    }

    public boolean contains(Materials material, float x, float y)
    {
        return getButton(material).contains(x, y);
    }

    public Color getButtonColor(Materials material)
    {
        return getButton(material).getColor();
    }

    public float[] getPosAndDims(Materials material)
    {
        float [] posAndDims = new float [4];
        posAndDims[0] = getButton(material).getX();
        posAndDims[1] = getButton(material).getY();
        posAndDims[2] = getButton(material).getWidth();
        posAndDims[3] = getButton(material).getHeight();
        return posAndDims;
    }

    private Button getButton(Materials material)
    {
        Button button;
        switch(material)
        {
            case AIR:
                button = airButton;
                break;
            case SAND:
                button = sandButton;
                break;
            case WATER:
                button = waterButton;
                break;
            case STEEL:
                button = steelButton;
                break;
            default:
                throw new NotImplementedException();
        }
        return button;
    }
}

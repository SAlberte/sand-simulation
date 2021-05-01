package com.mygdx.sandsimulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import java.util.LinkedList;

import myutils.MaterialBrushes;
import myutils.Materials;
import myutils.ParticleDrawInfo;

public class SandPit
{
    public int yBoxesNum;
    public int xBoxesNum;
    public float boxWidth;
    public float boxHeight;
    public Materials currentMaterial = Materials.SAND;
    public ParticleDrawInfo[][] worldGrid;
    public boolean [][] isWorldGridPartStationary;
    private final int WorldGridPartsXPartsNum = 15;
    private final int WorldGridPartsYPartsNum = 30;
    private int WorldGridPartsXPartLen;
    private int WorldGridPartsYPartLen;
    private int sideChanger = -1;
    MaterialBrushes materialBrushes = new MaterialBrushes();
    public LinkedList<ParticleDrawInfo> particlesToDraw = new LinkedList<>();
    private int topUIEdge;

    public SandPit(int topUIedge)
    {
        this.topUIEdge = topUIedge;
        autoAdjustWorldGrid();
    }

    private void autoAdjustWorldGrid()
    {
        yBoxesNum = Gdx.graphics.getHeight()/5;
        xBoxesNum = Gdx.graphics.getWidth()/5;
        xBoxesNum = (xBoxesNum/WorldGridPartsXPartsNum)*WorldGridPartsXPartsNum;
        yBoxesNum = (yBoxesNum/WorldGridPartsYPartsNum)*WorldGridPartsYPartsNum;
        boxWidth = Gdx.graphics.getWidth()/(float)xBoxesNum;
        boxHeight = Gdx.graphics.getHeight()/(float)yBoxesNum;
        worldGrid = new ParticleDrawInfo[xBoxesNum][yBoxesNum];
        for(int y=0; y<yBoxesNum; y++)
            for(int x=0;x< xBoxesNum; x++)
            {
                worldGrid[x][y] = new ParticleDrawInfo(
                        x,
                        y,
                        Materials.AIR,
                        new Color(0.223f, 0.176f, 0.176f, 1));
            }
        isWorldGridPartStationary = new boolean[WorldGridPartsXPartsNum][WorldGridPartsYPartsNum];
        WorldGridPartsXPartLen = xBoxesNum / WorldGridPartsXPartsNum;
        WorldGridPartsYPartLen = yBoxesNum / WorldGridPartsYPartsNum;
        for(int y=0; y<WorldGridPartsYPartsNum; y++)
            for(int x=0;x< WorldGridPartsXPartsNum; x++)
            {
                isWorldGridPartStationary[x][y] = true;
            }
    }

    private void addNewMaterials()
    {
        int touchX;
        int touchY;
        if (Gdx.input.isTouched()) {
            float boxWidth = Gdx.graphics.getWidth() / (float) xBoxesNum;
            float boxHeight = Gdx.graphics.getHeight() / (float) yBoxesNum;
            touchX = Gdx.input.getX();
            touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            int grainRad = materialBrushes.getBrushRadius(currentMaterial);
            int pointsNum = materialBrushes.getBrushPoints(currentMaterial);
            for (int i = 0; i <= pointsNum; i++) {
                int new_x = (int) ((touchX - grainRad + (int) (Math.random() * ((grainRad + grainRad) + 1))) / boxWidth);
                int new_y = (int) ((touchY - grainRad + (int) (Math.random() * ((grainRad + grainRad) + 1))) / boxHeight);
                if (new_x >= 0 && new_x < xBoxesNum && new_y >= 0 && new_y * boxHeight < topUIEdge)
                {
                    Color new_color = generateNewColor(currentMaterial);
                    worldGrid[new_x][new_y] = new ParticleDrawInfo(
                            new_x,
                            new_y,
                            currentMaterial,
                            new_color);
                    addParticleToDrawList(new_x, new_y);
                }
            }
        }
    }

    public void update()
    {
        addNewMaterials();
        sideChanger *= -1;
        if(sideChanger < 0)
        {
            for(int yPart=0;yPart<WorldGridPartsYPartsNum;yPart++)
            {
                int yStart = WorldGridPartsYPartLen * yPart;
                int yEnd = yStart + WorldGridPartsYPartLen;
                LinkedList <Integer> xPartsToUpdate = new LinkedList<>();
                for(int i=0;i<WorldGridPartsXPartsNum;i++)
                {
                    if(!isWorldGridPartStationary[i][yPart])
                    {
                        xPartsToUpdate.add(i);
                        isWorldGridPartStationary[i][yPart]= true;
                    }
                }
                for(int yStartIter=yStart; yStartIter<yEnd;yStartIter++)
                {
                    for(Integer x : xPartsToUpdate)
                    {
                        int xStart = WorldGridPartsXPartLen * x;
                        int xEnd = xStart + WorldGridPartsXPartLen;
                        updateWorldGridFromLeftToRight(xStart, xEnd, yStartIter);
                    }
                }
            }
        }
        else
        {
            for(int yPart=0;yPart<WorldGridPartsYPartsNum;yPart++)
            {
                int yStart = WorldGridPartsYPartLen * yPart;
                int yEnd = yStart + WorldGridPartsYPartLen;
                LinkedList <Integer> xPartsToUpdate = new LinkedList<>();
                for(int i=WorldGridPartsXPartsNum-1;i>=0;i--)
                {
                    if(!isWorldGridPartStationary[i][yPart])
                    {
                        xPartsToUpdate.add(i);
                        isWorldGridPartStationary[i][yPart]= true;
                    }
                }
                for(int yStartIter=yStart; yStartIter<yEnd;yStartIter++)
                {

                    for(Integer x : xPartsToUpdate)
                    {
                        int xStart = WorldGridPartsXPartLen * x;
                        int xEnd = xStart + WorldGridPartsXPartLen;
                        updateWorldGridFromRightToLeft(xStart, xEnd, yStartIter);
                    }
                }
            }
        }
    }
    private void updateWorldGridFromLeftToRight(
            int xStart,
            int xEnd,
            int y)
    {
        for(int x=xStart;x< xEnd; x++)
            updateWorldGrid(x, y);
    }
    private void updateWorldGridFromRightToLeft(
            int xStart,
            int xEnd,
            int y)
    {
        for(int x=xEnd-1;x>=xStart; x--)
            updateWorldGrid(x, y);
    }

    private void updateWorldGrid(int x, int y)
    {
        switch(worldGrid[x][y].material)
        {
            case SAND:
                updateSand(x, y);
                break;
            case WATER:
                updateWater(x, y);
                break;
            default: break;
        }
    }

    private void updateSand(int x, int y)
    {
        if(y-1 >=0)
        {
            if(worldGrid[x][y-1].material == Materials.AIR)
            {
                swapGridValues(x, y, x, y-1);
            }
            else if(worldGrid[x][y-1].material == Materials.WATER)
            {
                if (Math.random()> 0.7f)
                    swapGridValues(x, y, x, y-1);
            }
            else if(x-1 >= 0 && worldGrid[x-1][y-1].material != Materials.SAND && worldGrid[x-1][y-1].material != Materials.STEEL)
            {
                swapGridValues(x, y, x-1, y-1);
            }
            else if(x+1 < xBoxesNum && worldGrid[x+1][y-1].material != Materials.SAND && worldGrid[x+1][y-1].material != Materials.STEEL)
            {
                swapGridValues(x, y, x+1, y-1);
            }
        }
    }

    private void updateWater(int x, int y)
    {
        if(y-1 >=0)
        {
            if (worldGrid[x][y - 1].material == Materials.AIR)
                swapGridValues(x, y, x, y - 1);

            else if (isWaterRightDownMove(x, y))
                moveWaterRightDown(x, y);

            else if (isWaterLeftDownMove(x, y))
                moveWaterLeftDown(x, y);

            else if (isWaterRightMove(x, y))
                moveWaterRight(x, y);

            else if (isWaterLeftMove(x, y))
                moveWaterLeft(x, y);
        }

    }

    private boolean isWaterLeftDownMove(int x, int y)
    {
        return x-1 >= 0 && worldGrid[x - 1][y - 1].material == Materials.AIR;
    }

    private boolean isWaterRightDownMove(int x, int y)
    {
        return x+1 < xBoxesNum && worldGrid[x+1][y-1].material == Materials.AIR;
    }

    private boolean isWaterRightMove(int x, int y)
    {
        return x+1 < xBoxesNum && worldGrid[x+1][y].material == Materials.AIR && worldGrid[x+1][y-1].material != Materials.AIR;
    }

    private boolean isWaterLeftMove(int x, int y)
    {
        return x-1 >= 0 && worldGrid[x-1][y].material == Materials.AIR && worldGrid[x-1][y-1].material != Materials.AIR;
    }
    private void moveWaterLeftDown(int x, int y)
    {

        swapGridValues(x, y, x-1, y-1);
    }

    private void moveWaterRightDown(int x, int y)
    {

        swapGridValues(x, y, x+1, y-1);
    }

    private void moveWaterRight(int x, int y)
    {

        swapGridValues(x, y, x+1, y);
    }

    private void moveWaterLeft(int x, int y)
    {
        swapGridValues(x, y, x-1, y);
    }

    private void swapGridValues(int x, int y, int i, int j)
    {
        Materials element_buff = worldGrid[x][y].material;
        Color color_buff = worldGrid[x][y].color;

        worldGrid[x][y].material = worldGrid[i][j].material;
        worldGrid[x][y].color = worldGrid[i][j].color;
        worldGrid[i][j].material = element_buff;
        worldGrid[i][j].color = color_buff;
        addParticleToDrawList(x, y);
        addParticleToDrawList(i, j);
    }

    private Color generateNewColor(Materials currentMaterial)
    {
        switch(currentMaterial)
        {
            case AIR:
                return new Color(0.223f, 0.176f, 0.176f, 1);
            case SAND:
                return new Color(0949f+(float)(Math.random()/10-0.05f), 0.899f+(float)(Math.random()/5-0.1f), 0.184f+(float)(Math.random()/5-0.1f), 1);
            case WATER:
                return new Color(0.2f+(float)(Math.random()/5-0.1f), 0.635f+(float)(Math.random()/5-0.1f), 0.858f+(float)(Math.random()/5-0.1f), 1);
            case STEEL:
                return Color.LIGHT_GRAY;
        }
        return new Color(0.223f, 0.176f, 0.176f, 1);
    }

    private void addParticleToDrawList(int x, int y)
    {
        particlesToDraw.add(worldGrid[x][y]);
        updateIsWorldGridPartStationary(x, y);
    }

    private void updateIsWorldGridPartStationary(int x, int y)
    {

        int xPart = x / WorldGridPartsXPartLen;
        int yPart = y / WorldGridPartsYPartLen;

        isWorldGridPartStationary[xPart][yPart] = false;
        if(xPart > 0)
        {
            isWorldGridPartStationary[xPart-1][yPart] = false;
            if(yPart > 0)
                isWorldGridPartStationary[xPart-1][yPart-1] = false;
            if(yPart < WorldGridPartsYPartsNum-1)
                isWorldGridPartStationary[xPart-1][yPart+1] = false;
        }
        if(xPart < WorldGridPartsXPartsNum-1)
        {
            isWorldGridPartStationary[xPart+1][yPart] = false;
            if(yPart < WorldGridPartsYPartsNum-1)
                isWorldGridPartStationary[xPart+1][yPart+1] = false;
            if(yPart > 0)
                isWorldGridPartStationary[xPart+1][yPart-1] = false;
        }
        if(yPart < WorldGridPartsYPartsNum-1)
            isWorldGridPartStationary[xPart][yPart+1] = false;
        if(yPart > 0)
            isWorldGridPartStationary[xPart][yPart-1] = false;
    }

}

package com.mygdx.sandsimulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;

public class SandSimulation extends ApplicationAdapter
{
	private static final float SCALE = 2.0f;
	public static final float PIXEL_PER_METER = 32f;
	private float timeSinceLastRender = 0;
	ShapeRenderer shapeRenderer;
	int yBoxesNum = 130;
	int xBoxesNum = 80;
	int currentMaterial = 1;
	int[][] worldGrid;
	int particleSpeed = 1;
	int sideChanger = -1;
	int updateX = 0;
	int updateY = 0;


	private OrthographicCamera orthographicCamera;
	@Override
	public void create ()
	{
		shapeRenderer = new ShapeRenderer();
		orthographicCamera = new OrthographicCamera();
		orthographicCamera.setToOrtho(
				false, Gdx.graphics.getWidth() / SCALE,
				Gdx.graphics.getHeight() / SCALE);
		worldGrid = new int[xBoxesNum][yBoxesNum];
		for(int y=0; y<yBoxesNum; y++)
			for(int x=0;x< xBoxesNum; x++)
				worldGrid[x][y] = 0;

		Gdx.input.setInputProcessor(new InputAdapter()
		{

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button)
			{
				if (currentMaterial == 2)
					currentMaterial = 1;
				else currentMaterial = 2;
				return true;
			}
		});
	}

	@Override
	public void render ()
	{
//		timeSinceLastRender+=Gdx.graphics.getDeltaTime();
//		if(timeSinceLastRender > 1 )
//		{
//			timeSinceLastRender = 0;
//
//		}
		sideChanger *= -1;
		updateWorld();
		Gdx.gl.glClearColor(0.223f, 0.176f, 0.176f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float boxWidth = Gdx.graphics.getWidth()/(float)xBoxesNum;
		float boxHeight = Gdx.graphics.getHeight()/(float)yBoxesNum;
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		for(int y=0; y<yBoxesNum; y++)
			for(int x=0;x< xBoxesNum; x++)
			{
				switch (worldGrid[x][y])
				{
					case 0:
						shapeRenderer.setColor(0.223f, 0.176f, 0.176f, 1f);
						break;
					case 1:
						shapeRenderer.setColor(0.737f, 0.529f, 0.043f, 1);
						break;
					case 2:
						shapeRenderer.setColor(0.043f, 0.737f, 0.701f, 1);
						break;
				}
				shapeRenderer.box(x * boxWidth, y * boxHeight, 0, boxWidth, boxHeight, 0);
			}
		shapeRenderer.end();


	}
	@Override
	public void resize(int width, int height)
	{
		orthographicCamera.setToOrtho(false, width / SCALE, height / SCALE);
	}
	@Override
	public void dispose()
	{
		shapeRenderer.dispose();
	}

	private void updateWorld()
	{
		int touchX;
		int touchY;
		if (Gdx.input.isTouched())
		{
			float boxWidth = Gdx.graphics.getWidth()/(float)xBoxesNum;
			float boxHeight = Gdx.graphics.getHeight()/(float)yBoxesNum;
			touchX = Gdx.input.getX();
			touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
			int grainRad = (int)(Math.random() * 10);
			for(int i=0; i<=10; i++)
			{
				int new_x = (int)((touchX-grainRad + (int)(Math.random() * ((grainRad + grainRad) + 1)))/boxWidth);
				int new_y = (int)((touchY-grainRad + (int)(Math.random() * ((grainRad + grainRad) + 1)))/boxHeight);
				if(new_x >=0 && new_x < xBoxesNum && new_y >= 0 && new_y <= yBoxesNum)
					worldGrid[new_x][new_y] = currentMaterial;
			}
		}
		if(sideChanger < 0)
		for(int updateY=0; updateY<yBoxesNum; updateY++)
			for(int updateX=0;updateX< xBoxesNum; updateX++)
				switch(worldGrid[updateX][updateY])
				{
					case 0:
						updateEmpty();
					break;
					case 1:
						updateSand(updateX, updateY);
					break;
					case 2:
						updateWater(updateX, updateY);
					break;
				}
		else
		{
			for(int updateY=0; updateY<yBoxesNum; updateY++)
				for(int updateX=xBoxesNum-1;updateX>=0; updateX--)
					switch(worldGrid[updateX][updateY])
					{
						case 0:
							updateEmpty();
							break;
						case 1:
							updateSand(updateX, updateY);
							break;
						case 2:
							updateWater(updateX, updateY);
							break;
					}
		}
	}
	private void updateEmpty()
	{
		;
	}

	private void updateSand(int x, int y)
	{
		if(y-1 >=0)
		{
			if(worldGrid[x][y-1] != 1)
			{
				swapGridValues(x, y, x, y-1);
			}
			else if(x-1 >= 0 && worldGrid[x-1][y-1] != 1)
			{
				int maxMove;
				for(maxMove = 1; maxMove <= particleSpeed; maxMove++)
				{
					if(x-maxMove < 0 || y-maxMove < 0)
						break;
					if(worldGrid[x-maxMove][y-maxMove] == 1)
						break;
				}
				maxMove--;
				swapGridValues(x, y, x-maxMove, y-maxMove);
			}
			else if(x+1 < xBoxesNum && worldGrid[x+1][y-1] != 1)
			{
				int maxMove;
				for(maxMove = 1; maxMove <= particleSpeed; maxMove++)
				{
					if(x+maxMove >= xBoxesNum || y-maxMove < 0)
						break;
					if(worldGrid[x+maxMove][y-maxMove] == 1)
						break;
				}
				maxMove--;
				swapGridValues(x, y, x+maxMove, y-maxMove);
			}
		}
	}

	private void updateWater(int x, int y)
	{
		if(y-1 >=0)
		{
			if (worldGrid[x][y - 1] == 0)
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
		return x-1 >= 0 && worldGrid[x - 1][y - 1] == 0;
	}

	private boolean isWaterRightDownMove(int x, int y)
	{
		return x+1 < xBoxesNum && worldGrid[x+1][y-1] == 0;
	}

	private boolean isWaterRightMove(int x, int y)
	{
		return x+1 < xBoxesNum && worldGrid[x+1][y] == 0 && worldGrid[x+1][y-1] != 0;
	}

	private boolean isWaterLeftMove(int x, int y)
	{
		return x-1 >= 0 && worldGrid[x-1][y] == 0 && worldGrid[x-1][y-1] != 0;
	}
	private void moveWaterLeftDown(int x, int y)
	{
		int maxMove;
		for(maxMove = 1; maxMove <= particleSpeed+1; maxMove++)
		{
			if(x-maxMove < 0 || y-maxMove < 0)
				break;
			if(!(worldGrid[x-maxMove][y-maxMove] == 0))
				break;
		}
		maxMove--;
		swapGridValues(x, y, x-maxMove, y-maxMove);
	}

	private void moveWaterRightDown(int x, int y)
	{
		int maxMove;
		for(maxMove = 1; maxMove <= particleSpeed+1; maxMove++)
		{
			if(x+maxMove >= xBoxesNum || y-maxMove < 0)
				break;
			if(!(worldGrid[x+maxMove][y-maxMove] == 0))
				break;
		}
		maxMove--;
		swapGridValues(x, y, x+maxMove, y-maxMove);
	}

	private void moveWaterRight(int x, int y)
	{
		int maxMove;
		for(maxMove = 1; maxMove <= particleSpeed+3; maxMove++)
		{
			if(x+maxMove >= xBoxesNum)
				break;
			if(worldGrid[x+maxMove][y] != 0)
				break;
		}
		maxMove--;
		swapGridValues(x, y, x+maxMove, y);
		if(sideChanger > 0 )
			updateX+=maxMove+1;
	}

	private void moveWaterLeft(int x, int y)
	{
		int maxMove;
		for(maxMove = 1; maxMove <= particleSpeed+3; maxMove++)
		{
			if(x-maxMove < 0)
				break;
			if(worldGrid[x-maxMove][y] != 0)
				break;
		}
		maxMove--;
		swapGridValues(x, y, x-maxMove, y);
		if(sideChanger < 0 )
			updateX-=maxMove+1;
	}

	private void swapGridValues(int x, int y, int i, int j)
	{
		int buff;
		buff = worldGrid[x][y];
		worldGrid[x][y] = worldGrid[i][j];
		worldGrid[i][j] = buff;
	}
}
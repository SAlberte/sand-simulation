package com.mygdx.sandsimulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.LinkedList;
import java.util.ListIterator;

import myutils.ParticleDrawInfo;

public class SandSimulation extends ApplicationAdapter
{
	private static final float SCALE = 2.0f;
	public static final float PIXEL_PER_METER = 32f;
	ShapeRenderer shapeRenderer;
	int yBoxesNum;
	int xBoxesNum;
	float boxWidth;
	float boxHeight;
	int currentMaterial = 1;
	ParticleDrawInfo[][] worldGrid;
	int particleSpeed = 1;
	int sideChanger = -1;
	int updateX = 0;
	int updateY = 0;
	LinkedList<ParticleDrawInfo> particlesToDraw = new LinkedList<>();
	FPSLogger fps;
	FrameBuffer fbo;
	SpriteBatch batch;
	Rectangle sandButton;
	Rectangle waterButton;
	Rectangle eraseButton;
	Rectangle steelButton;





	private OrthographicCamera orthographicCamera;
	@Override
	public void create ()
	{
		sandButton = new Rectangle(
				(int)(0.1f*Gdx.graphics.getWidth()),
				(int)(0.9f*Gdx.graphics.getHeight()),
				(int)(Gdx.graphics.getWidth()/10f),
				(int)(Gdx.graphics.getWidth()/10f));
		waterButton = new Rectangle(
				(int)(0.25f*Gdx.graphics.getWidth()),
				(int)(0.9f*Gdx.graphics.getHeight()),
				(int)(Gdx.graphics.getWidth()/10f),
				(int)(Gdx.graphics.getWidth()/10f));
		steelButton = new Rectangle(
				(int)(0.4f*Gdx.graphics.getWidth()),
				(int)(0.9f*Gdx.graphics.getHeight()),
				(int)(Gdx.graphics.getWidth()/10f),
				(int)(Gdx.graphics.getWidth()/10f));
		eraseButton = new Rectangle(
				(int)(0.55f*Gdx.graphics.getWidth()),
				(int)(0.9f*Gdx.graphics.getHeight()),
				(int)(Gdx.graphics.getWidth()/10f),
				(int)(Gdx.graphics.getWidth()/10f));

		yBoxesNum = Gdx.graphics.getHeight()/6;
		xBoxesNum = Gdx.graphics.getWidth()/6;
		boxWidth = Gdx.graphics.getWidth()/(float)xBoxesNum;
		boxHeight = Gdx.graphics.getHeight()/(float)yBoxesNum;
		fps = new FPSLogger();
		shapeRenderer = new ShapeRenderer();
		fbo = new FrameBuffer(
				Pixmap.Format.RGBA8888,
				Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(),
				false);

		batch = new SpriteBatch();
		orthographicCamera = new OrthographicCamera();
		orthographicCamera.setToOrtho(
				false, Gdx.graphics.getWidth() / SCALE,
				Gdx.graphics.getHeight() / SCALE);
		worldGrid = new ParticleDrawInfo[xBoxesNum][yBoxesNum];
		for(int y=0; y<yBoxesNum; y++)
			for(int x=0;x< xBoxesNum; x++)
			{
				worldGrid[x][y] = new ParticleDrawInfo(
						x,
						y,
						0,
						new Color(0.223f, 0.176f, 0.176f, 1));
			}


		Gdx.input.setInputProcessor(
				new InputAdapter()
		{
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button)
			{
				float touchX = Gdx.input.getX();
				float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
				if (eraseButton.contains(touchX, touchY))
					currentMaterial = 0;
				if (sandButton.contains(touchX, touchY))
					currentMaterial = 1;
				if (waterButton.contains(touchX, touchY))
					currentMaterial = 2;
				if (steelButton.contains(touchX, touchY))
					currentMaterial = 3;
				return true;
			}
			});

			Gdx.gl.glClearColor(0.223f, 0.176f, 0.176f, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			fbo.begin();
					shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
					shapeRenderer.setColor(Color.YELLOW);
					shapeRenderer.box(
							sandButton.x,
							sandButton.y,
							0,
							sandButton.width,
							sandButton.height,
							0
					);
					shapeRenderer.setColor(Color.BLUE);
					shapeRenderer.box(
							waterButton.x,
							waterButton.y,
							0,
							waterButton.width,
							waterButton.height,
							0
					);
					shapeRenderer.setColor(Color.LIGHT_GRAY);
					shapeRenderer.box(
					steelButton.x,
					steelButton.y,
					0,
					steelButton.width,
					steelButton.height,
					0
					);
					shapeRenderer.setColor(Color.RED);
					shapeRenderer.box(
							eraseButton.x,
							eraseButton.y,
							0,
							eraseButton.width,
							eraseButton.height,
							0
					);
					shapeRenderer.end();
			fbo.end();

	}

	@Override
	public void render ()
	{
		long start = System.currentTimeMillis();
		updateWorld();
		long finish = System.currentTimeMillis();
		System.out.println("Update Time");
		System.out.println(finish - start);

		start = System.currentTimeMillis();
		Gdx.gl.glClearColor(0.223f, 0.176f, 0.176f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(fbo.getColorBufferTexture(),
		0,
		0,
			Gdx.graphics.getWidth(),
			Gdx.graphics.getHeight(),
		0,
		0,
			Gdx.graphics.getWidth(),
			Gdx.graphics.getHeight(),
		false,
		true);

		batch.end();
		fbo.begin();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		for(ParticleDrawInfo particle : particlesToDraw)
		{
			shapeRenderer.setColor(particle.color);
			shapeRenderer.box(
						particle.x * boxWidth,
				     	particle.y * boxHeight,
						0, boxWidth, boxHeight,
						0);

		}
		particlesToDraw.clear();
		shapeRenderer.end();
		fbo.end();

		finish = System.currentTimeMillis();
		System.out.println("czas rysowania");
		System.out.println(finish - start);
		fps.log();
	}
	@Override
	public void resize(int width, int height)
	{
		orthographicCamera.setToOrtho(false, width / SCALE, height / SCALE);
	}
	@Override
	public void dispose()
	{
		fbo.dispose();

		shapeRenderer.dispose();
	}

	private void updateWorld()
	{
		int touchX;
		int touchY;
		sideChanger *= -1;
		if (Gdx.input.isTouched())
		{
			float boxWidth = Gdx.graphics.getWidth()/(float)xBoxesNum;
			float boxHeight = Gdx.graphics.getHeight()/(float)yBoxesNum;
			touchX = Gdx.input.getX();
			touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
			int grainRad = (int)(Math.random() * 20);
			for(int i=0; i<=50; i++)
			{
				int new_x = (int)((touchX-grainRad + (int)(Math.random() * ((grainRad + grainRad) + 1)))/boxWidth);
				int new_y = (int)((touchY-grainRad + (int)(Math.random() * ((grainRad + grainRad) + 1)))/boxHeight);
				if(new_x >=0 && new_x < xBoxesNum && new_y >= 0 && new_y*boxHeight <sandButton.y-sandButton.height )
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
		if(sideChanger < 0)
		for(int updateY=0; updateY<yBoxesNum; updateY++)
			for(int updateX=0;updateX< xBoxesNum; updateX++)
				switch(worldGrid[updateX][updateY].element)
				{
					case 1:
						updateSand(updateX, updateY);
					break;
					case 2:
						updateWater(updateX, updateY);
					break;
					default:
						break;
				}
		else
		{
			for(int updateY=0; updateY<yBoxesNum; updateY++)
				for(int updateX=xBoxesNum-1;updateX>=0; updateX--)
					switch(worldGrid[updateX][updateY].element)
					{
						case 1:
							updateSand(updateX, updateY);
							break;
						case 2:
							updateWater(updateX, updateY);
							break;
						default: break;
					}
		}
	}

	private void updateSand(int x, int y)
	{
		if(y-1 >=0)
		{
			if(worldGrid[x][y-1].element == 0 || worldGrid[x][y-1].element == 2)
			{
				swapGridValues(x, y, x, y-1);
			}
			else if(x-1 >= 0 && worldGrid[x-1][y-1].element != 1 && worldGrid[x-1][y-1].element != 3)
			{
				swapGridValues(x, y, x-1, y-1);
			}
			else if(x+1 < xBoxesNum && worldGrid[x+1][y-1].element != 1 && worldGrid[x+1][y-1].element != 3)
			{
				swapGridValues(x, y, x+1, y-1);
			}
		}
	}

	private void updateWater(int x, int y)
	{
		if(y-1 >=0)
		{
			if (worldGrid[x][y - 1].element == 0)
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
		return x-1 >= 0 && worldGrid[x - 1][y - 1].element == 0;
	}

	private boolean isWaterRightDownMove(int x, int y)
	{
		return x+1 < xBoxesNum && worldGrid[x+1][y-1].element == 0;
	}

	private boolean isWaterRightMove(int x, int y)
	{
		return x+1 < xBoxesNum && worldGrid[x+1][y].element == 0 && worldGrid[x+1][y-1].element != 0;
	}

	private boolean isWaterLeftMove(int x, int y)
	{
		return x-1 >= 0 && worldGrid[x-1][y].element == 0 && worldGrid[x-1][y-1].element != 0;
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
		int element_buff = worldGrid[x][y].element;
		Color color_buff = worldGrid[x][y].color;

		worldGrid[x][y].element = worldGrid[i][j].element;
		worldGrid[x][y].color = worldGrid[i][j].color;
		worldGrid[i][j].element = element_buff;
		worldGrid[i][j].color = color_buff;
		particlesToDraw.add(worldGrid[x][y]);
		particlesToDraw.add(worldGrid[i][j]);
	}

	private Color generateNewColor(int currentMaterial)
	{
		switch(currentMaterial)
		{
			case 0:
				return new Color(0.223f, 0.176f, 0.176f, 1);
			case 1:
				return new Color(0949f+(float)(Math.random()/10-0.05f), 0.899f+(float)(Math.random()/5-0.1f), 0.184f+(float)(Math.random()/5-0.1f), 1);
			case 2:
				return new Color(0.2f+(float)(Math.random()/5-0.1f), 0.635f+(float)(Math.random()/5-0.1f), 0.858f+(float)(Math.random()/5-0.1f), 1);
			case 3:
				return Color.LIGHT_GRAY;
		}
		return new Color(0.223f, 0.176f, 0.176f, 1);
	}
	private void addParticleToDrawList(int x, int y)
	{
		particlesToDraw.add(worldGrid[x][y]);
	}
}
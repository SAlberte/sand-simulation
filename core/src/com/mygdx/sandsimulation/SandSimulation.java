package com.mygdx.sandsimulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import java.util.LinkedList;

import myutils.Button;
import myutils.MaterialBrushes;
import myutils.MaterialButtons;
import myutils.ParticleDrawInfo;
import myutils.Materials;

public class SandSimulation extends ApplicationAdapter
{
	private static final float SCALE = 2.0f;
	private ShapeRenderer shapeRenderer;
	private FrameBuffer fbo;
	private SpriteBatch batch;
	private MaterialButtons materialButtons;
	private Button eraseButton;
	private SandPit sandPit;


	private OrthographicCamera orthographicCamera;
	@Override
	public void create ()
	{
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

		materialButtons = new MaterialButtons();
		eraseButton = new Button(
				(int)(0.85f* Gdx.graphics.getWidth()),
				(int)(0.9f*Gdx.graphics.getHeight()),
				(int)(Gdx.graphics.getWidth()/10f),
				(int)(Gdx.graphics.getWidth()/10f),
				Color.RED);

		sandPit = new SandPit(materialButtons.getTopUIEdge());
		Gdx.input.setInputProcessor(
				new InputAdapter()
		{
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button)
			{
				float touchX = Gdx.input.getX();
				float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

				if (materialButtons.contains(Materials.AIR, touchX, touchY))
					sandPit.currentMaterial = Materials.AIR;
				if (materialButtons.contains(Materials.SAND, touchX, touchY))
					sandPit.currentMaterial = Materials.SAND;
				if (materialButtons.contains(Materials.WATER, touchX, touchY))
					sandPit.currentMaterial = Materials.WATER;
				if (materialButtons.contains(Materials.STEEL, touchX, touchY))
					sandPit.currentMaterial = Materials.STEEL;
				if (eraseButton.contains(touchX, touchY))
				{
					sandPit.reset();
					drawInitialUI();
				}
				return true;
			}
			});
		drawInitialUI();

	}
	private void clearScreen()
	{
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0.223f, 0.176f, 0.176f, 1f);
		shapeRenderer.rect(
				0,
				0,
				Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		shapeRenderer.end();
	}

	private void drawInitialUI()
	{
		Gdx.gl.glClearColor(0.223f, 0.176f, 0.176f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		fbo.begin();
		clearScreen();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		drawMaterialButton(Materials.AIR);
		drawMaterialButton(Materials.SAND);
		drawMaterialButton(Materials.WATER);
		drawMaterialButton(Materials.STEEL);
		drawButton(eraseButton);

		shapeRenderer.end();
		fbo.end();
	}

	private void drawButton(Button button)
	{
		shapeRenderer.setColor(button.getColor());
		shapeRenderer.rect(
				button.getX(),
				button.getY(),
				button.getWidth(),
				button.getHeight()
		);
	}

	private void drawMaterialButton(Materials material)
	{
		shapeRenderer.setColor(materialButtons.getButtonColor(material));
		float[] posAndDims = materialButtons.getPosAndDims(material);
		shapeRenderer.rect(
				posAndDims[0],
				posAndDims[1],
				posAndDims[2],
				posAndDims[3]
				);
	}


	@Override
	public void render ()
	{
		for(int i=0;i<2;i++)
			sandPit.update();

		Gdx.gl.glClearColor(0.223f, 0.176f, 0.176f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

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
		for(ParticleDrawInfo particle : sandPit.particlesToDraw)
		{
			shapeRenderer.setColor(particle.color);
			shapeRenderer.rect(
						particle.x * sandPit.boxWidth,
				     	particle.y * sandPit.boxHeight,
						sandPit.boxWidth,
						sandPit.boxHeight);

		}
		sandPit.particlesToDraw.clear();
		shapeRenderer.end();
		fbo.end();
	}
	@Override
	public void resize(int width, int height)
	{
		orthographicCamera.setToOrtho(
				false,
				width / SCALE,
				height / SCALE);
	}
	@Override
	public void dispose()
	{
		fbo.dispose();

		shapeRenderer.dispose();
	}

}
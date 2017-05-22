package app;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;

public class EndlessGame extends BasicGame
{
	static final int zoomChangeRate = 2;
	private static final int SCREEN_HEIGHT = 1024;
	private static final int SCREEN_WIDTH = 1024;
	private final int startingGridSize = 32;
	Player player;
	Map currentMap;
	int lastMapCreate = 0;
	int level = 0;
	private boolean colorChangeRateInFlux = false;
	boolean zoomLevelInFlux;
	
	public static Random random;
	
	static int zoomLevel = 20;	
	static int colorChangeRate = 50;
	
	public EndlessGame(String gamename)
	{
		super(gamename);
		random = new Random(System.currentTimeMillis());
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		player = new Player();
		currentMap = new Map(SCREEN_WIDTH, SCREEN_HEIGHT, startingGridSize);
	}

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		processInput(gc.getInput(), delta);
	}

	private void processInput(Input input, int delta) throws SlickException {
		lastMapCreate += delta;
		// Process adjustments to color change rate
		if (input.isKeyDown(Input.KEY_UP) ) {
			adjustColorChangeRate(1);
		}
		else if (input.isKeyDown(Input.KEY_DOWN)) {
			adjustColorChangeRate(-1);
		}
		else if (colorChangeRateInFlux){
			colorChangeRateInFlux = false;
		}
		// Process adjustments to zoom level
		if (input.isKeyDown(Input.KEY_LEFT)) {
			currentMap.adjustZoomLevel(this, 1);
		}
		else if (input.isKeyDown(Input.KEY_RIGHT)) {
			currentMap.adjustZoomLevel(this, -1);
		}
		else if (zoomLevelInFlux) {
			zoomLevelInFlux = false;
		}
		
		// Go down one
		if (input.isKeyDown(Input.KEY_X)) {
			if (makeNewMap(true, delta)) {
				level++;
			}
		}
		// Go down one
		else if (input.isKeyDown(Input.KEY_Z)) {
			if (makeNewMap(false, delta)) {
				level--;
			}
		}
		else currentMap.move(input, delta);
		currentMap.updateTiles(delta);
	}

	private void adjustColorChangeRate(int change) {
		if (!colorChangeRateInFlux) {
			colorChangeRate += change;
			System.out.println("Color change rate = " + colorChangeRate);
			colorChangeRateInFlux = true;
		}
	}
	
	/**
	 * 
	 * @param down	True if this map is below the current map level, false if it is above.
	 * @return
	 * @throws SlickException
	 */
	private boolean makeNewMap(boolean down, int delta) throws SlickException {
		if (lastMapCreate > 1000) {
			lastMapCreate = 0;
			int newMapSize = down ? zoomLevel * 2 : zoomLevel / 2;
			currentMap = new Map(SCREEN_WIDTH, SCREEN_HEIGHT, newMapSize);
			return true;
		}
		return false;
	}
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		g.drawImage(currentMap.getMapImage(), 0, 0);
		g.setColor(Color.black);
		g.fillOval(SCREEN_WIDTH / 2 - 10, SCREEN_HEIGHT / 2 - 10, 20, 20);
		g.setColor(Color.white);
		g.drawString("LEVEL: " + level, 10, 30);
		g.drawString("TILES: " + currentMap.getTotalTileCount(), 10, 45);
		g.drawString("VISIBLE: " + currentMap.getVisibleTileCount(), 10, 60);
		g.drawString("CHANGE RATE: " + colorChangeRate, 10, 75);
		g.drawString("ZOOM: " + zoomLevel, 10, 90);
	}

	public static void main(String[] args)
	{
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new EndlessGame("Endless"));
			appgc.setDisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT, false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(EndlessGame.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
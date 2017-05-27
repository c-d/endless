package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class Map {

	private static final int COLOR_CHANGE_RATE = 300;

	// How frequently to process movement input
	private final int MOVE_DELAY = 20;
	
	// Map dimensions
	private int width;
	private int height;
	
	private Image image;
	private Graphics graphics;
	private ArrayList<ArrayList<Tile>> grid;
	
	// The 'window' view frame onto the map
	private int visibleXStart = 0;
	private int visibleXEnd;
	private int visibleYStart = 0;
	private int visibleYEnd;
	
	private long lastMove = 0;

	private int lastTileColorChange;
	

	public Map(int width, int height, int gridSize) throws SlickException {
		this.width = width;
		this.height = height;
		EndlessGame.zoomLevel = gridSize;
		image = new Image(width, height);
		graphics = image.getGraphics();
		
		createVisibleGrid();
	}
	
	private void createVisibleGrid() {
		grid = new ArrayList<ArrayList<Tile>>();
		for (int x = 0; x < width / EndlessGame.zoomLevel; x++) {
			ArrayList<Tile> col = new ArrayList<Tile>();
			for (int y = 0; y < height / EndlessGame.zoomLevel; y++) {
				// Start blank tiles.
				col.add(new Tile());
			}
			grid.add(x, col);
		}
		visibleXEnd = width / EndlessGame.zoomLevel;
		visibleYEnd = height / EndlessGame.zoomLevel;
	}

	public Image getMapImage() throws SlickException {
		//graphics.setBackground(new Color(0,50,50,255));
		int avgR = 0, avgG = 0, avgB = 0;
		graphics.clear();
		
		int renderX = 0;
		int renderY = 0;
		for (int x = visibleXStart; x < visibleXEnd; x++) {
			renderY = 0;
			for (int y = visibleYStart; y < visibleYEnd; y++) {
				Tile tile = grid.get(x).get(y);
				// Apply a random adjustment to the color of every tile, ever render
				avgR += tile.getColor().getRed();
				avgG += tile.getColor().getGreen();
				avgB += tile.getColor().getBlue();
				graphics.setColor(tile.getColor());
				graphics.fillRect(renderX * EndlessGame.zoomLevel,  renderY * EndlessGame.zoomLevel,  EndlessGame.zoomLevel,  EndlessGame.zoomLevel);
				renderY++;
				// After it's rendered, we clear the focus of a tile. It will be reset again before the next render if needed...
				tile.setHasFocus(false);
			}
			renderX++;
		}
		int tileCount = getVisibleTileCount();
		avgR = avgR / tileCount;
		avgG = avgG / tileCount;
		avgB = avgB / tileCount;
		int avgColor = avgR + avgG + avgB;
		//System.out.println("Average color = " + avgColor);
		
		//System.out.println(renderX + "," + renderY);
		graphics.flush();
		return image;
	}
	
	public void move(Input input, int delta) {
		// Only move if an acceptable amount of time has elapsed since we last processed movement
		lastMove += delta; 
		if (lastMove > MOVE_DELAY) {
			// Reset the move clock
			lastMove = 0;
			int tileCount = Tile.number;
			boolean changed = false;
			if (input.isKeyDown(Input.KEY_W)) {
				moveUp();
				changed = true;
				MidiPlayer.getInstance().play(Note.A);
			}
			else if (input.isKeyDown(Input.KEY_S)) {
				changed = true;
				moveDown();
				MidiPlayer.getInstance().play(Note.B);
			}
			if (input.isKeyDown(Input.KEY_A)) {
				changed = true;
				moveLeft();
				MidiPlayer.getInstance().play(Note.C);
			}
			else if (input.isKeyDown(Input.KEY_D)) {
				changed = true;
				moveRight();
				MidiPlayer.getInstance().play(Note.D);
			}
			/*
			if (changed) {
				System.out.println("Added " + (Tile.number - tileCount) + " new tiles.");
				System.out.println("Total tiles = " + Tile.number);
			}
			*/
		}
	}

	public void moveRight() {
		// If we're furthermost right, we need a new row to the right
		if (visibleXEnd == grid.size()) {
			addCol(grid.size());
		}
		// Now change the view window
		visibleXStart++;
		visibleXEnd++;
	}

	public void moveLeft() {
		// If we're furthermost left, we need a new row to the left
		if (visibleXStart == 0) {
			addCol(0);
		}
		else {
			// Otherwise just move the view to the left
			visibleXStart--;
			visibleXEnd--;
		}
	}

	public void moveDown() {
		if (visibleYEnd == grid.get(0).size()) {
			addRow(grid.get(0).size());
		}
		visibleYStart++;
		visibleYEnd++;
	}

	public void moveUp() {
		// If we're furthermost top, we need a new row at the top
		if (visibleYStart == 0) {
			addRow(0);
		}
		else {
			// Otherwise just move the view up
			visibleYStart--;
			visibleYEnd--;
		}
	}
	
	public void addTilesIfNeeded() {
		while (visibleYStart < 0) {
			addRow(0);
			visibleYStart++;
			visibleYEnd++;
		}
		while (visibleYEnd > grid.get(0).size()) {
			addRow(grid.get(0).size());
		}
		while (visibleXStart < 0) {
			addCol(0);
			visibleXStart++;
			visibleXEnd++;
		}
		while (visibleXEnd > grid.size()) {
			addCol(grid.size());
		}
	}

	private void addRow(int y) {
		int yOffset = 0;
		if (y == 0) {	// Adding row at top
			yOffset = y + 1;
		}
		else if (y == grid.get(0).size()) { // Adding row at bottom
			yOffset = y - 1;
		}
		
		for (int x = 0; x < grid.size(); x++) {
			ArrayList<Tile> col = grid.get(x);
			ArrayList<Tile> nearbyTiles = new ArrayList<Tile>();
			
			if (!grid.isEmpty()) {
				// Tile left/below
				if (x != 0) {
					nearbyTiles.add(grid.get(x - 1).get(yOffset));
				}
				// Tile below
				nearbyTiles.add(grid.get(x).get(yOffset));
				// Tile right/below
				if (x != grid.size() - 1) {
					nearbyTiles.add(grid.get(x + 1).get(yOffset));
				}
			}
			
			col.add(y, new Tile(nearbyTiles));
		}
	}

	private void addCol(int x) {
		ArrayList<Tile> previousCol = null;
		if (!grid.isEmpty()) {
			if (x == 0) 
				previousCol = grid.get(0);
			if (x == grid.size()) 
				previousCol = grid.get(x - 1);
		}
		
		ArrayList<Tile> col = new ArrayList<Tile>();
		for (int y = 0; y < grid.get(0).size(); y++) {
			// Get nearby tiles so that we can generate a similar color
			ArrayList<Tile> nearbyTiles = new ArrayList<Tile>();
			if (previousCol != null) {
				// Tile before
				if (y != 0) nearbyTiles.add(previousCol.get(y - 1));
				// Adjacent tile
				nearbyTiles.add(previousCol.get(y));
				// Tile after
				if (y != previousCol.size() - 1) nearbyTiles.add(previousCol.get(y + 1));
				// Previous tile in the new column
				if (y != 0) nearbyTiles.add(col.get(col.size() - 1));
			}
			col.add(new Tile(nearbyTiles));
		}
		grid.add(x, col);
	}
	
	public int getTotalTileCount() {
		int sum = 0;
		for (ArrayList<Tile> col : grid) {
			sum += col.size();
		}
		return sum;
	}
	
	public int getVisibleTileCount() {
		return (visibleXEnd - visibleXStart) * (visibleYEnd - visibleYStart);
	}

	/**
	 * Zoom the view in and out.
	 * @param endlessGame TODO
	 * @param change Positive values increase zoom level, negative values decrease zoom level. Magnitude has no impact.
	 */
	void adjustZoomLevel(EndlessGame endlessGame, int change) {
		if (!endlessGame.zoomLevelInFlux && 
				(EndlessGame.zoomLevel > 1 || change > 0)) { // If we are at 1, can only go up, not down
			
			int visibleX = ((visibleXEnd - visibleXStart) / EndlessGame.zoomChangeRate);
			int visibleY = ((visibleYEnd - visibleYStart) / EndlessGame.zoomChangeRate);
			
			if (change > 0) {	// Zoom in
				EndlessGame.zoomLevel = EndlessGame.zoomChangeRate * EndlessGame.zoomLevel;
				
				// Need to update the number of tiles that will be rendered
				int newVisibleXHalf = visibleX / 2;	// Number of tiles displayed / 2
				int newVisibleYHalf = visibleY / 2;
				
				visibleXStart += newVisibleXHalf;
				visibleXEnd -= newVisibleXHalf;
				
				visibleYStart += newVisibleYHalf;
				visibleYEnd -= newVisibleYHalf;
			}
			else {	// Zoom out
				EndlessGame.zoomLevel = EndlessGame.zoomLevel / EndlessGame.zoomChangeRate;
				
				// Need to update the number of tiles that will be rendered
				int newVisibleXDouble = visibleX;	// Number of tiles displayed * 2
				int newVisibleYDouble = visibleY;
				
				// Need to update the number of tiles that will be rendered (now we render fewer, larger tiles)
				visibleXStart -= newVisibleXDouble;
				visibleXEnd += newVisibleXDouble;
				
				visibleYStart -= newVisibleYDouble;
				visibleYEnd += newVisibleYDouble;
				// Since we're zooming out, there may be portions of the map which have not been created yet.
				addTilesIfNeeded();
			}
			System.out.println("Zoom level = " + EndlessGame.zoomLevel + 
					". Rendering range = " + visibleXStart + "," + visibleYStart + " - " + visibleXEnd + "," + visibleYEnd);
			endlessGame.zoomLevelInFlux = true;
		}
	}
	
	public void updateTiles(int delta) {
		lastTileColorChange += delta;
		if (lastTileColorChange > COLOR_CHANGE_RATE) {
			for (int x = visibleXStart; x < visibleXEnd; x++) {
				for (int y = visibleYStart; y < visibleYEnd; y++) {
					Tile tile = grid.get(x).get(y);
					tile.adjustColor();
				}
			}
		}
	}

	public void updatePlayerLocation(int mouseX, int mouseY) {
		int tileX = (int) mouseX / EndlessGame.zoomLevel;
		int tileY = (int) mouseY / EndlessGame.zoomLevel;
		grid.get(tileX).get(tileY).setHasFocus(true);
	}

}
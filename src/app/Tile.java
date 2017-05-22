package app;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;

public class Tile {
	
	private static Random rand = new Random();
	private static Color globalColor = new Color(150, 150, 150);
	
	private Color color;
	public static int number;

	/**
	 * Create a new tile. The color of the created tile will be randomized based on the average color of neighbouring tiles.
	 * @param nearbyTiles List of local tiles. 
	 */
	public Tile(ArrayList<Tile> nearbyTiles) {
		Color baseColor = nearbyTiles.isEmpty() ? globalColor : getAverageColor(nearbyTiles);
		color = getRandomColor(baseColor);	
		number++;
	}
	
	/**
	 * Create a blank (white) tile.
	 */
	public Tile() {
		color = new Color(0,0,0);
	}

	/**
	 * Get the average color of a set of tiles.
	 * @param neighbourTiles
	 * @return
	 */
	private static Color getAverageColor(ArrayList<Tile> neighbourTiles) {
		float div = neighbourTiles.size();
		float r = 0;
		float g = 0;
		float b = 0;
		for (Tile tile : neighbourTiles) {
			r += tile.getColor().getRed();
			g += tile.getColor().getGreen();
			b += tile.getColor().getBlue();
		}
		return new Color(Math.round(r / div), Math.round(g / div), Math.round(b / div));
	}

	/**
	 * Get a random color which is 'close' to a given color.
	 * @param baseColor Seed color which the returned color will be within the vicinity of.
	 * @return Random color which is close to baseColor, with it's proximity being determined by EndlessGame.colorChangeRate.
	 */
	private Color getRandomColor(Color baseColor) {
		int r = getRandomDiff(baseColor.getRed());
		int g = getRandomDiff(baseColor.getGreen());
		int b = getRandomDiff(baseColor.getBlue());
		globalColor = new Color(r, g, b);
		return globalColor;
	}
	
	private int getRandomDiff(int input) {
		int result = (input + (rand.nextInt() % EndlessGame.colorChangeRate));
		result = Math.max(result, 0);
		result = Math.min(result, 255);
		return result;
	}

	public Color getColor() {
		return color;
	}
	
	/**
	 * Randomly adjust color by minor increments, with no consideration of neighbouring tiles.
	 */
	public void adjustColor() {
		int r1 = EndlessGame.random.nextInt(3);
		int r2 = EndlessGame.random.nextInt(2) == 0 ? 1 : -1;
		switch (r1) {
			case 0: color = getRealColor(color.getRed() + r2, color.getGreen(), color.getBlue()); break;
			case 1: color = getRealColor(color.getRed(), color.getGreen() + r2, color.getBlue()); break;
			case 2: color = getRealColor(color.getRed(), color.getGreen(), color.getBlue() + r2); break;
		}
	}
	
	/**
	 * Given 3 integers, returns a color within a real range (0-255 RGB)
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	private Color getRealColor(int r, int g, int b) {
		if (r > 255) r = 255;
		if (g > 255) g = 255;
		if (b > 255) b = 255;
		if (r < 0) r = 0;
		if (g < 0) g = 0;
		if (b < 0) b = 0;
		return new Color(r, g, b);
	}

}

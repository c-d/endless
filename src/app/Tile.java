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
		color = new Color(255,255,255);
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

}

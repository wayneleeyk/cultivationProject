package com.potatoes.cultivation.utils;

import java.util.Random;

public class HexMap {
	
	//keeps track of the height and width of out hex map
	private int width;
	private int height;
	
	//private String mapShape;
	private String [][] array;
	
	//constructor
	public HexMap (int userW , int userH ){ 
		width = userW;
		height= userH;
		createMap();
		addRandomTiles();
	}
	
	//Initializes the array representation of the map
	private void createMap(){
		array = new String [width][height];
		
	}
	//getter
	public String [][] getArray(){
		return array;
	}
	
	
	private void addRandomTiles(){
		// determine how many trees and meadows are needed
		int numTrees = (int)(Math.round(height * width * .2));
		int numMeadow = (int)(Math.round(height * width * .1));
		int column = 0;
		int row = 0;
		//counter
		int i = 0;
		
		//first we have to surround the border with sea tiles
		//start with entire first row
		while(i < width){
			array[i][0] = "sea";
			i++;
		}
		//reset counter and now fill the first column
		i = 0;
		while(i < height){
			array[0][i] = "sea";
			i++;
		}
		//reset the counter and now fill the last column
		i = 0;
		while(i < height ){
			array[width-1][i] = "sea";
			i++;
		}
		//reset the counter and now fill the last row
		i = 0;
		while(i < width ){
			array[i][height-1] = "sea";
			i++;
		}
		//reset counter
		i = 0;
		
		//randomly insert said amount of trees and meadows into the 2D array map
		//First create random number generator
		Random rand = new Random();
		//insert trees
		while (i < numTrees){
			column = rand.nextInt(width);
			row = rand.nextInt(height);
			//only move on to setting the next tile if the tile is unnamed
			if(array[column][row] == null){
				array[column][row] = "tree";
				i++;
			}
		}
			
			//reset the counter
			i = 0;
		// now add the meadow tiles, same procedure but with meadows
			while (i < numMeadow){
				column = rand.nextInt(width);
				row = rand.nextInt(height);
				//only move on to setting the next tile if the tile is unnamed
				if(array[column][row] == null){
					array[column][row] = "meadow";
					i++;
				}
			}
			
			// change all the "null" spaces to read as grass
			for (int a = 0; a < width; a++){
				for (int b = 0; b < height; b++){
					if(array[a][b] == null){
						array[a][b] = "grass";
					}
				}
			}
	}
	
}


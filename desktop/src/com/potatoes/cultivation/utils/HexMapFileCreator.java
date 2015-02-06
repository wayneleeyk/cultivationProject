package com.potatoes.cultivation.utils;

//Run this file to create a map representation of the tiles. A txt file will be created in the working directory.
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class HexMapFileCreator {
	
	public static void main (String[] arg) {
		
		//prompt user for input
		Scanner user_input = new Scanner( System.in );
		System.out.println("Enter width: ");
		int width = user_input.nextInt();
		System.out.println("Enter height: ");
		int height = user_input.nextInt();
		user_input.close();
		
		//create the hexMap
		HexMap map = new HexMap(width,height);
		//map.createMap();
		//map.addRandomTiles();
		
		//now Let's print it to a file!
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("hexMapFile.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < width ; i++){
			
			for (int j = 0; j < height ; j++){
				writer.print(map.array[i][j] + ", ");
			}
			writer.println();
		}
		
		writer.close();
		
		
	}
	
	

}

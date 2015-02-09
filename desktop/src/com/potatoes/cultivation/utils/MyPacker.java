package com.potatoes.cultivation.utils;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
public class MyPacker {
    public static void main (String[] args) throws Exception {
    	// Pack the GUI related files into 1 atlas
        TexturePacker.process("../core/assets/gui", "../core/assets/", "gui");
        
        // Pack the in-game related texture into 1 atlas
        TexturePacker.process("../core/assets/ingame", "../core/assets/", "ingame");
    }
}
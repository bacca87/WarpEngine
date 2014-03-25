package com.marcobaccarani.src.warp.utils;

import com.badlogic.gdx.math.Vector2;

public class Utility {
	public static Vector2[] verticiesToV2Array(float[] vertices) {		
		Vector2[] v2Array = new Vector2[vertices.length/2];
		for(int v = 0, i = 0; i<vertices.length; i+=2, v++) {
			v2Array[v] = new Vector2(vertices[i], vertices[i+1]);
		}
		return v2Array;
	}
	
	public static float[] mulFloatArray(float[] array, float scalar) {	
		for(int i = 0; i < array.length; i++) {
			array[i] *= scalar;
		}
		
		return array;
	}
}

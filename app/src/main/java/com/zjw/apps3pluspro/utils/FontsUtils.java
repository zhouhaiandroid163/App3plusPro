package com.zjw.apps3pluspro.utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontsUtils {

	
	public static Typeface modefyNumber(Context context){
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/DINCond-Medium.otf");
		return typeface;
	}
	
	public static Typeface modefyLetter(Context context){
		Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/DIN-BoldAlternate.otf");
		return face;
	}

	public static Typeface modefyCHU(Context context){
		Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Medium.otf");
		return face;
	}

	public static Typeface modefyNormal(Context context){
		Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Book.otf");
		return face;
	}

	public static Typeface modefyXI(Context context){
		Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Light.otf");
		return face;
	}

	public static Typeface modefyEscape(Context context) {
		Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Escape.ttf");
		return face;
	}
}

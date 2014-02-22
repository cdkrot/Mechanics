package com.cdkrot.mechanics.util;

public class dirvec extends veci3_base
{
	public static final dirvec Yneg = new dirvec(  0, -1,  0);
	public static final dirvec Ypos = new dirvec(  0, +1,  0);
	public static final dirvec Zneg = new dirvec(  0,  0, -1);
	public static final dirvec Zpos = new dirvec(  0,  0, +1);
	public static final dirvec Xneg = new dirvec( -1,  0,  0);
	public static final dirvec Xpos = new dirvec( +1,  0,  0);
	public final static dirvec[] list = new dirvec[]{Yneg,Ypos,Zneg,Zpos,Xneg,Xpos};
	
	private dirvec(int x, int y, int z) {super(x,y,z);}
	
	public final boolean isFacingNegative = (x+y+z)<0;
	
}

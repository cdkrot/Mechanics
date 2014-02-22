package com.cdkrot.mechanics.util;

public class veci3_base
{
	public int x;
	public int y;
	public int z;

	public veci3_base(){x=0; y=0; z=0;}
	public veci3_base(int x, int y, int z){this.x = x; this.y = y; this.z = z;}
	
	public veci3_base clone(){return new veci3_base(x,y,z);}
	public veci3      cloneAsVeci3(){return new veci3(x,y,z);};
}

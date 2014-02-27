package com.cdkrot.mechanics.util;

public class vecf3
{
	public float x;
	public float y;
	public float z;
	
	public vecf3(){x=0f; y=0f; z=0f;}
	public vecf3(float x, float y, float z){this.x = x; this.y = y; this.z = z;}
	
	public vecf3 multiply(float m){x=m*x; y= m*y; z=m*z; return this;}
	public vecf3 add(veci3 s2){x+=s2.x; y+=s2.y; z+=s2.z; return this;}
	public vecf3 add(int x_, int y_, int z_){return this.add(new veci3(x_,y_,z_));}
	public vecf3 clone(){return new vecf3(x,y,z);}
	public String shortDescription(){return "Vecf3["+x+", "+y+", "+z+"]";};
	public vecf3 incAllByOne(){x++; y++; z++; return this;}
	public vecd3 tovecd3(){return new vecd3(x,y,z);}

}

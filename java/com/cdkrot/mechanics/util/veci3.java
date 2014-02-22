package com.cdkrot.mechanics.util;

public class veci3 extends veci3_base
{	
	public veci3(){super();}
	public veci3(int x, int y, int z){super(x,y,z);}


	public veci3 multiply(int m){x=m*x; y= m*y; z=m*z; return this;}
	public veci3 add(veci3_base vec){x+=vec.x; y+=vec.y; z+=vec.z; return this;}
	public veci3 add(int x_, int y_, int z_){return this.add(new veci3(x_,y_,z_));}
	public String toString(){return "Veci3: "+x+" "+y+" "+z;};
	public veci3 incAllByOne(){x++; y++; z++; return this;}
	public vecf3 mutliplyf(float f){return new vecf3(x*f, y*f, z*f);}
	public vecf3 multiply3f(float x_, float y_, float z_){return new vecf3(x*x_, y*y_, z*z_);}
	public veci3 substract(veci3 v){x-=v.x; y-=v.y; z-=v.z; return this;}
	
	public veci3 clone(){return new veci3(x,y,z);}
}

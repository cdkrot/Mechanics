package com.cdkrot.mechanics.util;

import net.minecraft.util.MathHelper;

public class vecd3
{
	public double x;
	public double y;
	public double z;
	
	public vecd3(){x=0.0; y=0.0; z=0.0;}
	public vecd3(double x, double y, double z){this.x = x; this.y = y; this.z = z;}
	
	public vecd3 multiply(float m){x=m*x; y= m*y; z=m*z; return this;}
	public vecd3 multiply(double m){x=m*x; y= m*y; z=m*z; return this;}
	public vecd3 substract(vecd3 v){x-=v.x; y-=v.y; z-=v.y; return this;}
	public vecd3 substract(veci3 v){x-=v.x; y-=v.y; z-=v.z; return this;}
	public vecd3 add(vecd3 s2){x+=s2.x; y+=s2.y; z+=s2.z; return this;}
	public vecd3 add(double x_, double y_, double z_){return this.add(new vecd3(x_,y_,z_));}

	
	public vecd3 clone(){return new vecd3(x,y,z);}
	@Override
	public String toString(){return "Vecd3["+x+", "+y+", "+z+"]";}
	public float length(){return MathHelper.sqrt_double(x*x+y*y+z*z);}
}

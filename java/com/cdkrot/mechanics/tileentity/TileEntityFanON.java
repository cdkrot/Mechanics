package com.cdkrot.mechanics.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.cdkrot.mechanics.Mechanics;
import com.cdkrot.mechanics.util.Utility;
import com.cdkrot.mechanics.util.dirvec;
import com.cdkrot.mechanics.util.veci3;

//TODO: should work, however MUST be tested.
public class TileEntityFanON extends TileEntity
{

	private dirvec dirvec;
	private veci3 base;//base
	//private vecd3 power_base;
	private boolean initialized=false;
	private int ePs;//energy per step
    public void init()
    {
    	if (initialized)
    		return;
    	initialized=true;
    	int meta = this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
    	dirvec = com.cdkrot.mechanics.util.dirvec.list[meta&7];
    	base = new veci3(xCoord, yCoord, zCoord);

	    ePs = dirvec.y==0? 1 : 3;
    	/*power_base = dirvec.cloneAsVeci3()
    		.multiply3f(0.1f, 0.05f, 0.1f).tovecd3();//vertical pwr 2xless than horizontal

    	power_base.multiply(12.0d);//power is reverse-linear to destination
*/
    }
    
    @Override
    public void updateEntity()
    {
    	init();
		if (!Mechanics.fan.updatePowered(worldObj, xCoord, yCoord, zCoord))//update meta
			return;

		goOnAndTrace();
		//exit, TileEntity destructed.
		/*AxisAlignedBB pool = updatePool();
		List<Entity> list = worldObj.getEntitiesWithinAABB(Entity.class, pool);
		for (Entity e: list)
		{
			if (e.isDead)
				continue;
			vecd3 power_local = power_base.clone();
			vecd3 entity_pos = Utility.vecFromEntity(e);
			entity_pos.substract(base).substract(dirvec);//base=this_pos.
			entity_pos.add(-0.5, -0.5, -0.5);
			double dest = entity_pos.length();
			power_local.multiply(1 / (dest));
			e.addVelocity(power_local.x, power_local.y, power_local.z);
			if (e.motionY>0)
				e.fallDistance=0.0f;
		}*/
    }
    
    @SuppressWarnings("unchecked")
	public void goOnAndTrace()
    {
    	veci3 cur = this.base.clone();
    	int power = 12;
    	while (power>0)
    	{
    		AxisAlignedBB selection = Utility.SelectPoolBasingOnVectorAndInc(base, dirvec);
    		Block b = worldObj.getBlock(cur.x, cur.y, cur.z);
			if (b!=null)
				return;
    		Entity e = Utility.randomFromList((List<Entity>)worldObj.getEntitiesWithinAABB(Entity.class, selection), worldObj.rand);
		    if (e!=null)
		    {
    			e.addVelocity(dirvec.x*power, dirvec.y*power, dirvec.z*power);
    		    return;
			}
			power-=ePs;
    		cur.add(dirvec);//move forward
    	}
    }

	/*
	public AxisAlignedBB updatePool()
	{
		
		veci3 vec = base.clone();//vec1
		veci3 vec2 = base.clone();//vec2
		vec2.add(dirvec);
		{
			for (int i=0; i<12; i++, vec2.add(dirvec))
			{
				Block b = worldObj.getBlock(vec2.x, vec2.y, vec2.z);
				if (b!=null	&& b.isOpaqueCube() && b.isNormalCube())
				{
					vec2.substract(dirvec);
					break;//stop here
				}
			}
		}
		Utility.SwapVectorsComponentsi(vec, vec2);
		vec2.incAllByOne();
		return AxisAlignedBB.getAABBPool().getAABB(vec.x, vec.y, vec.z, vec2.x, vec2.y, vec2.z);

	}
*/
}

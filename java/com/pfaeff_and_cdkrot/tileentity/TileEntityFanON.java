package com.pfaeff_and_cdkrot.tileentity;

import com.pfaeff_and_cdkrot.util.Utility;
import com.pfaeff_and_cdkrot.util.veci3;
import com.pfaeff_and_cdkrot.ForgeMod;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import com.pfaeff_and_cdkrot.util.dirvec;

import java.util.List;

//TODO: something wrong here too, should fix it.
public class TileEntityFanON extends TileEntity
{

	private dirvec dirvec;
	private veci3 base;//base
	//private vecd3 power_base;
	private boolean initialized=false;
	private double ePs;//energy per step
    public void init()
    {
    	if (initialized)
    		return;
    	initialized=true;
    	int meta = this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
    	dirvec = dirvec.list[meta&7];
    	base = new veci3(xCoord, yCoord, zCoord);
    	/*power_base = dirvec.cloneAsVeci3()
    		.multiply3f(0.1f, 0.05f, 0.1f).tovecd3();//vertical pwr 2xless than horizontal

    	power_base.multiply(12.0d);//power is reverse-linear to destination
*/
    }
    
    @Override
    public void updateEntity()
    {
    	init();
		if (!ForgeMod.fan.updatePowered(worldObj, xCoord, yCoord, zCoord))//update meta
			return;//exit, TileEntity destructed.
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
    
    public void goOnAndTrace()
    {
    	veci3 cur = this.base.clone();
    	double power = 12.0/ePs;
    	while (power>0)
    	{
    		AxisAlignedBB selection = Utility.SelectPoolBasingOnVectorAndInc(base, dirvec);
    		Block b = worldObj.getBlock(cur.x, cur.y, cur.z);
    		b.setBlockBoundsBasedOnState(worldObj, cur.x, cur.y, cur.z);
    		
    		Entity e = Utility.randomFromList((List<Entity>)worldObj.getEntitiesWithinAABB(Entity.class, selection), worldObj.rand);
    		if (e!=null)
    			e.addVelocity(dirvec.x*power, dirvec.y*power, dirvec.z*power);
    		power-=1;
    		cur.add(dirvec);//move forward
    	}
    }
    
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
}

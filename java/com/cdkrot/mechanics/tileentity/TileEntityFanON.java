package com.cdkrot.mechanics.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.cdkrot.mechanics.Mechanics;
import com.cdkrot.mechanics.util.Utility;
import com.cdkrot.mechanics.util.VecI3;
import com.cdkrot.mechanics.util.VecI3Base;

public class TileEntityFanON extends TileEntity {

    private VecI3Base dirvec;
    private VecI3 base;// base
    // private vecd3 power_base;
    private boolean initialized = false;
    private int ePs;// energy per step
	private double ePm;// energy per entity move;

    public void init() {
        if (initialized)
            return;
        initialized = true;
        int meta = this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        dirvec = com.cdkrot.mechanics.util.DirectionalVecs.list[meta & 7];
        base = new VecI3(xCoord, yCoord, zCoord);

        ePs = dirvec.y == 0 ? 1 : 3; //need to play with nums to get more realistic effects
		ePm = dirvec.y == 0 ? 30.0 : 48.0;
    }

    @Override
    public void updateEntity() {
        init();
        if (!Mechanics.fan.updatePowered(worldObj, xCoord, yCoord, zCoord))// update meta
            return;//exit, TileEntity destructed

        goOnAndTrace();
    }

    @SuppressWarnings("unchecked")
    public void goOnAndTrace() {
        VecI3 cur = this.base.clone();
		//cur.add(dirvec);
        int power = 6;
        while (power > 0) {
            AxisAlignedBB selection = Utility.SelectPoolBasingOnVectorAndInc(cur, dirvec);
            Block b = worldObj.getBlock(cur.x+dirvec.x, cur.y+dirvec.y, cur.z+dirvec.z);
            if (b!= Blocks.air)
			{
				Mechanics.modLogger.info(b.toString());
				return;
			}
			Entity e = Utility.randomFromList((List<Entity>) worldObj.getEntitiesWithinAABB(Entity.class, selection), worldObj.rand);
            if (e != null) {
                e.addVelocity(dirvec.x * power / ePm, dirvec.y * power / ePm, dirvec.z * power / ePm);
                return;
            }
            power -= ePs;
            cur.add(dirvec);// move forward
        }
    }

}

package com.pfaeff_and_cdkrot.tileentity;

import com.pfaeff_and_cdkrot.block.BlockLightSensor;

import net.minecraft.tileentity.TileEntity;

public class TileEntityLightSensor extends TileEntity
{
	@Override
    public void updateEntity()
	{
        if (this.worldObj.getTotalWorldTime() % 60==0)
        {
            if (this.blockType != null && this.blockType instanceof BlockLightSensor)
            {
                ((BlockLightSensor)this.blockType).updateSensorOutput(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            }
            else
            	this.invalidate();
        }
    }
}

package com.pfaeff_and_cdkrot.block;

import java.util.Random;

import com.pfaeff_and_cdkrot.ForgeMod;
import com.pfaeff_and_cdkrot.MechanicsHelpCommand;
import com.pfaeff_and_cdkrot.lang.LocaleDataTable;
import com.pfaeff_and_cdkrot.util.Utility;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

//Constructor modified; imports added; package moved; added additional icon for Side(texturepack special)
//work finished
public class BlockJumpPad extends Block
{
	
	@SideOnly(Side.CLIENT)
	private IIcon iconSide;
	private static float pws[];
	private Random r = new Random();
	static
	{
		pws = new float[16];
		pws[0] = 1.00f; pws[ 8] = 3.00f;
		pws[1] = 1.25f; pws[ 9] = 3.25f;
		pws[2] = 1.50f; pws[10] = 3.50f;
		pws[3] = 1.75f; pws[11] = 3.75f;
		pws[4] = 2.00f; pws[12] = 4.00f;
		pws[5] = 2.25f; pws[13] = 0.00f;
		pws[6] = 2.50f; pws[14] = 0.50f;
		pws[7] = 2.75f; pws[15] = 0.75f;

	}
	
	public BlockJumpPad()
	{
		super(Material.ground);

		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);

		this.setHardness(1.0F).setStepSound(Block.soundTypeStone).setBlockName("mechanics::jumppad");
	}

	//Launches "entity" into the air
	public void jump(Entity entity, int meta)
	{
		float pwr = pws[meta];
		if (pwr!=0.0f && entity.motionY < 1)
		{
			double factor= MathHelper.getRandomDoubleInRange(r, 0.9f, 1.1f);
			entity.motionY = 0;
			entity.fallDistance -= pws[meta]*7.5f*factor;
			if (entity.fallDistance<0)
				entity.fallDistance=0.0f;
			else
				Utility.doEntityFall(entity);
			double ThirdLevelDeltaX = MathHelper.getRandomDoubleInRange(r, -0.125, 0.125);
			double ThirdLevelDeltaZ = MathHelper.getRandomDoubleInRange(r, -0.125, 0.125);
			ThirdLevelDeltaX*=factor;
			ThirdLevelDeltaZ*=factor;
			entity.addVelocity(ThirdLevelDeltaX, pws[meta], ThirdLevelDeltaZ);
		}
	
	}

	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity entity)
	{
		jump(entity, world.getBlockMetadata(x, y, z));
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (entity.posY > y)//on top
		{
			jump(entity, world.getBlockMetadata(x, y, z));
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerBlockIcons(IIconRegister ir)
    {
        this.blockIcon = ir.registerIcon(ForgeMod.modid_lc+":jumppad");
        this.iconSide = ir.registerIcon(ForgeMod.modid_lc+":jumppad_side");
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public IIcon getIcon(int s, int m)
	{
    	if (s==0||s==1)
    		return this.blockIcon;
    	return iconSide;
    }
	
    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer player, int side, float x_, float y_, float z_)
    {
        if (w.isRemote)
        {
            return player.isSneaking();
        }
        if (!player.isSneaking())
        	return false;
        int meta = w.getBlockMetadata(x, y, z);
        meta++;
        meta&=15;//0...15
        w.setBlockMetadataWithNotify(x, y, z, meta, 6);//send upd, suppress rerendering.
	    MechanicsHelpCommand.SendLineToPlayer(player, "\u00A7A"+LocaleDataTable.chatJumppad+" \u00A74"+getPwrDescr(meta)+"\u00A7A.");
        return true;
    }
    
    @Override
    public boolean isSideSolid(IBlockAccess iba, int x, int y, int z, ForgeDirection side)
    {
        return (side.ordinal()==0);//bottom
    }

    @Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	private String getPwrDescr(int meta)
	{
		float pwr = pws[meta];
		return (pwr==0.0f)?"OFF":""+pwr;
	}
}
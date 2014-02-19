package com.pfaeff_and_cdkrot;


import java.util.Arrays;

import com.pfaeff_and_cdkrot.lang.LocaleDataTable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class MechanicsHelpCommand extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "mechanics";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length==0)// /mechanics
		{
			System.out.println(">"+Arrays.deepToString(LocaleDataTable.mechanics_home));
			SendLinesToPlayer(sender, LocaleDataTable.mechanics_home);
			return;
		}
		if (args.length==1)
		{
			if (args[0].equals("help"))
			{
				SendLineToPlayer(sender, "Function not supported any more.");
				return;
			}
			if (args[0].equals("version"))
			{
				SendLinesToPlayer(sender, LocaleDataTable.version_info);
				return;
			}
			if (args[0].equals("credits"))
			{
				SendLinesToPlayer(sender, LocaleDataTable.credits);
				return;
			}
			if (args[0].equalsIgnoreCase("whatamilooking")||args[0].equalsIgnoreCase("look"))
			{
				if (sender instanceof EntityPlayer)
				{
					EntityPlayer player = (EntityPlayer)sender;
					//player.worldObj.
					//player.worldObj.rayTraceBlocks(par1Vec3, par2Vec3)
					MovingObjectPosition mop = rayTraceEntity(player,30.0f, 1.0f);
					if (mop==null)
					{
						SendLineToPlayer(sender, LocaleDataTable.tracer_nobody);
						return;
					}
					if (mop.typeOfHit.ordinal()==0)//block
					{
						SendLineToPlayer(sender, LocaleDataTable.tracer_block);
						World world = player.worldObj;
						int x = mop.blockX;
						int y = mop.blockY;
						int z = mop.blockZ;
						int id =world.getBlockId(x, y, z);
						int meta = world.getBlockMetadata(x, y, z);
						String name = Block.blocksList[id].getLocalizedName();
						SendLineToPlayer(sender,
						String.format("ID:%d; X:%d; Y:%d; Z:%d; META:%d; SIDE:%d; NAME:%s",
						id,x,y,z,meta,mop.sideHit, name));
					}
					//TODO: Fix entity support here.
					else//entity
					{
						Entity e = mop.entityHit;
						SendLineToPlayer(sender, "You are looking at entity#");
						String.format("ID:%d; X:%d; Y:%d; Z:%d; NAME:%s",
						e.entityId, e.posX, e.posY, e.posZ, e.getEntityName());
					}
					return;
				}
				SendLineToPlayer(sender, LocaleDataTable.not_a_player);
				return;
			}
		}
		else if (args.length==2)
		{	
			if (args[0].equals("help"))
			{
				SendLineToPlayer(sender, "Funcction unsupported.");
			}
		}
		SendLineToPlayer(sender, LocaleDataTable.cmdWrong);
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return true;
	}
			
	public static void SendLineToPlayer(ICommandSender sender, String data)
	{
		sender.sendChatToPlayer(ChatMessageComponent.createFromText(data));//I hope this will work.
	}
	
	public static void SendLinesToPlayer(ICommandSender sender, String[] data)
	{
		for (String s: data)
			SendLineToPlayer(sender, s);
	}

	
	public MovingObjectPosition rayTraceEntity(EntityPlayer p, double dst, float par3)
    {
        Vec3 pos = p.worldObj.getWorldVec3Pool().getVecFromPool(p.posX, p.posY + p.getEyeHeight(), p.posZ);//start vec
        Vec3 look = p.getLookVec();
        look.xCoord=look.xCoord*dst+pos.xCoord;
        look.yCoord=look.yCoord*dst+pos.yCoord;
        look.zCoord=look.zCoord*dst+pos.zCoord;
        MovingObjectPosition block = p.worldObj.rayTraceBlocks_do_do(pos, look, false, false);
        //p.worldObj
        return block;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/mechanics";
	}
	
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

}

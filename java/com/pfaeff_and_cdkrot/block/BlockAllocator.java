package com.pfaeff_and_cdkrot.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import com.pfaeff_and_cdkrot.BehaviourDispenseItemStack;
import com.pfaeff_and_cdkrot.ForgeMod;
import com.pfaeff_and_cdkrot.api.allocator.*;
import com.pfaeff_and_cdkrot.tileentity.TileEntityAllocator;
import com.pfaeff_and_cdkrot.tileentity.TileEntityBenchmark;
import com.pfaeff_and_cdkrot.util.Utility;
import com.pfaeff_and_cdkrot.util.veci3;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.dispenser.*;
import net.minecraft.client.renderer.texture.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//Gui open func modified; imports added; package moved;
//unfinished

public class BlockAllocator extends BlockContainer
{
	private final BehaviourDispenseItemStack dispenser = new BehaviourDispenseItemStack();
	
	
	@SideOnly(Side.CLIENT)
	public Icon[] icons;

	/**
	 * Constructor
	 * 
	 */
	public BlockAllocator(int i)
	{
		super(i, Material.rock);
		this.setHardness(3.5F).setStepSound(Block.soundStoneFootstep)
				.setUnlocalizedName("mechanics::allocator");
	}

	@Override
	public TileEntity createNewTileEntity(World w)
	{
		TileEntity tile = new TileEntityAllocator();
		tile.setWorldObj(w);
		return tile;
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, int par5, int par6)
	{
		TileEntityAllocator allocator = (TileEntityAllocator) world
				.getBlockTileEntity(i, j, k);
		if (allocator != null)
		{
			ItemStack itemStack = allocator.getStackInSlot(0);
			if (itemStack != null)
			{
				EntityItem entityItem = new EntityItem(world, i, j, k,
						new ItemStack(itemStack.itemID, 1,
								itemStack.getItemDamage()));

				if (itemStack.hasTagCompound())
					entityItem.getEntityItem().setTagCompound(
							(NBTTagCompound) itemStack.getTagCompound().copy());

				final double var15 = 0.05;
				entityItem.motionX = world.rand.nextGaussian() * var15;
				entityItem.motionY = world.rand.nextGaussian() * var15 + 0.2F;
				entityItem.motionZ = world.rand.nextGaussian() * var15;
				world.spawnEntityInWorld(entityItem);
			}
		}
		super.breakBlock(world, i, j, k, par5, par6);
	}

	@Override
	public int tickRate(World world)
	{
		return 4;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);
		if (!world.isRemote)
		{
			int meta = Utility.getDefaultDirectionsMeta(world, x, y, z); // moved
																			// to
																			// utility
			world.setBlockMetadataWithNotify(x, y, z, meta, 2);
		}
	}

	/**
	 * Returns true, if the item is allowed to pass
	 * (Filter is array filled with filter items. Lists filled with null will not pass correctly.
	 * @param world
	 * @param i
	 * @param j
	 * @param k
	 * @param itemID
	 *            item to check
	 * @return
	 */
	private boolean passesFilter(ItemStack item, ItemStack[] filter)
	{
		if (filter == null)
			return true;
		boolean t = true;
		for (ItemStack filter_: filter)
		{
			if (filter_ == null)
				continue;
			t = false;
			if ((item.itemID == filter_.itemID)
				&& (item.getItemDamage() == filter_.getItemDamage()))
				if (item.getTagCompound()==null)
					if (filter_.getTagCompound()==null)
						return true;
					else
						;
				else
					if (item.getTagCompound().equals(filter_.getTagCompound()))
						return true;
		}
		return t;
	}

	/**
	 * Returns the container (IIventory) at position (x,y,z) if it exists.
	 */
	protected IInventoryEX containerAtPos(World world, int x, int y, int z)
	{
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		IInventoryEX inv = AllocatorRegistry.instance.getIInventoryFor(world, x, y, z);
		
		if (inv!=null)
			return inv;
		else
			if (tile instanceof IInventoryEX)
				return (IInventoryEX)tile;
			else if (tile instanceof IInventory)
				return IInventoryWrapper.createDefault((IInventory)tile);
			else
				return null;
	}

	/**
	 * INPUT!
	 * Returns a random item (index) from the container, using the same rule as the
	 * dispenser
	 */
	public int getRandomItemStackFromContainer(IInventoryEX inventory,
			Random rand, ItemStack[] filter)
	{
		if (inventory == null) return -1;
		IInventory base = inventory.asIInventory();
		if (base == null) return -1;
		int ret = -1, j = 1;
		
		for (int k = inventory.getInventoryInputBegin(); 
				k <= inventory.getInventoryInputEnd(); k++)
		{
			ItemStack s = base.getStackInSlot(k);
			if ((s != null) && passesFilter(s, filter) && (rand.nextInt(j) == 0))
			{
				ret = k; j++;
			}
		}
		return ret;
	}


	/**
	 * Spits out an item (like the dropper, but the whole stack)
	 */
	protected void dispense(World world, int i, int j, int k, ItemStack item)
	{
		BlockSourceImpl blockImpl = new BlockSourceImpl(world, i, j, k);
		TileEntityAllocator allocator = (TileEntityAllocator) blockImpl
				.getBlockTileEntity();

		if (allocator != null)
		{
			int meta = world.getBlockMetadata(i, j, k) & 7;
			// IInventory hopper = TileEntityHopper.func_96117_b(world,
			// //WARNING: Hopper doesn't have following (or equiv. with other
			// name) method!!!
			// (double) (i + Facing.offsetsXForSide[meta]), //WARNING what
			// "hopper" variable does?
			// (double) (j + Facing.offsetsYForSide[meta]), //unfinished?
			// (double) (k + Facing.offsetsZForSide[meta]));
			
			//TODO: todo
			ItemStack stack = this.dispenser.dispense(blockImpl, item);
			if (stack != null && stack.stackSize == 0)
				stack = null;//so strange code
		}
	}

	/**
	 * Handles the item output. Returns true, if item was successfully put out.
	 * 
	 * @param z
	 */
	private boolean outputItem(World world, int x, int y, int z, veci3 dir,
			ItemStack item, Random random)
	{
		int X_ = x + dir.x;
		int Y_ = y + dir.y;
		int Z_ = z + dir.z;
		IInventoryEX output = containerAtPos(world, X_, Y_, Z_);

		if (output == null)
		{
			List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(X_, Y_, Z_, X_+1, Y_+1, Z_));
			List<IInventoryEX> invs = AllocatorRegistry.instance.getIInventoryAllInFor(entities, false);
			if (invs.size()>0)
				output = invs.get(random.nextInt(invs.size()));
	        else
	        {
				if (!(Block.opaqueCubeLookup[world.getBlockId(X_, Y_, Z_)]))
				{
					dispense(world, x, y, z, item);
					return true;
				}
					return false;
	        }
		}
			IInventory base = output.asIInventory();
		
			int index = -1;
			int inventorySize = base.getSizeInventory();
				inventorySize--;
			for (int l = output.getInventoryInputBegin(); l < output.getInventoryInputEnd(); l++)
			{
				boolean canStack = false;// Check if stacking is possible
				ItemStack baseStack = base.getStackInSlot(l);
				if (baseStack == null && item.stackSize<= base.getInventoryStackLimit() 
					&& base.isItemValidForSlot(l, item))
				{
					base.setInventorySlotContents(l, item);
					return true;
				}
				if (
					((baseStack.isStackable())&&(item.isStackable()))
					&&(baseStack.getItem().itemID == item.itemID) &&
					(baseStack.getItemDamage() == item.getItemDamage())
					&&(baseStack.stackSize+item.stackSize <= 
						Math.min(base.getInventoryStackLimit(), item.getMaxStackSize())))
					//item is valid for stack (i am sure).
					
				{	
					baseStack.stackSize+=item.stackSize;
					return true;
				}
			}
		return false;
	}

	/**
	 * Handles all the item input/output
	 */
	private void allocateItems(World world, int x, int y, int z, Random random)
    {
    	veci3 d = Utility.getDirectionVectorFori(world.getBlockMetadata(x, y, z));
    	
    	TileEntityAllocator tile = (TileEntityAllocator) world.getBlockTileEntity(x, y, z);
    	ItemStack[] filter = tile.allocatorFilterItems;
    	IInventoryEX input = containerAtPos(world, x-d.x, y-d.y, z-d.z);
    	if (input == null)
    	{
        	List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox((double)(x-d.x), (double)y-d.y, (double)(z-d.z), (double)(x -d.x+ 1), (double)(y -d.y+ 1), (float)(z -d.z+ 1)));
        	List<IInventoryEX> invs = AllocatorRegistry.instance.getIInventoryAllInFor(entities, true);
        	if (invs.size()>0)
        		input = invs.get(random.nextInt(invs.size()));
        	else
        		return;//no input.
    	}
    	int itemIndex = getRandomItemStackFromContainer(input, random, filter);
    	if (itemIndex >= 0)
    	{	
    		IInventory base = input.asIInventory();
    		ItemStack item = input.asIInventory().getStackInSlot(itemIndex);
    		if (outputItem(world, x, y, z, d, item, random))
    		{	
    			base.decrStackSize(itemIndex, item.stackSize);
    			input.onOutputSuccessful(itemIndex, null);//TODO: 
    			return;
    		}
    	}
    }

	@Override
	public void updateTick(World world, int i, int j, int k, Random random)
	{
		if (world.isBlockIndirectlyGettingPowered(i, j, k)
				|| world.isBlockIndirectlyGettingPowered(i, j + 1, k))
		{
			allocateItems(world, i, j, k, random);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id)
	{
		if (id > 0 && Block.blocksList[id].canProvidePower())
		{
			if (world.isBlockIndirectlyGettingPowered(x, y, z)
					|| world.isBlockIndirectlyGettingPowered(x, y + 1, z))
				world.scheduleBlockUpdate(x, y, z, blockID, tickRate(world));
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase e, ItemStack stack)
	{
		int side = Utility.getMetadataForBlockAnyPlaced(world, x, y, z, e);
		world.setBlockMetadataWithNotify(x, y, z, side, 4);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer entityplayer, int par6, float par7, float par8,
			float par9)
	{
		if (!world.isRemote)
			entityplayer.openGui(ForgeMod.instance, 0, world, x, y, z);
		return true;
	}


	// ------TEXTURES-------//

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(ForgeMod.modid_lc
				+ ":pfaeff_topbottom");
		icons = new Icon[] {
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_sidel"),//0
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_sider"),//1
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_in"),//2
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_out"),//3
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_m_in"),//4
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_m_out"),//5
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_m_sideup"),//6
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_m_sidedown") };//7
	}

	// Soooo dirty item view
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metaAlwaysZero)
	{
		return getIconForTerrain(side, 2);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess iba, int x, int y, int z, int s)
	{
		return this.getIconForTerrain(s, iba.getBlockMetadata(x, y, z));
	}

	@SideOnly(Side.CLIENT)
	public Icon getIconForTerrain(int side, int meta)
	{
		if (meta == 0)//facing down
			if (side == meta)
				return icons[5];//out
			else if (side == Utility.getOppositeSide(meta))
				return icons[4];//in
			else
				return icons[7];//facedown
		if (meta == 1)
			if (side == meta)
				return icons[4];//in
			else if (side == Utility.getOppositeSide(meta))
				return icons[5];//out
			else
				return icons[6];//faceup
		//===meta!=0; meta!=1===//
		if (side == meta)
			return icons[3];//out
		else if (side == Utility.getOppositeSide(meta))
			return icons[2];//in
		else if (side==0 || side==1)	
			return this.blockIcon;//topbottom
		else
		{
			boolean t = (side == 2)||(side == 3);
			if (
				((Math.abs(side - meta) == 2)&&(t))
				||
				((Math.abs(side - Utility.getOppositeSide(meta)) == 2)&&(!t)))
			{
				return icons[0];//sidel
			}
				 else 
					return icons[1];//sider
		}		
	}
	
	public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World world, int x, int y, int z, int s)
    {
        TileEntityAllocator tileentity = (TileEntityAllocator) world.getBlockTileEntity(x, y, z);
        return Container.calcRedstoneFromInventory(tileentity);
    }
}
package com.pfaeff_and_cdkrot.block;

import java.util.List;
import java.util.Random;

import com.pfaeff_and_cdkrot.BehaviourDispenseItemStack;
import com.pfaeff_and_cdkrot.ForgeMod;
import com.pfaeff_and_cdkrot.api.allocator.*;
import com.pfaeff_and_cdkrot.tileentity.TileEntityAllocator;
import com.pfaeff_and_cdkrot.util.Utility;
import com.pfaeff_and_cdkrot.util.dirvec;

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
import cpw.mods.fml.relauncher.Side;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAllocator extends BlockContainer
{
	private final BehaviourDispenseItemStack dispenser = new BehaviourDispenseItemStack();
	
	
	@SideOnly(Side.CLIENT)
	public IIcon[] icons;

	/**
	 * Constructor
	 * 
	 */
	public BlockAllocator()
	{
		super(Material.rock);
		this.setHardness(3.5F).setStepSound(Block.soundTypeStone).setBlockName("mechanics::allocator");
	}

	@Override
	public TileEntity createNewTileEntity(World w, int i)
	{
		return new TileEntityAllocator();
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block par5, int par6)
	{
		TileEntityAllocator allocator = (TileEntityAllocator) world.getTileEntity(i, j, k);
		if (allocator != null)
		{
			ItemStack itemStack = allocator.getStackInSlot(0);
			if (itemStack != null)
			{
				EntityItem entityItem = new EntityItem(world, i, j, k,
						new ItemStack(itemStack.getItem(), 1, itemStack.getItemDamage()));

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
			if ((item.getItem() == filter_.getItem())
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
		TileEntity tile = world.getTileEntity(x, y, z);
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
			// int meta = world.getBlockMetadata(i, j, k) & 7;
			// IInventory hopper = TileEntityHopper.func_96117_b(world,
			// //WARNING: Hopper doesn't have following (or equiv. with other
			// name) method!!!
			// (double) (i + Facing.offsetsXForSide[meta]), //WARNING what
			// "hopper" variable does?
			// (double) (j + Facing.offsetsYForSide[meta]), //unfinished?
			// (double) (k + Facing.offsetsZForSide[meta]));
			
			//TODO: todo
			ItemStack stack = this.dispenser.dispense(blockImpl, item);
			//if (stack != null && stack.stackSize == 0)
			//	stack = null;
			//removed this, because the code above did nothing anyways. so strange code
		}
	}

	/**
	 * Handles the item output. Returns true, if item was successfully put out.
	 * 
	 */
	private boolean outputItem(World world, int x, int y, int z, dirvec dir,
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
				if (!(world.getBlock(X_, Y_, Z_).isOpaqueCube()))
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
				ItemStack baseStack = base.getStackInSlot(l);
				if (baseStack == null && item.stackSize<= base.getInventoryStackLimit() 
					&& base.isItemValidForSlot(l, item))
				{
					base.setInventorySlotContents(l, item);
					return true;
				}
				if (
					((baseStack.isStackable())&&(item.isStackable()))
					&&(baseStack.getItem() == item.getItem()) &&
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
	    dirvec d = dirvec.list[world.getBlockMetadata(x, y, z)];

    	TileEntityAllocator tile = (TileEntityAllocator) world.getTileEntity(x, y, z);
    	ItemStack[] filter = tile.allocatorFilterItems;
    	IInventoryEX input = containerAtPos(world, x-d.x, y-d.y, z-d.z);
    	if (input == null)
    	{
        	List<Entity> entities = (List<Entity>)world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox((double)(x-d.x), (double)y-d.y, (double)(z-d.z), (double)(x -d.x+ 1), (double)(y -d.y+ 1), (float)(z -d.z+ 1)));
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
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b)
	{
		if (b.canProvidePower())
		{
			if (world.isBlockIndirectlyGettingPowered(x, y, z)
					|| world.isBlockIndirectlyGettingPowered(x, y + 1, z))
				world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase e, ItemStack stack)
	{
		int side = Utility.getMetadataForBlockAnyPlaced(x, y, z, e);
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
	public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(ForgeMod.modid_lc+ ":pfaeff_topbottom");
		icons = new IIcon[] {
				iconRegister.registerIcon(ForgeMod.modid_lc + ":allocator_sidel"),//0
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_sider"),//1
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_in"),//2
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_out"),//3
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_m_in"),//4
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_m_out"),//5
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_m_sideup"),//6
				iconRegister.registerIcon(ForgeMod.modid_lc+":allocator_m_sidedown") };//7
	}

	// Soooo dirty item view
	// TODO: check if this changed since 1.6
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metaAlwaysZero)
	{
		return getIconForTerrain(side, 2);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s)
	{
		return this.getIconForTerrain(s, iba.getBlockMetadata(x, y, z));
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconForTerrain(int side, int meta)
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

	@Override
	public boolean hasComparatorInputOverride()
    {
        return true;
    }

	@Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int s)
    {
        TileEntityAllocator tileentity = (TileEntityAllocator) world.getTileEntity(x, y, z);
        return Container.calcRedstoneFromInventory(tileentity);
    }
}
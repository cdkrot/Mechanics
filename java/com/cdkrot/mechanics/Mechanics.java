package com.cdkrot.mechanics;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import com.cdkrot.mechanics.api.allocator.AllocatorRegistry;
import com.cdkrot.mechanics.api.allocator.MechanicsModProvider;
import com.cdkrot.mechanics.api.allocator.VannilaProvider;
import com.cdkrot.mechanics.block.BlockAllocator;
import com.cdkrot.mechanics.block.BlockBenchmark;
import com.cdkrot.mechanics.block.BlockFan;
import com.cdkrot.mechanics.block.BlockJumpPad;
import com.cdkrot.mechanics.block.BlockLightSensor;
import com.cdkrot.mechanics.gui.GuiHandler;
import com.cdkrot.mechanics.net.PacketTransformer;
import com.cdkrot.mechanics.tileentity.TileEntityAllocator;
import com.cdkrot.mechanics.tileentity.TileEntityBenchmark;
import com.cdkrot.mechanics.tileentity.TileEntityFanON;
import com.cdkrot.mechanics.tileentity.TileEntityLightSensor;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;


@Mod(modid = Mechanics.modid, version = "6.0", name = "Mechanics mod")
public class Mechanics {
	public static final String modid = "mechanics_mod";

	public static Logger modLogger;

	public static Block allocator, jumpPad, benchmark;
	public static BlockLightSensor lightSensor;
	public static BlockFan fan;

	public static PacketTransformer networkHandler = new PacketTransformer();

	public static CreativeTabs tabMechanics = new CreativeTabs(modid.toLowerCase()) {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(allocator);
		}
	};

	@Instance(modid)
	public static Mechanics instance;

	//@SidedProxy(clientSide = "com.cdkrot.mechanics.network.ClientProxy", serverSide = "com.cdkrot.mechanics.network.CommonProxy")
	//public static CommonProxy proxy;
	//in fact proxy's are unused nowadays, so commenting out.

	@EventHandler
	@SuppressWarnings("unused")
	public void preInit(FMLPreInitializationEvent event) {
		// load configuration properties
		modLogger = event.getModLog();
		modLogger.info("Going to preinit updated mod_Allocator, mod_LightSensor, mod_JumpPad, cdkrot_Fan");
		Configuration c = new Configuration(event.getSuggestedConfigurationFile());
		c.load();
			BlockBenchmark.radius = c.get("GENERAL", "benchmark.radius", 32).getInt();
			BlockBenchmark.def = c.get("GENERAL", "benchmark.radius", "Benchmark: (&&x, &&y, &&z) time: &time.&msec").getString();
		c.save();
		modLogger.info("PreInit state done.");
	}

	@EventHandler
	@SuppressWarnings("unused")
	public void init(FMLInitializationEvent event)
	{
		// load blocks and such
		modLogger.info("Mechanics mod Initializing.");

		allocator = new BlockAllocator().setCreativeTab(tabMechanics).setHardness(3.5F).setStepSound(Block.soundTypeStone).setBlockName("mechanics::allocator");
		benchmark = new BlockBenchmark().setCreativeTab(tabMechanics).setHardness(2.0F).setBlockName("mechanics::benchmark");

		fan = new BlockFan().setCreativeTab(tabMechanics);

		lightSensor = new BlockLightSensor().setCreativeTab(tabMechanics);
		jumpPad = new BlockJumpPad().setCreativeTab(tabMechanics).setHardness(1.0F).setStepSound(Block.soundTypeStone).setBlockName("mechanics::jumppad");

		GameRegistry.registerBlock(allocator, "Allocator_Pfaeff");
		GameRegistry.registerBlock(lightSensor, "LightSensor_Pfaeff");
		GameRegistry.registerBlock(jumpPad, "JumpPad_Pfaeff");
		GameRegistry.registerBlock(fan, "cdkrot_Fan");
		GameRegistry.registerBlock(benchmark, "mechanics_Benchmark");

		TileEntity.addMapping(TileEntityAllocator.class, "Allocator");
		TileEntity.addMapping(TileEntityLightSensor.class, "pfaeffs_lsensor");
		TileEntity.addMapping(TileEntityFanON.class, "cdkrot_fan");
		TileEntity.addMapping(TileEntityBenchmark.class, "mechanics::benchmark");

		if (event.getSide().isClient())
			invokeClientSetup();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		networkHandler.initalise();
	}

	/**
	 * Setups client-only stuff
	 */
	@SideOnly(Side.CLIENT)
	public void invokeClientSetup()
	{
		TextureMap iconRegister = Minecraft.getMinecraft().getTextureMapBlocks();
		Minecraft.getMinecraft().getTextureManager().onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
		allocator.registerBlockIcons(iconRegister);
		jumpPad.registerBlockIcons(iconRegister);
		benchmark.registerBlockIcons(iconRegister);
		lightSensor.registerBlockIcons(iconRegister);
		fan.registerBlockIcons(iconRegister);
		//Some explanation:
		//firstly we need to registerBlockIcons so we will have our IIcons in regisrty loaded.
		//but in fact Minecraft caches all textures to one big atlas(one textures)
		//and this happens BEFORE @Initializiation called. But experimenting i founded, that
		//you can force mc to recreate an atlas.
		//!! This solution is quite bad, but i need to solve it by anyway, i didn't finded tutorials
		//!! about texturing in 1.7 so...
	}

	@EventHandler
	@SuppressWarnings("unused")
	public void postInit(FMLPostInitializationEvent e) {
		// load recipes and register Allocator provision
		GameRegistry.addRecipe(new ItemStack(allocator, 1), "X#X", "X$X", "X#X", 'X', Blocks.cobblestone, '#', Items.redstone, '$', Items.gold_ingot);
		GameRegistry.addRecipe(new ItemStack(lightSensor, 1),  "A", "B", "C", 'A', Blocks.glass, 'B', Items.quartz, 'C', Blocks.wooden_slab);
		GameRegistry.addRecipe(new ItemStack(jumpPad, 4), "X", "#", 'X', Items.slime_ball, '#', Blocks.wooden_pressure_plate);
		GameRegistry.addRecipe(new ItemStack(fan),"CsC", "sSs", "RsR", 'C', Blocks.cobblestone, 's', Items.stick, 'S', Items.slime_ball, 'R', Blocks.redstone_block);
		GameRegistry.addRecipe(new ItemStack(benchmark), "WSW", "WDW", "RRR", 'W', new ItemStack(Blocks.wool, 1, 14), 'S', Items.sign, 'D', Blocks.dispenser, 'R', Items.redstone);

		AllocatorRegistry.instance.add(new VannilaProvider());
		AllocatorRegistry.instance.add(new MechanicsModProvider());
		networkHandler.postInitialise();
	}
}

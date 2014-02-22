package com.cdkrot.mechanics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

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
import com.cdkrot.mechanics.network.CommonProxy;
import com.cdkrot.mechanics.tileentity.TileEntityAllocator;
import com.cdkrot.mechanics.tileentity.TileEntityBenchmark;
import com.cdkrot.mechanics.tileentity.TileEntityFanON;
import com.cdkrot.mechanics.tileentity.TileEntityLightSensor;
import com.cdkrot.mechanics.util.Utility;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Mechanics.modid, version = "0.0.6", name = "Mechanics mod")
public class Mechanics {
	public static final String modid = "Mechanics_mod";

	public static Logger modLogger;

	public static Block allocator, jumpPad, Benchmark;
	public static BlockLightSensor LightSensor;
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

	@SidedProxy(clientSide = "com.cdkrot.mechanics.network.ClientProxy", serverSide = "com.cdkrot.mechanics.network.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// load configuration properties
		modLogger = event.getModLog();
		File conf_path = event.getSuggestedConfigurationFile();
		modLogger.info("Going to preinit updated mod_Allocator, mod_LightSensor, mod_JumpPad, cdkrot_Fan, using configuration " + conf_path.getAbsolutePath());
		try {
			Map<String, String> config = Utility.loadKeyValueMap(new FileInputStream(conf_path));
			String benchmark_radius = config.get("benchmark.radius");
			String benchmark_def = config.get("benchmark_def");
			BlockBenchmark.radius = (benchmark_radius == null) ? 32 : Integer.parseInt(benchmark_radius);
			BlockBenchmark.def = (benchmark_def == null) ? "Benchmark: (&&x, &&y, &&z) time: &time" : benchmark_def;
		} catch (IOException e) {
			modLogger.info("Config not exist, using default values.");
		}
		modLogger.info("PreInit state done.");
	}

	@EventHandler
	public void init(FMLInitializationEvent event) throws IllegalArgumentException, IllegalAccessException {
		// load blocks and such
		modLogger.info("Initializing.");

		allocator = new BlockAllocator().setCreativeTab(tabMechanics).setHardness(3.5F).setStepSound(Block.soundTypeStone).setBlockName("mechanics::allocator");
		Benchmark = new BlockBenchmark().setCreativeTab(tabMechanics).setHardness(2.0F).setBlockName("mechanics::benchmark");

		fan = new BlockFan().setCreativeTab(tabMechanics);

		LightSensor = new BlockLightSensor().setCreativeTab(tabMechanics);
		jumpPad = new BlockJumpPad().setCreativeTab(tabMechanics).setHardness(1.0F).setStepSound(Block.soundTypeStone).setBlockName("mechanics::jumppad");

		GameRegistry.registerBlock(allocator, "Allocator_Pfaeff");
		GameRegistry.registerBlock(LightSensor, "LightSensor_Pfaeff");
		GameRegistry.registerBlock(jumpPad, "JumpPad_Pfaeff");
		GameRegistry.registerBlock(fan, "cdkrot_Fan");
		GameRegistry.registerBlock(Benchmark, "mechanics_Benchmark");

		TileEntity.addMapping(TileEntityAllocator.class, "Allocator");
		TileEntity.addMapping(TileEntityLightSensor.class, "pfaeffs_lsensor");
		TileEntity.addMapping(TileEntityFanON.class, "cdkrot_fan");
		TileEntity.addMapping(TileEntityBenchmark.class, "mechanics::benchmark");

		proxy.doInit();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		networkHandler.initalise();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		// load recipes and register Allocator provision
		GameRegistry.addRecipe(new ItemStack(allocator, 1), new Object[] {
			"X#X", "X$X", "X#X", 'X', Blocks.cobblestone, '#', Items.redstone, '$', Items.gold_ingot
		});
		GameRegistry.addRecipe(new ItemStack(LightSensor, 1), new Object[] {
			"A", "B", "C", 'A', Blocks.glass, 'B', Items.quartz, 'C', Blocks.wooden_slab
		});
		GameRegistry.addRecipe(new ItemStack(jumpPad, 4), new Object[] {
			"X", "#", 'X', Items.slime_ball, '#', Blocks.wooden_pressure_plate
		});
		GameRegistry.addRecipe(new ItemStack(fan), new Object[] {
			"CsC", "sSs", "RsR", 'C', Blocks.cobblestone, 's', Items.stick, 'S', Items.slime_ball, 'R', Blocks.redstone_block
		});
		GameRegistry.addRecipe(new ItemStack(Benchmark), new Object[] {
			"WSW", "WDW", "RRR", 'W', new ItemStack(Blocks.wool, 1, 14), 'S', Items.sign, 'D', Blocks.dispenser, 'R', Items.redstone
		});

		AllocatorRegistry.instance.add(new VannilaProvider());
		AllocatorRegistry.instance.add(new MechanicsModProvider());
		networkHandler.postInitialise();
	}

	public static void warning(String message) {
		modLogger.warn(message);
	}
}

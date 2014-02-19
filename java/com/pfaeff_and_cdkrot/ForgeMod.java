package com.pfaeff_and_cdkrot;

import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.Configuration;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Objects;
import com.pfaeff_and_cdkrot.api.allocator.AllocatorRegistry;
import com.pfaeff_and_cdkrot.api.allocator.MechanicsModProvider;
import com.pfaeff_and_cdkrot.api.allocator.VannilaProvider;
import com.pfaeff_and_cdkrot.block.*;
import com.pfaeff_and_cdkrot.gui.GuiHandler;
import com.pfaeff_and_cdkrot.lang.*;
import com.pfaeff_and_cdkrot.net.NetworkHandler;
import com.pfaeff_and_cdkrot.tileentity.*;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "Mechanics_mod", version = "4.0", name = "Mechanics mod")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels={"mechanics|1", "mechanics|2"},
packetHandler=NetworkHandler.class)
public class ForgeMod
{
	public static Block allocator;
	public static int allocatorID;

	public static BlockLightSensor LightSensor;
	public static int LightSensorID;

	public static Block jumpPad;
	public static int jumpPadID;

	public static BlockFan fan;
	public static int fanID;

	public static Block Benchmark;
	public static int BenchmarkID;

	public static Logger modLogger;
	public static ModCreativeTab creativeTab;

	public static String modid_lc = "Mechanics_mod";
	
	public static String lang;
	
//	@SidedProxy(clientSide = "com.pfaeff_and_cdkrot.ClientProxy", serverSide = "com.pfaeff_and_cdkrot.BaseProxy")
//	public static BaseProxy proxy;

	// public static LocaleData lang;
	// public static LocaleHelpData help;
	@Instance("Mechanics_mod")
	public static ForgeMod instance;

	public ForgeMod()
	{
	}

	// aka @PreInit
	@EventHandler
	public void configure(FMLPreInitializationEvent event)
	{
		modLogger = event.getModLog();
		modLogger
				.info("PreIniting mod_Allocator, mod_LightSensor, mod_JumpPad, cdkrot_Fan");
		Configuration c = new Configuration(
				event.getSuggestedConfigurationFile());
		c.load();
		allocatorID = c.getBlock("allocator_ID", 3560).getInt(3560);

		LightSensorID = c.getBlock("lightsensor", 3561).getInt(3561);

		jumpPadID = c.getBlock("jumppad_ID", 3562).getInt(3562);

		fanID = c.getBlock("FanID", 3563).getInt(3563);

		BenchmarkID = c.getBlock("BenchmarkID", 3564).getInt(3564);
			BlockBenchmark.radius = c.get("Benchmark", "radius", 32).getInt(32);
			BlockBenchmark.def = c.get("Benchmark", "defPattern", "Benchmark: (&&x, &&y, &&z) time: &time").getString();
		creativeTab = new ModCreativeTab();

		lang = (c.get("general", "langID", "en").getString());

		modLogger.info("PreInit state done.");
		c.save();
	}

	// aka @Init
	@EventHandler
	public void construct(FMLInitializationEvent event) throws IllegalArgumentException, IllegalAccessException
	{
		modLogger.info("Initializing.");
		allocator = new BlockAllocator(allocatorID).setCreativeTab(creativeTab);
		TileEntity.addMapping(TileEntityAllocator.class, "Allocator");
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
		GameRegistry.registerBlock(allocator, "Allocator_Pfaeff");
		GameRegistry.addRecipe(new ItemStack(allocator, 1), new Object[] {
				"X#X", "X$X", "X#X", Character.valueOf('X'), Block.cobblestone,
				Character.valueOf('#'), Item.redstone, Character.valueOf('$'),
				Item.ingotGold });
		/////////////////////////LightSensor
		LightSensor = new BlockLightSensor(LightSensorID)
				.setCreativeTab(creativeTab);
		TileEntity.addMapping(TileEntityLightSensor.class, "pfaeffs_lsensor");
		GameRegistry.registerBlock(LightSensor, "LightSensor_Pfaeff");
		GameRegistry.addRecipe(new ItemStack(LightSensor, 1),
				new Object[] { "A", "B", "C", Character.valueOf('A'),
						Block.glass, Character.valueOf('B'), Item.netherQuartz,
						Character.valueOf('C'), Block.woodSingleSlab });
		/////////////////////////JumpPad
		jumpPad = new BlockJumpPad(jumpPadID).setCreativeTab(creativeTab);
		GameRegistry.registerBlock(jumpPad, "JumpPad_Pfaeff");
		GameRegistry.addRecipe(new ItemStack(jumpPad, 4),
				new Object[] { "X", "#", Character.valueOf('X'),
						Item.slimeBall, Character.valueOf('#'),
						Block.pressurePlatePlanks });
		//////////////////FAN
		fan = new BlockFan(fanID).setCreativeTab(creativeTab);
		TileEntity.addMapping(TileEntityFanON.class, "cdkrot_fan");
		GameRegistry.registerBlock(fan, "cdkrot_Fan");
		GameRegistry.addRecipe(
				new ItemStack(fan),
				new Object[] { "CsC", "sSs", "RsR", Character.valueOf('C'),
						Block.cobblestone, Character.valueOf('s'), Item.stick,
						Character.valueOf('S'), Item.slimeBall,
						Character.valueOf('R'), Block.blockRedstone });
		/////////////////Benchmark Block
		Benchmark = new BlockBenchmark(BenchmarkID).setCreativeTab(creativeTab);
		GameRegistry.registerBlock(Benchmark, "mechanics_Benchmark");
		GameRegistry.addRecipe(new ItemStack(Benchmark),
		new Object[] { "WSW", "WDW", "RRR", Character.valueOf('W'),
			new ItemStack(Block.cloth,1, 14), Character.valueOf('S'), Item.sign,
			Character.valueOf('D'), Block.dispenser,
			Character.valueOf('R'), Item.redstone });

		TileEntity.addMapping(TileEntityBenchmark.class, "mechanics::benchmark");
		///////////////// END
		modLogger.info("Localizing.");
			Localizer names_map = Localizer.LoadNamesMap(lang);
			names_map.NameObject(allocator,"allocator");
			names_map.NameObject(fan, "fan");
			names_map.NameObject(LightSensor, "lightsensor");
			names_map.NameObject(jumpPad, "jumppad");
			names_map.NameObject(Benchmark, "benchmark");
			names_map.NameCreativeTab("Mod_Pfaeff_and_cdkrot", "creativetab");
		modLogger.info("Forcing lang assets to preload.");
		LocaleDataTable.chatJumppad.charAt(0);//do static
		
		modLogger.info("Init state done.");
		//proxy.doInit();
		System.gc();// clean up.
	}
	
	@EventHandler
	public void finality(FMLPostInitializationEvent e)
	{
		AllocatorRegistry.instance.add(new VannilaProvider());
		AllocatorRegistry.instance.add(new MechanicsModProvider());
	}
	
	public static void loginfo(String message, Object... data)
	{
		modLogger.info(String.format(message, data));
	}

	public static void loginfo(String message)
	{
		modLogger.info(message);
	}

	// aka @ServerStarting
	@EventHandler
	public void install(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new MechanicsHelpCommand());
	}

	public static void warning(String message)
	{
		modLogger.warning(message);
	}
}

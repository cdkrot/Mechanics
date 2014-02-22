package com.pfaeff_and_cdkrot;

import com.pfaeff_and_cdkrot.net.PacketTransformer;
import com.pfaeff_and_cdkrot.util.Utility;
import com.sun.org.apache.xerces.internal.parsers.XMLParser;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.Configuration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

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
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Mod(modid = "Mechanics_mod", version = "5.0", name = "Mechanics mod")
public class ForgeMod
{
	public static Block allocator;
	public static BlockLightSensor LightSensor;
	public static Block jumpPad;
	public static BlockFan fan;
	public static Block Benchmark;

	public static Logger modLogger;
	public static ModCreativeTab creativeTab;

	public static String modid_lc = "Mechanics_mod";
	
	public static String lang;

	public static PacketTransformer networkHandler = new PacketTransformer();

	@Instance("Mechanics_mod")
	public static ForgeMod instance;

	@EventHandler
	public void configure(FMLPreInitializationEvent event)
	{
		modLogger = event.getModLog();
		File conf_path = event.getSuggestedConfigurationFile();
		modLogger.info("Going to preinit updated mod_Allocator, mod_LightSensor, mod_JumpPad, cdkrot_Fan, using configuration " + conf_path.getAbsolutePath());
		try
		{
			Map<String, String> config = Utility.loadKeyValueMap(new FileInputStream(conf_path));
			String benchmark_radius = config.get("benchmark.radius");
			String benchmark_def = config.get("benchmark_def");
			BlockBenchmark.radius = (benchmark_radius==null) ? 32 : Integer.parseInt(benchmark_radius);
			BlockBenchmark.def = (benchmark_def==null)? "Benchmark: (&&x, &&y, &&z) time: &time" : benchmark_def;
		}
		catch (IOException e)
		{
			modLogger.info("Config not exist, using default values.");
		}
		modLogger.info("PreInit state done.");
	}

	@EventHandler
	public void construct(FMLInitializationEvent event) throws IllegalArgumentException, IllegalAccessException
	{
		modLogger.info("Initializing.");
		allocator = new BlockAllocator().setCreativeTab(creativeTab);
		TileEntity.addMapping(TileEntityAllocator.class, "Allocator");
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
		GameRegistry.registerBlock(allocator, "Allocator_Pfaeff");
		GameRegistry.addRecipe(new ItemStack(allocator, 1), new Object[] {
				"X#X", "X$X", "X#X", Character.valueOf('X'), Block.cobblestone,
				Character.valueOf('#'), Item.redstone, Character.valueOf('$'),
				Item.ingotGold });
		/////////////////////////LightSensor
		LightSensor = new BlockLightSensor().setCreativeTab(creativeTab);
		TileEntity.addMapping(TileEntityLightSensor.class, "pfaeffs_lsensor");
		GameRegistry.registerBlock(LightSensor, "LightSensor_Pfaeff");
		GameRegistry.addRecipe(new ItemStack(LightSensor, 1),
				new Object[] { "A", "B", "C", Character.valueOf('A'),
						Block.glass, Character.valueOf('B'), Item.netherQuartz,
						Character.valueOf('C'), Block.woodSingleSlab });
		/////////////////////////JumpPad
		jumpPad = new BlockJumpPad().setCreativeTab(creativeTab);
		GameRegistry.registerBlock(jumpPad, "JumpPad_Pfaeff");
		GameRegistry.addRecipe(new ItemStack(jumpPad, 4),
				new Object[] { "X", "#", Character.valueOf('X'),
						Item.slimeBall, Character.valueOf('#'),
						Block.pressurePlatePlanks });
		//////////////////FAN
		fan = new BlockFan().setCreativeTab(creativeTab);
		TileEntity.addMapping(TileEntityFanON.class, "cdkrot_fan");
		GameRegistry.registerBlock(fan, "cdkrot_Fan");
		GameRegistry.addRecipe(
				new ItemStack(fan),
				new Object[] { "CsC", "sSs", "RsR", Character.valueOf('C'),
						Block. cobblestone, Character.valueOf('s'), Item.stick,
						Character.valueOf('S'), Item.slimeBall,
						Character.valueOf('R'), Block.blockRedstone });
		/////////////////Benchmark Block
		Benchmark = new BlockBenchmark().setCreativeTab(creativeTab);
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
		Block.blockRegistry.
		modLogger.info("Init state done.");
		//proxy.doInit();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		networkHandler.initalise();
	}
	
	@EventHandler
	public void finality(FMLPostInitializationEvent e)
	{
		AllocatorRegistry.instance.add(new VannilaProvider());
		AllocatorRegistry.instance.add(new MechanicsModProvider());

		networkHandler.postInitialise();
	}

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

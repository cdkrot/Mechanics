package com.pfaeff_and_cdkrot.lang;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.pfaeff_and_cdkrot.ForgeMod;

import cpw.mods.fml.common.registry.LanguageRegistry;
public class Localizer extends LocaleStringMap
{
	private LanguageRegistry registry = LanguageRegistry.instance();
	
	public Localizer(Map<String, String> namesMap)
	{
		super(namesMap);
	}
	
	public Localizer(String localeFile)
	{
		super(localeFile);
	}
	
	
	public void NameObject(Object obj, String key)
	{
		registry.addName(obj, get(key));
	}
	
	public void NameCreativeTab(String tabkey, String key)
	{
		registry.addStringLocalization("itemGroup."+tabkey, get(key));
	}
	
	public static Localizer LoadNamesMap(String lang)
	{
		Localizer names_map = new Localizer("names-"+lang+".map");
		if (!lang.equals("en"))
			names_map.populateFrom(Localizer.readToMap("names-en.map"));
		return names_map;
	}
}
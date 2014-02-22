package com.pfaeff_and_cdkrot.lang;

import java.util.Map;

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
	
	
	@SuppressWarnings("deprecation")
	public void NameObject(Object obj, String key)
	{
		LanguageRegistry.addName(obj, get(key));
	}
	
	@SuppressWarnings("deprecation")
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
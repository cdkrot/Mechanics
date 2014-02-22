package com.pfaeff_and_cdkrot.lang;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import com.pfaeff_and_cdkrot.ForgeMod;

import cpw.mods.fml.common.registry.LanguageRegistry;
public class LocaleStringMap
{

	private Map<String, String> namesMap;
	
	public LocaleStringMap(Map<String, String> namesMap)
	{
		this.namesMap = namesMap;
	}
	
	public LocaleStringMap(String localeFile)
	{
		this.namesMap = readToMap(localeFile);
	}
	
	public void populateFrom(Map<String, String> altMap)
	{
		Set<Entry<String,String>> entries = altMap.entrySet();
		for (Entry<String,String> entry: entries)
			if (!namesMap.containsKey(entry.getKey()))
				namesMap.put(entry.getKey(), entry.getValue());
	}
	
	public String get(String key)
	{
		String value = namesMap.get(key);
		if (value == null)
			value = "unknown."+key;
		return value;
	}
	
	
	public static Map<String, String> readToMap(String file)
	{
		Map<String, String> map = new HashMap<String,String>();
		InputStream input = Localizer.class.getClassLoader().getResourceAsStream("localizations/mechanics/"+file);
		if (input==null)
		{
			ForgeMod.modLogger.warn("[Language Localizer] Failed opening locale file :"+file+".");
			return map;
		}
		Scanner scanner = new Scanner(input, "UTF-16");
		if (scanner.hasNext())//at least 1 line
		scanner.nextLine();//first line is skipped because of encoding problem
		while (scanner.hasNextLine())
		{
			String l = scanner.nextLine();
			if (l.startsWith("#")||l.equals(""))
				continue;//ignored
			String parts[] = l.split("::", 2);
			if (parts.length<2)
				ForgeMod.warning("[Language Localizer] Bad line in lang file: "+l);
			else if(!map.containsKey(parts[0]))
				map.put(parts[0], parts[1]);
			else
				ForgeMod.modLogger.warn("[Language Localizer] Key allready registered: "+parts[0]+".");
		}
		scanner.close();
		return map;
	}

	public static LocaleStringMap LoadGuiMap(String lang)
	{
		LocaleStringMap names_map = new LocaleStringMap("gui-"+lang+".map");
		if (!lang.equals("en"))
			names_map.populateFrom(LocaleStringMap.readToMap("gui-en.map"));
		return names_map;
	}
}
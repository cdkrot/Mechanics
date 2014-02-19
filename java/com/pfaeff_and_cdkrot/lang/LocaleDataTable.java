package com.pfaeff_and_cdkrot.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.Level;

import com.pfaeff_and_cdkrot.ForgeMod;
import com.pfaeff_and_cdkrot.util.Utility;

//TODO: use vanilla lang system

public class LocaleDataTable
{
	static
	{
		LocaleStringMap table = LocaleStringMap.LoadGuiMap(ForgeMod.lang);
		Field fs[] = LocaleDataTable.class.getFields();
		for (Field f: fs)
		{
			try
			{
				Localizable l = f.getAnnotation(Localizable.class);
				if (f.getType().equals(String.class) && l!=null)
					f.set(null,table.get(l.localeKey()));
				else if (f.getType().equals(String[].class) && l!=null)
				{
					String arr[] = Utility.loadFileAsStringArray("localizations/mechanics/"+l.localeKey()+"."+ForgeMod.lang, "UTF-16");
					f.set(null, arr);
				}
			}
			catch (Throwable t)
			{
				ForgeMod.modLogger.warn("[Language Localizer] Exception occured during localization phase.", t);
			}

		}
	}
	@Localizable(localeKey = "chatJmppad")
	public static String chatJumppad;
	@Localizable(localeKey = "cmdWrong")
	public static String cmdWrong;
	@Localizable(localeKey = "translator")
	public static String translator;
	@Localizable(localeKey = "!player")
	public static String not_a_player;
	@Localizable(localeKey = "noarticle")
	public static String article_not_available;
	////////////help
	public static final String[] version_info = new String[]
			{"\u00A72Mechanics mod\u00A7r::\u00A79Version.",
			 "\u00A7AVersion \u00A79 4.-1 (aka prefinal) [1.6.4]\u00A7A 12 December 2013."};
	public static final String[] credits = new String[]
			{"\u00A72Mechanics mod\u00A7r::\u00A79Credits.",
			 "Mod based on \"Pfaeffs Mods\" by Pfaeff.",
			 "Textures are made by Pfaeff.",
			 "Mechanics mod's author",
			 "(adapting, improving, and adding new stuff) is cdkrot.",
			 "This Mod's code is licensed under GPLv3",
			 "And all other mod assets (including textures and localization files) under Creative Commons Share Alike license.",
			 "License texts available at http://creativecommons.org/licenses/by-sa/3.0/legalcode and http://www.gnu.org/licenses/gpl-3.0.html"};
	
	@Localizable(localeKey = "mechanics-help")
	public static String[] mechanics_home;
	@Localizable(localeKey = "tracer-nobody")
	public static String tracer_nobody;
	@Localizable(localeKey = "tracer-block")
	public static String tracer_block;


}

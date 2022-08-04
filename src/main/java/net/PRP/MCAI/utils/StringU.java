package net.PRP.MCAI.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;

public class StringU
{
    private static final Pattern PATTERN_CONTROL_CODE = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    public static final String pattern = "\n==================3=3=========3=3===============5===3==1==33==1==33==1==3\n";
    private static Map<String, String> trnslt = new HashMap<>() {/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{
    	put("й","N");
    	put("ц","|_|,");
    	put("у","y");
    	put("к","K");
    	put("е","e");
    	put("н","H");
    	put("г","G");
    	put("ш","|_|_|");
    	put("щ","|_|_|");
    	put("з","3");
    	put("х","x");
    	put("ъ","|o");
    	put("ф","o|o");
    	put("ы","|o |");
    	put("в","B");
    	put("а","a");
    	put("п","p");
    	put("р","P");
    	put("о","O");
    	put("л","L");
    	put("д","D");
    	put("ж","|-|-|");
    	put("э","3");
    	put("я","R");
    	put("ч","4");
    	put("с","C");
    	put("м","M");
    	put("и","N");
    	put("т","T");
    	put("ь","|o");
    	put("б","b");
    	put("ю","|-0");
    }};
    
    public static String componentToString(Component smth) {
		//System.out.println(packet.getMessage().toString());
		StringBuilder message = new StringBuilder();
		if (smth instanceof TranslatableComponent) {
			TranslatableComponent a = (TranslatableComponent)smth;
			if (!a.args().isEmpty()) {
				for (Component arg : a.args()) {
					if (arg instanceof TextComponent) {
						message.append(" ").append(((TextComponent)arg).content());
					}
				}
			}
			if (!a.children().isEmpty()) {
				for (Component chl : a.children()) {
					if (chl instanceof TextComponent) {
						message.append(" ").append(((TextComponent)chl).content());
					}
				}
			}
		} else if (smth instanceof TextComponent) {
			TextComponent a = (TextComponent)smth;
			message.append((a).content());
			if (!a.children().isEmpty()) {
				for (Component chl : a.children()) {
					if (chl instanceof TextComponent) {
						message.append(" ").append(((TextComponent)chl).content());
					}
				}
			}
		}
		return message.toString();
	}
    
    public static boolean contains(List<String> list, String what) {
    	for (String str : list) {
    		if (str.contains(what)) return true;
    	}
    	return false;
    }
    
    public static boolean backwardContains(List<String> list, String what) {
    	for (String str : list) {
    		if (what.contains(str)) return true;
    	}
    	return false;
    }
    
    public static String translit(String text) {
    	for (Entry<String, String> entry : trnslt.entrySet()) {
    		text = text.replace(entry.getKey(), entry.getValue());
    	}
    	return text;
    }
    
    public static String ticksToElapsedTime(int ticks)
    {
        int i = ticks / 20;
        int j = i / 60;
        i = i % 60;
        return i < 10 ? j + ":0" + i : j + ":" + i;
    }

    public static String stripControlCodes(String text)
    {
        return PATTERN_CONTROL_CODE.matcher(text).replaceAll("");
    }

    /**
     * Returns a value indicating whether the given string is null or empty.
     */
    public static boolean isNullOrEmpty(@Nullable String string)
    {
        return org.apache.commons.lang3.StringUtils.isEmpty(string);
    }
    
    public static String RndLetter() {
    	return "q w e r t y u i o p a s d f g h j k l z x c v b n m".split(" ")[MathU.rnd(0, 25)];
    }
    
    public static String RndRuLetter() {
    	return "й ц у к е н г ш щ з х ъ ф ы в а п р о л д ж э я ч с м и т ь б ю".split(" ")[MathU.rnd(0, 31)];
    }
}

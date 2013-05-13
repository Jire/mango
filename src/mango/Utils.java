package mango;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {

	public static String between(String string, String first, String second) {
		Pattern pattern = Pattern
				.compile((first.length() == 1 ? "\\" : "") + first + "(.*?)"
						+ (second.length() == 1 ? "\\" : "") + second);
		Matcher matcher = pattern.matcher(string);
		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	public static String between(String string, char first, char second) {
		return between(string, Character.toString(first),
				Character.toString(second));
	}

	public static String strBetween(String string) {
		return between(string, '"', '"');
	}

}
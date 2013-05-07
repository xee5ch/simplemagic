package com.j256.simplemagic.types;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * A literal string search starting at the given offset. The same modifier flags can be used as for string patterns. The
 * modifier flags (if any) must be followed by /number the range, that is, the number of positions at which the match
 * will be attempted, starting from the start offset. This is suitable for searching larger binary expressions with
 * variable offsets, using \ escapes for special characters. The offset works as for regex. *
 * 
 * @author graywatson
 */
public class SearchType extends StringType {

	private final static Pattern TARGET_PATTERN = Pattern.compile("(.*?)(/[Bbc]*)?(/(\\d+))?");

	@Override
	public Object convertTestString(String test, int offset) {
		return convertTestString(TARGET_PATTERN, test, offset);
	}

	@Override
	public Object isMatch(Object testValue, Long andValue, boolean unsignedType, Object extractedValue, int offset,
			byte[] bytes) {
		StringTestInfo info = (StringTestInfo) testValue;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
		try {
			int lineCount;
			// if offset is 1 then we need to pre-read 1 line
			for (lineCount = 0; lineCount < offset; lineCount++) {
				// if eof then no match
				if (reader.readLine() == null) {
					return null;
				}
			}

			for (; lineCount < info.maxOffset; lineCount++) {
				String line = reader.readLine();
				// if eof then no match
				if (line == null) {
					break;
				}
				for (int i = 0; i < line.length(); i++) {
					String match = findOffsetMatch(info, i, null, line);
					if (match != null) {
						return match;
					}
				}
			}
			return null;
		} catch (IOException e) {
			// probably won't get here
			return null;
		}
	}
}

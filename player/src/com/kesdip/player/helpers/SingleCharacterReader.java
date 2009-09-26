package com.kesdip.player.helpers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class SingleCharacterReader {

	Logger logger = Logger.getLogger(SingleCharacterReader.class);

	List<String> charsList;
	int charsIndex = 0;
	int chars = 0;

	public SingleCharacterReader(String content) {
		sliceToChars(content);
	}

	private void sliceToChars(String content) {
		chars = content.length();
		charsList = new ArrayList<String>();
		for (int i = 1;i <= chars;i++) {
			charsList.add(content.substring((i-1),i));
		}
		charsList.add(content.substring(chars));

		if (logger.isDebugEnabled()) {
			logger.debug("number of chars: " + chars);
			StringBuilder sb = new StringBuilder();
			for (String ch :charsList)
				sb.append(ch);
			
			logger.debug(sb.toString());
				
		}
	}

	public String nextChar() {
		String retVal = charsList.get(charsIndex);
		charsIndex++;
		if (charsIndex == charsList.size()) 
			charsIndex = 0;

		

		return retVal;
	}

}

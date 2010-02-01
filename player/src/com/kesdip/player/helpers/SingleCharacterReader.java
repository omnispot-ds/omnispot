package com.kesdip.player.helpers;

import java.util.List;

import org.apache.log4j.Logger;

public class SingleCharacterReader {

	Logger logger = Logger.getLogger(SingleCharacterReader.class);

	List<String> charsList;
	int charsIndex = 0;
	int chars = 0;
	boolean newcontent = false;
	String newContent;
	
	char[] characters;

	public SingleCharacterReader(String content) {
		sliceToChars(content);
	}

	public void updateContent(String newContent) {
		newcontent = true;
		this.newContent = newContent;
	}

	private void sliceToChars(String content) {
		
		characters = content.toCharArray();
		
		if (logger.isDebugEnabled()) {
			logger.debug("number of chars: " + chars);
			StringBuilder sb = new StringBuilder();
			for (String ch :charsList)
				sb.append(ch);

			logger.debug(sb.toString());
		}
	}

	/**
	 * 
	 * @return the next character (in a loop)
	 */
	public String nextChar() {
		if (newcontent && charsIndex == 0)
		{
			if (logger.isDebugEnabled())
				logger.debug("updating content...");
			
			sliceToChars(newContent);
			newcontent = false;
		}	
		String retVal = new String (new char[]{characters[charsIndex]});;
		
		charsIndex++;
		if (charsIndex == characters.length) {
			charsIndex = 0;
		}

		return retVal;
	}

}

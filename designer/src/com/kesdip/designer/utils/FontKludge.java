package com.kesdip.designer.utils;

import java.awt.Font;

import org.eclipse.swt.graphics.FontData;

public class FontKludge extends Font {

	private static final long serialVersionUID = 1L;
	
	private FontData fontData;

	public FontKludge(String name, int style, int size) {
		super(name, style, size);
		fontData = null;
	}

	public void setFontData(FontData fontData) {
		this.fontData = fontData;
	}
	
	public FontData getFontData() {
		return fontData;
	}

	@Override
	public String toString() {
	    
        String  strStyle;

        if (isBold()) {
            strStyle = isItalic() ? "bolditalic" : "bold";
        } else {
            strStyle = isItalic() ? "italic" : "plain";
        }

        return "java.awt.Font[family=" + getFamily() + ",name=" +
        	name + ",style=" + strStyle + ",size=" + size + "]";
	}

}

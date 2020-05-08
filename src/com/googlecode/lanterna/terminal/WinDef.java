package com.googlecode.lanterna.terminal;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

public interface WinDef extends com.sun.jna.platform.win32.WinDef {

	/**
	 * COORD structure
	 */
	@FieldOrder({ "X", "Y" })
	public static class COORD extends Structure {

		public short X;
		public short Y;

		@Override
		public String toString() {
			return String.format("COORD[%s,%s]", X, Y);
		}
	}

	/**
	 * SMALL_RECT structure
	 */
	@FieldOrder({ "Left", "Top", "Right", "Bottom" })
	public static class SMALL_RECT extends Structure {

		public short Left;
		public short Top;
		public short Right;
		public short Bottom;

		@Override
		public String toString() {
			return String.format("SMALL_RECT[%s,%s,%s,%s]", Left, Top, Right, Bottom);
		}
	}

	/**
	 * CONSOLE_SCREEN_BUFFER_INFO structure
	 */
	@FieldOrder({ "dwSize", "dwCursorPosition", "wAttributes", "srWindow", "dwMaximumWindowSize" })
	public static class CONSOLE_SCREEN_BUFFER_INFO extends Structure {

		public COORD dwSize;
		public COORD dwCursorPosition;
		public WORD wAttributes;
		public SMALL_RECT srWindow;
		public COORD dwMaximumWindowSize;

		@Override
		public String toString() {
			return String.format("CONSOLE_SCREEN_BUFFER_INFO[%s,%s,%s,%s,%s]", dwSize, dwCursorPosition, wAttributes, srWindow, dwMaximumWindowSize);
		}
	}

}

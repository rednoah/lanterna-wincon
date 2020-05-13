package com.googlecode.lanterna.terminal;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;

public interface WinDef extends com.sun.jna.platform.win32.WinDef {

	/**
	 * COORD structure
	 */
	@FieldOrder({ "X", "Y" })
	public static class COORD extends Structure {

		public SHORT X;
		public SHORT Y;

		@Override
		public String toString() {
			return String.format("COORD(%s,%s)", X, Y);
		}
	}

	/**
	 * SMALL_RECT structure
	 */
	@FieldOrder({ "Left", "Top", "Right", "Bottom" })
	public static class SMALL_RECT extends Structure {

		public SHORT Left;
		public SHORT Top;
		public SHORT Right;
		public SHORT Bottom;

		@Override
		public String toString() {
			return String.format("SMALL_RECT(%s,%s)(%s,%s)", Left, Top, Right, Bottom);
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
			return String.format("CONSOLE_SCREEN_BUFFER_INFO(%s,%s,%s,%s,%s)", dwSize, dwCursorPosition, wAttributes, srWindow, dwMaximumWindowSize);
		}
	}

	@FieldOrder({ "EventType", "Event" })
	public static class INPUT_RECORD extends Structure {

		public static final byte KEY_EVENT = 0x01;
		public static final byte MOUSE_EVENT = 0x02;
		public static final byte WINDOW_BUFFER_SIZE_EVENT = 0x04;

		public WORD EventType;
		public UNION Event;

		public static class UNION extends Union {
			public KEY_EVENT_RECORD KeyEvent;
			public MOUSE_EVENT_RECORD MouseEvent;
			public WINDOW_BUFFER_SIZE_RECORD WindowBufferSizeEvent;
		}

		@Override
		public void read() {
			super.read();
			switch (EventType.byteValue()) {
			case KEY_EVENT:
				Event.setType(KEY_EVENT_RECORD.class);
				Event.read();
				break;
			case MOUSE_EVENT:
				Event.setType(MOUSE_EVENT_RECORD.class);
				Event.read();
				break;
			case WINDOW_BUFFER_SIZE_EVENT:
				Event.setType(WINDOW_BUFFER_SIZE_RECORD.class);
				Event.read();
				break;
			}
		}

		@Override
		public String toString() {
			return String.format("INPUT_RECORD(%s)", EventType);
		}
	}

	@FieldOrder({ "bKeyDown", "wRepeatCount", "wVirtualKeyCode", "wVirtualScanCode", "uChar", "dwControlKeyState" })
	public static class KEY_EVENT_RECORD extends Structure {

		public BOOL bKeyDown;
		public WORD wRepeatCount;
		public WORD wVirtualKeyCode;
		public WORD wVirtualScanCode;
		public char uChar;
		public DWORD dwControlKeyState;

		@Override
		public String toString() {
			return String.format("KEY_EVENT_RECORD(%s,%s,%s,%s,%s,%s)", bKeyDown, wRepeatCount, wVirtualKeyCode, wVirtualKeyCode, wVirtualScanCode, uChar, dwControlKeyState);
		}
	}

	@FieldOrder({ "dwMousePosition", "dwButtonState", "dwControlKeyState", "dwEventFlags" })
	public static class MOUSE_EVENT_RECORD extends Structure {

		public COORD dwMousePosition;
		public DWORD dwButtonState;
		public DWORD dwControlKeyState;
		public DWORD dwEventFlags;

		@Override
		public String toString() {
			return String.format("MOUSE_EVENT_RECORD(%s,%s,%s,%s)", dwMousePosition, dwButtonState, dwControlKeyState, dwEventFlags);
		}
	}

	@FieldOrder({ "dwSize" })
	public static class WINDOW_BUFFER_SIZE_RECORD extends Structure {

		public COORD dwSize;

		@Override
		public String toString() {
			return String.format("WINDOW_BUFFER_SIZE_RECORD(%s)", dwSize);
		}
	}

}

package com.googlecode.lanterna.terminal;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.function.Consumer;

import com.googlecode.lanterna.terminal.WinDef.INPUT_RECORD;
import com.googlecode.lanterna.terminal.WinDef.KEY_EVENT_RECORD;
import com.googlecode.lanterna.terminal.WinDef.MOUSE_EVENT_RECORD;
import com.googlecode.lanterna.terminal.WinDef.WINDOW_BUFFER_SIZE_RECORD;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class WindowsConsoleInputStream extends InputStream {

	private final HANDLE hConsoleInput;
	private final Charset charset;

	public WindowsConsoleInputStream(HANDLE hConsoleInput, Charset charset) {
		this.hConsoleInput = hConsoleInput;
		this.charset = charset;
	}

	private INPUT_RECORD[] readConsoleInput() throws IOException {
		INPUT_RECORD[] lpBuffer = new INPUT_RECORD[64];
		DWORD nLength = new DWORD(lpBuffer.length);
		DWORDByReference lpNumberOfEventsRead = new DWORDByReference();
		if (Wincon.INSTANCE.ReadConsoleInput(hConsoleInput, lpBuffer, nLength, lpNumberOfEventsRead)) {
			int n = lpNumberOfEventsRead.getValue().intValue();
			return Arrays.copyOfRange(lpBuffer, 0, n);
		}
		throw new EOFException();
	}

	private int availableConsoleInput() {
		DWORDByReference lpcNumberOfEvents = new DWORDByReference();
		if (Wincon.INSTANCE.GetNumberOfConsoleInputEvents(hConsoleInput, lpcNumberOfEvents)) {
			return lpcNumberOfEvents.getValue().intValue();
		}
		return 0;
	}

	private ByteBuffer buffer = ByteBuffer.allocate(0);

	@Override
	public synchronized int read() throws IOException {
		while (!buffer.hasRemaining()) {
			buffer = readKeyEvents(true);
		}

		return buffer.get();
	}

	@Override
	public synchronized int read(byte[] b, int offset, int length) throws IOException {
		// read more
		while (length > 0 && !buffer.hasRemaining()) {
			buffer = readKeyEvents(true);
		}

		int n = Math.min(buffer.remaining(), length);
		buffer.get(b, offset, n);
		return n;
	}

	@Override
	public synchronized int available() throws IOException {
		if (buffer.hasRemaining()) {
			return buffer.remaining();
		}

		buffer = readKeyEvents(false);
		return buffer.remaining();
	}

	private ByteBuffer readKeyEvents(boolean blocking) throws IOException {
		StringBuilder keyEvents = new StringBuilder();

		if (blocking || availableConsoleInput() > 0) {
			for (INPUT_RECORD i : readConsoleInput()) {
				filter(i, keyEvents);
			}
		}

		return charset.encode(CharBuffer.wrap(keyEvents));
	}

	private void filter(INPUT_RECORD input, Appendable keyEvents) throws IOException {
		switch (input.EventType.byteValue()) {
		case INPUT_RECORD.KEY_EVENT:
			if (input.Event.KeyEvent.uChar != 0 && input.Event.KeyEvent.bKeyDown.booleanValue()) {
				keyEvents.append(input.Event.KeyEvent.uChar);
			}
			if (keyEventHandler != null) {
				keyEventHandler.accept(input.Event.KeyEvent);
			}
			break;
		case INPUT_RECORD.MOUSE_EVENT:
			if (mouseEventHandler != null) {
				mouseEventHandler.accept(input.Event.MouseEvent);
			}
			break;
		case INPUT_RECORD.WINDOW_BUFFER_SIZE_EVENT:
			if (windowBufferSizeEventHandler != null) {
				windowBufferSizeEventHandler.accept(input.Event.WindowBufferSizeEvent);
			}
			break;
		}
	}

	private Consumer<KEY_EVENT_RECORD> keyEventHandler = null;
	private Consumer<MOUSE_EVENT_RECORD> mouseEventHandler = null;
	private Consumer<WINDOW_BUFFER_SIZE_RECORD> windowBufferSizeEventHandler = null;

	public void onKeyEvent(Consumer<KEY_EVENT_RECORD> handler) {
		if (keyEventHandler == null) {
			keyEventHandler = handler;
		} else {
			keyEventHandler = keyEventHandler.andThen(handler);
		}
	}

	public void onMouseEvent(Consumer<MOUSE_EVENT_RECORD> handler) {
		if (mouseEventHandler == null) {
			mouseEventHandler = handler;
		} else {
			mouseEventHandler = mouseEventHandler.andThen(handler);
		}
	}

	public void onWindowBufferSizeEvent(Consumer<WINDOW_BUFFER_SIZE_RECORD> handler) {
		if (windowBufferSizeEventHandler == null) {
			windowBufferSizeEventHandler = handler;
		} else {
			windowBufferSizeEventHandler = windowBufferSizeEventHandler.andThen(handler);
		}
	}

}

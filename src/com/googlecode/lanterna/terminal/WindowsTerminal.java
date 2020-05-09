package com.googlecode.lanterna.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.BasicCharacterPattern;
import com.googlecode.lanterna.input.CharacterPattern;
import com.googlecode.lanterna.input.KeyDecodingProfile;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.ansi.UnixLikeTerminal;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public class WindowsTerminal extends UnixLikeTerminal {

	private static final HANDLE CONSOLE_INPUT_HANDLE = Wincon.INSTANCE.GetStdHandle(Wincon.STD_INPUT_HANDLE);
	private static final HANDLE CONSOLE_OUTPUT_HANDLE = Wincon.INSTANCE.GetStdHandle(Wincon.STD_OUTPUT_HANDLE);

	private int[] settings;

	public WindowsTerminal() throws IOException {
		this(System.in, System.out, Charset.defaultCharset(), CtrlCBehaviour.CTRL_C_KILLS_APPLICATION);
	}

	public WindowsTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset, CtrlCBehaviour terminalCtrlCBehaviour) throws IOException {
		super(new AsyncBlockingInputStream(terminalInput), terminalOutput, terminalCharset, terminalCtrlCBehaviour);
	}

	@Override
	protected KeyDecodingProfile getDefaultKeyDecodingProfile() {
		ArrayList<CharacterPattern> keyDecodingProfile = new ArrayList<CharacterPattern>();
		// handle Key Code 13 as ENTER
		keyDecodingProfile.add(new BasicCharacterPattern(new KeyStroke(KeyType.Enter), '\r'));
		// handle everything else as per default
		keyDecodingProfile.addAll(super.getDefaultKeyDecodingProfile().getPatterns());
		return () -> keyDecodingProfile;
	}

	@Override
	protected void acquire() throws IOException {
		super.acquire();

		int terminalOutputMode = getConsoleOutputMode();
		terminalOutputMode |= Wincon.ENABLE_VIRTUAL_TERMINAL_PROCESSING;
		terminalOutputMode |= Wincon.DISABLE_NEWLINE_AUTO_RETURN;
		Wincon.INSTANCE.SetConsoleMode(CONSOLE_OUTPUT_HANDLE, terminalOutputMode);

		int terminalInputMode = getConsoleInputMode();
		terminalInputMode |= Wincon.ENABLE_MOUSE_INPUT;
		terminalInputMode |= Wincon.ENABLE_WINDOW_INPUT;
		terminalInputMode |= Wincon.ENABLE_VIRTUAL_TERMINAL_INPUT;
		Wincon.INSTANCE.SetConsoleMode(CONSOLE_INPUT_HANDLE, terminalInputMode);
	}

	@Override
	public synchronized void saveTerminalSettings() throws IOException {
		settings = new int[] { getConsoleInputMode(), getConsoleOutputMode() };
	}

	@Override
	public synchronized void restoreTerminalSettings() throws IOException {
		if (settings != null) {
			Wincon.INSTANCE.SetConsoleMode(CONSOLE_INPUT_HANDLE, settings[0]);
			Wincon.INSTANCE.SetConsoleMode(CONSOLE_OUTPUT_HANDLE, settings[1]);
		}
	}

	@Override
	public synchronized void keyEchoEnabled(boolean enabled) throws IOException {
		int mode = getConsoleInputMode();
		if (enabled) {
			mode |= Wincon.ENABLE_ECHO_INPUT;
		} else {
			mode &= ~Wincon.ENABLE_ECHO_INPUT;
		}
		Wincon.INSTANCE.SetConsoleMode(CONSOLE_INPUT_HANDLE, mode);
	}

	@Override
	public synchronized void canonicalMode(boolean enabled) throws IOException {
		int mode = getConsoleInputMode();
		if (enabled) {
			mode |= Wincon.ENABLE_LINE_INPUT;
		} else {
			mode &= ~Wincon.ENABLE_LINE_INPUT;
		}
		Wincon.INSTANCE.SetConsoleMode(CONSOLE_INPUT_HANDLE, mode);
	}

	@Override
	public synchronized void keyStrokeSignalsEnabled(boolean enabled) throws IOException {
		int mode = getConsoleInputMode();
		if (enabled) {
			mode |= Wincon.ENABLE_PROCESSED_INPUT;
		} else {
			mode &= ~Wincon.ENABLE_PROCESSED_INPUT;
		}
		Wincon.INSTANCE.SetConsoleMode(CONSOLE_INPUT_HANDLE, mode);
	}

	@Override
	protected TerminalSize findTerminalSize() throws IOException {
		WinDef.CONSOLE_SCREEN_BUFFER_INFO screenBufferInfo = new WinDef.CONSOLE_SCREEN_BUFFER_INFO();
		Wincon.INSTANCE.GetConsoleScreenBufferInfo(CONSOLE_OUTPUT_HANDLE, screenBufferInfo);
		int columns = screenBufferInfo.srWindow.Right - screenBufferInfo.srWindow.Left + 1;
		int rows = screenBufferInfo.srWindow.Bottom - screenBufferInfo.srWindow.Top + 1;
		return new TerminalSize(columns, rows);
	}

	@Override
	public void registerTerminalResizeListener(Runnable runnable) throws IOException {
		// not implemented
	}

	public synchronized TerminalPosition getCursorPosition() {
		WinDef.CONSOLE_SCREEN_BUFFER_INFO screenBufferInfo = new WinDef.CONSOLE_SCREEN_BUFFER_INFO();
		Wincon.INSTANCE.GetConsoleScreenBufferInfo(CONSOLE_OUTPUT_HANDLE, screenBufferInfo);
		int column = screenBufferInfo.dwCursorPosition.X - screenBufferInfo.srWindow.Left;
		int row = screenBufferInfo.dwCursorPosition.Y - screenBufferInfo.srWindow.Top;
		return new TerminalPosition(column, row);
	}

	private int getConsoleInputMode() {
		IntByReference lpMode = new IntByReference();
		Wincon.INSTANCE.GetConsoleMode(CONSOLE_INPUT_HANDLE, lpMode);
		return lpMode.getValue();
	}

	private int getConsoleOutputMode() {
		IntByReference lpMode = new IntByReference();
		Wincon.INSTANCE.GetConsoleMode(CONSOLE_OUTPUT_HANDLE, lpMode);
		return lpMode.getValue();
	}
}

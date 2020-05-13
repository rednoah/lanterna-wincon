package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.terminal.WinDef.CONSOLE_SCREEN_BUFFER_INFO;
import com.googlecode.lanterna.terminal.WinDef.INPUT_RECORD;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LPVOID;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Wincon extends StdCallLibrary, com.sun.jna.platform.win32.Wincon {

	Wincon INSTANCE = Native.load("kernel32", Wincon.class, W32APIOptions.UNICODE_OPTIONS);

	int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;
	int DISABLE_NEWLINE_AUTO_RETURN = 0x0008;
	int ENABLE_VIRTUAL_TERMINAL_INPUT = 0x0200;

	boolean GetConsoleScreenBufferInfo(HANDLE hConsoleOutput, CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo);

	boolean ReadConsoleInput(HANDLE hConsoleInput, INPUT_RECORD[] lpBuffer, DWORD nLength, DWORDByReference lpNumberOfEventsRead);

	boolean GetNumberOfConsoleInputEvents(HANDLE hConsoleInput, DWORDByReference lpcNumberOfEvents);

	boolean WriteConsole(HANDLE hConsoleOutput, String lpBuffer, DWORD nNumberOfCharsToWrite, DWORDByReference lpNumberOfCharsWritten, LPVOID lpReserved);

}

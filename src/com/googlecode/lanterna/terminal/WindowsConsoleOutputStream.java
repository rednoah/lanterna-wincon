package com.googlecode.lanterna.terminal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class WindowsConsoleOutputStream extends OutputStream {

	private final HANDLE hConsoleOutput;
	private final Charset charset;

	public WindowsConsoleOutputStream(HANDLE hConsoleOutput, Charset charset) {
		this.hConsoleOutput = hConsoleOutput;
		this.charset = charset;
	}

	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	@Override
	public synchronized void write(int b) {
		buffer.write(b);
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) {
		buffer.write(b, off, len);
	}

	@Override
	public synchronized void flush() throws IOException {
		String characters = buffer.toString(charset);
		buffer.reset();

		Wincon.INSTANCE.WriteConsole(hConsoleOutput, characters, new DWORD(characters.length()), null, null);
	}

}

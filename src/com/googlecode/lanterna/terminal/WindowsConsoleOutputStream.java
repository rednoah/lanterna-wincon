package com.googlecode.lanterna.terminal;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public class WindowsConsoleOutputStream extends OutputStream {

	private final HANDLE hConsoleOutput;
	private final Charset decoder;

	public WindowsConsoleOutputStream(Charset decoder) {
		this(Wincon.INSTANCE.GetStdHandle(Wincon.STD_OUTPUT_HANDLE), decoder);
	}

	public WindowsConsoleOutputStream(HANDLE hConsoleOutput, Charset decoder) {
		this.hConsoleOutput = hConsoleOutput;
		this.decoder = decoder;
	}

	public HANDLE getHandle() {
		return hConsoleOutput;
	}

	public Charset getDecoder() {
		return decoder;
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
		String characters = buffer.toString(decoder);
		buffer.reset();

		IntByReference lpNumberOfCharsWritten = new IntByReference();
		while (!characters.isEmpty()) {
			if (!Wincon.INSTANCE.WriteConsole(hConsoleOutput, characters, characters.length(), lpNumberOfCharsWritten, null)) {
				throw new EOFException();
			}
			characters = characters.substring(lpNumberOfCharsWritten.getValue());
		}
	}

}

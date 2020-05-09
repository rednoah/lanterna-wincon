package com.googlecode.lanterna.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Standard Input on Windows will always report available() == 0 so we need to
 * do read() ahead of time in the background so we can return correct
 * available() values.
 */
public class AsyncBlockingInputStream extends InputStream implements Runnable {

	private final InputStream in;

	public AsyncBlockingInputStream(InputStream in) {
		this(in, r -> new Thread(r).start());
	}

	public AsyncBlockingInputStream(InputStream in, Executor executor) {
		this.in = in;
		executor.execute(this);
	}

	private final LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();

	@Override
	public void run() {
		try {
			int i = 0;
			while (i >= 0) {
				i = in.read();
				queue.add(i);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public int read() throws IOException {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		// read one byte and no more
		return super.read(b, off, len > 1 ? 1 : len);
	}

	@Override
	public int available() throws IOException {
		return queue.size();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}

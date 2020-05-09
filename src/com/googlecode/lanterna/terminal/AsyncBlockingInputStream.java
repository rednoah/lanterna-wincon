package com.googlecode.lanterna.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

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

	private final ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(64);

	@Override
	public void run() {
		try {
			int i = 0;
			while (i >= 0) {
				i = in.read();
				queue.put(i);
			}
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int read() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		// read exactly one byte (e.g. keystroke) and no more so we block only if and
		// when we run out of individual bytes to read
		return super.read(b, off, len > 1 ? 1 : len);
	}

	@Override
	public int available() {
		return queue.size();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}

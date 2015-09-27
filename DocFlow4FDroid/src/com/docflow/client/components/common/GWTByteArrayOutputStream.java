package com.docflow.client.components.common;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class implements an output stream in which the data is written into a
 * byte array. The buffer automatically grows as data is written to it. The data
 * can be retrieved using <code>toByteArray()</code> and <code>toString()</code>
 * .
 * <p>
 * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in this
 * class can be called after the stream has been closed without generating an
 * <tt>IOException</tt>.
 * 
 * @author Arthur van Hoff
 * @since JDK1.0
 */

public class GWTByteArrayOutputStream extends OutputStream {

	private StringBuilder sb = new StringBuilder();

	public GWTByteArrayOutputStream() {
	}

	public synchronized String toString() {
		return sb.toString();
	}

	public void write(int b) throws IOException {
		sb.append((char) b);

	}

}
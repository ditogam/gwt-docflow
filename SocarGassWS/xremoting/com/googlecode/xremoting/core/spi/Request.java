package com.googlecode.xremoting.core.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 * Represents a single request/response pair.
 * </p>
 * <p>
 * The contract mandates the following order of invocation of methods:
 * <ol>
 * <li>getOutputStream()</li>
 * <li>commitRequest()</li>
 * <li>getInputStream()</li>
 * <li>releaseRequest()</li>
 * </ol>
 * Each of these methods is to be called maximum once.
 * </p>
 * <p>
 * releaseRequest() method MUST be called anyway, even if error occurred.
 * </p>
 * 
 * @author Roman Puchkovskiy
 * @see Requester
 */
public interface Request {
	/**
	 * Commits a request. That is, changes request state to the one in which no
	 * data may be sent to remote side, and data may be got from remote side.
	 * 
	 * @throws IOException
	 *             if i/o error occurs
	 */
	void commitRequest() throws IOException;

	/**
	 * Returns {@link InputStream} from which user may get response data.
	 * 
	 * @return input stream
	 * @throws IOException
	 *             if i/o error occurs
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Returns {@link OutputStream} to which user may send request data.
	 * 
	 * @return output stream
	 * @throws IOException
	 *             if i/o error occurs
	 */
	OutputStream getOutputStream() throws IOException;

	String getResponceType();

	/**
	 * Releases resources claimed by request (closes connections, etc.).
	 */
	void releaseRequest();
}

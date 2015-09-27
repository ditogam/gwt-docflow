package com.googlecode.xremoting.core.xstream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.googlecode.xremoting.core.message.Invocation;
import com.googlecode.xremoting.core.message.Result;
import com.googlecode.xremoting.core.message.Thrown;
import com.googlecode.xremoting.core.spi.SerializationException;
import com.googlecode.xremoting.core.spi.Serializer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.BaseException;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * <p>
 * {@link Serializer} which uses XStream as a serialization engine. So,
 * basically, data is converted to XML during the serialization.
 * </p>
 * <p>
 * This is the default XRemoting serializer.
 * </p>
 * 
 * @author Roman Puchkovskiy
 */
@SuppressWarnings("deprecation")
public class XStreamSerializer implements Serializer {

	protected XStream xstream;
	private static final int BUFFER_SIZE = 1024 * 4;

	public XStreamSerializer() {
		super();
		xstream = createXStream();
	}

	protected XStream createXStream() {
		XStream xs = new XStream(){
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				return new MapperWrapper(next) {
					@SuppressWarnings("rawtypes")
					public boolean shouldSerializeMember(Class definedIn,
							String fieldName) {
						try {
							return definedIn != Object.class
									|| realClass(fieldName) != null;
						} catch (CannotResolveClassException cnrce) {
							return false;
						}
					}
				};
			}
		};
		xs.alias("invocation", Invocation.class);
		xs.alias("result", Result.class);
		xs.alias("thrown", Thrown.class);
		return xs;
	}

	public void serialize(Object object, OutputStream os)
			throws SerializationException, IOException {
		try {

			ByteArrayOutputStream bos_compressed = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(bos_compressed);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(bos,
					getDefaultCharset());

			xstream.toXML(object, writer);
			byte[] bt = bos.toByteArray();
			@SuppressWarnings("unused")
			int len = bt.length;

			gos.write(bt);
			gos.finish();
			gos.close();

			bt = bos_compressed.toByteArray();
			len = bt.length;
			os.write(bt);
		} catch (StreamException e) {
			throw new IOException(e);
		} catch (BaseException e) {
			throw new SerializationException(e);
		}
	}

	public static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static byte[] decompress(byte[] compressed) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(compressed);
		GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
		byte[] data = new byte[BUFFER_SIZE];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int bytesRead;
		while ((bytesRead = gis.read(data)) != -1) {
			os.write(data, 0, bytesRead);
		}
		data = os.toByteArray();
		gis.close();
		is.close();
		return data;
	}

	@SuppressWarnings("unchecked")
	public <T> T deserialize(InputStream is) throws SerializationException,
			IOException {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			copyLarge(is, bos);
			byte[] bt = bos.toByteArray();
			bt = decompress(bt);
			
			ByteArrayInputStream bis = new ByteArrayInputStream(bt);
			return (T) xstream.fromXML(createReader(bis));
		} catch (BaseException e) {
			throw new SerializationException(e);
		}
	}

	protected Writer createWriter(OutputStream os)
			throws UnsupportedEncodingException {
		return new OutputStreamWriter(os, getDefaultCharset());
	}

	protected Reader createReader(InputStream is)
			throws UnsupportedEncodingException {
		return new InputStreamReader(is, getDefaultCharset());
	}

	protected String getDefaultCharset() {
		return "utf-8";
	}

}

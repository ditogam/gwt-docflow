package org.oscim.database.spatialite.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import android.content.Context;

public class AssetHelper {
	/**
	 * Copies the asset with the specified name to the specified directory.
	 * 
	 * @param ctx
	 *            the context to which the asset belongs
	 * @param assetFilename
	 *            the filename of the asset to be copied
	 * @param destinationDir
	 *            the destination directory to which the asset is to be copied
	 * @throws IOException
	 *             when the asset couldn't be copied
	 */
	public static void copyAsset(Context ctx, String assetFilename,
			File destinationFile) {
		InputStream in = null;
		OutputStream out = null;

		try {
			try {
				in = ctx.getAssets().open(assetFilename);
				out = new FileOutputStream(destinationFile);
				copyFile(in, out);
			} finally {
				if (in != null) {
					in.close();
				}

				if (out != null) {
					out.flush();
					out.close();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;

		// Copy from input stream to output stream
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public static String readToString(Context ctx, String assetFilename) {
		InputStream in = null;
		try {
			try {
				in = ctx.getAssets().open(assetFilename);
				// See: http://stackoverflow.com/a/5445161/614177
				return new Scanner(in).useDelimiter("\\A").next();
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

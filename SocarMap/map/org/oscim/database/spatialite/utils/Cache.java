/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.database.spatialite.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import jsqlite.Stmt;

import org.oscim.core.Tile;
import org.oscim.database.spatialite.Values;
import org.oscim.database.spatialite.sqlgenerator.SQLGenerator;

import android.content.Context;
import android.os.Environment;

public class Cache {
	private static final String CACHE_DIRECTORY = "/Android/data/org.oscim.app/cache/";
	private static final String CACHE_FILE = ".tile";

	private static File cacheDir;

	private static jsqlite.Database mDatabase = null;

	public static byte[] compress(String text) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			OutputStream out = new DeflaterOutputStream(baos);
			out.write(text.getBytes("UTF-8"));
			out.close();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return baos.toByteArray();
	}

	private static File createDirectory(String pathName) {
		File file = new File(pathName);
		if (!file.exists() && !file.mkdirs()) {
			throw new IllegalArgumentException("could not create directory: "
					+ file);
		} else if (!file.isDirectory()) {
			throw new IllegalArgumentException("not a directory: " + file);
		} else if (!file.canRead()) {
			throw new IllegalArgumentException("cannot read directory: " + file);
		} else if (!file.canWrite()) {
			throw new IllegalArgumentException("cannot write directory: "
					+ file);
		}
		return file;
	}

	@SuppressWarnings("unused")
	private static File createPath(Tile tile) {
		String externalStorageDirectory = Environment
				.getExternalStorageDirectory().getAbsolutePath();
		String cacheDirectoryPath = externalStorageDirectory + CACHE_DIRECTORY;
		cacheDir = createDirectory(cacheDirectoryPath);
		File f = new File(cacheDir, tile.zoomLevel + "");
		f = new File(f, tile.tileX + "");
		f = new File(f, tile.tileY + CACHE_FILE);
		return f;
	}

	public static String decompress(byte[] bytes) {
		InputStream in = new InflaterInputStream(
				new ByteArrayInputStream(bytes));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[8192];
			int len;
			while ((len = in.read(buffer)) > 0)
				baos.write(buffer, 0, len);
			return new String(baos.toByteArray(), "UTF-8");
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public synchronized static ArrayList<Values> getFromCatch(Tile tile,
			SQLGenerator generator) {

		ArrayList<Values> result = null;

		if (mDatabase == null)
			return null;
		Stmt st = null;
		try {
			st = mDatabase
					.prepare("select layer,cnt,data from catch where z=? and x=? and y=?");
			int index = 1;
			st.bind(index++, tile.zoomLevel);
			st.bind(index++, tile.tileX);
			st.bind(index++, tile.tileY);
			while (st.step()) {
				index = 0;
				int layer = st.column_int(index++);
				int count = st.column_int(index++);
				byte[] bytedata = st.column_bytes(index++);
				String data = decompress(bytedata);
				if (result == null)
					result = new ArrayList<Values>();
				result.add(new Values(layer, count, data, generator
						.getTagsParcerForLayer(layer)));
			}
		} catch (Exception e) {

		} finally {
			if (st != null)
				try {
					st.close();
				} catch (Exception e2) {

				}
		}

		// File f = createPath(tile);
		// if (f.exists()) {
		// FileReader fir = null;
		// BufferedReader br = null;
		// try {
		// result = new ArrayList<Values>();
		// fir = new FileReader(f);
		// br = new BufferedReader(fir);
		// String thisLine;
		// while ((thisLine = br.readLine()) != null) { // while loop begins
		// here
		// int index = thisLine.indexOf(',');
		// int layer = Integer.parseInt(thisLine.substring(0, index));
		// thisLine = thisLine.substring(index + 1);
		// index = thisLine.indexOf(',');
		// int count = Integer.parseInt(thisLine.substring(0, index));
		// String values = thisLine.substring(index + 1);
		// result.add(new Values(layer, count, values, generator
		// .getTagsParcerForLayer(layer)));
		// }
		// } catch (Exception e) {
		// result = null;
		// if (fir != null)
		// try {
		// fir.close();
		// } catch (Exception e2) {
		// n
		// }
		// if (br != null)
		// try {
		// br.close();
		// } catch (Exception e2) {
		// n
		// }
		// fir = null;
		// br = null;
		// f.delete();
		//
		// } finally {
		// if (fir != null) {
		// try {
		// fir.close();
		// } catch (Exception e2) {
		// n
		// }
		// }
		// if (br != null) {
		// try {
		// br.close();
		// } catch (Exception e2) {
		// n
		// }
		// }
		// }
		// }
		return result;
	}

	public static synchronized void initCache(Context ctx, String fileName) {
		if (mDatabase != null)
			return;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				AssetHelper.copyAsset(ctx, "tilecatch.sqlite", file);
			}
			mDatabase = new jsqlite.Database();
			mDatabase.open(file.getAbsolutePath(),
					jsqlite.Constants.SQLITE_OPEN_READWRITE);
		} catch (Throwable e) {
			mDatabase = null;
		}

	}

	public static void writeCatch(ArrayList<Values> result, Tile tile) {

		if (mDatabase == null)
			return;
		Stmt st = null;
		try {
			st = mDatabase
					.prepare("delete from catch where z=? and x=? and y=?");
			int index = 1;
			st.bind(index++, tile.zoomLevel);
			st.bind(index++, tile.tileX);
			st.bind(index++, tile.tileY);
			st.step();
		} catch (Exception e) {

		} finally {
			if (st != null)
				try {
					st.close();
				} catch (Exception e2) {

				}
		}

		try {
			st = mDatabase
					.prepare("insert into catch (z,x,y,layer,cnt,data) values(?,?,?,?,?,?)");
			for (Values values : result) {
				int index = 1;

				st.bind(index++, tile.zoomLevel);
				st.bind(index++, tile.tileX);
				st.bind(index++, tile.tileY);
				st.bind(index++, values.layer);
				st.bind(index++, values.count);
				st.bind(index++, compress(values.data));
				st.step();
				st.clear_bindings();
				st.reset();
			}
		} catch (Exception e) {

		} finally {
			if (st != null)
				try {
					st.close();
				} catch (Exception e2) {

				}
		}

		// File file = createPath(tile);
		// File dir = file.getParentFile();
		// if (!dir.exists())
		// dir.mkdirs();
		// file.delete();
		// boolean doneOnce = false;
		// FileOutputStream fos = null;
		// try {
		// fos = new FileOutputStream(file);
		// for (Values values : result) {
		// String value = (doneOnce ? "\n" : "") + values.layer + "," +
		// values.count + ","
		// + values.data;
		// fos.write(value.getBytes("UTF8"));
		// doneOnce = true;
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// if (fos != null)
		// try {
		// fos.flush();
		// } catch (Exception e2) {
		// n
		// }
		// if (fos != null)
		// try {
		// fos.close();
		// } catch (Exception e2) {
		// n
		// }
		// }
	}

}

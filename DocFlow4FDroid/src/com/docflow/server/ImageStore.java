package com.docflow.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.MultipartStream.ItemInputStream;

import com.common.db.DBConnection;
import com.docflow.server.db.MDBConnection;
import com.docflow.server.db.SimpleFTPClient;
import com.isomorphic.datasource.DSRequest;
import com.isomorphic.servlet.ISCFileItem;
import com.isomorphic.util.DataTools;

public class ImageStore {

	public static ImageData getImageData(int id, Object hr) throws Exception {
		if (hr != null)
			return MDBConnection.getImageHr(id);
		else
			return MDBConnection.getImage(id, null);
	}

	public Map add(DSRequest record) throws Exception {
		InputStream stream = null;
		Connection connection = null;
		Integer id = null;
		try {
			ImageData newImage = new ImageData();

			ISCFileItem file = record.getUploadedFile("imageInputStream");
			List ls = record.getUploadedFileStreams();
			Map mp = record.getValues();
			System.err.println(" file is null mp=" + mp);
			String realFileName = "";
			if (file != null) {
				System.err.println(" file is not null");
				realFileName = file.getFileName();
				stream = file.getInputStream();
				file.getShortFileName();
				newImage.setContenttype(file.getContentType());
			} else {

				System.err.println(" file is null mp=" + mp);
				stream = (InputStream) mp.get("imageInputStream");
				realFileName = (String) mp.get("imageInputStream_filename");
				if (realFileName == null) {
					realFileName = (String) mp.get("imgname");
				}
				newImage.setContenttype("applicaton/octet-stream");
			}
			System.err.println("realFileName = " + realFileName);
			if (realFileName == null || realFileName.trim().equals("")) {
				throw new Exception(
						"áƒ�áƒ áƒ�áƒ¡áƒ¬áƒ�áƒ áƒ˜ áƒ¡áƒ£áƒ áƒ�áƒ—áƒ˜ !");
			}
			if (realFileName.startsWith("[") && realFileName.endsWith("]")) {
				realFileName = realFileName.substring(1);
				realFileName = realFileName.substring(0,
						realFileName.length() - 1);
			}
			String fakePath = "C:\\fakepath\\";
			if (realFileName.startsWith(fakePath)) {
				realFileName = realFileName.substring(fakePath.length());
			}
			System.err.println("realFileName = " + realFileName);
			int index = realFileName.indexOf(".");
			if (index <= 0) {
				throw new Exception(
						"áƒ�áƒ áƒ�áƒ¡áƒ¬áƒ�áƒ áƒ˜ áƒ¡áƒ£áƒ áƒ�áƒ—áƒ˜ 1!");
			}
			String ext = realFileName.substring(index);
			if (ext == null || ext.trim().equals("")) {
				throw new Exception(
						"áƒ�áƒ áƒ�áƒ¡áƒ¬áƒ�áƒ áƒ˜ áƒ¡áƒ£áƒ áƒ�áƒ—áƒ˜ 2!");
			}

			newImage.setImagename(realFileName);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = stream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();
			byte[] arr = buffer.toByteArray();
			newImage.setImgData(arr);
			connection = DBConnection.getConnection("DocFlow");

			id = MDBConnection.saveImageData(newImage, connection);
			System.err.println("IMAGEEEEEEEEEEEEEEEEEEE IDDD==" + id);
			newImage.setId(id.intValue());
			System.err
					.println("SAVINGGGGG IMAGEEEEEEEEEEEEEEEEEEE FTP    IDDD=="
							+ id);
			saveImageFile((SimpleFTPClient) null, id, arr, connection);
			System.err
					.println("AFTERRRR SAVINGGGGG IMAGEEEEEEEEEEEEEEEEEEE FTP    IDDD=="
							+ id);
			connection.commit();

			System.err
					.println("AFTERRRR COMMITTT SAVINGGGGG IMAGEEEEEEEEEEEEEEEEEEE FTP    IDDD=="
							+ id);
			Map mpd = DataTools.getProperties(newImage);
			return mpd;
		} catch (Exception e) {
			System.err
					.println("ERRORRR AFTERRRR SAVINGGGGG IMAGEEEEEEEEEEEEEEEEEEE FTP    IDDD=="
							+ id);
			e.printStackTrace();
			try {
				if (connection != null)
					connection.rollback();
			} catch (Exception e2) {

			}
			e.printStackTrace();
			throw new Exception(
					"áƒ¨áƒ”áƒªáƒ“áƒ�áƒ›áƒ� áƒ¡áƒ£áƒ áƒ�áƒ—áƒ˜áƒ¡ áƒ�áƒ¢áƒ•áƒ˜áƒ áƒ—áƒ•áƒ˜áƒ¡áƒ�áƒ¡ : "
							+ e.toString());
		} finally {
			try {
				if (connection != null)
					DBConnection.freeConnection(connection);
			} catch (Exception e2) {

			}

			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}

		}
	}

	public static ImageData saveFile(ItemInputStream is, String content_type,
			String file_name) throws Exception {
		String realFileName = file_name;
		if (realFileName == null || realFileName.trim().equals("")) {
			throw new Exception("Invalid file name !");
		}
		if (realFileName.startsWith("[") && realFileName.endsWith("]")) {
			realFileName = realFileName.substring(1);
			realFileName = realFileName.substring(0, realFileName.length() - 1);
		}
		String fakePath = "C:\\fakepath\\";
		if (realFileName.startsWith(fakePath)) {
			realFileName = realFileName.substring(fakePath.length());
		}
		System.err.println("realFileName = " + realFileName);
		int index = realFileName.indexOf(".");
		if (index <= 0) {
			throw new Exception("Invalid file name doesn't have extention!!");
		}
		String ext = realFileName.substring(index);
		if (ext == null || ext.trim().equals("")) {
			throw new Exception("Invalid file name doesn't have extention2!!");
		}
		ImageData newImage = new ImageData();
		newImage.setContenttype(content_type);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		byte[] arr = buffer.toByteArray();

		newImage.setImgData(arr);
		newImage.setImagename(realFileName);
		Connection connection = null;
		try {
			connection = DBConnection.getConnection("DocFlow");
			Integer id = MDBConnection.saveImageData(newImage, connection);
			newImage.setId(id);
			saveImageFile((SimpleFTPClient) null, id, arr, connection);
			System.err
					.println("AFTERRRR SAVINGGGGG IMAGEEEEEEEEEEEEEEEEEEE FTP    IDDD=="
							+ id);
			connection.commit();
			System.err.println("IMAGEEEEEEEEEEEEEEEEEEE IDDD==" + id);
			return newImage;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				connection.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}

	public static void saveImageFile(SimpleFTPClient client, Integer id,
			byte arr[], Connection connection) throws Exception {
		InputStream input = null;
		SimpleFTPClient ftp = null;
		try {
			ftp = client == null ? MDBConnection.getImageFtp(connection)
					: client;

			String fileName = "F" + id.toString();

			input = new ByteArrayInputStream(arr);

			ftp.uploadStream(fileName, input);
		} finally {

			try {
				if (input != null)
					input.close();
			} catch (Exception e2) {

			}

		}
	}

	public static void saveImageFile(String dirname, Integer id, byte arr[],
			Connection connection) throws Exception {
		FileOutputStream fos = null;
		try {
			dirname = dirname == null ? MDBConnection
					.getImageStoreFolder(connection) : dirname;

			String fileName = "F" + id.toString();
			fos = new FileOutputStream(new File(dirname, fileName));
			fos.write(arr);
			fos.flush();
		} finally {

			try {
				if (fos != null)
					fos.close();
			} catch (Exception e2) {

			}

		}
	}

	public static void main(String[] args) throws Exception {

		// Connection connection = DBConnection.getConnection("DocFlow");
		// Statement stmt = connection.createStatement();
		// ResultSet res = stmt.executeQuery("select id from tblimages");
		// String dirname = MDBConnection.getImageStoreFolderTmp(connection);
		// while (res.next()) {
		// ImageData newImage = MDBConnection.getImage(res.getInt(1),
		// connection);
		// saveImageFile(dirname, newImage.getId(), newImage.getImgData(),
		// connection);
		//
		// }
		// DBConnection.freeConnection(connection);

		String fakePath = "C:\\fakepath\\";
		String realFileName = "[" + fakePath + "sdasdasds]";
		System.err.println("realFileName = " + realFileName);
		if (realFileName.startsWith("[") && realFileName.endsWith("]")) {
			realFileName = realFileName.substring(1);
			realFileName = realFileName.substring(0, realFileName.length() - 1);
		}
		if (realFileName.startsWith(fakePath)) {
			realFileName = realFileName.substring(fakePath.length());
		}
		System.err.println("realFileName = " + realFileName);

	}

	// public static void main(String[] args) throws Exception {
	//
	// Connection connection = DBConnection.getConnection("DocFlow");
	// Statement stmt = connection.createStatement();
	// ResultSet res = stmt.executeQuery("select id from tblimages");
	// FTPClient client = MDBConnection.getImageFtp(connection);
	// while (res.next()) {
	// ImageData newImage = MDBConnection.getImage(res.getInt(1),
	// connection);
	// saveImageFile(client, newImage.getId(), newImage.getImgData(),
	// connection);
	//
	// }
	// client.disconnect();
	// connection.close();
	//
	// }
}

package src.com.socargass.tabletexporter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import nanoxml.XMLElement;

public class Test {
	public static void main(String[] args) throws Exception {

		try {

			// this is the file to be downloaded
			String main_url = "http://homepc.homelinux.org:55557/SocarExportWP/";

			String urlStr = main_url + "index.jsp?subregion=24";
			urlStr = main_url + "getclassifier.jsp?type=region";
			byte[] bt = downlodad(urlStr);
			String str = new String(bt, "UTF8");
			str = str.trim();
			XMLElement xml = new XMLElement();
			xml.parseString(str);
			Vector<XMLElement> vec = xml.getChildren();
			for (XMLElement xmlElement : vec) {
				Object value = xmlElement.getAttribute("value");
				System.out.println(xmlElement.getAttribute("id") + "="
						+ value);
			}
			System.out.println(str);
			// catch some possible errors...
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static byte[] downlodad(String urlStr)
			throws MalformedURLException, IOException, ProtocolException,
			FileNotFoundException {
		URL url = new URL(urlStr);

		// create the new connection
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();

		// set up some things on the connection
		urlConnection.setRequestMethod("GET");
		urlConnection.setDoOutput(true);

		// and connect!
		urlConnection.connect();

		// set the path where we want to save the file
		// in this case, going to save it on the root directory of the
		// sd card.

		// create a new file, specifying the path, and the filename
		// which we want to save the file as.

		// this will be used to write the downloaded data into the file we
		// created
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String hf = urlConnection.getHeaderField("Content-Disposition");

		// this will be used in reading the data from the internet
		InputStream inputStream = urlConnection.getInputStream();

		// this is the total size of the file
		int totalSize = urlConnection.getContentLength();
		// variable to store total downloaded bytes
		int downloadedSize = 0;

		// create a buffer...
		byte[] buffer = new byte[1024];
		int bufferLength = 0; // used to store a temporary size of the
								// buffer

		// now, read through the input buffer and write the contents to the
		// file
		while ((bufferLength = inputStream.read(buffer)) > 0) {
			// add the data in the buffer to the file in the file output
			// stream (the file on the sd card
			bos.write(buffer, 0, bufferLength);
			// add up the size so we know how much is downloaded
			downloadedSize += bufferLength;
			// this is where you would do something to report the prgress,
			// like this maybe

		}
		// close the output stream when done
		bos.flush();
		bos.close();
		return bos.toByteArray();
	}
}

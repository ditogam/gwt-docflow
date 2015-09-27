package com.socar.map;

import java.awt.BorderLayout;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.socar.map.exporter.SqlIteFileExporterNew;

public class Viewer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3410447206667332960L;

	public Viewer(String connectionString, String user, String password)
			throws Exception {

		final ImagePanel panel = new ImagePanel();

		Class.forName("org.postgresql.Driver");
		Connection conn = DriverManager.getConnection(connectionString, user,
				password);

		// Class.forName("org.sqlite.JDBC");
		// Properties prop = new Properties();
		// Connection conn = DriverManager.getConnection("jdbc:sqlite:"
		// + "TilesDB_24.sqlite", prop);

		ArrayList<ImageData> datas = new ArrayList<ImageData>();
		String sql = "select zoom||'/'||x||'/'||y zxy, file_data from maps.rcntbl inner join "
				+ "maps.mapfiledatazxy k on rcn=rcnid where newid is not null and k.created order by length(file_data) desc limit 10000 ";
		sql = "select zoom||'/'||x||'/'||y zxy, file_data from mapfiledatazxy order by length(file_data) desc limit 10000";
		sql = "select zoom||'/'||x||'/'||y zxy, file_data from mapfiledatazxy order by zoom,x,y limit 100000";
		sql = "select zoom||'/'||x||'/'||y zxy, file_data from mapfiledatazxy order by length(file_data) desc limit 10000";
		sql = "select zoom||'/'||x||'/'||y zxy, img_data from maps.zoom_xy where rcn_id=3  order by length(img_data) desc limit 10000";
		sql = "select zoom||'/'||x||'/'||y zxy, img_data from maps.zoom_xy where  zoom<14 order by length(img_data) desc limit 10000";
		// sql="select zoom||'/'||x||'/'||y zxy, file_data from maps.mapfiledatazxy where zoom=16 and rcn is   null  and  created and fromfile order by zoom,x,y limit 1000";
		ResultSet rs = conn.createStatement().executeQuery(sql);
		while (rs.next()) {
			ImageData im = new ImageData();
			im.setZxy(rs.getString("zxy"));
			byte[] data = rs.getBytes("img_data");
			int originalLength = data.length;
			data = SqlIteFileExporterNew.compress(data);
			int compressedLength = data.length;
			data = SqlIteFileExporterNew.decompress(data);
			int decompressedLength = data.length;
			im.setData(data);
			datas.add(im);
		}
		conn.close();
		this.setSize(750, 750);
		this.setLocation(300, 200);
		final JList list = new JList(datas.toArray());
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!list.getValueIsAdjusting()) {
					ImageData s = (ImageData) list.getSelectedValue();
					System.out.println("Selected " + s);

					try {
						panel.setImageData(s);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});
		this.getContentPane().add(BorderLayout.WEST, new JScrollPane(list));
		this.getContentPane().add(BorderLayout.CENTER, panel);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream("props.properties"));
		new Viewer(props.getProperty("connection"), props.getProperty("user"),
				props.getProperty("pwd"));
	}
}

package com.socar.map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4624683874891856170L;
	private Image image;

	public ImagePanel() {
		// TODO Auto-generated constructor stub
	}

	public void setImageData(ImageData data) throws Exception {
		image = data.saveToFile();
		repaint();

	}

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		if (image == null)
			return;
		Graphics2D g2d = (Graphics2D) g;
		int x = (this.getWidth() - image.getWidth(null)) / 2;
		int y = (this.getHeight() - image.getHeight(null)) / 2;
		g2d.drawImage(image, x, y, null);
	}

}

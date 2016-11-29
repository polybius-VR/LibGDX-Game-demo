package com.project.editor;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.sql.*;

@SuppressWarnings("serial")
public class HelloWorld extends JFrame{
	private JButton jbtOK = new JButton("OK");
	private JButton jbtDialogOK = new JButton("OK");
	JDialog dialog = new JDialog();
	JLabel label = new JLabel("Please wait...");

	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = new HelloWorld();
				frame.setLocationRelativeTo(null);
				frame.setSize(500, 500);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("Control Circle");
				frame.setVisible(true);
				frame.pack();
			}
		});
	}

	public void imageDb(File imgfile){
		System.out.println("Insert Image Example!");
		String driverName = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "testdb";
		String userName = "root";
		String password = "root";
		Connection con = null;
		try{
		   Class.forName(driverName);
		   con = DriverManager.getConnection(url+dbName,userName,password);
		   //Statement st = con.createStatement();
		   //File imgfile = new File("pic.jpg");
		  
		  FileInputStream fin = new FileInputStream(imgfile);
		 
		   PreparedStatement pre =
		   con.prepareStatement("insert into image values(?,?,?)");
		 
		   pre.setString(1,"test");
		   pre.setInt(2,3);
		   pre.setBinaryStream(3,(InputStream)fin,(int)imgfile.length());
		   pre.executeUpdate();
		   System.out.println("Successfully inserted the file into the database!");

		   pre.close();
		   con.close(); 
		}catch (Exception e1){
			System.out.println(e1.getMessage());
		}
	}
	
	public void retrieveImage(){
		System.out.println("Retrive Image Example!");
		String driverName = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "testdb";
		String userName = "root";
		String password = "root";
		Connection con = null;
		try{
			Class.forName(driverName);
			con = DriverManager.getConnection(url+dbName,userName,password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select image from image");
			int i = 0;
			while (rs.next()) {
				InputStream in = rs.getBinaryStream(1);
				OutputStream f = new FileOutputStream(new File("test"+i+".png"));
				i++;
				int c = 0;
				while ((c = in.read()) > -1) {
					f.write(c);
				}
				f.close();
				in.close();
			}
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}

	public HelloWorld(){
		JPanel panel = new JPanel(); // Use the panel to group buttons
		panel.add(jbtOK);

		//this.add(canvas, BorderLayout.CENTER); // Add canvas to center
		this.add(panel, BorderLayout.SOUTH); // Add buttons to the frame 

		dialog.setLocationRelativeTo(null);
		dialog.setTitle("Please Wait...");
		dialog.add(label);
		dialog.add(jbtDialogOK);
		dialog.pack();

		OkListener listener1 = new OkListener();
		jbtOK.addActionListener(listener1);

		OkListener2 listener2 = new OkListener2();
		jbtDialogOK.addActionListener(listener2);
	}

	private void drawFile(){
		BufferedImage img = new BufferedImage(240, 160, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < img.getWidth(); i++){
			for (int j = 0; j < img.getHeight(); j++){
				if ((j > 8 && j < 11) || (j == 0 || j == img.getHeight() - 1) || i == 0 || i == img.getWidth() - 1)					
					img.setRGB(i, j, 0xffffff);
			}
		}

		img.setRGB(8, 8, 0xff0000);

		//File outputFile = new File(".." + File.separator + "desktop" + File.separator + "assets" + File.separator + "levels.png");
		File outputFile = new File("levels.png");
		try {
			ImageIO.write(img, "png", outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		imageDb(outputFile);
		//retrieveImage();
	}

	class OkListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			drawFile();
			dialog.setVisible(true); // show the dialog on the screen
			// Do something here
			//dialog.setVisible(false); // set visibility to false when the code has run
		}
	}

	class OkListener2 implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//dialog.setVisible(true); // show the dialog on the screen

			// Do something here
			dialog.setVisible(false); // set visibility to false when the code has run
		}

	}
}

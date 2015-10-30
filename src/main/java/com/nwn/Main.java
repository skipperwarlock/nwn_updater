package com.nwn;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	public static void updaterThreadExample(){
		Path serverFileJson = Paths.get("test.json");
		Path nwnDir = Paths.get("C:\\NeverwinterNights\\NWN");
		NwnUpdater nwnUpdater = new NwnUpdater(nwnDir, serverFileJson);
		Thread updateThread = new Thread(nwnUpdater, "Update Thread");
		System.out.println(updateThread.getState());
		System.out.println(updateThread.isAlive());
		updateThread.start();
		System.out.println(updateThread.getState());
		System.out.println(updateThread.isAlive());
		nwnUpdater.terminate();
		System.out.println(updateThread.getState());
		System.out.println(updateThread.isAlive());
	}

	private static void launchHomeView(){
		//apply nimbus theme
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

		//initialize gui
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new NwnUpdaterHomeView().setVisible(true);
			}
		});
	}
   
	private static void regexTesting(){
		String testString = "[ServerName:coolserver.net,FileUrl:rawr.com],[ServerName:rawr2,FileUrl:ftp://www.dropbox.com/rawr/rawrFile-052.txt]";
		String pattern = "(ServerName\\:?[a-zA-Z0-9_\\./\\-\\:]+)(\\,)(FileUrl\\:?[a-zA-Z0-9\\-_\\./\\:]+)";
		Pattern testPattern = Pattern.compile(pattern);
		Matcher m = testPattern.matcher(testString);
		while(m.find()){
			System.out.println(m.group(0));	
		}
	}

	public static void main(String[] args) throws Exception{
		updaterThreadExample();
//		nwnUpdaterConfig.getInstance().load("NwnUpdater.cfg");
//		launchHomeView();
	}

}

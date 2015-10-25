package com.nwn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	
	private static Path nwnDir;
	private static ArrayList<ServerInfo> serverList = new ArrayList<>();
	
	public static void updaterThreadExample(){
		Path serverFileJson = Paths.get("test.json");
		Path nwnDir = Paths.get("C:\\NeverwinterNights\\NWN");
		NwnUpdater nwnUpdater = new NwnUpdater(nwnDir, serverFileJson);
		Thread updateThread = new Thread(nwnUpdater, "Update Thread");
		updateThread.start();
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
			public void run() {
				new NwnUpdaterHomeView(nwnDir, serverList).setVisible(true);
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

	private static void generateConfig(){
		OutputStream output = null;
		try{
			output = new FileOutputStream(cfg);
			Properties prop = new Properties();
			prop.setProperty("nwnDir", "C:\\NeverwinterNights\\NWN");
			prop.setProperty("serverList","");
			prop.store(output,null);
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			if(output != null){
				try{
					output.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
		}
	}

	private static void loadConfig(String cfg){
		File cfgFile = new File(cfg);
		if(!cfgFile.exists()){
			generateConfig();
		}
		Properties prop = new Properties();
		InputStream is = null;
		try{
			is = new FileInputStream(cfg);
			prop.load(is);
			nwnDir = Paths.get(prop.getProperty("nwnDir"));
			parseServerList(prop.getProperty("serverList"));
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			if(is != null){
				try{
					is.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}

	private static void parseServerList(String serverListProperty){
		String serverName;
		String txtServerUrl;
		URL serverUrl;
		String pattern = "(ServerName\\:?[a-zA-Z0-9_\\./\\-\\:]+)(\\,)(FileUrl\\:?[a-zA-Z0-9\\-_\\./\\:]+)";
		Pattern testPattern = Pattern.compile(pattern);
		Matcher m = testPattern.matcher(serverListProperty);
		while(m.find()){
			serverName   = m.group(1);
			serverName   = serverName.substring(serverName.indexOf(':'));
			txtServerUrl = m.group(3);
			txtServerUrl = txtServerUrl.substring(txtServerUrl.indexOf(':'));
			System.out.println("server name:" + serverName + " server url:" + txtServerUrl);
			try{
				serverUrl = new URL(txtServerUrl);
				serverList.add(new ServerInfo(serverName,serverUrl));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
    
    public static void main(String[] args) {
	    loadConfig("NwnUpdater.cfg");
	    launchHomeView();
    }

}

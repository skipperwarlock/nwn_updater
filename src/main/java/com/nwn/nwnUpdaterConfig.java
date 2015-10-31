/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author Sam
 */
public class nwnUpdaterConfig {
	private Path	              nwnDir;
	private ArrayList<ServerInfo> serverList;
	private String		      fileName;

	private static nwnUpdaterConfig instance = null;
	
	protected nwnUpdaterConfig(){}
	
	public static nwnUpdaterConfig getInstance(){
		if(instance == null){
			instance = new nwnUpdaterConfig();
		}
		return instance;
	}

	/**
	 * @return the nwnDir
	 */
	public Path getNwnDir() {
		return nwnDir;
	}

	/**
	 * @param nwnDir the nwnDir to set
	 */
	public void setNwnDir(Path nwnDir) {
		this.nwnDir = nwnDir;
	}

	/**
	 * @param nwnDir the nwnDir to set
	 */
	public void setNwnDir(String nwnDir) {
		this.nwnDir = Paths.get(nwnDir);
	}

	/**
	 * @return the serverList
	 */
	public ArrayList<ServerInfo> getServerList() {
		return serverList;
	}

	/**
	 * @param serverList the serverList to set
	 */
	public void setServerList(ArrayList<ServerInfo> serverList) {
		this.serverList = new ArrayList<>(serverList);
	}

	/**
	 * stub out properties in new config file
	 * @param cfg name of config file
	 */
	private void generateConfig(String cfg){
		OutputStream output = null;
		try{
			output = new FileOutputStream(cfg);
			Properties prop = new Properties();
			prop.setProperty("nwnDir", nwnDir.toString());
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

	/**
	 * load provided config file to this object
	 * If it does not exist, generate a basic config file with provided name
	 * @param cfg name of config file
	 */
	public void load(String cfg){
		nwnDir       = null;
		serverList   = new ArrayList<>();
		fileName     = cfg;
		File cfgFile = new File(cfg);

		if(!cfgFile.exists()){
			generateConfig(cfg);
		}
		Properties prop = new Properties();
		InputStream is  = null;
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

	/**
	 * write changes to current config file
	 */
	public void save(){
		String serverListPropertyString = "";
		for(ServerInfo serverInfo:serverList){
			serverListPropertyString += serverInfo.getPropertyString();
		}
		OutputStream output = null;
		try{
			output          = new FileOutputStream(this.fileName);
			Properties prop = new Properties();
			prop.setProperty("nwnDir", nwnDir.toString());
			prop.setProperty("serverList", serverListPropertyString);
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
	
	/**
	 * write a new config file with current properties
	 * current config stays active on this object
	 * @param cfg name of new config file 
	 */
	public void saveAs(String cfg){
		String serverListPropertyString = "";
		for(ServerInfo serverInfo:serverList){
			serverListPropertyString += serverInfo.getPropertyString();
		}
		OutputStream output = null;
		try{
			output          = new FileOutputStream(cfg);
			Properties prop = new Properties();
			prop.setProperty("nwnDir", nwnDir.toString());
			prop.setProperty("serverList", serverListPropertyString);
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
	
	/**
	 * parse the serverList property of the config file for server name and url
	 * @param serverListProperty 
	 */
	private void parseServerList(String serverListProperty){
		String serverName;
		String txtServerUrl;
		URL serverUrl;
		String pattern = "(ServerName\\:?[a-zA-Z0-9_\\./\\-\\:]+)(\\,)(FileUrl\\:?[a-zA-Z0-9\\-_\\./\\:]+)";
		Pattern testPattern = Pattern.compile(pattern);
		Matcher m = testPattern.matcher(serverListProperty);
		while(m.find()){
			serverName   = m.group(1);
			serverName   = serverName.substring(serverName.indexOf(':')+1);
			txtServerUrl = m.group(3);
			txtServerUrl = txtServerUrl.substring(txtServerUrl.indexOf(':')+1);
//			System.out.println("server name:" + serverName + " server url:" + txtServerUrl);
			try{
				serverUrl = new URL(txtServerUrl);
				serverList.add(new ServerInfo(serverName,serverUrl));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
    
}

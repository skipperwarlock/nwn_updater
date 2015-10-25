/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nwn;

import java.net.URL;

/**
 *
 * @author Sam
 */
public class ServerInfo {
	private String serverName;
	private URL fileUrl;

	public ServerInfo(String serverName, URL fileUrl){
		this.serverName = serverName;
		this.fileUrl = fileUrl;
	}
	
	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param serverName the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * @return the fileUrl
	 */
	public URL getFileUrl() {
		return fileUrl;
	}

	/**
	 * @param fileUrl the fileUrl to set
	 */
	public void setFileUrl(URL fileUrl) {
		this.fileUrl = fileUrl;
	}

	
}
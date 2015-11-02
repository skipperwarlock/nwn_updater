/*
This file is part of NWN Server Updater.

    NWN Server Updater is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    NWN Server Updater is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NWN Server Updater.  If not, see <http://www.gnu.org/licenses/>.
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

	@Override
	public String toString(){
		return serverName;
	}

	/**
	 * @return string used for config file 
	 */
	public String getPropertyString(){
		return "[ServerName:"+serverName+",FileUrl:"+fileUrl.toString()+"]";
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

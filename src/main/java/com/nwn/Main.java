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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
//	public static void updaterthreadexample(){
//		path serverfilejson = paths.get("test.json");
//		path nwndir = paths.get("c:\\neverwinternights\\nwn");
//		nwnupdater nwnupdater = new nwnupdater(nwndir, serverfilejson);
//		thread updatethread = new thread(nwnupdater, "update thread");
//		updatethread.start();
//		updatethread.interrupt();
//	}

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
//		String pattern = "(ServerName\\:?[a-zA-Z0-9_\\./\\-\\:]+)(\\,)(FileUrl\\:?[a-zA-Z0-9\\-_\\./\\:]+)";
		String pattern = "(?<=\\[)(ServerName\\:?.*?)\\,(FileUrl\\:?.*?)(?=\\])";
		Pattern testPattern = Pattern.compile(pattern);
		Matcher m = testPattern.matcher(testString);
		while(m.find()){
			System.out.println(m.group(0));	
			System.out.println(m.group(1));
			System.out.println(m.group(2));
		}
	}

	public static void main(String[] args) throws Exception{
//		regexTesting();
		nwnUpdaterConfig.getInstance().load("NwnUpdater.cfg");
		launchHomeView();
	}

}
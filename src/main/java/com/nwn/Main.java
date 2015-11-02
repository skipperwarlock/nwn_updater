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

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

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
		String license = "This is free and unencumbered software released into the public domain.\n" +
			"\n" +
			"Anyone is free to copy, modify, publish, use, compile, sell, or\n" +
			"distribute this software, either in source code form or as a compiled\n" +
			"binary, for any purpose, commercial or non-commercial, and by any\n" +
			"means.\n" +
			"\n" +
			"In jurisdictions that recognize copyright laws, the author or authors\n" +
			"of this software dedicate any and all copyright interest in the\n" +
			"software to the public domain. We make this dedication for the benefit\n" +
			"of the public at large and to the detriment of our heirs and\n" +
			"successors. We intend this dedication to be an overt act of\n" +
			"relinquishment in perpetuity of all present and future rights to this\n" +
			"software under copyright law.\n" +
			"\n" +
			"THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND,\n" +
			"EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF\n" +
			"MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.\n" +
			"IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR\n" +
			"OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,\n" +
			"ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR\n" +
			"OTHER DEALINGS IN THE SOFTWARE.";
		File cfg = new File("NwnUpdater.cfg");
		if(!cfg.exists()){
			JOptionPane.showMessageDialog(null, license);
		}
		nwnUpdaterConfig.getInstance().load("NwnUpdater.cfg");
		launchHomeView();
	}

}
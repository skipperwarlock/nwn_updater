/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nwn;

/**
 *
 * @author Sam
 */
public class NwnUpdaterGuiController {
	private NwnUpdaterHomeView activeGui;
	
	private NwnUpdaterGuiController() {}
	
	public static NwnUpdaterGuiController getInstance() {
		return NwnUpdaterGuiControllerHolder.INSTANCE;
	}
	
	private static class NwnUpdaterGuiControllerHolder {
		private static final NwnUpdaterGuiController INSTANCE = new NwnUpdaterGuiController();
	}

	public void setGui(NwnUpdaterHomeView gui){
		activeGui = gui;
	}

	public void setGuiUpdateBtn(String text){
		activeGui.setUpdateBtnText(text);
	}
}

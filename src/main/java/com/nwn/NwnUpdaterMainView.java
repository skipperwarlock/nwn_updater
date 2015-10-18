package com.nwn;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Sam on 10/17/2015.
 */
public class NwnUpdaterMainView extends JFrame{
    private JPanel rootPanel;
    private JButton btnCancel;
    private JButton btnRun;
    private JProgressBar progressBar1;
    private JTextField textField1;
    private JTextField textField2;
    private JButton btnSelectNwn;
    private JButton btnSelectServerFile;
    private JTextArea usrOutputTxt;
    private JScrollPane usrOutputScroll;
    private JProgressBar progressBar2;
    Thread updateThread;

    public NwnUpdaterMainView() {
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        btnRun.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(btnRun.isEnabled()) {
                    progressBar1.setValue(0);
                    super.mousePressed(e);
                    Path serverFileJson = Paths.get("test.json");
                    Path nwnDir = Paths.get("C:\\NeverwinterNights\\NWN");
                    NwnUpdater nwnUpdater = new NwnUpdater(nwnDir, serverFileJson);
                    updateThread = new Thread(nwnUpdater, "Update Thread");
                    updateThread.start();
                    btnRun.setEnabled(false);
                }
            }
        });
        btnCancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(updateThread != null) {
                    updateThread.interrupt();
                }
                //todo: this will be removed with the ability to update multiple servers at once
                btnRun.setEnabled(true);
            }
        });
        setVisible(true);
    }
}

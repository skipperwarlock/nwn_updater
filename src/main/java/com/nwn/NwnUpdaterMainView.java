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
    private JButton btn1;
    private JPanel rootPanel;

    public NwnUpdaterMainView() {
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        btn1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                Path serverFileJson = Paths.get("test.json");
                Path nwnDir = Paths.get("C:\\NeverwinterNights\\NWN");
                NwnUpdater nwnUpdater = new NwnUpdater(nwnDir, serverFileJson);
                Thread updateThread = new Thread(nwnUpdater, "Update Thread");
                updateThread.start();
            }
        });
        setVisible(true);
    }
}

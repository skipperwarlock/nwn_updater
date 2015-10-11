package com.nwn;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by Sam on 10/10/2015.
 */
public class NwnUpdater implements Runnable{
    private ArrayList<ServerFile> serverFileList;
    private Path nwnRootPath;

    public NwnUpdater() {
    }

    @Override
    public void run() {
        //by this point, serverFileList should already be populated
        //we need to:
        //parse the necessary directories to determine if all files are present
        //download files not present
        //move files to correct directories
        //delete any tmp data
    }

    public ArrayList<ServerFile> getServerFileList(){
        return serverFileList;
    }

    public Path getNwnRootPath(){
        return nwnRootPath;
    }

    public void setServerFileList(ArrayList<ServerFile> newServerFileList){
        serverFileList = newServerFileList;
    }

    public void setNwnRootPath(Path newNwnRootPath){
        nwnRootPath = newNwnRootPath;
    }
}

package com.nwn;

/**
 * Created by Sam on 10/16/2015.
 */
public enum FolderByExt {
    BMU("music"),
    WAV("ambient"),
    HAK("hak"),
    TLK("tlk"),
    COMPRESSED("compressed_tmp");

    private String value;

    FolderByExt(String value){
        this.value = value;
    }

    public String toString(){
        return this.value;
    }
}

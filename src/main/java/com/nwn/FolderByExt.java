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

/**
 * Keep track of which folders are associated with which file extensions
 * note: getFolderByExtension checks statically for extension names. If you add here, add there also
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

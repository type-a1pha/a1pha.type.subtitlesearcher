/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package srtmanager;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 *
 * @author IKAROS
 */
public class SrtFileManager {
    
    private String filename;
    private ArrayList<Subtitle> structure;
    
    public SrtFileManager(String filename){
        this.filename = filename;
        this.structure = null;
    }
    
    public ArrayList<Subtitle> getStructure(){
        if(structure == null){
            File file = new File(filename);
            try{
                DataInputStream istream = new DataInputStream(new FileInputStream(file));
                byte[] raw_bytes = new byte[(int)file.length()];
                istream.read(raw_bytes,0,(int)file.length());
                istream.close();
                String s = new String(raw_bytes);
                structure = SrtParser.parse(s);
            }
            catch(Exception e){
                System.out.println("ERRORE APERTURA FILE "+filename+": "+e.getMessage());
            }
        }
        return structure;          
    }
    
    public static void main(String[] args){
        System.out.println((new SrtFileManager("prova.srt").getStructure()).size());
    }
    
}

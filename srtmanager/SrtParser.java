/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package srtmanager;

import java.util.ArrayList;

/**
 *
 * @author IKAROS
 */
public class SrtParser {

    private static final boolean debug_mode = false;
    private static int err_count = 0;
    private static int parse_count = 0;
    
    public final static String new_line = "\r\n";
    
    public static SubTime parseSubTime(String str) throws NumberFormatException {
        String[] blocks;
        blocks = str.split("[,:]");
        if(blocks.length!=4){
            throw new NumberFormatException("Time format exception in SrtParser:\n"+str);
        }
        return new SubTime(new Integer(blocks[0]),new Integer(blocks[1]),new Integer(blocks[2]),new Integer(blocks[3]));
    }
    
    public static Subtitle parseSubtitle(String str) throws SubFormatException {
        parse_count++;
        String[] blocks, timeBlocks;
        blocks = str.split(new_line,2);
        if(blocks.length!=2){
            throw new SubFormatException("Subtitle format exception in SrtParser (0):\n"+str);
        }
        blocks[0] = blocks[0].replace(" --> ",new_line);
        timeBlocks = blocks[0].split(new_line);
        if(timeBlocks.length!=2){
            throw new SubFormatException("Subtitle format exception in SrtParser (1):\n"+str);
        }
        try{
            return new Subtitle(parseSubTime(timeBlocks[0]),parseSubTime(timeBlocks[1]),blocks[1]);
        }
        catch(NumberFormatException e){
            throw new SubFormatException("Subtitle format exception in SrtParser (2):\n"+str);
        }
    }
    
    public static ArrayList<Subtitle> parse(String content){
        String[] blocks, aux;
        blocks = content.split(new_line+new_line,0); //0 means discarding empty strings
        ArrayList<Subtitle> result =  new ArrayList<>(blocks.length);
        for(int i=0; i<blocks.length; i++){
            if( ! blocks[i].equals("") ){
                //I'm expecting the String to begin with a pattern of the form number\n
                //In any case even if it is missing, it will be accepted
                aux = blocks[i].split(new_line,2);
                try{
                    try{
                        Integer.parseInt(aux[0].replaceAll("﻿", ""));
                        result.add( parseSubtitle(aux[1]) );
                    }
                    catch(NumberFormatException e){
                        result.add( parseSubtitle(blocks[i]) );
                    }
                }
                catch(SubFormatException e){
                    err_count++;
                    if(debug_mode) System.out.println("ERRORE ("+i+"-th element): "+e.getMessage());
                }
            }
        }
        return result;
    }
    
    public static int getErrorCount(){
        return err_count;
    }
    
    public static int getParseCount(){
        return parse_count;
    }
    
    public static void main(String[] args) throws SubFormatException {
        String temp = "1\r\n00:00:06,900 --> 00:00:09,100\r\nHowever, if it's observed\r\nafter it's left the planen\r\n\r\n00:00:06,900 --> 00:00:09,100\r\nHowever, if it's observed\nafter it's left the plane";
        //SrtParser.parseSubtitle(temp);
        //System.out.println(SrtParser.parseSubtitle(temp).toString());
        //System.out.println(result.get(0));
        ArrayList<Subtitle> result = SrtParser.parse(temp);
        //System.out.println(new String("ciao").equalsIgnoreCase("Ciao"));
        System.out.println("end");
    }
    
}

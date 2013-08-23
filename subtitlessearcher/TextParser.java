/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitlessearcher;

import java.util.ArrayList;
import srtmanager.*;

/**
 *
 * @author IKAROS
 */
public class TextParser {
    
    public static String signs = "!@\"#$%^&*():;.,{}?.-";
    public static String[] tags = {SrtParser.new_line,"<b>","</b>","<i>","</i>"};
    
    public static String cookedText(String text){
        String result;
        result = new String(text);
        result = result.replaceAll("["+signs+"]"," ");
        for(int i=0; i<tags.length; i++){
            result = result.replace(tags[i]," ");
        }
        return result.toLowerCase();
    }
    
    public static ArrayList<String> splitText(String text){
        ArrayList<String> result = new ArrayList<>();
        String[] temp;
        temp = text.split(" ",0);
        for(int i=0; i<temp.length; i++){
            if( ! temp[i].equals("") && ! temp[i].equals(" ") ){
                result.add(temp[i].trim());
            }
        }
        return result;
    }
    
    public static void main(String args[]){
        /*String temp = "-What's the bright side?-Only nine more months to Comic-Con.";
        System.out.println(cookedText(temp));
        ArrayList<String> a = splitText(cookedText(temp));
        System.out.println(a.size());
        for(int i=0; i<a.size(); i++){
            System.out.println(a.get(i));
        }*/
        ArrayList<Subtitle> subs = new SrtFileManager("prova.srt").getStructure();
        for(int i=0; i<subs.size(); i++){
            System.out.println(i+"-> "+cookedText(subs.get(i).getText()));
        }
    }
    
}

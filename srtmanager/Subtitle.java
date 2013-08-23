/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package srtmanager;

/**
 *
 * @author IKAROS
 */
public class Subtitle {
    
    private SubTime start, end;
    private String text;
    
    public Subtitle(SubTime start, SubTime end, String text){
        this.start = start;
        this.end = end;
        this.text = text;
    }
    
    public SubTime getStart(){
        return this.start;
    }
    
    public SubTime getEnd(){
        return this.end;
    }
    
    public String getText(){
        return this.text;
    }
    
    public boolean equals(Subtitle obj){
        return this.start.equals(obj.start) &&
                this.end.equals(obj.end) && this.text.equals(obj.text);
    }
    
    @Override
    public String toString(){
        return this.start.toString()+" --> "+this.end.toString()+"\n"+
                this.text;
    }
    
}

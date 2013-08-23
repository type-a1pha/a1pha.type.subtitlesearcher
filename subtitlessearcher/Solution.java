/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitlessearcher;

import java.util.ArrayList;
import java.util.Iterator;
import srtmanager.*;

/**
 *
 * Class holding the data structures describing the solution found for
 * a given file
 * 
 * @author IKAROS
 */
public class Solution {
    //INPUT
    public String pattern; 
    public ArrayList<Subtitle> subtitles;
    //AUXILIARY
    public ArrayList<String> source;
    public ArrayList<String> target;
    public ArrayList<Subtitle> references;
    //RESULT
    public int[] cost;
    public int[] match_len;
    public int[] match_pos;
    //POST-PROCESSED RESULT
    private ArrayList<Pair<String,Pair<SubTime,SubTime>>> result;
    private ArrayList<String> additional_result;
    
    public Solution(String pattern, ArrayList<Subtitle> subtitles,
            ArrayList<String> source, ArrayList<String> target, ArrayList<Subtitle> references,
            int[] cost, int[] match_len, int[] match_pos){
        this.pattern = pattern;
        this.subtitles = subtitles;
        this.source = source;
        this.target = target;
        this.references = references;
        this.cost = cost;
        this.match_len = match_len;
        this.match_pos = match_pos;
    }
    
    public boolean isEmpty(){
        if(result == null){//!!!
            this.createResult();
        }
        Iterator<Pair<String,Pair<SubTime,SubTime>>> it = result.iterator();
        while(it.hasNext()){
            Pair<SubTime,SubTime> interval = it.next().second();
            if( ! interval.first().equals(SubTime.nullTime()) || ! interval.second().equals(SubTime.nullTime())){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Create an explicit result from the data collected by the algorithm
     */
    private void createResult(){
        if(result != null){ return; }
        int dim = cost.length>0 ? cost[0] : 0;
        result = new ArrayList<>(dim);
        
        additional_result = new ArrayList<>(dim);
        
        int pos = 0;
        while(pos < source.size()){
                if(match_len[pos] > 0){ //if a matching positione exists
                    String key = source.get(pos);
                    for(int i=1; i<match_len[pos]; i++){
                        key = key + " " + source.get(pos+i);
                    }
                    SubTime start = references.get(match_pos[pos]).getStart();
                    SubTime end = references.get(match_pos[pos]+match_len[pos]-1).getEnd();
                    result.add(new Pair(key, new Pair(start, end)));
                    
                    String additional_info = "";
                    Subtitle current_sub = null;
                    for(int i=match_pos[pos]; i<match_pos[pos]+match_len[pos]; i++){
                        if(current_sub == null || !current_sub.equals(references.get(i))){
                            current_sub = references.get(i);
                            additional_info = additional_info + current_sub.getText().replace(SrtParser.new_line," | ") + " ";
                        }
                    }
                    additional_result.add(additional_info);
                    
                    pos = pos + match_len[pos];
                }
                else{ //otherwise the world has no match
                    result.add(new Pair(source.get(pos), new Pair(SubTime.nullTime(), SubTime.nullTime())));
                    pos++;
                }
        }
        //return result;
    }
    
    public int getSolutionCost(){
        return cost.length>0 ? cost[0] : Integer.MAX_VALUE;
    }
    
    @Override
    public String toString(){
        if(result == null){
            this.createResult();
        }
        String temp = "";
        for(int i=0; i<result.size(); i++){
            temp = temp + "[ " + result.get(i).second().first() + " , " + 
                    result.get(i).second().second() + " ] : " + 
                    result.get(i).first() + "\n";
        }
        return temp;
    }
    
    public String toExtendedString(){
        if(result == null){
            this.createResult();
        }
        String temp = "";
        for(int i=0; i<result.size(); i++){
            temp = temp + "[ " + result.get(i).second().first() + " , " + 
                    result.get(i).second().second() + " ] : " + 
                    result.get(i).first();
            temp = temp + "   -> " + additional_result.get(i) + "\n";
        }
        return temp;
    }
    
}

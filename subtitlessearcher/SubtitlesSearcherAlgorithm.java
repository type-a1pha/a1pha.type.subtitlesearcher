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
public class SubtitlesSearcherAlgorithm {
    
    /**
     * 
     * 
     * @param pattern
     * @param subtitles
     * @return 
     */
    public static Solution createSolution(String pattern, ArrayList<Subtitle> subtitles){
        ArrayList<String> source = TextParser.splitText(TextParser.cookedText(pattern));
        ArrayList<String> target = new ArrayList<>();
        ArrayList<Subtitle> references = new ArrayList<>();
        for(int i=0; i<subtitles.size(); i++){ //iterate over subtitles
            ArrayList<String> temp = TextParser.splitText(TextParser.cookedText( subtitles.get(i).getText() ));
            for(int j=0; j<temp.size(); j++){ //iterate over current split text
                target.add(temp.get(j));
                references.add(subtitles.get(i)); //added temp.size() times
            }
        }
        //search procedure
        int[] cost = new int[source.size()];
        int[] match_len = new int[source.size()];
        int[] match_pos = new int[source.size()];
        //auxiliary array for randomness
        int[] count = new int[source.size()];
        for(int s=source.size()-1; s>=0; s--){
            cost[s] = Integer.MAX_VALUE;
            match_len[s] = 0;
            match_pos[s] = -1; //match not found as default value
            count[s] = 0;
            for(int t=0; t<target.size(); t++){
                int curr_len = 0;
                //search through possible matches of different length
                while(s+curr_len<source.size() && t+curr_len<target.size() &&
                        source.get(s+curr_len).equals(target.get(t+curr_len))){
                    curr_len++;
                    int sub_cost = 0; //cost of the current considered subproblem
                    if(s+curr_len < source.size()){
                        sub_cost = cost[s+curr_len];
                    }
                    //update current optimal cost
                    if(cost[s] > 1 + sub_cost){
                        count[s] = 1;
                        cost[s] = 1 + sub_cost;
                        match_len[s] = curr_len;
                        match_pos[s] = t;
                    }
                    if(cost[s] == 1 + sub_cost){
                        count[s]++;
                        if(SubtitlesSearcher.randomize() && (SubtitlesSearcher.randomInt() % count[s] == 0)){
                            cost[s] = 1 + sub_cost;
                            match_len[s] = curr_len;
                            match_pos[s] = t;
                        }
                    }
                }
            }
            if(match_pos[s] == -1){ //no match found {count[s] == 0}             
                if(s == source.size()-1){ //== cost.length - 1
                    cost[s] = 1;
                }
                else{
                    cost[s] = 1 + cost[s+1];
                }
            }
        }
        /*
            System.out.println("ITERAZIONE "+ (source.size()-s));
            for(int i=0; i<cost.length; i++){
                System.out.print(cost[i]+" ");
            }
            System.out.print("\n");
            for(int i=0; i<cost.length; i++){
                System.out.print(match_len[i]+" ");
            }
            System.out.print("\n");
            for(int i=0; i<cost.length; i++){
                System.out.print(match_pos[i]+" ");
            }
            System.out.print("\n");*/
        return new Solution(pattern, subtitles, source, target, references,
            cost, match_len, match_pos);
    }
    
}

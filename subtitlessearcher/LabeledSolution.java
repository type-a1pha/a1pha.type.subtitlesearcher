/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitlessearcher;

import java.util.Comparator;

/**
 * Wrapper class for a Solution which with an associated filename
 * 
 * @author IKAROS
 */
public class LabeledSolution extends Solution {
    
    String filename = "";
    
    public LabeledSolution(String filename, Solution solution){
        super(solution.pattern, solution.subtitles, solution.source, solution.target, solution.references,
            solution.cost, solution.match_len, solution.match_pos);
        this.filename = filename;
    }
    
    public String getFilename(){
        return this.filename;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof LabeledSolution){
            return this.filename.equals(((LabeledSolution)obj).filename);
        }
        return false;
    }
    
    @Override
    public String toString(){
        return "File: " + getFilename() + "\nCost: " + getSolutionCost() +
                    "\n" + super.toString();
    }
    
    @Override
    public String toExtendedString(){
        return "File: " + getFilename() + "\nCost: " + getSolutionCost() +
                    "\n" + super.toExtendedString();
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitlessearcher;

import java.util.Comparator;

/**
 *
 * @author IKAROS
 */
public class SolutionComparator implements Comparator<Solution> {

        @Override
        public int compare(Solution sol1, Solution sol2){
            return sol1.getSolutionCost() - sol2.getSolutionCost();
        }
        
}
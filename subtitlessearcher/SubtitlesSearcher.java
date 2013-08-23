/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitlessearcher;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import srtmanager.*;

/**
 *
 * @author IKAROS
 */
public class SubtitlesSearcher {
    
    private static boolean random = true;
    private static boolean takeLast = true;
    private static boolean printEmptyOnes = false;
    private static Random r = new Random(System.currentTimeMillis());
    
    public static boolean randomize(){
        return random;
    }
    
    public static int randomInt(){
        return r.nextInt();
    }
    public static void test(){
        /*String filename = "prova.srt";
        String input = "Come on";
        SrtFileManager srt_file = new SrtFileManager("prova.srt");
        SubtitleSearcherAlgorithms searcher = new SubtitleSearcherAlgorithms(input,srt_file.getStructure());
        searcher.doSearch();
        System.out.println("File: "+filename+"\nCost: "+searcher.getResult().size());
        printResult(searcher.getResult());*/
    }
    
    /**
     * Compute a LabeledSolution for the given file
     * 
     * @param input
     * @param filename
     * @return 
     */
    public static LabeledSolution oneSolutionOneFile(String input, String filename){
        File file = new File(filename);
        if( file.isDirectory() ){
            return null;
        }
        SrtFileManager srt_file = new SrtFileManager(filename);
        return new LabeledSolution(filename, SubtitlesSearcherAlgorithm.createSolution(input,srt_file.getStructure()));
    }
    
    /**
     * Compute a list of solutions containing one solution for every file in the root_path folder
     * If root_path is a file (and not a folder), a list containing (only) the solution relative to the
     * file will be returned.
     * 
     * @param input
     * @param root_path
     * @return 
     */
    public static LinkedList<LabeledSolution> manySolutionsManyFiles(String input, String root_path){
        LinkedList<LabeledSolution> result = new LinkedList<>();
        File folder = new File(root_path);
        if( ! folder.isDirectory() ){
            result.addLast(oneSolutionOneFile(input, root_path));
            return result;
        }
        File[] files = folder.listFiles();
        for(int i=0; i<files.length; i++){
            if(files[i].isDirectory()){
                // call recursively on sub-directories
                result.addAll(manySolutionsManyFiles(input, files[i].getAbsolutePath()));
            }
            else{
                // add the solution for the current file
                result.addLast(oneSolutionOneFile(input, files[i].getAbsolutePath()));
            }
        }
        return result;
    }
    
    private static String load_input(String filename){
        String result = "";
        File file = new File(filename);
        try{
            DataInputStream istream = new DataInputStream(new FileInputStream(file));
            byte[] raw_bytes = new byte[(int)file.length()];
            istream.read(raw_bytes,0,(int)file.length());
            istream.close();
            result = new String(raw_bytes);
        }
        catch(Exception e){
            System.out.println("ERRORE APERTURA FILE "+filename+": "+e.getMessage());
        }
        return result;
    }
    
    /**
     * @param args the command line arguments
     * 
     * MODE 1: print one solution for every file found in the folder
     * MODE 2: print only the best solutions for the files in the given folder
     */
    public static void main(String[] args) {
        int mode = 2;
        boolean extended_print = true; //not available in mode 3
        String input = load_input("input_text.txt");
        String root = "search_folder";
        String filename = "prova.srt";
        if(mode == 0){ //One solution from one file; the file is filename
            LabeledSolution sol = oneSolutionOneFile(input, filename);
            if(sol == null){
                System.out.println("Mode 0: No file processed");
            }
            else{
                if(extended_print){
                    System.out.println("Mode 0: 1 file processed\n\n" + sol.toExtendedString());
                }
                else{
                    System.out.println("Mode 0: 1 file processed\n\n" + sol);
                }
            }
        }
        if(mode == 1){ //print one solution for every file found in the folder
            LinkedList<LabeledSolution> to_print = manySolutionsManyFiles(input, root);
            System.out.println("Mode 1: " + to_print.size() + " Files Processed\n");
            Iterator<LabeledSolution> it = to_print.iterator();
            while(it.hasNext()){
                LabeledSolution sol = it.next();
                if(!sol.isEmpty() || printEmptyOnes){
                    if(extended_print){
                        System.out.println(sol.toExtendedString());
                    }
                    else{
                        System.out.println(sol);
                    }
                }
            }       
        }
        if(mode == 2){ //compute one solution for every file in the folder but print only the best ones
            LinkedList<LabeledSolution> solutions = manySolutionsManyFiles(input, root);
            int min = Integer.MAX_VALUE;
            Iterator<LabeledSolution> it = solutions.iterator();
            while(it.hasNext()){
                LabeledSolution sol = it.next();
                if(sol.getSolutionCost() < min){
                    min = sol.getSolutionCost();
                }
            }
            System.out.println("Mode 2: " + solutions.size() + " Files Processed\n");
            it = solutions.iterator();
            while(it.hasNext()){
                LabeledSolution sol = it.next();
                if(sol.getSolutionCost() == min){
                    if(!sol.isEmpty() || printEmptyOnes){
                        if(extended_print){
                            System.out.println(sol.toExtendedString());
                        }
                        else{
                            System.out.println(sol);
                        }
                    }
                }
            }
        }
        if(mode == 3){ //mix the solutions to get the best one built from all the files
            //it's like (SHOULD BE!!!) the solution computed from a bigger file given from all the original files mixed together
            LinkedList<LabeledSolution> solutions = manySolutionsManyFiles(input, root);
            System.out.println("Mode 3: " + solutions.size() + " Files Processed\n");
            if(solutions.size() > 0){
                //create a mixed solution
                int[] mix_cost = new int[solutions.get(0).cost.length];
                LabeledSolution[] ref = new LabeledSolution[solutions.get(0).cost.length];
                //auxiliary array for randomness
                int[] count = new int[solutions.get(0).cost.length];
                for(int c=mix_cost.length-1; c>=0; c--){
                    mix_cost[c] = Integer.MAX_VALUE;
                    ref[c] = null; //CHECK FOR THIS IN THE RESULTING ARRAY!!!
                    count[c] = 0;
                    for(int s=0; s<solutions.size(); s++){
                        LabeledSolution curr_sol = solutions.get(s);
                        if(curr_sol.match_len[c]>0){
                            int sub_cost = 0;
                            if(c+curr_sol.match_len[c] < mix_cost.length){
                                sub_cost = mix_cost[c+curr_sol.match_len[c]];
                            }
                            if(mix_cost[c] > 1+sub_cost){
                                count[c] = 1;
                                mix_cost[c] = 1 + sub_cost;
                                ref[c] = curr_sol;
                            }
                            if(mix_cost[c] == 1+sub_cost){
                                count[c]++;
                                if(takeLast || (randomize() && (randomInt() % count[c] == 0))){
                                    mix_cost[c] = 1 + sub_cost;
                                    ref[c] = curr_sol;
                                }
                            }
                        }
                    }
                    if(ref[c] == null){ //no match exists among all the solutions (count[s] == 0)
                        if(c == mix_cost.length-1){
                            mix_cost[c] = 1;
                        }
                        else{
                            mix_cost[c] = 1 + mix_cost[c+1];
                        }
                    }
                }
                System.out.println("Searching for: "+solutions.get(0).source.size()+" words");
                //print the found solution
                System.out.println("Cost: "+mix_cost[0]);
                //retrieve a copy of the source for ease
                ArrayList<String> source = solutions.get(0).source;
                int curr_pos = 0;
                while(curr_pos < source.size()){
                    if(ref[curr_pos] != null){
                        LabeledSolution sol = ref[curr_pos]; //local best solution
                        System.out.print("[ "+sol.references.get(sol.match_pos[curr_pos]).getStart()); //starting time
                        System.out.print(" , ");
                        System.out.print(sol.references.get(sol.match_pos[curr_pos]+sol.match_len[curr_pos]-1).getEnd()); //ending time
                        System.out.print(" ] : ");
                        for(int i=0; i<sol.match_len[curr_pos]; i++){
                            System.out.print(source.get(curr_pos+i)+" ");
                        }
                        System.out.print("("+sol.getFilename()+")\n");
                        curr_pos = curr_pos + sol.match_len[curr_pos];
                    }
                    else{
                        System.out.println("[ "+(SubTime.nullTime())+" , "+ (SubTime.nullTime())+" ] ; "+
                                source.get(curr_pos)+" (no match found) ");
                        curr_pos++;
                    }
                }
            }
        }
        System.out.println("Error count during parsing: "+SrtParser.getErrorCount()+" on "+
                SrtParser.getParseCount()+" parsed");
    }
}

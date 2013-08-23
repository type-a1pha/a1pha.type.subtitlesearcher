/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subtitlessearcher;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    private static void mode_1(String input, String root, boolean extended_print){
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
            System.out.println("Error count during parsing: "+SrtParser.getErrorCount()+" on "+
            SrtParser.getParseCount()+" parsed");
    }
    
    private static void mode_2(String input, String root, boolean extended_print){
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
            System.out.println("Error count during parsing: "+SrtParser.getErrorCount()+" on "+
            SrtParser.getParseCount()+" parsed");
    }
    
    private static void mode_3(String input, String root, boolean extended_print){
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
            System.out.println("Error count during parsing: "+SrtParser.getErrorCount()+" on "+
            SrtParser.getParseCount()+" parsed");
    }
    
    private static void info(){
        System.out.println(
                "Subtitle Searcher\n\n"
                
                + "Arguments are:\n"
                + "-mode { 1,2,3 }\n"
                + "-inputstr String\n"              
                + "-inputfile filename.txt\n"
                + "-folder String\n"
                + "-ext //for extended print (default)\n"
                + "-notext //for non extended print\n"
                + "-output filename"
                + "-info //for (this) info paragraph\n\n"
                
                + "Behavior:"
                + "MODE 1: print one solution for every file found in the folder\n"
                + "MODE 2: print only the best solutions (based on the cost values) for the files in the given folder\n"
                + "MODE 3: mix the solutions from all the files to get the best possible solution out of all the information given\n"    
        );
    }
    
    /**
     * @param args the command line arguments
     * 
     * MODE 1: print one solution for every file found in the folder
     * MODE 2: print only the best solutions (based on the cost values) for the files in the given folder
     * MODE 3: mix the solutions from all the files to get the best possible solution out of all the information given
     */
    public static void main(String[] args) {
        int[] availableModes = {1,2,3};
        int mode = 0;
        boolean extended_print = true; //not available in mode 3
        /* String input = load_input("input_text.txt");
        String root = "search_folder";
        String filename = "prova.srt";
        */
        String input = null;
        String root = null;
        String outputFilename = null;
        if(args.length == 0){
            info();
            return;
        }
        for(int i=0; i<args.length; i++){
            switch(args[i]){
                case "-mode": 
                    if(i+1 < args.length){
                        try{
                            mode = Integer.parseInt(args[i+1]);
                            boolean found = false;
                            for(int k=0; k<availableModes.length; k++){
                                if(availableModes[k] == mode){
                                    found = true;
                                }
                            }
                            if( ! found ){
                                System.out.println("ERROR: -mode argument is not in {1,2,3}");
                                return;
                            }
                        } catch(NumberFormatException e) {
                            System.out.println("ERROR: -mode argument is not an integer");
                            return;
                        }  
                    } else {
                        System.out.println("ERROR: -mode value not supplied");
                        return;
                    }
                    System.out.println("-mode: value supplied "+mode);
                    i++;
                    break;
                case "-inputstr": 
                    if(i+1 < args.length){
                        input = args[i+1];
                    } else {
                        System.out.println("ERROR: -inputstr value not supplied");
                        return;
                    } 
                    System.out.println("-inputstr: value supplied "+input);
                    i++;
                    break;
                case "-inputfile": 
                    if(i+1 < args.length){
                        input = load_input(args[i+1]);
                    } else {
                        System.out.println("ERROR: -inputfile value not supplied");
                        return;
                    }                     
                    System.out.println("-inputfile: value supplied "+args[i+1]);
                    i++;
                    break;
                case "-folder":
                    if(i+1 < args.length){
                        root = args[i+1];
                    } else {
                        System.out.println("ERROR: -folder value not supplied");
                        return;
                    } 
                    System.out.println("-folder: value supplied "+root);
                    i++;
                    break;
                case "-outputfile": 
                    if(i+1 < args.length){
                        outputFilename = args[i+1];
                    } else {
                        System.out.println("ERROR: -outputfile value not supplied");
                        return;
                    }                     
                    System.out.println("-inputfile: value supplied "+outputFilename);
                    i++;
                    break;
                case "-ext": 
                    extended_print = true;
                    break;
                case "-notext": 
                    extended_print = false;
                    break;
                case "-info": 
                    info();
                    break;
                default:
                    System.out.println("ERROR: "+args[i]+" is not a valid argument or modifier");
                    break;
            }
        }
        if(input == null || root == null){
            System.out.println("ERROR: Missing arguments: -mode, -inputstr or -inputfile, and -folder are necessary! (-info for info)");
            return;
        }
        File file = new File(root);
        if( ! file.isDirectory() ){
            System.out.println("ERROR: The provided path in -folder is not a folder!");
            return;
        }
        PrintStream outStream = null;
        if( outputFilename != null ){
            try{
                outStream = new PrintStream(outputFilename+".txt");
            } catch (Exception e){
                System.out.println("ERROR: Unable to create or write file: "+outputFilename+".txt");
                return;
            }
            System.setOut(outStream);
        }
        //call the appropriate routine
        switch(mode){
            case 1: mode_1(input,root,extended_print); break;
            case 2: mode_2(input,root,extended_print); break;
            case 3: mode_3(input,root,extended_print); break;
            default: 
                System.setOut(System.out);
                System.out.println("ERROR: no mode value specified (use -mode)");
        } 
        if(outStream != null){ outStream.close(); }
    }
}

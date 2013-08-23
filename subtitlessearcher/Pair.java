/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package subtitlessearcher;

/**
 *
 * @author Ikaros
 */
public class Pair<A,B> {

    private A first = null;
    private B second = null;

    public Pair(A first, B second){
        this.first = first;
        this.second = second;
    }

    public A first(){
        return first;
    }

    public B second(){
        return second;
    }

    public void setFirst(A first){
        this.first = first;
    }

    public void setSecond(B second){
        this.second = second;
    }

}

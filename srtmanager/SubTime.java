/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package srtmanager;

/**
 *
 * @author IKAROS
 */
public class SubTime {
    
    private Integer h, m, s, millis;
    private static SubTime nullTime = new SubTime(-1,0,0,0);
  
    public SubTime(Integer h, Integer m, Integer s, Integer millis){
        this.h = h;
        this.m = m;
        this.s = s;
        this.millis = millis;
    }
    
    public static SubTime nullTime(){
        return nullTime;
    }
    
    public Integer getHour(){
        return this.h;
    }
    
    public Integer getMinute(){
        return this.m;
    }
    
    public Integer getSecond(){
        return this.s;
    }
    
    public Integer getMillisecond(){
        return this.millis;
    }
    
    public boolean equals(SubTime obj){
        return this.h == obj.h && this.m == obj.m &&
                this.s == obj.s && this.millis == obj.millis;
    }
    
    @Override
    public String toString(){
        String _h,_m,_s,_millis;
        _h = h.toString();
        _m = m.toString();
        _s = s.toString();
        _millis = millis.toString();
        if(_h.length() == 1){ _h = "0"+_h; }
        if(_m.length() == 1){ _m = "0"+_m; }
        if(_s.length() == 1){ _s = "0"+_s; }
        if(_millis.length() == 2){ _millis = "0"+_millis; }
        if(_millis.length() == 1){ _millis = "00"+_millis; }
        return _h+":"+_m+":"+_s+","+_millis;
    }
    
}

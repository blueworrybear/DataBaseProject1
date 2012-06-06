/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlManipulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author ljybowser
 */
public class SqlSetOperator {
    
    
    public static Set union(Set a, Set b){
        
        
        Set res = new HashSet<String>();
        
        Iterator it = a.iterator();
        
        while(it.hasNext()){
            
            String _currentPK = (String)it.next();
            
            res.add(_currentPK);
            
        }
        
        it = b.iterator();
        
        while(it.hasNext()){
         
            String _currentPK = (String)it.next();
            
            res.add(_currentPK);
            
        }
        
        
        return res;
    }
    
    
    public static Set intersect(Set a,Set b){
        
        HashSet<String> res = new HashSet<String>();
        
        Iterator it  = a.iterator();
        
        while(it.hasNext()){
            
            String _currentPK = (String)it.next();
            
            if(b.contains(_currentPK)){
                res.add(_currentPK);
            }
            
        }
        
        return res;
    }
    
    public static ArrayList intersect(ArrayList a,ArrayList b){
        
        ArrayList<String> res = new ArrayList<String>();
        
        Iterator it  = a.iterator();
        
        HashSet<String> _tmp = new HashSet<String>();
        
        
        while(it.hasNext()){
            
            String _currentPK = (String)it.next();
            
            _tmp.add(_currentPK);
            
        }
        
        it = b.iterator();
        
        while(it.hasNext()){
            
            String _currentPK = (String)it.next();
            
            if(!_tmp.add(_currentPK)){
                res.add(_currentPK);
            }
            
        }
        
        return res;
    }
    
    public static ArrayList union(ArrayList a,ArrayList b){
        
        ArrayList<String> res = new ArrayList<String>();
        
        Iterator it  = a.iterator();
        
        HashSet<String> _tmp = new HashSet<String>();
        
        while(it.hasNext()){
            
            String _currentPK = (String)it.next();
            
            res.add(_currentPK);
            _tmp.add(_currentPK);
            
        }
        
        it = b.iterator();
        
        while(it.hasNext()){
            
            String _currentPK = (String)it.next();
            
            if(_tmp.add(_currentPK)){
                res.add(_currentPK);
            }
            
        }
        
        return res;
    }
    
    
}

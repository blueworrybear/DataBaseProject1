///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
package DataStructure;

import com.google.common.collect.HashMultimap;
import databaseproject.SqlExecutionFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


//@author ljybowser

public class MultiHashTable {
    

    String tableName = "";
    HashMap<String,HashMultimap<String,String>> content = new HashMap<String,HashMultimap<String,String>>();
    
    
    public MultiHashTable(String tableName){
    
        this.tableName = tableName;
        HashMap<String,Object> _tablecontent = (HashMap)SqlExecutionFactory.dataRecord.getHashTable(tableName);
        
        //System.out.println("Content ="+_tablecontent);
        
        Set<String> primaryKeySet = _tablecontent.keySet();
        Set<String> colNameSet = null;
        HashMultimap<String,String> _columnIndex;
        
        
        
        Iterator it = primaryKeySet.iterator();
        
        colNameSet = ((HashMap<String,Object>)_tablecontent.get((String)it.next())).keySet();
        
        Iterator it2 = colNameSet.iterator();
        
        while(it2.hasNext()){//Looped via colName
            String _currentColumnName = (String)it2.next();
            
            //System.out.println("ColName ="+_currentColumnName);
            
            it = primaryKeySet.iterator();
            _columnIndex = HashMultimap.create();
            
            while(it.hasNext()){//Looped via PK
                String _currentPrimaryKey = (String)it.next();
                
                
                
                Map<String,Object> _currentTuple = (Map<String,Object>)_tablecontent.get(_currentPrimaryKey);
                
                //System.out.println("Tuple = "+_currentTuple);
                
                _columnIndex.put((String)_currentTuple.get(_currentColumnName),_currentPrimaryKey);
                
                
            }
            
            //System.out.println("PUT:"+_currentColumnName);
            content.put(_currentColumnName,_columnIndex);
            
            
        }
        
        
        
    }
    
    public Set getContentSet(String colName,String colContent){
        
       
        
        HashMultimap<String,String> map = this.content.get(colName);
        
        //System.out.println("ColContent="+colContent+"\nColname="+colName+"\nANS="+map.get(colContent));
        
        return map.get(colContent);
        
    }
    
    @Override
    public String toString(){
        return content.toString();
    }
    
    
}

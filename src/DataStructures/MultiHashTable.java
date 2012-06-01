/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import databaseproject.SqlExecutionFactory;
import java.util.*;


/**
 *
 * @author ljybowser
 */
public class MultiHashTable {
    

    String tableName;
    HashMap<String,HashMultimap<Object,Object>> content;
    
    
    public MultiHashTable(String tableName){
    
        this.tableName = tableName;
        HashMap<String,Object> _tablecontent = (HashMap)SqlExecutionFactory.dataRecord.getHashTable(tableName);
        
        
        Set<String> primaryKeySet = _tablecontent.keySet();
        Set<String> colNameSet = null;
        
        
        
        
        Iterator it = primaryKeySet.iterator();
        
        colNameSet = ((HashMap<String,Object>)_tablecontent.get((String)it.next())).keySet();
        
        Iterator it2 = colNameSet.iterator();
        
        while(it2.hasNext()){
            String _currentColumnName = (String)it2.next();
            it = primaryKeySet.iterator();
            HashMultimap<Object,Object> _columnIndex = HashMultimap.create();
            
            while(it.hasNext()){
                String _currentPrimaryKey = (String)it.next();
                
                Map<String,Object> _currentTuple = (Map<String,Object>)_tablecontent.get(_currentPrimaryKey);
                
                _columnIndex.put(_currentTuple.get(_currentColumnName),_currentPrimaryKey);
                
                
            }
            
            content.put(_currentColumnName,_columnIndex);
            
            
        }
        
        
        
    }
    
    
}

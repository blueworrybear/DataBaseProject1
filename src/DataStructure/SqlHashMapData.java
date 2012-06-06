///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.

package DataStructure;

import SqlManipulation.SqlSetOperator;
import com.google.common.collect.HashMultimap;
import databaseproject.queryDataRecord;
import java.util.*;


// * @author ljybowser

public class SqlHashMapData {
    
    HashMap<String,MultiHashTable> content = new HashMap<String,MultiHashTable>();
    ArrayList<String> tableNameList = new ArrayList<String>();
    
    public SqlHashMapData(){
        
        Iterator it =  queryDataRecord.tableNames.iterator();
        //System.out.println("AHHHHHH!");
        
        while(it.hasNext()){
            
            String _tableName = (String)it.next();
            
            //System.out.println(_tableName);
            
            tableNameList.add(_tableName);
            //System.out.println(_tableName);
            content.put(_tableName,new MultiHashTable(_tableName));
            
            
        }
        
        
        
       
    }
    
   
    public void printContent(String tableName){
        System.out.println("99999\n"+this.content.get(tableName));
    }
    
    
    public Set getEntrySet(String tableName, String colName , String tableContent){
        
        MultiHashTable _selectedTable = (MultiHashTable)this.content.get(tableName);
        
        //System.out.println("TableName= "+tableName+" ColName= "+colName+" content= "+tableContent );
        
        if(_selectedTable==null){
            System.err.println("ERROR: Table not exist!");
        }
        
        //System.out.println(_selectedTable);
        
        return _selectedTable.getContentSet(colName, tableContent);
    }
    
    public ArrayList<ArrayList<String>> getJoinEntrySet(String tableName1,String tableName2,String colName1,String colName2){
        
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        
        System.out.println("T1="+tableName1+"\nT2="+tableName2+"\ncolName1= "+colName1+"\ncolName2"+colName2);
        
        Set contentKeySet1 ;
        Set contentKeySet2 ;
        Set joinContentKeySet;
        ArrayList<String> joinPrimaryKeySet1 = new ArrayList<String>();
        ArrayList<String> joinPrimaryKeySet2 = new ArrayList<String>();
        MultiHashTable table1 = (MultiHashTable)this.content.get(tableName1);
        MultiHashTable table2 = (MultiHashTable)this.content.get(tableName2);
        HashMultimap<String,String> tableContent1 = (HashMultimap<String,String>)table1.content.get(colName1);
        HashMultimap<String,String> tableContent2 = (HashMultimap<String,String>)table2.content.get(colName2);
        
        contentKeySet1 = tableContent1.keySet();
        contentKeySet2 = tableContent2.keySet();
        
        joinContentKeySet = SqlSetOperator.intersect(contentKeySet1, contentKeySet2);
        
        Iterator _it = joinContentKeySet.iterator();
        
        while(_it.hasNext()){
            
            String _str = (String)_it.next();
            
            Set _s1 = tableContent1.get(_str);
            Set _s2 = tableContent2.get(_str);
            //joinPrimaryKeySet1.add(tableContent1.get(_str));
            //joinPrimaryKeySet2.add(tableContent2.get(_str));
            
            Iterator _it2 = _s1.iterator();
            
            while(_it2.hasNext()){
                String _currentPK1 = (String)_it2.next();
                joinPrimaryKeySet1.add(_currentPK1);
            }
            
            _it2 = _s2.iterator();
            
            while(_it2.hasNext()){
                String _currentPK1 = (String)_it2.next();
                joinPrimaryKeySet2.add(_currentPK1);
            }
            
            
            
      
               
        }
        
        result.add(joinPrimaryKeySet1);
        result.add(joinPrimaryKeySet2);
        
        
        
        return result;
    }
    
    
}

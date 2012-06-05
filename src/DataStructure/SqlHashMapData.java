///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.

package DataStructure;

import databaseproject.queryDataRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


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
    
    
}

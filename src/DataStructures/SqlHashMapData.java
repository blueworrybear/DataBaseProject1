///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package DataStructures;
//
//import databaseproject.queryDataRecord;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//
///**
// *
// * @author ljybowser
// */
//public class SqlHashMapData {
//    
//    HashMap<String,MultiHashTable> content;
//    ArrayList<String> tableNameList;
//    
//    public SqlHashMapData(){
//        
//        Iterator it =  queryDataRecord.tableNames.iterator();
//        
//        while(it.hasNext()){
//            
//            String _tableName = (String)it.next();
//            tableNameList.add(_tableName);
//            content.put(_tableName,new MultiHashTable(_tableName));
//            
//        }
//        
//        
//        
//        
//    }
//    
//    
//    
//}

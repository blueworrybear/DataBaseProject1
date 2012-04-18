/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlManipulation;

import SqlInstructionFetcher.SqlInsertIntoFetcher;
import databaseproject.SqlExecutionFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ljybowser
 */
public class SqlInsertTableExec {
    
    SqlInsertIntoFetcher sqlInfo;
    String tableName;
    File colFile,contentFile;
    HashMap<String,Integer> hash;
    
    
    
    
    public SqlInsertTableExec(SqlInsertIntoFetcher insertInfo){
        
        
        sqlInfo = insertInfo;
        
        tableName = sqlInfo.fetchTableName();
        
        colFile = new File(tableName+".txt");
        
        
        
    }
    
    private ArrayList<Map<String,Object>> parseColNameFile(){
        
        ArrayList<Map<String,Object>>  colInfo = new ArrayList<Map<String,Object>>();
        String contentBuf;
        ArrayList<String> colContent = new ArrayList<String>();
        String[] tok;
        String line;
        
        if(!colFile.exists()){
            return null;
        }else{
                try {
                    FileReader fReader = new FileReader(colFile);
                    BufferedReader bfReader = new BufferedReader(fReader);
                 
                    while((line = bfReader.readLine()) != null){
                       
                        colContent.add(line);
                    }
                    
                    Iterator it = colContent.iterator();
                    //contentBuf = (String)it.next();        
                    while(it.hasNext()){
                        
                        contentBuf = (String)it.next();
                        contentBuf = (String)contentBuf.replaceAll(",","");
                        tok = contentBuf.split(":");
                        
                        Map map = new HashMap<String, Object>();
                        
                        map.put("Name", tok[0]);
                        map.put("Type", tok[1]);
                        map.put("Quantity", Integer.parseInt(tok[2]));
                        if(tok[3].equals("true"))
                            map.put("PRIMARY", true);
                        else
                            map.put("PRIMARY", false);
                        colInfo.add(map);
                        //System.out.println(map.get("Name"));
                    }
                    
                    
                } catch (IOException ex) {
                    
                Logger.getLogger(SqlInsertTableExec.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        
        
        
        
        return colInfo;
        
    }
    
    private void prepareNoFile(){
        hash = SqlExecutionFactory.record.getHashTable(tableName);
        
        if(hash==null){
            hash = new HashMap<String,Integer>();
        }
        
        
        
    }
    
    private void prepare(){
        
        //Unimplemented Method
        
       
    }
    
    
    public boolean exec(){
        
        ArrayList<Object> insertContent;
        ArrayList<Map<String,Object>>  colInfo;
        ArrayList<Object> colValue = new ArrayList<Object>();
        ArrayList<String> colSeq;
        StringBuffer primaryKeySet = new StringBuffer();
        Iterator it;
        
        prepareNoFile();
        
        if(!colFile.exists()){
            System.out.println("ERROR: Table Not exists!!!!");
            return false;
        }else
        {
            contentFile = new File(tableName+"_content.txt");
            colInfo = parseColNameFile();
             
            if(sqlInfo.fetchInsertSequence().isEmpty()){ 
                  colSeq = new ArrayList<String>();  
                  it = colInfo.iterator();  
                  while(it.hasNext()){
                        Map map = (Map)it.next();
                        colSeq.add((String)map.get("Name"));
                        
                  }
                    
            }else{
                 colSeq = sqlInfo.fetchInsertSequence();
            }
             colValue =  sqlInfo.fetchInsertValue(); 
            if(colInfo.size()!=colValue.size()){
                    System.out.println("ERROR: Value number incorrect!!!");
                    System.out.println("ColInfo="+colInfo.size()+" and inputValue="+colValue.size());
                    return false;
            }else
            {      
                Object value;
                it = colInfo.iterator();
                while(it.hasNext()){
                     Map map = (Map)it.next();
                     value = colValue.get((Integer)colSeq.indexOf(map.get("Name")));
                     if( map.get("Type").equals("INT") )
                     {
                         if( !value.getClass().toString().equals("class java.lang.Integer") )
                         {
                             System.out.println("ERROR: Not Integer");
                             return false;
                         }
                     }else
                     {
//                         System.out.println("Else::"+value);
                         //System.out.println(map.get("Quantity"));
                         if( ((String)value).replaceAll("(^\'|^\"|\'$|\"$)", "").length() > ((Integer)map.get("Quantity")) )
                         {
                             System.out.println("ERROR!!! Argument Too LONG!!!"+value+":"+map.get("Quantity"));
                             return false;
                         }
                     }
                     if((Boolean)map.get("PRIMARY"))
                     {
                         primaryKeySet.append(value);
                     }
                     //System.out.println(value);
                }
                
                if(hash.containsKey(primaryKeySet.toString()))
                {
                     System.out.println("ERROR!! PRIMARY KEY DUPLICATED!!!!!!");
                     return false;
                }else
                {
                     hash.put(primaryKeySet.toString(),0);
                     try {
                        FileWriter fw = new FileWriter(contentFile,true);
                        it = colInfo.iterator();
                        while(it.hasNext())
                        {
                            Map m = (Map)it.next();
                            value = colValue.get(colSeq.indexOf(m.get("Name")));
                            fw.append(value+",");
                        }
                        fw.close();
                     }catch (IOException ex) {
                        Logger.getLogger(SqlInsertTableExec.class.getName()).log(Level.SEVERE, null, ex);
                     }
                }  
                
            }
            
            
        } 
        
        SqlExecutionFactory.record.addSqlContentHashMap(tableName,hash);
        return true;
    }
    
}

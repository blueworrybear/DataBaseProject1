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
                        map.put("Quantity", tok[2]);
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
        ArrayList<String> colValue = new ArrayList<String>();
        ArrayList<String> colSeq;
        //ArrayList<Integer> indexSeq = new ArrayList<Integer>();
        String primaryKeySet="";
        Iterator it;
        
        prepareNoFile();
        
        if(!colFile.exists()){
            System.out.println("ERROR: Table Not exists!!!!");
            return false;
        }else{
            contentFile = new File(tableName+"_content.txt");
            
            //try {
             //   FileWriter fw = new FileWriter(contentFile,true);
                colInfo = parseColNameFile();
                Iterator i = sqlInfo.fetchInsertValue().iterator();
                
                while(i.hasNext()){
                    
                    
                    colValue.add(i.next().toString());
                    
                }
                
                
                if(!sqlInfo.fetchInsertSequence().isEmpty()){ 
                    
                    colSeq = sqlInfo.fetchInsertSequence();
                    
                    it = colInfo.iterator();
                    
                    //Iterator it2 = colInfo.iterator();
                }//If insert has sepuence.
                else{
                    colSeq = new ArrayList<String>();
                    Iterator it2 = colInfo.iterator();
                    
                    while(it2.hasNext()){
                        Map map = (Map)it2.next();
                        colSeq.add((String)map.get("Name"));
                        
                    }
                    
                }
                    
                    
                    
                    if(colInfo.size()!=colValue.size()){
                        System.out.println("ERROR: Value number incorrect!!!");
                        System.out.println("ColInfo="+colInfo.size()+" and inputValue="+colValue.size());
                        return false;
                    }else{
                        
                        it = colInfo.iterator();
                      
                       
                        while(it.hasNext()){
                           
                            String str2;
                            Map map = (HashMap)it.next();
                            if("INT".equals((String)map.get("Type"))){
                                if(colValue.get((Integer)colSeq.indexOf(map.get("Name")))!=null){
                                    
                                    str2 = (String)colValue.get((Integer)colSeq.indexOf(map.get("Name")));    
                                    
                                    if(!str2.matches("^[0-9]+$")){
                                        
                                        System.out.println("ERROR: Not Integer");
                                        
                                        return false;
                                    }
                                    
                                }
                            }else{
                                
                                str2 = (String)colValue.get((Integer)colSeq.indexOf(map.get("Name"))); 
                                
                                if(str2.length()>((Integer)map.get("Quantity"))){
                                    System.out.println("ERROR!!! Argument Too LONG!!!");
                                }
                                
                                
                            }
                            
                            if((Boolean)map.get("PRIMARY")){
                                
                                
                                primaryKeySet+=(";;"+(String)colValue.get(colSeq.indexOf(map.get("PRIMARY"))));
                                
                            }
                                
                            
                            
                            
                        }
                        
                        
                       if(hash.containsKey(primaryKeySet)){
                           System.out.println("ERROR!! PRIMARY KEY DUPLICATED!!!!!!");
                           return false;
                       }else{
                           hash.put(primaryKeySet,0);
                       }
                       
                       it = colInfo.iterator();
                try {
                    FileWriter fw = new FileWriter(contentFile,true);
                    
                    while(it.hasNext()){
                           
                          Map m = (Map)it.next();
                          
                          String s = (String) colValue.get(colSeq.indexOf(m.get("Name")));
                          
                          fw.append(s+",");
                          
                           
                       }
                    
                    fw.close();
                    
                    
                } catch (IOException ex) {
                   
                    Logger.getLogger(SqlInsertTableExec.class.getName()).log(Level.SEVERE, null, ex);
                }
                       
                       
                       
                        
                       //fw.close();
                       SqlExecutionFactory.record.addSqlContentHashMap(tableName,hash);
                       
                    
                       
                    }
                    
                    
                    
                    
                    
                
                
                
           // } catch (IOException ex) {
           //     Logger.getLogger(SqlInsertTableExec.class.getName()).log(Level.SEVERE, null, ex);
           // }
            
        }
        
        
        return true;
    }
    
}

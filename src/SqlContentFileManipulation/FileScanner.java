/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlContentFileManipulation;

import SqlManipulation.SqlColNameFileParser;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ljybowser
 */
public class FileScanner {
    
    ArrayList<String> txtList; 
    
    public FileScanner(){
        
        txtList = new ArrayList<String>();
        
        
        File fs = new File("./");
        String[] fileList = fs.list();
        System.out.println("!!");
        for(int i=0;i<fileList.length;i++){
            if(fileList[i].matches("^.+[^_content]\\.txt$")){
                
                txtList.add(fileList[i].replaceAll("\\.txt$",""));
                
            }
            
        }
    
    }
    
   
    
    public ArrayList<String> getTableNames(){
        
        return txtList;
        
    
    }
    
    public HashMap<String, Object> getTableContent(String tableName){
        
        HashMap<String, Object> table = new HashMap<String, Object>();
        File contentFile;
        SqlColNameFileParser colNameParser = new SqlColNameFileParser();
        ArrayList<Map<String,Object>> colInfo = new ArrayList<Map<String,Object>>();
        ArrayList<String> colName = new ArrayList<String>();
        ArrayList<Boolean> colPK = new ArrayList<Boolean>();
        ArrayList<String> contentUnarranged = new ArrayList<String>();
        String[] tok;
        ArrayList<String> pKcomb = new ArrayList<String>();
        //String pKcomb = new String();
        
        boolean isfound = false;
        
        Iterator it = txtList.iterator();
        
        while(it.hasNext()){
        
            String str = (String)it.next();
            
            if(str.equals(tableName)){
                isfound = true;
                break;
            }
            
        
        }
        
        if(isfound){
             contentFile = new File(tableName+"_content.txt");
             if(contentFile.exists()){
                 
                 colInfo = colNameParser.parseColNameFile(tableName);
                 
                 it = colInfo.iterator();
                 
                 while(it.hasNext()){
                     Map<String,Object> map = (Map<String,Object>)it.next();
                     
                     colName.add((String)map.get("Name"));
                     colPK.add((Boolean)map.get("PRIMARY"));
                     
                 }
                 
                 
                 
                 
                try {
                    FileReader fReader = new FileReader(contentFile);
                    BufferedReader bfReader = new BufferedReader(fReader);
                    String line;
                    try {
                        while((line = bfReader.readLine()) != null){
                            
                            tok = line.split(",");
                            contentUnarranged.addAll(Arrays.asList(tok));
                            
                            
                        
                     }
                    for(int i=0;i<contentUnarranged.size();i++)
                    {
                        HashMap<String, Object> tupleMap = new HashMap<String, Object>();
                        String primaryKey = "";
                        
                        tupleMap.put(colName.get(i%colName.size()), contentUnarranged.get(i));
                        if( colPK.get(i%colPK.size()) )
                        {
                            primaryKey += contentUnarranged.get(i);
                        }
                        if(i%colName.size() == 0 && i!=0)
                        {
                            table.put(primaryKey, tupleMap);
                        }
                        
                    }
                        
                        
                        
                    } catch (IOException ex) {
                        Logger.getLogger(FileScanner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FileScanner.class.getName()).log(Level.SEVERE, null, ex);
                }
                 
                 
             }
             
             
        }else{
            System.err.println("Invalid Table Name");
            return null;
        }
        
        
        
        
        return table;
    
    
    }
    
  
    
    
    
}

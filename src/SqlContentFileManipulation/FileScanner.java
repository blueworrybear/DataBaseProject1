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
        //SqlColNameFileParser colNameParser = new SqlColNameFileParser();
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
                 
                 colInfo = SqlColNameFileParser.parseColNameFile(tableName);
                 
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
                    
                    int numberOfRow = contentUnarranged.size()/colName.size();
                    int numberOfCol = colName.size();
                    for(int row=0;row<numberOfRow;row++)
                    {
                        HashMap<String, Object> tupleMap = new HashMap<String, Object>();
                        String primaryKey = "";
                        
                        for(int index=0;index<numberOfCol;index++)
                        {
                            int position = row*numberOfCol + index;
                            tupleMap.put(colName.get(index), contentUnarranged.get(position));
                            if( colPK.get(index) )
                            {
                                primaryKey += contentUnarranged.get(position);
                            }
                        }
                        table.put(primaryKey, tupleMap);
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

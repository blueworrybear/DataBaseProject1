/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlManipulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
public class SqlColNameFileParser {
    
    
    public SqlColNameFileParser(){
        
        //Empty Constructor
        
    }
    public static HashMap<String, String> parseColType(String tableName)
    {
        HashMap result = new HashMap<String, String>();
        ArrayList<String> content = new ArrayList<String>();
        String line, buffer;
        String[] token;
        boolean hasPrimary = false;
        
        File fpt = new File(tableName+".txt");
        if(!fpt.exists())
        {
            System.out.println("This "+tableName+" table does not exist.");
            return null;
        }else
        {
            try
            {
                FileReader fReader = new FileReader(fpt);
                BufferedReader bfReader = new BufferedReader(fReader);
                
                while((line = bfReader.readLine()) != null)
                {
                    content.add(line);
                }
                Iterator it = content.iterator();
                while(it.hasNext())
                {
                    buffer = (String)it.next();
                    buffer = (String)buffer.replaceAll(",","");
                    token = buffer.split(":");
                    
                    if(token[1].equals("VARCHAR") || token[1].equals("CHAR"))
                    {
                        result.put(token[0], "String");
                    }else
                    {
                        result.put(token[0], "Integer");
                    }
                    
                    if(token[3].equals("true"))
                    {
                        if(token[1].equals("VARCHAR") || token[1].equals("CHAR"))
                        {
                            result.put("primary", "String");
                        }else
                        {
                            result.put("primary", "Integer");
                        }
                        hasPrimary = true;
                    }
                }
                if(hasPrimary == false)
                {
                    result.put("primary", "String");
                }
                
            }catch(IOException e)
            {
                System.out.println(e);
            }
            
            return result;
        }
        
    }
    
    public static ArrayList<Map<String,Object>> parseColNameFile(String tableName){
        
        ArrayList<Map<String,Object>>  colInfo = new ArrayList<Map<String,Object>>();
        String contentBuf;
        ArrayList<String> colContent = new ArrayList<String>();
        String[] tok;
        String line;
        
        File colFile = new File(tableName+".txt");
        
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
    
    
}

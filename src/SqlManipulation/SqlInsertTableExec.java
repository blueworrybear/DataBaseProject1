/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlManipulation;

import SqlInstructionFetcher.SqlInsertIntoFetcher;
import databaseproject.SqlExecutionFactory;
import java.io.*;
import java.util.*;
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
    HashMap<String,Object> hash;
    Map<String,Object> value_pair;
    
    
    
    
    public SqlInsertTableExec(SqlInsertIntoFetcher insertInfo){
        
        
        sqlInfo = insertInfo;
        
        tableName = sqlInfo.fetchTableName();
        
        colFile = new File(tableName+".txt");
        
        this.value_pair = new HashMap<String, Object>();
        
        ArrayList info_list = sqlInfo.fetchInsertValue();
        if (sqlInfo.fetchInsertSequence().size() == 0) {
            ArrayList _list = this.parseColNameFile();
            Iterator _it = _list.iterator();
            Iterator _info_it = info_list.iterator();
            while (_it.hasNext()) {                
                Map<String,Object> _map = (Map<String,Object>)_it.next();
                if(_info_it.hasNext()){
                    this.value_pair.put(_map.get("Name").toString(),(_info_it.next()));
                }else{
                    this.value_pair.put(_map.get("Name").toString(),"null");
                }
            }
        }else{
            Iterator _it = sqlInfo.fetchInsertSequence().iterator();
            Iterator _info_it = info_list.iterator();
            while(_it.hasNext() && _info_it.hasNext()){
                this.value_pair.put(_it.next().toString(), _info_it.next());
            }
        }
        
        Iterator map_it = this.parseColNameFile().iterator();
//        while (map_it.hasNext()) {            
////            String name = ((Map<String,Object>)map_it.next()).get("Name").toString();
////            System.out.println(name+": "+this.value_pair.get(name).toString());
//        }
        
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
            hash = new HashMap<String,Object>();
        }
        
        
        
    }
    
    private void prepare(){
        
        //Unimplemented Method
        
       
    }
    
    
    public boolean exec() throws Exception{
        
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
            if(colInfo.size()!=colValue.size() && colSeq.size() == 0){
                    Exception Number_of_Value_not_match = new Exception("The number of input values doesn't match the number of the columns.");
                    System.out.println("ERROR: Value number incorrect!!!");
                    System.out.println("ColInfo="+colInfo.size()+" and inputValue="+colValue.size());
                    throw Number_of_Value_not_match;
//                    return false;
            }else if(colInfo.size()!=colValue.size() && colSeq.size() != 0){
//                System.out.print("need handle");
                ArrayList list = this.parseColNameFile();
                Iterator _it = list.iterator();
                while(_it.hasNext()){
                    Map _map = (Map) _it.next();
                    if((Boolean)_map.get("PRIMARY") == true){
                        if(this.value_pair.get(_map.get("Name")) == null){
                            System.out.println("ERROR");
                        }else{
                            if((_map.get("Type")).toString().equals("INT")&& this.value_pair.get(_map.get("Name")).getClass().toString().equals("class java.lang.Integer")){
                                
                            }else if(((_map.get("Type").toString()).equals("VARCHAR")||(_map.get("Type").toString()).equals("CHAR")) && !this.value_pair.get(_map.get("Name")).toString().equals("null")){
                                
                            }else{
                                Exception ex = new Exception("Primary key is null.");
                                throw ex;
                            }
                        }
                    }
                }
                hash.put(primaryKeySet.toString(),this.value_pair);
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
                    Exception Duplicated_Key_Excepiton = new Exception("Primary Key Duplicated.");
                     System.out.println("ERROR!! PRIMARY KEY DUPLICATED!!!!!!");
                     throw Duplicated_Key_Excepiton;
//                     return false;
                }else
                {
                     hash.put(primaryKeySet.toString(),this.value_pair);
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
    
    private void printSplitLine(int i){
        
        for(int a=0;a<i+sqlInfo.fetchInsertSequence().size()+1;a++){
            System.out.print("-");
        }
        
        System.out.println("");
        
    }
    
//    public void display(){
//        Set set = hash.entrySet();
//        Iterator it = set.iterator();
//        
//        while (it.hasNext()) {            
//            System.out.println(((Map.Entry)it.next()).getKey());
//        }
//    }
//    

    public void display(){
        
        ArrayList<Map<String,Object>> colInfo = parseColNameFile();
        
        ArrayList<String> colSeq = sqlInfo.fetchInsertSequence();
        if(colSeq.size() == 0){
            Iterator colInfo_it = colInfo.iterator();
            
            while (colInfo_it.hasNext()) {                
                Map<String,Object> info_map = (Map<String,Object>) colInfo_it.next();
                colSeq.add(info_map.get("Name").toString());
            }
        }
        
        ArrayList<Object> colValue = sqlInfo.fetchInsertValue();
        
        Iterator it;
        Map s;
        
        
        int i,len=0,j,sumlen=0;
        
        it = colInfo.iterator();
        while(it.hasNext()){
            s  = (Map)it.next();
            i = colSeq.indexOf(s.get("Name").toString());
            if((Integer)s.get("Quantity") != 0){
                len = ((Integer)s.get("Quantity")) +2 -s.get("Name").toString().length();
            }else{
                len = colValue.get(i).toString().length()-s.get("Name").toString().length();
            }
            if(len>0){
                sumlen += len;
            }else{
                sumlen+=s.get("Name").toString().length();
            }
            sumlen += 1;            
        }
        printSplitLine(sumlen);
        sumlen = 0;
        System.out.print("|");
        it = colInfo.iterator();
        while(it.hasNext()){
            
            s  = (Map)it.next();
            i = colSeq.indexOf(s.get("Name").toString());
            
            System.out.print(s.get("Name"));
            
                if((Integer)s.get("Quantity") != 0){
                    len = ((Integer)s.get("Quantity")) +2 -s.get("Name").toString().length();
                }else{
                    len = colValue.get(i).toString().length()-s.get("Name").toString().length();
                }
                
                
                if(len>0){
                    
//                    sumlen+=colValue.get(i).toString().length();
                    sumlen += len;
                    
                    for(j=0;j<len;j++){
                        System.out.print(" ");
                    }
                }else{
                    sumlen+=s.get("Name").toString().length();
                }
                sumlen += 1;
            
            
            System.out.print("|");
            
            
            
            
            
        }
        System.out.println("");
        
        printSplitLine(sumlen);
        /*  End of print Head*/
        
        Set set = hash.entrySet();
        Iterator tuple_it = set.iterator();
        while(tuple_it.hasNext()){
            Map<String,Object> tuple = (Map<String,Object>) ((Map.Entry)tuple_it.next()).getValue();
            Iterator colInfo_it = colInfo.iterator();
            System.out.print("|");
            while (colInfo_it.hasNext()) {                
                Map<String,Object> info_map = (Map<String,Object>) colInfo_it.next();
                System.out.print(tuple.get(info_map.get("Name")));
                if((Integer)info_map.get("Quantity") != 0){
                    len = tuple.get(info_map.get("Name")).toString().length()-2 - ((Integer)info_map.get("Quantity"));
                    len = Math.min(len, tuple.get(info_map.get("Name")).toString().length()-info_map.get("Name").toString().length());
                }else{
                    len = tuple.get(info_map.get("Name")).toString().length()-info_map.get("Name").toString().length();
                }
//                len = tuple.get(info_map.get("Name")).toString().length()-info_map.get("Name").toString().length();
                if(len<0){
                    len = -len;
                    for(j=0;j<len;j++){
                        System.out.print(" ");
                    }

                }
                System.out.print("|");
            }
            System.out.println("");
        }
        printSplitLine(sumlen);
        System.out.println();
    }
}

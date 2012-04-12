/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject1;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
/**
 *
 * @author bear
 */

public class SqlExecutionFactory {
    
    Object dataBaseHandler;
    private SqlStatementType sqlType;
    private String sqlIns;
    String[] sqlArgSplitted;
    String[] tableNameArray;
    String tableName;
    String sqlArg;
    boolean isPassed = true;
    BufferedReader reader;
    public SqlExecutionFactory(){
        
    }
    
    public SqlExecutionFactory(Object dataBaseHandler){
        this.dataBaseHandler = dataBaseHandler;
    }
    
    private boolean sqlDatatypeCheck(String type, String content){
        
        
        
        if(type.toLowerCase().indexOf("int")!=-1){
            if(!content.matches("[0123456789]+")){
                System.out.println(content+" is not an INTEGER!!");
                return false;
            }else{
                return true;
            }
            
        }else if(type.toLowerCase().indexOf("varchar")!=-1){
            int len = Integer.parseInt(type.substring(type.indexOf("(")+1,type.length()-1));
            if(len<content.length()){
                System.out.println("Content TOO LONG!!!");
                return false;
            }else{
                return true;
            }
            
        }else if(type.toLowerCase().indexOf("char")!=-1){
            int len = Integer.parseInt(type.substring(type.indexOf("(")+1,type.length()-1));
            if(len<content.length()){
                System.out.println("Content TOO LONG!!!"+" typelen="+len+"  content Len="+content.length()+" typeString="+type+" content="+content);
                return false;
            }else{
                return true;
            }
        }else{
            System.out.println("UNDEFINED DATATYPE "+type);
            return false;
        }
        
        
        
    }
    
    public void setInstruction(String instruc)
    {
        sqlIns = instruc;
        sqlType = SqlInstrucInterpreter.interpreteStatementType(instruc);
    }
    
    public void exeSql(){
        switch (sqlType){
            case CREATE:
                System.out.println("create"+" "+sqlIns);
               
                sqlArg = sqlIns.substring(sqlIns.indexOf("(")+1,sqlIns.length()-3); //Fetch Arguments of command CREATE
                //System.out.println(sqlArg);
                sqlArgSplitted = sqlArg.split(",");  //Split the arguments
                tableNameArray = sqlIns.split(" ");
                File saveFile = new File(tableNameArray[2]+".txt");
                try {
                     FileWriter columnNameWriter = new FileWriter(saveFile);
                     //columnNameWriter.write(sqlArg+";");
                     
                     for(int i=0;i<sqlArgSplitted.length;i++){
                         
                         //sqlArgSplitted[i].replace(" ",":");
                         
                         System.out.println(sqlArgSplitted[i]);
                         columnNameWriter.write(sqlArgSplitted[i]+";");
                         
                     }
                     
                     columnNameWriter.close();
                    } 
                catch (IOException ex) {
                     Logger.getLogger(SqlExecutionFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                
                break;
            case INSERT:
                System.out.println("insert "+sqlIns);
                
                
                //int colNum;
                tableNameArray = sqlIns.split(" ");
                tableName = tableNameArray[2];
                //boolean checkPass = false;
                StringBuilder colContent = new StringBuilder();
                String readLine = new String();
                String content = new String();
                try{
                    System.out.println(tableName+".txt");
                    FileInputStream inputStream = new FileInputStream(tableName+".txt");
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    while( (readLine = reader.readLine())!=null){
                        
                        
                        
                        if(readLine.length()>0){
                            colContent.append(readLine).append(" ");
                            
                        }
                    }
                    content = colContent.toString();
                    System.out.println("content="+content);
                
                } catch (Exception ex) {
                    Logger.getLogger(SqlExecutionFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                try {
                    FileWriter contentWriter = new FileWriter(tableName+"_content"+".txt");
                    //content.replaceAll(",", " ");
                    content.replaceAll("\\s+"," ");
                    System.out.println("content2="+content);
                    String[] contentSeg = content.split(";");
                    String[] dataTypeArray = new String[contentSeg.length];
                    String[] ripSeg;
                    for(int i=0;i<contentSeg.length;i++){
                      //  System.out.println(contentSeg[i]);
                    
                        ripSeg = contentSeg[i].split("\\s+");
                      //  System.out.println(ripSeg.length);
                        if(ripSeg.length<2){
                            break;
                        }
                        dataTypeArray[i] = ripSeg[2];
                        
                        System.out.println("a="+dataTypeArray[i]);
                        
                        
                    }
                    if("values".equals(tableNameArray[3].toLowerCase())){
                        String sqlArg = sqlIns.substring(sqlIns.indexOf("(")+1,sqlIns.length()-3);
                        
                        String[] sqlArgSplitted = sqlArg.split(",");
                        
                        for(int i=0;i<sqlArgSplitted.length;i++){
                            sqlArgSplitted[i] = sqlArgSplitted[i].trim();
                            sqlArgSplitted[i] = sqlArgSplitted[i].replaceAll("‘","");
                            sqlArgSplitted[i] = sqlArgSplitted[i].replaceAll("’","");
                            
                            if(sqlArgSplitted.length>dataTypeArray.length){
                                System.out.println("ERROR! TOO MANY ARGUMENTS!!");
                                break;
                            }
                            
                            if(!sqlDatatypeCheck(dataTypeArray[i], sqlArgSplitted[i])){
                                System.out.println("INSERT FAILED!!");
                                isPassed = false;
                                break;
                            }
                            
                        }
                        
                       
                        if(isPassed){
                            contentWriter.write(sqlArg);
                            System.out.println("INSERT SUCCESS "+sqlArg); 
                        }
                        contentWriter.close();
                        //sqlDatatypeCheck(dataType, sqlArgSplitted);
                        
                        
                        
                        
                    }else if(tableNameArray[3].matches("^(.+)$")){
                        
                    }
                    
                    
                      
                    
                } catch (IOException ex) {
                    Logger.getLogger(SqlExecutionFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                
                
                
                
                break;
            default:
                System.out.println("no");
                break;
        }
    }
    
}

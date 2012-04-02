/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject1;
import java.io.*;

/**
 *
 * @author bear
 */
public class SqlDataReader {
    
    private String dataPath;
    BufferedReader reader;
    
    
    
    public SqlDataReader(String dataPath){
        this.dataPath = dataPath;
    }
    
    public void openReader(){
        try {
            FileInputStream inputStream = new FileInputStream(dataPath);
            reader = new BufferedReader(new InputStreamReader(inputStream));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public String readNextInstruc(){
        String line = new String();
        StringBuffer instruc = new StringBuffer();
        try {
            while ((line = reader.readLine())!= null) {                
                if(line.length() > 0){
                    instruc.append(line+" ");
                    if(line.endsWith(";") == true){
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error");
        }
        return instruc.toString();
    }
}

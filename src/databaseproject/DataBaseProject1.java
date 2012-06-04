/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;

import DataStructure.SqlBTreeData;
import SqlReader.SqlDataReader;
import SqlReader.SqlFileDataReader;
import SqlContentFileManipulation.FileScanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import SqlInstructionFetcher.SqlSelectFetcher;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import model.SelectColumn;
import model.SelectWhere;

/**
 *
 * @author bear
 */
//Test
//Test2
public class DataBaseProject1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /*
         * NOTICE:
         * The end of the reading SQL file is that 
         * if the reader read the null value.
         */
        System.out.println("Begin Main Function");
        SqlDataReader reader = new SqlFileDataReader("src/Resource/data.in");
        SqlExecutionFactory factory = new SqlExecutionFactory();
        
        
           //   This is a test program
                    SqlBTreeData Btree = new SqlBTreeData("AUTHOR", "AUTHORID");
                    ArrayList<Object> result = Btree.get("<", 22);
                    Iterator it = result.iterator();
                    while(it.hasNext())
                    {
                        Object obj = it.next();
                        if(obj instanceof Integer)
                        {
                            System.out.printf("ID = %d\n",((Integer)obj).intValue());
                        }else
                        {
                            System.out.printf("ID = %s\n",obj.toString());
                        }
                    }
           //   End test program
                    
                    
        /*reader.openReader();
        String instruc = new String();
        while(!(instruc = reader.readNextInstruc()).equals("")){
            factory.setInstruction(instruc);
            try {
                factory.exeSql();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }*/
    }
}

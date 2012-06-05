/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;

import DataStructure.SqlBTreeData;
import DataStructure.SqlHashMapData;
import SqlReader.SqlDataReader;
import SqlReader.SqlFileDataReader;
import SqlContentFileManipulation.FileScanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import SqlInstructionFetcher.SqlSelectFetcher;
import SqlManipulation.SqlSelectTableExec_BTree;
import SqlManipulation.SqlSelectTableExec_HashTable;
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
        
        //TEST BLOCK
        //SqlHashMapData sqlHashData = new SqlHashMapData();
        
        
        
        //System.out.println("44");
        
        
        //SqlSelectFetcher fetch = new SqlSelectFetcher("SELECT SUM(B.BOOKID) FROM BOOK AS B,AUTHOR AS A WHERE   400 > B.PAGES AND A.AUTHORID < 3");
        //SqlSelectTableExec_BTree bt = new SqlSelectTableExec_BTree(fetch);
        //bt.exec();
        //bt.display();
//        SqlBTreeData Btree = new SqlBTreeData("AUTHOR", "AUTHORID");
//        ArrayList<Object> result = Btree.get("<", 3);
//        System.out.println("fin test");
        /*
         * NOTICE:
         * The end of the reading SQL file is that 
         * if the reader read the null value.
         */
        System.out.println("Begin Main Function");
        SqlDataReader reader = new SqlFileDataReader("src/Resource/data2.in");
        SqlExecutionFactory factory = new SqlExecutionFactory();
        
//        String table = "BOOK";
//                    String column = "TITLE";
//                    SqlBTreeData Btree = new SqlBTreeData(table, column);
//                    ArrayList<Object> result = Btree.get( "=", "BIBLE");
//                    Iterator it = result.iterator();
//                    while(it.hasNext())
//                    {
//                        Object obj = it.next();
//                        int ID = Integer.parseInt((String)obj);
//                        System.out.printf("ID = %d\n",ID);
//                    }
        reader.openReader();
        String instruc = new String();
        while(!(instruc = reader.readNextInstruc()).equals("")){
            factory.setInstruction(instruc);
            try {
                factory.exeSql();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
}

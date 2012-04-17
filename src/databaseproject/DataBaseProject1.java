/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;

import SqlReader.SqlDataReader;
import SqlReader.SqlFileDataReader;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        System.out.println("Start");
        /*
         * NOTICE:
         * The end of the reading SQL file is that 
         * if the reader read the null value.
         */
        SqlDataReader reader = new SqlFileDataReader("src/Resource/data.in");
        reader.openReader();
        SqlExecutionFactory factory = new SqlExecutionFactory();
        factory.setInstruction(reader.readNextInstruc());
        try {
            factory.exeSql();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        factory.setInstruction(reader.readNextInstruc());
        try {
            factory.exeSql();
        } catch (Exception ex) {
            System.out.print("error");
        }
    }
}

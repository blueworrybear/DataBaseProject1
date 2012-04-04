/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject1;

import databaseproject1.SqlFilter.SqlInstrucFilter;
import databaseproject1.SqlFilter.SqlInstrucBackSlashFilter;
import databaseproject1.SqlFilter.SqlInstrucWhiteSpaceFilter;
import databaseproject1.SqlFilter.SqlInstruction;

/**
 *
 * @author bear
 */
public class DataBaseProject1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Start");
        SqlDataReader reader = new SqlFileDataReader("src/Resource/data.in");
        reader.openReader();
        SqlInstruction ins = new SqlInstruction(reader.readNextInstruc());
        SqlInstrucFilter filtWhite = new SqlInstrucWhiteSpaceFilter();
        SqlInstrucFilter filtSplash = new SqlInstrucBackSlashFilter();
        filtWhite.setFilter(ins);
        filtSplash.setFilter(filtWhite);
        System.out.println(filtSplash.filter());
//        System.out.println(reader.readNextInstruc());
        SqlExecutionFactory factory = new SqlExecutionFactory();
        factory.setInstruction(reader.readNextInstruc());
        factory.exeSql();
        factory.setInstruction(reader.readNextInstruc());
        factory.exeSql();
    }
}

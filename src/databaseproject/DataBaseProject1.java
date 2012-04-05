/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;

import SqlFilter.SqlInstrucFilter;
import SqlFilter.SqlInstrucBackSlashFilter;
import SqlFilter.SqlInstrucWhiteSpaceFilter;
import SqlFilter.SqlInstruction;
import SqlInstructionFetcher.SqlCreateFetcher;

/**
 *
 * @author bear
 */
//Test
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
        SqlCreateFetcher fetcher = new SqlCreateFetcher(filtSplash.filter());
        System.out.println(fetcher.fetchCommandString());
        System.out.println(fetcher.fetchClause());
        //        System.out.println(reader.readNextInstruc());
        SqlExecutionFactory factory = new SqlExecutionFactory();
        factory.setInstruction(reader.readNextInstruc());
        factory.exeSql();
        factory.setInstruction(reader.readNextInstruc());
        factory.exeSql();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;

import SqlReader.SqlFileDataReader;
import SqlReader.SqlDataReader;
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
//Test2
public class DataBaseProject1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Start");
        SqlDataReader reader = new SqlFileDataReader("src/Resource/data.in");
        reader.openReader();
        SqlExecutionFactory factory = new SqlExecutionFactory();
        factory.setInstruction(reader.readNextInstruc());
        factory.exeSql();
        factory.setInstruction(reader.readNextInstruc());
        factory.exeSql();
    }
}

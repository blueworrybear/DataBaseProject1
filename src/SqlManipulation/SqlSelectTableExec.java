/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlManipulation;

import SqlInstructionFetcher.SqlSelectFetcher;
import databaseproject.SqlExecutionFactory;
import java.util.*;
import model.SelectColumn;
import model.SelectWhere;

/**
 *
 * @author loe800210
 */
public class SqlSelectTableExec {
    
    SqlSelectFetcher selectFetcher;
    ArrayList<SelectColumn> selColumn;
    ArrayList<String> fromTable;
    ArrayList<SelectWhere> whereClause;
    HashMap<String, ArrayList<Object>> outputTable;
    int count;
    
    public SqlSelectTableExec(SqlSelectFetcher selectFetcher)
    {
        this.selectFetcher = selectFetcher;
        selColumn = this.selectFetcher.fetchColumns();
        fromTable = this.selectFetcher.fetchFromExpressions();
        whereClause = this.selectFetcher.fetchWhereExpressions();
        outputTable = new HashMap<String, ArrayList<Object>>();
        count = 0;
    }
    
    public String operandType(String tableName, String colName)
    {
        ArrayList<Map<String, Object>> colInfo;
        String type = "";
        
        colInfo = SqlColNameFileParser.parseColNameFile(tableName);
        Iterator it = colInfo.iterator();
        while(it.hasNext())
        {
              Map<String, Object> col = (Map<String, Object>)it.next();
              if( col.get("Name").toString().equals(colName) )
              {
                    type = col.get("Type").toString();
                    return type;
              }
        }
        return type;
        
    }
    
    public boolean isTableInFrom(String tableName)
    {
        if( this.fromTable != null )
        {
            Iterator itr = this.fromTable.iterator();
            while(itr.hasNext())
            {
                String name = (String)itr.next();
                if( name.equals(tableName) )
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isColInTable(String tableName, String colName)
    {
        ArrayList<Map<String, Object>> colInfo;
        colInfo = SqlColNameFileParser.parseColNameFile(tableName);
        Iterator it = colInfo.iterator();
        while(it.hasNext())
        {
             Map<String, Object> col = (Map<String, Object>)it.next();
             if( col.get("Name").toString().equals(colName) )
             {
                   return true;
             }
        }
        return false;
    }
    
    public boolean checkColTable(String tableName, String column)
    {
        if( !this.isTableInFrom(tableName) )
        {
            System.out.println("Syntex error : Table \""+tableName+"\" does not exist after From clause.");
            return false;
        }
        if( !this.isColInTable(tableName, column))
        {
            System.out.println("Syntex error : Column \""+tableName+"\" does not exist in the table \""+column+"\".");
            return false;
        }
        return true;
    }
    
    public boolean checkSelectLogic()
    {   
        if( this.fromTable == null )
        {
            System.out.println("Syntex error : Cannot do SELECT while no table existing.");
            return false;
        }
        
        int aggCount = 0;
        
        Iterator it = this.selColumn.iterator();
        while(it.hasNext())
        {
            SelectColumn col = (SelectColumn)it.next();
            
            if( col.getAggregation()== null )
            {
                if( !this.checkColTable(col.getTable(), col.getColumn()) )
                {
                    return false;
                }
                
            }else
            {
                if( col.getAggregation().equals("SUM") )
                {
                    if( col.getColumn().equals("*") )
                    {
                        System.out.println("Syntex error : \"*\" cannot in SUM aggregation.");
                        return false;
                    }else 
                    {
                        if( !this.checkColTable(col.getTable(), col.getColumn()) )
                        {
                            return false;
                        }
                        if( !this.operandType(col.getTable(), col.getColumn()).equals("INT") )
                        {
                            System.out.println("Syntex error : The type of column \""+col.getColumn()+"\" is not integer(In the SUM aggregation function).");
                            return false;
                        }
                    }
                }else
                {
                    if( !col.getColumn().equals("*") )
                    {
                        if( !this.checkColTable(col.getTable(), col.getColumn()) )
                        {
                            return false;
                        }
                    }
                }
                
                aggCount++;
            }
        }
        
        if( aggCount!=0 && aggCount != this.selColumn.size() )
        {
            System.out.println("Logic error : Set cannot be combined with aggregation function.");
            return false;
        }
        
        return true;
    }
    
    public boolean checkFromLogic()
    {
        
        if( this.fromTable != null)
        {
            Iterator it = this.fromTable.iterator();
            while(it.hasNext())
            {
                String table = (String)it.next();
                if( SqlExecutionFactory.dataRecord.getHashTable(table) == null )
                {
                    System.out.println("Syntex error : Table \""+table+"\" is not exist.");
                    return false;
                }
            }
            return true;
        }else
        {
            System.out.println("Syntex error : No table exists after FROM clause.");
            return false;
        }
        
    }
    
    
    public boolean checkWhereLogic(int num)
    {
        if( this.whereClause.get(num).get_operand1_is_integer() )
        {
            if( !this.checkColTable(this.whereClause.get(num).get_operand2_tableName(), this.whereClause.get(num).get_operand2_column()) )
            {
                return false;
            }
            if( !this.operandType(this.whereClause.get(num).get_operand2_tableName(), this.whereClause.get(num).get_operand2_column()).equals("INT"))
            {
                System.out.println("Syntex error : Incompatible type of Integer comparing with String.");
                return false;
            }
        }else if( this.whereClause.get(num).get_operand2_is_integer() )
        {
            if( !this.checkColTable(this.whereClause.get(num).get_operand1_tableName(), this.whereClause.get(num).get_operand1_column()) )
            {
                return false;
            }
            if( !this.operandType(this.whereClause.get(num).get_operand1_tableName(), this.whereClause.get(num).get_operand1_column()).equals("INT") )
            {
                System.out.println("Syntex error : Incompatible type of Integer comparing with String.");
                return false;
            }
        }else if( this.whereClause.get(num).get_operand1_tableName() == null )
        {
            if( !this.checkColTable(this.whereClause.get(num).get_operand2_tableName(), this.whereClause.get(num).get_operand2_column()) )
            {
                return false;
            }
            if( this.operandType(this.whereClause.get(num).get_operand2_tableName(), this.whereClause.get(num).get_operand2_column()).equals("INT") )
            {
                System.out.println("Syntex error : Incompatible type of Integer comparing with String.");
                return false;
            }
        }else if( this.whereClause.get(num).get_operand2_tableName() == null )
        {
            if( !this.checkColTable(this.whereClause.get(num).get_operand1_tableName(), this.whereClause.get(num).get_operand1_column()) )
            {
                return false;
            }
            if( this.operandType(this.whereClause.get(num).get_operand1_tableName(), this.whereClause.get(num).get_operand1_column()).equals("INT") )
            {
                System.out.println("Syntex error : Incompatible type of Integer comparing with String.");
                return false;
            }
        }else
        {
            if( !this.checkColTable(this.whereClause.get(num).get_operand1_tableName(), this.whereClause.get(num).get_operand1_column()) )
            {
                return false;
            }
            if( !this.checkColTable(this.whereClause.get(num).get_operand2_tableName(), this.whereClause.get(num).get_operand2_column()) )
            {
                return false;
            }
            if(    (     this.operandType(this.whereClause.get(num).get_operand1_tableName(), this.whereClause.get(num).get_operand1_column()).equals("INT")
                      && this.operandType(this.whereClause.get(num).get_operand2_tableName(), this.whereClause.get(num).get_operand2_column()).equals("INT") )
                || (     !this.operandType(this.whereClause.get(num).get_operand1_tableName(), this.whereClause.get(num).get_operand1_column()).equals("INT")
                      && !this.operandType(this.whereClause.get(num).get_operand2_tableName(), this.whereClause.get(num).get_operand2_column()).equals("INT")  )   )
            {
                return true;
            }else
            {
                System.out.println("Syntex error : Incompatible type of Integer comparing with String.");
                return false;
            }
        }
        return true;
    }
    
    public boolean checkSyntex()
    {
        if( checkFromLogic() && checkSelectLogic())
        {
            if( this.whereClause != null )
            {
                if( this.whereClause.size() == 1 )
                {
                    if( !checkWhereLogic(0) )
                    {
                        return false;
                    }
                }else if( this.whereClause.size() == 2 )
                {
                    if( !checkWhereLogic(0) )
                    {
                        return false;
                    }
                    if( !checkWhereLogic(1) )
                    {
                        return false;
                    }
                }
            }
            return true;
        }else
        {
            return false;
        }
    }
    
    public ArrayList<Object> table(String tableName)
    {
        ArrayList<Object> tableList = new ArrayList<Object>();
        Iterator itr = SqlExecutionFactory.dataRecord.getHashTable(tableName).entrySet().iterator();
        while(itr.hasNext())
        {
              Map.Entry<String, Object> tuple = (Map.Entry<String, Object>)itr.next();
              tableList.add(tuple.getValue());
        }
        return tableList;
    }
    
    public boolean booleanExp(SelectWhere clause, Map<String, Object> tuple)
    {
        String operator = clause.get_operator();
        
        if( clause.get_operand1_is_integer() )
        {
            int op1 = Integer.parseInt(clause.get_operand1_column());
            int op2 = Integer.parseInt((String)( (Map<String, Object>)( tuple.get(clause.get_operand2_tableName()) ) ).get(clause.get_operand2_column()));
            
            if( (operator.equals("=") && op1==op2) || (operator.equals(">") && op1>op2) || (operator.equals("<") && op1<op2) )
            {
                return true;
            }
            
        }else if( clause.get_operand2_is_integer() )
        {
            int op1 = Integer.parseInt((String)( (Map<String, Object>)( tuple.get(clause.get_operand1_tableName()) ) ).get(clause.get_operand1_column()));
            int op2 = Integer.parseInt(clause.get_operand2_column());
            if( (operator.equals("=") && op1==op2) || (operator.equals(">") && op1>op2) || (operator.equals("<") && op1<op2) )
            {
                return true;
            }
        }else if( clause.get_operand1_tableName() == null )
        {
            String op1 = clause.get_operand1_column();
            String op2 = ( (Map<String, Object>)( tuple.get(clause.get_operand2_tableName()) ) ).get(clause.get_operand2_column()).toString();
            if( op1.equals(op2) )
            {
                return true;
            }
        }else if( clause.get_operand2_tableName() == null )
        {
            String op1 = ( (Map<String, Object>)( tuple.get(clause.get_operand1_tableName()) ) ).get(clause.get_operand1_column()).toString();
            String op2 = clause.get_operand2_column();
            if( op1.equals(op2) )
            {
                return true;
            }
        }else
        {
            if( operandType(clause.get_operand1_tableName(), clause.get_operand1_column()).equals("INT") )
            {
                int op1 = (Integer)( (Map<String, Object>)( tuple.get(clause.get_operand1_tableName()) ) ).get(clause.get_operand1_column());
                int op2 = (Integer)( (Map<String, Object>)( tuple.get(clause.get_operand2_tableName()) ) ).get(clause.get_operand2_column());
                
                if( (operator.equals("=") && op1==op2) || (operator.equals(">") && op1>op2) || (operator.equals("<") && op1<op2) )
                {
                    return true;
                }
            }else
            {
                String op1 = ( (Map<String, Object>)( tuple.get(clause.get_operand1_tableName()) ) ).get(clause.get_operand1_column()).toString();
                String op2 = ( (Map<String, Object>)( tuple.get(clause.get_operand2_tableName()) ) ).get(clause.get_operand2_column()).toString();
                
                if( op1.equals(op2) )
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public void sumAgg(Map<String, Object> tupleHash)
    {
        for(int i=0;i<this.selColumn.size();i++)
        {
            if(  this.selColumn.get(i).getAggregation().equals("SUM") )
            {
                if( tupleHash.get(this.selColumn.get(i).getTable()) != null )
                {
                    Map<String, Object> tuple = (Map<String, Object>)tupleHash.get(this.selColumn.get(i).getTable());
                    int colValue = Integer.parseInt((String)tuple.get(this.selColumn.get(i).getColumn()));
                    this.selColumn.get(i).addSum(colValue);
                }
            }
        }
        
    }
    
    public boolean exec()
    {   
        if( !checkSyntex() )
        {
            return false;
        }
        
        ArrayList<Object> tableList1;
        ArrayList<Object> tableList2;
        ArrayList<Object> outcome1 = new ArrayList<Object>();
        ArrayList<Object> outcome2 = new ArrayList<Object>();
        
        if( this.fromTable.size() == 1 )   // Table number = 1
        {
            tableList1 = this.table(this.fromTable.get(0));
            Map tuple = new HashMap<String, Object>();
            for(int i=0;i<tableList1.size();i++)
            {
                tuple.put(this.fromTable.get(0), tableList1.get(i));
                    
                if( this.whereClause!= null )
                {
                    if( selectFetcher.fetchBooleanFunction().equals("AND") &&
                        ( booleanExp(this.whereClause.get(0), tuple) && booleanExp(this.whereClause.get(1), tuple) ) )   //  AND
                    {
                        outcome1.add(tableList1.get(i));
                        this.count++;
                        this.sumAgg(tuple);
                    }else if( selectFetcher.fetchBooleanFunction().equals("OR") &&
                        ( booleanExp(this.whereClause.get(0), tuple) || booleanExp(this.whereClause.get(1), tuple) ) )   // OR
                    {
                        outcome1.add(tableList1.get(i));
                        this.count++;
                        this.sumAgg(tuple);
                    }else if( selectFetcher.fetchBooleanFunction().equals("") && booleanExp(this.whereClause.get(0), tuple) )
                    {
                        outcome1.add(tableList1.get(i));
                        this.count++;
                        this.sumAgg(tuple);
                    }
                }else
                {
                    outcome1.add(tableList1.get(i));
                    this.count++;
                    this.sumAgg(tuple);
                }
                    
                tuple.remove(this.fromTable.get(0));
            }
            outputTable.put(this.fromTable.get(0), outcome1);
            
        }else if( this.fromTable.size() == 2 )   // Table number = 2
        {
            tableList1 = this.table(this.fromTable.get(0));
            tableList2 = this.table(this.fromTable.get(1));
            Map tuple = new HashMap<String, Object>();
            
            for(int i=0;i<tableList1.size();i++)
            {
                tuple.put(this.fromTable.get(0), tableList1.get(i));
                
                for(int j=0;j<tableList2.size();j++)
                {
                    tuple.put(this.fromTable.get(1), tableList2.get(j));
                        
                    if( this.whereClause!= null )
                    {
                        if( selectFetcher.fetchBooleanFunction().equals("AND") &&
                            ( booleanExp(this.whereClause.get(0), tuple) && booleanExp(this.whereClause.get(1), tuple) ) )  // AND
                        {
                            outcome1.add(tableList1.get(i));
                            outcome2.add(tableList2.get(j));
                            this.count++;
                            this.sumAgg(tuple);
                        }else if( selectFetcher.fetchBooleanFunction().equals("OR") && 
                            ( booleanExp(this.whereClause.get(0), tuple) || booleanExp(this.whereClause.get(1), tuple) ) )   // OR
                        {
                            outcome1.add(tableList1.get(i));
                            outcome2.add(tableList2.get(j));
                            this.count++;
                            this.sumAgg(tuple);
                        }else if( selectFetcher.fetchBooleanFunction().equals("") && booleanExp(this.whereClause.get(0), tuple) )
                        {
                            outcome1.add(tableList1.get(i));
                            outcome2.add(tableList2.get(j));
                            this.count++;
                            this.sumAgg(tuple);
                        }
                    }else
                    {
                        outcome1.add(tableList1.get(i));
                        outcome2.add(tableList2.get(j));
                        this.count++;
                        this.sumAgg(tuple);
                    }
                    
                    tuple.remove(this.fromTable.get(1));
                }
                tuple.remove(this.fromTable.get(0));
            }
            
            outputTable.put(this.fromTable.get(0), outcome1);
            outputTable.put(this.fromTable.get(1), outcome2);
            
        }
        
        return true;
    }
    
    public void display()
    {
        
        if( this.selColumn.get(0).getAggregation().equals("") )
        {
            if( !this.selColumn.get(0).getColumn().equals("*") )
            {
                for(int row=0;row<this.count;row++)
                {
                    for(int col=0;col<this.selColumn.size();col++)
                    {
                        String tableName = this.selColumn.get(col).getTable();
                        String colName = this.selColumn.get(col).getColumn();
        
                        String value = ((Map<String, Object>)outputTable.get(tableName).get(row)).get(colName).toString();
                
                        System.out.printf(" %s ",value);
                    }
                    System.out.printf("\n");
                }
            }else
            {
                HashMap<String, ArrayList<String>> colHash = new HashMap<String, ArrayList<String>>();
               
                for(int i=0;i<this.fromTable.size();i++)
                {
                    ArrayList<String> colSet = new ArrayList<String>();
                    ArrayList<Map<String, Object>> colInfo = SqlColNameFileParser.parseColNameFile(this.fromTable.get(i));
                    
                    Iterator it = colInfo.iterator();
                    while(it.hasNext())
                    {
                        Map<String, Object> col = (Map<String, Object>)it.next();
                        String colName = col.get("Name").toString();
                        colSet.add(colName);
                    }
                    colHash.put(this.fromTable.get(i), colSet);
                }
                
                for(int row=0;row<this.count;row++)
                {
                    for(int i=0;i<this.fromTable.size();i++)
                    {
                        ArrayList<String> colName = colHash.get(this.fromTable.get(i));
                        for(int col=0;col<colName.size();col++)
                        {
                            String value = ((Map<String, Object>)outputTable.get(this.fromTable.get(i)).get(row)).get(colName.get(col)).toString();
                            
                            System.out.print(value+"  ");
                        }
                        System.out.print("-----");
                    }
                    System.out.println();
                }
            }
        }else
        {
            for(int i=0;i<this.selColumn.size();i++)
            {
                if( this.selColumn.get(i).getAggregation().equals("SUM") )
                {
                    System.out.print(this.selColumn.get(i).getSum()+" ");
                }else if( this.selColumn.get(i).getAggregation().equals("COUNT") )
                {
                    System.out.print(this.count+" ");
                }
            }
        }
        
        
    }
}

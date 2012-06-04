/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructure;

import DataStructure.BPlusTree.Node;
import SqlManipulation.SqlColNameFileParser;
import databaseproject.SqlExecutionFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
/**
 *
 * @author loe800210
 */
public class SqlBTreeData
{

    BPlusTree tree;
    String keyType;
    
    public SqlBTreeData(String tableName, String columnName)
    {
        this.createBPlusTree(tableName, columnName);
        System.out.println("The B+tree's height is "+this.tree.getHeight()+".");
    }
    
    private void createBPlusTree(String tableName, String columnName)
    {
        Object objKey, objValue;
        
        Map map = SqlColNameFileParser.parseColType(tableName);
        Iterator itr = SqlExecutionFactory.dataRecord.getHashTable(tableName).entrySet().iterator();
        
        
        if(map.get(columnName).equals("Integer") && map.get("primary").equals("Integer"))
        {
            keyType = "Integer";
            this.tree = new BPlusTree<Integer, Integer>();
            
            while(itr.hasNext())
            {
                Map.Entry<String, Object> tuple = (Map.Entry<String, Object>)itr.next();
                objKey = ((Map<String, Object>)tuple.getValue()).get(columnName);
                objValue = tuple.getKey();
                //System.out.println(Integer.parseInt((String)objKey));
                this.tree.put(Integer.parseInt((String)objKey), Integer.parseInt((String)objValue));
            }
            
        }else if(map.get(columnName).equals("Integer") && map.get("primary").equals("String"))
        {
            keyType = "Integer";
            tree = new BPlusTree<Integer, String>();
            
            while(itr.hasNext())
            {
                Map.Entry<String, Object> tuple = (Map.Entry<String, Object>)itr.next();
                objKey = ((Map<String, Object>)tuple.getValue()).get(columnName);
                objValue = tuple.getKey();
                
                this.tree.put(Integer.parseInt((String)objKey), objValue.toString());
            }
            
        }else if(map.get(columnName).equals("String") && map.get("primary").equals("Integer"))
        {
            keyType = "String";
            tree = new BPlusTree<String, Integer>();
            
            while(itr.hasNext())
            {
                Map.Entry<String, Object> tuple = (Map.Entry<String, Object>)itr.next();
                objKey = ((Map<String, Object>)tuple.getValue()).get(columnName);
                objValue = tuple.getKey();
                
                this.tree.put(objKey.toString(), Integer.parseInt((String)objValue));
            }
            
        }else if(map.get(columnName).equals("String") && map.get("primary").equals("String"))
        {
            keyType = "String";
            tree = new BPlusTree<String, String>();
            
            while(itr.hasNext())
            {
                Map.Entry<String, Object> tuple = (Map.Entry<String, Object>)itr.next();
                objKey = ((Map<String, Object>)tuple.getValue()).get(columnName);
                objValue = tuple.getKey();
                
                this.tree.put(objKey.toString(), objValue.toString());
            }
        }
        
    }
    
    public ArrayList<Object> get(String operator, Object key)
    {
        if( keyType.equals("Integer") )
        {
            ArrayList<Object> answerSet = new ArrayList<Object>();
            Node node = this.tree.getRange(((Integer)key).intValue());
            
            int count = 0;
        
            if( operator.equals("<") )
            {
                if(node == null)
                {
                    
                }
                while(node != null)
                {
                    System.out.println("The finding node has "+node.numberOfKeys()+" keys.");
                    if(count == 0)
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            if( tree.less(node.getEntry(i).getKey(), ((Integer)key).intValue()) )
                            {
                                answerSet.addAll(node.getEntry(i).getPrimaryKey());
                            }
                        }
                    }else
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            answerSet.addAll(node.getEntry(i).getPrimaryKey());
                        }
                    }
                    node = node.getFront();
                    count++;
                }
            }else if( operator.equals("<=") )
            {
                while(node != null)
                {
                    if(count == 0)
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            if( tree.less(node.getEntry(i).getKey(), ((Integer)key).intValue()) || tree.equal(node.getEntry(i).getKey(), ((Integer)key).intValue()) )
                            {
                                answerSet.addAll(node.getEntry(i).getPrimaryKey());
                            }
                        }
                    }else
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            answerSet.addAll(node.getEntry(i).getPrimaryKey());
                        }
                    }
                    node = node.getFront();
                    count++;
                }
            }else if( operator.equals(">") )
            {
                while(node != null)
                {
                    if(count == 0)
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            if( tree.less(((Integer)key).intValue(), node.getEntry(i).getKey()) )
                            {
                                answerSet.addAll(node.getEntry(i).getPrimaryKey());
                            }
                        }
                    }else
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            answerSet.addAll(node.getEntry(i).getPrimaryKey());
                        }
                    }
                    node = node.getNext();
                    count++;
                }
            }else if( operator.equals(">=") )
            {
                while(node != null)
                {
                    if(count == 0)
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            if( tree.less(((Integer)key).intValue(), node.getEntry(i).getKey()) || tree.equal(((Integer)key).intValue(), node.getEntry(i).getKey()) )
                            {
                                answerSet.addAll(node.getEntry(i).getPrimaryKey());
                            }
                        }
                    }else
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            answerSet.addAll(node.getEntry(i).getPrimaryKey());
                        }
                    }
                    node = node.getNext();
                    count++;
                }
            }else if( operator.equals("=") )
            {
                for(int i=0;i<node.numberOfKeys();i++)
                {
                    if( tree.equal(node.getEntry(i).getKey(), ((Integer)key).intValue()) )
                    {
                        answerSet.addAll(node.getEntry(i).getPrimaryKey());
                    }
                }
            }
            return answerSet;
            
        }else
        {
            ArrayList<Object> answerSet = new ArrayList<Object>();
            Node node = this.tree.getRange(key.toString());
            int count = 0;
        
            if( operator.equals("<") )
            {
                while(node != null)
                {
                    if(count == 0)
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            if( tree.less(node.getEntry(i).getKey(), key.toString()) )
                            {
                                answerSet.addAll(node.getEntry(i).getPrimaryKey());
                            }
                        }
                    }else
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            answerSet.addAll(node.getEntry(i).getPrimaryKey());
                        }
                    }
                    node = node.getFront();
                    count++;
                }
            }else if( operator.equals("<=") )
            {
                while(node != null)
                {
                    if(count == 0)
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            if( tree.less(node.getEntry(i).getKey(), key.toString()) || tree.equal(node.getEntry(i).getKey(), key.toString()))
                            {
                                answerSet.addAll(node.getEntry(i).getPrimaryKey());
                            }
                        }
                    }else
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            answerSet.addAll(node.getEntry(i).getPrimaryKey());
                        }
                    }
                    node = node.getFront();
                    count++;
                }
            }else if( operator.equals(">") )
            {
                while(node != null)
                {
                    if(count == 0)
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            if( tree.less(key.toString(), node.getEntry(i).getKey()) )
                            {
                                answerSet.addAll(node.getEntry(i).getPrimaryKey());
                            }
                        }
                    }else
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            answerSet.addAll(node.getEntry(i).getPrimaryKey());
                        }
                    }
                    node = node.getNext();
                    count++;
                }
            }else if( operator.equals(">=") )
            {
                while(node != null)
                {
                    if(count == 0)
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            if( tree.less(key.toString(), node.getEntry(i).getKey()) || tree.equal(key.toString(), node.getEntry(i).getKey()) )
                            {
                                answerSet.addAll(node.getEntry(i).getPrimaryKey());
                            }
                        }
                    }else
                    {
                        for(int i=0;i<node.numberOfKeys();i++)
                        {
                            answerSet.addAll(node.getEntry(i).getPrimaryKey());
                        }
                    }
                    node = node.getNext();
                    count++;
                }
            }else if( operator.equals("=") )
            {
                for(int i=0;i<node.numberOfKeys();i++)
                {
                    if( tree.equal(node.getEntry(i).getKey(), key.toString()) )
                    {
                        answerSet.addAll(node.getEntry(i).getPrimaryKey());
                    }
                }
            }
            return answerSet;
        }
    }  
    
}

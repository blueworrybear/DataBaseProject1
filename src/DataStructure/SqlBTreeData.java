/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructure;

import DataStructure.BPlusTree.Node;
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
    String primaryType;
    
    public SqlBTreeData(String tableName, String columnName)
    {
        this.setType(tableName, columnName);
        this.createBPlusTree(tableName, columnName);
    }
    
    private void createBPlusTree(String tableName, String columnName)
    {
        Object objKey, objValue;
        
        Iterator itr = SqlExecutionFactory.dataRecord.getHashTable(tableName).entrySet().iterator();
        
        
        if(keyType.equals("Integer") && primaryType.equals("Integer"))
        {
            this.tree = new BPlusTree<Integer, Integer>();
            
            while(itr.hasNext())
            {
                Map.Entry<String, Object> tuple = (Map.Entry<String, Object>)itr.next();
                objKey = ((Map<String, Object>)tuple.getValue()).get(columnName);
                objValue = tuple.getKey();
                
                this.tree.put(((Integer)objKey).intValue(), ((Integer)objValue).intValue());
            }
            
        }else if(keyType.equals("Integer") && primaryType.equals("String"))
        {
            tree = new BPlusTree<Integer, String>();
            
            while(itr.hasNext())
            {
                Map.Entry<String, Object> tuple = (Map.Entry<String, Object>)itr.next();
                objKey = ((Map<String, Object>)tuple.getValue()).get(columnName);
                objValue = tuple.getKey();
                
                this.tree.put(((Integer)objKey).intValue(), objValue.toString());
            }
            
        }else if(keyType.equals("String") && primaryType.equals("Integer"))
        {
            tree = new BPlusTree<String, Integer>();
            
            while(itr.hasNext())
            {
                Map.Entry<String, Object> tuple = (Map.Entry<String, Object>)itr.next();
                objKey = ((Map<String, Object>)tuple.getValue()).get(columnName);
                objValue = tuple.getKey();
                
                this.tree.put(objKey.toString(), ((Integer)objValue).intValue());
            }
            
        }else if(keyType.equals("String") && primaryType.equals("String"))
        {
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
    
    private void setType(String tableName, String columnName)
    {
        Object objKey, objPrimary;
        
        Iterator itr = SqlExecutionFactory.dataRecord.getHashTable(tableName).entrySet().iterator();
        
        Map.Entry<String, Object> tuple = (Map.Entry<String, Object>)itr.next();
        objKey = ((Map<String, Object>)tuple.getValue()).get(columnName);
        objPrimary = tuple.getKey();
        
        if(objKey instanceof Integer)
        {
            this.keyType = "Integer";
        }else
        {
            this.keyType = "String";
        }
        
        if(objPrimary instanceof Integer)
        {
            this.primaryType = "Integer";
        }else
        {
            this.primaryType = "String";
        }
    }
    
    public ArrayList<Object> get(String operator, Object key)
    {
        if(key instanceof Integer)
        {
            ArrayList<Object> answerSet = new ArrayList<Object>();
            Node node = this.tree.get(((Integer)key).intValue());
            int count = 0;
        
            if( operator.equals("<") )
            {
                while(node != null)
                {
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
            Node node = this.tree.get(key.toString());
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

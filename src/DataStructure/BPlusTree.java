/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructure;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author loe800210
 */
public class BPlusTree<keyType extends Comparable<keyType>, valueType> 
{
    private int minKeySize = 2;
    private int maxKeySize = 4;
    private int minChildSize = 3;
    private int maxChildSize = 5;
    
    
    private Node root = null;
    private int height;
    
    
    public BPlusTree()
    {
        
    }
    public int getHeight()
    {
        return height;
    }
    
    
    //  Entry declaration
    public class Entry<keyType extends Comparable<keyType>, valueType>
    {
        private keyType key;
        private ArrayList<Object> primaryKeySet = new ArrayList<Object>();
        public Entry(keyType key, valueType primaryKey)
        {
            this.key = key;
            this.primaryKeySet.add(primaryKey);
        }
        
        public keyType getKey()
        {
            return this.key;
        }
        
        public ArrayList<Object> getPrimaryKey()
        {
            return this.primaryKeySet;
        }
    }
    //  End Entry declaration
    
    //  Node structure declaration
    public class Node
    {
        private ArrayList<Entry> keys;
        private ArrayList<Node> children;
        private int childIndex;
        private Node parent;
        private Node front;
        private Node next;
        
        public Node(Node parent, int maxKeySize, int maxChildSize)
        {
            this.keys = new ArrayList<Entry>();
            this.children = new ArrayList<Node>();
            this.childIndex = 0;
            this.parent = parent;
            this.front = null;
            this.next = null;
        }
        
        
        public Entry getEntry(int index)
        {
            if( index >= this.keys.size() )
            {
                System.out.println("Error : Cannot get the KEY with null.");
                return null;
            }else
            {
                return this.keys.get(index);
            }
        }
        
        private void addEntry(Entry entry)
        {
            for(int i=0;i<this.keys.size();i++)
            {
                if( equal(this.keys.get(i).key, entry.key) )
                {
                    this.keys.get(i).primaryKeySet.addAll(entry.primaryKeySet);
                    return;
                }
            }
            this.keys.add(entry);
            
            for(int i=this.keys.size()-1;i>0;i--)
            {
                if( less(this.keys.get(i).key, this.keys.get(i-1).key) )
                {
                    // swap
                    Entry temp = this.keys.get(i-1);
                    this.keys.set(i-1, this.keys.get(i));
                    this.keys.set(i, temp);
                }
            }
        }
        
        private Node getChild(int index)
        {
            if( index >= this.children.size() )
            {
                System.out.println("Error : Cannot get the CHILD with null.");
                return null;
            }else
            {
                return this.children.get(index);
            }
        }
        
        private void addChild(Node child, int index)
        {
            child.parent = this;
            child.childIndex = index;
            this.children.add(index, child);
        }
        
        private void removeChild(int index)
        {
            this.children.set(index, null);
        }
        
        public int numberOfKeys()
        {
            return this.keys.size();
        }
        
        public int numberOfChildren()
        {
            return this.children.size();
        }
        
        public Node getFront()
        {
            return this.front;
        }
        
        public Node getNext()
        {
            return this.next;
        }
        
    }
    //  end Node structure declaration
    
    public void put(keyType key, valueType value)
    {
        Entry newEntry = new Entry<keyType, valueType>(key, value);
        
        if(this.root == null)
        {
            this.root = new Node(null, this.maxKeySize, this.maxChildSize);
            this.root.addEntry(newEntry);
        }else
        {
            Node node = this.root;
            while(node != null)
            {
                if(node.numberOfChildren() == 0)
                {
                    node.addEntry(newEntry);
                    if(node.numberOfKeys() > this.maxKeySize)
                    {
                        this.split(node);
                    }
                    break;
                }else
                {
                    int last = node.numberOfKeys()-1;
                    if( this.less(newEntry.key, node.getEntry(0).key) )
                    {
                        node = node.getChild(0);
                        continue;
                    }
                    
                    for(int i=0;i<last;i++)
                    {
                        if( this.lessEqual(node.getEntry(i).key, newEntry.key) && this.less(newEntry.key, node.getEntry(i+1).key) )
                        {
                            node = node.getChild(i+1);
                            continue;
                        }
                    }
                    
                    if( this.lessEqual(node.getEntry(last).key, newEntry.key) )
                    {
                        node = node.getChild(last+1);
                        continue;
                    }
                }
            }
        }
    }
    private void split(Node node)
    {
        int medianIndex = node.numberOfKeys()/2;
        
        Node left = new Node(null, this.maxKeySize, this.maxChildSize);
        
        for(int i=0;i<medianIndex;i++)
        {
            left.addEntry(node.getEntry(i));
        }
        
        
        Node right = new Node(null, this.maxKeySize, this.maxChildSize);
        for(int i=medianIndex;i<node.numberOfKeys();i++)
        {
            right.addEntry(node.getEntry(i));
        }
        
        left.next = right;
        right.front = left;
        
        if(node.parent == null)
        {
            Node newRoot = new Node(null, this.maxKeySize, this.maxChildSize);
            newRoot.addEntry(node.getEntry(medianIndex));
            newRoot.addChild(left, 0);
            newRoot.addChild(right, 1);
            root = newRoot;
            
        }else
        {
            node.parent.removeChild(node.childIndex);
            node.parent.addEntry(node.getEntry(medianIndex));
            node.parent.addChild(left, node.childIndex);
            node.parent.addChild(right, node.childIndex+1);
            
            if(node.parent.numberOfKeys() > this.maxKeySize)
            {
                split(node.parent);
            }
            
        }
        
    }
    public Node get(keyType key)
    {
        Node node = root;
        while(node != null)
        {
            int last = node.numberOfKeys()-1;
            if( less(key, node.getEntry(0).key) )
            {
                if(node.numberOfChildren() > 0)
                {
                    node = node.getChild(0);
                    continue;
                }else
                {
                    break;
                }
            }
                    
            for(int i=0;i<last;i++)
            {
                if( equal(key, node.getEntry(i).key) )
                {
                    if(node.numberOfChildren() >0)
                    {
                        node = node.getChild(i+1);
                        continue;
                    }else
                    {
                        return node;
                    }
                }else if( less(node.getEntry(i).key, key) && less(key, node.getEntry(i+1).key) )
                {
                    if(node.numberOfChildren() > 0)
                    {
                        node = node.getChild(i+1);
                        continue;
                    }else
                    {
                        break;
                    }
                }
            }
                    
            if( equal(node.getEntry(last).key, key) )
            {
                if(node.numberOfChildren() >0)
                {
                    node = node.getChild(last+1);
                    continue;
                }else
                {
                    return node;
                }
            }else if( less(node.getEntry(last).key, key) )
            {
                if(node.numberOfChildren() >0)
                {
                    node = node.getChild(last+1);
                    continue;
                }else
                {
                    break;
                }
            }
        }
        
        return null;
    }
    public boolean less(Comparable key1, Comparable key2)
    {
        return key1.compareTo(key2) < 0;
    }
    public boolean equal(Comparable key1, Comparable key2)
    {
        return key1.compareTo(key2) == 0;
    }
    public boolean lessEqual(Comparable key1, Comparable key2)
    {
        return (key1.compareTo(key2) < 0 || key1.compareTo(key2) == 0);
    }
    
}
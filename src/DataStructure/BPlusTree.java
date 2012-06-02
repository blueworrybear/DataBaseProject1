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
        this.root = new Node(null, this.maxKeySize, this.maxChildSize);
        this.height = 0;
    }
    public int getHeight()
    {
        return this.height;
    }
    
    
    //  Entry declaration
    public class Entry<keyType extends Comparable<keyType>, valueType>
    {
        private keyType key;
        private ArrayList<Object> primaryKeySet;
        private Node left;
        private Node right;
        public Entry(keyType key, valueType primaryKey)
        {
            this.key = key;
            this.primaryKeySet = new ArrayList<Object>();
            this.primaryKeySet.add(primaryKey);
            this.left = null;
            this.right = null;
        }
        
        public keyType getKey()
        {
            return this.key;
        }
        
        public ArrayList<Object> getPrimaryKey()
        {
            return this.primaryKeySet;
        }
        
        public void setLeftChild(Node node)
        {
            this.left = node;
        }
        
        public void setRightChild(Node node)
        {
            this.right = node;
        }
        
        public Node getLeftChild()
        {
            return this.left;
        }
        
        public Node getRightChild()
        {
            return this.right;
        }
    }
    //  End Entry declaration
    
    //  Node structure declaration
    public class Node
    {
        private ArrayList<Entry> keys;
        private Node parent;
        private Node front;
        private Node next;
        private boolean hasChild;
        
        public Node(Node parent, int maxKeySize, int maxChildSize)
        {
            this.keys = new ArrayList<Entry>();
            this.parent = parent;
            this.front = null;
            this.next = null;
            this.hasChild = false;
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
        
        public int getEntryLocation(Entry entry)
        {
            for(int i=0;i<this.numberOfKeys();i++)
            {
                if( equal(this.getEntry(i).key, entry.key) )
                {
                    return i;
                }
            }
            return -1;
        }
        
        public int numberOfKeys()
        {
            return this.keys.size();
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
        
        Node node = this.root;
        while(node != null)
        {
            if(node.hasChild == false)
            {
                node.addEntry(newEntry);
                if(node.numberOfKeys() > this.maxKeySize)
                {
                    this.split(node);
                }
                break;
            }else
            {
                int i;
                for(i=0;i<node.numberOfKeys();i++)
                {
                    if( this.less(newEntry.key, node.getEntry(i).key) )
                    {
                        node = node.getEntry(i).getLeftChild();
                        break;
                    }else if( this.equal(node.getEntry(i).key, newEntry.key) )
                    {
                        node = node.getEntry(i).getRightChild();
                        break;
                    }
                }
                if(i == node.numberOfKeys() )
                {
                    node = node.getEntry(i-1).getRightChild();
                }
            }
               
        }
        
    }
    private void split(Node node)
    {
        int medianIndex = node.numberOfKeys()/2;
        
        Node left = new Node(null, this.maxKeySize, this.maxChildSize);
        Node right = new Node(null, this.maxKeySize, this.maxChildSize);
        
        for(int i=0;i<medianIndex;i++)
        {
            left.addEntry(node.getEntry(i));
        } 
        for(int i=medianIndex;i<node.numberOfKeys();i++)
        {
            right.addEntry(node.getEntry(i));
        }
        
        node.getEntry(medianIndex).setLeftChild(left);
        node.getEntry(medianIndex).setRightChild(right);
        
        left.next = right;
        right.front = left;
        
        if(node.parent == null)
        {
            Node newRoot = new Node(null, this.maxKeySize, this.maxChildSize);
            left.parent = newRoot;
            right.parent = newRoot;
            newRoot.addEntry(node.getEntry(medianIndex));
            newRoot.hasChild = true;
            this.root = newRoot;
        }else
        {
            left.parent = node.parent;
            right.parent = node.parent;
            node.parent.addEntry(node.getEntry(medianIndex));
            
            int location = node.parent.getEntryLocation(node.getEntry(medianIndex));
            
            if(location > 0)
            {
                node.parent.getEntry(location-1).setRightChild(left);
                node.parent.getEntry(location).left.front = node.parent.getEntry(location-1).right;
                node.parent.getEntry(location-1).right.next = node.parent.getEntry(location).left;
            }
            if(location < node.parent.numberOfKeys()-1)
            {
                node.parent.getEntry(location+1).setLeftChild(right);
                node.parent.getEntry(location).right.next = node.parent.getEntry(location+1).left;
                node.parent.getEntry(location+1).left.front = node.parent.getEntry(location).right;
            }
        }
            
    }
    
    
    public Node get(keyType key)
    {
        Node node = this.root;
        while(node != null)
        {
            if(node.hasChild == false)
            {
                for(int i=0;i<node.numberOfKeys();i++)
                {
                    if( this.equal(node.getEntry(i).key, key) )
                    {
                        //System.out.println(node.numberOfKeys());
                        return node;
                    }
                }
                return null;
            }else
            {
                int i;
                for(i=0;i<node.numberOfKeys();i++)
                {
                    if( this.less(key, node.getEntry(i).key) )
                    {
                        node = node.getEntry(i).getLeftChild();
                        break;
                    }else if( this.equal(node.getEntry(i).key, key) )
                    {
                        node = node.getEntry(i).getRightChild();
                        break;
                    }
                }
                if(i == node.numberOfKeys() )
                {
                    node = node.getEntry(i-1).getRightChild();
                }
            }
            
        }
        return null;
    }
    
    public Node getRange(keyType key)
    {
        Node node = this.root;
        while(node != null)
        {
            if(node.hasChild == false)
            {
                return node;
            }else
            {
                int i;
                for(i=0;i<node.numberOfKeys();i++)
                {
                    if( this.less(key, node.getEntry(i).key) )
                    {
                        node = node.getEntry(i).getLeftChild();
                        break;
                    }else if( this.equal(node.getEntry(i).key, key) )
                    {
                        node = node.getEntry(i).getRightChild();
                        break;
                    }
                }
                if(i == node.numberOfKeys() )
                {
                    node = node.getEntry(i-1).getRightChild();
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

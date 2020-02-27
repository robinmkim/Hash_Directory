package hw7;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashedDictionary<K, V> implements DictionaryInterface<K, V>
{
   // The dictionary:
	private int numberOfEntries;
	private static final int DEFAULT_CAPACITY = 5;        
	private static final int MAX_CAPACITY = 10000;
   
   // The hash table:
	private Entry<K, V>[] hashTable;
   private int tableSize;                                
   private static final int MAX_SIZE = 2 * MAX_CAPACITY; // Max size of hash table
   private boolean integrityOK = false;
	private static final double MAX_LOAD_FACTOR = 0.5;    // Fraction of hash table that can be filled
	private final Entry<K, V> AVAILABLE = new Entry<>(null, null); // Occupies locations in the hash table in the available state (locations whose entries were removed)
   
	
	public HashedDictionary()
	{
		this(DEFAULT_CAPACITY); // Call next constructor
	} // end default constructor
   
	

	/*
	 * TODO:
	 */
	public HashedDictionary(int initialCapacity)
	{
		checkCapacity(initialCapacity);
		numberOfEntries = 0;
		tableSize = getNextPrime(initialCapacity);
		checkSize(tableSize);
		Entry<K,V>[] temp = (Entry<K,V>[])new Entry[tableSize];
		hashTable = temp;
		integrityOK = true;
	} 
	
	/*
	 * We have completed this method for you, and you do not 
	 * have to modify it. We've added this method to display 
	 * the hash table for illustration and testing 
	 */
 	public void displayHashTable()
 	{
      checkIntegrity();
		for (int index = 0; index < hashTable.length; index++)
		{
    	if (hashTable[index] == null)
    		System.out.println("null ");
     	else if (hashTable[index] == AVAILABLE)
    		System.out.println("removed state");
     	else
    		System.out.println(hashTable[index].getKey() + " " + hashTable[index].getValue());
		} 
      System.out.println();
   } 

 	/*
 	 * TODO: See the description of this method in 
 	 * the interface DictionaryInterface. 
 	 */
	
	public V add(K key, V value)
	{
		V preVal;
		if(isFull())
		{
			enlargeHashTable();
		}
		int index = getHashIndex(key);
		index = linearProbe(index, key);
		
		assert(index>= 0) && ( index <hashTable.length);
		if(hashTable[index] == null)
		{
			hashTable[index] = new Entry<K, V>(key, value);
			numberOfEntries++;
			tableSize++;
			preVal = null;
		}
		else
		{
			preVal = hashTable[index].getValue();
			hashTable[index].setValue(value);
		}
      
		return preVal;
	
   } 

	/*
 	 * TODO: See the description of this method in 
 	 * the interface DictionaryInterface. 
 	 */
	
	public V remove(K key)
	{
		V removedVal = null;
		int index = getHashIndex(key);
		index = linearProbe(index, key);
		if(index!=-1)
		{
			removedVal = hashTable[index].getValue();
			hashTable[index] = null;
			numberOfEntries--;
		}
		return removedVal;
	
	} // end remove

	
	/*
 	 * TODO: See the description of this method in 
 	 * the interface DictionaryInterface. 
 	 */
	
   public V getValue(K key)
   {
	   checkIntegrity();
	   V result = null;
	   int index = getHashIndex(key);
	   index = linearProbe(index, key);
	   
	   if(index != -1)
	   {
		   result = hashTable[index].getValue();
	   }
	   return result;
   } 

   
    /*
	 * TODO: See the description of this method in 
	 * the interface DictionaryInterface. 
	 */
	
	public boolean contains(K key)
   {
   	boolean result = false;
   	int index = getHashIndex(key);
//   	index = linearProbe(index, key);
   	if(index!=-1)
   	{
   		result = true;
   	}
   	return result;
   } // end contains

	
	/*
 	 * TODO: See the description of this method in 
 	 * the interface DictionaryInterface. 
 	 */
		
   public boolean isEmpty()
   {
      return numberOfEntries == 0;
   } 
   
   public boolean isFull()
   {
	   return numberOfEntries==hashTable.length;
   }
   

   /*
	 * TODO: See the description of this method in 
	 * the interface DictionaryInterface. 
	 */
	
   public int getSize()
   {
	 return numberOfEntries;  
   } 

	public void clear()
	{ 
		for(int i = 0; i<hashTable.length; i++)
		{
			hashTable[i] = null;
		}
		numberOfEntries = 0;
	} 

	
	/*
 	 * TODO: See the description of this method in 
 	 * the interface DictionaryInterface. 
 	 */
	
	public Iterator<K> getKeyIterator()
	{ 
		return new keyIterator();
	} 
	
	
	/*
 	 * TODO: See the description of this method in 
 	 * the interface DictionaryInterface. 
 	 */
	
	public Iterator<V> getValueIterator()
	{	
		return new valueIterator();
	} 
   
	
	/*
	 * TODO: You will need to complete this method. This method
	 * will first call the hashCode on the key. The hashCode returned
	 * from the hashCode of the key is then checked against the length
	 * of the hashtable and you will then check for any collision 
	 * by calling either linearProbe or quadraticProbe methods. 
	 * You will want to call getHashIndex 
	 * from the method add, remove and getValue. 
	 */
	private int getHashIndex(K key)
	{
		int hashIndex = key.hashCode() % hashTable.length;
		if(hashIndex<0)
		{
			hashIndex = hashIndex + hashTable.length;
		}
		return hashIndex;
	} 
	
	/*
	 * We have completed this method for you, and you do not 
	 * have to modify it. Precondition: checkIntegrity has been called.  
	 */

	private int linearProbe(int index, K key)
	{
      boolean found = false;
      int availableIndex = -1; // Index of first available location (from which an entry was removed)
      
      while ( !found && (hashTable[index] != null) )
      {
         if (hashTable[index] != AVAILABLE)
         {
            if (key.equals(hashTable[index].getKey()))
               found = true; // Key found
            else             // Follow probe sequence
               index = (index + 1) % hashTable.length;         // Linear probing
         }
         else // Skip entries that were removed
         {
            // Save index of first location in removed state
            if (availableIndex == -1)
               availableIndex = index;
            
            index = (index + 1) % hashTable.length;            // Linear probing
         } 
      } 
      // Assertion: Either key or null is found at hashTable[index]
      
      if (found || (availableIndex == -1) )
         return index;                                      // Index of either key or null
      else
         return availableIndex;                          // Index of an available location
	} 
	
	/*
	 * We have completed this method for you, and you do not 
	 * have to modify it. Precondition: checkIntegrity has been called.  
	 */

   private int quadraticProbe(int index, K key)
   {
      boolean found = false;
      int availableIndex = -1; // Index of first available location (from which an entry was removed)
      int increment = 1;          // For quadratic probing
      
      while ( !found && (hashTable[index] != null) )
      {
         if ((hashTable[index] != null) && (hashTable[index] != AVAILABLE))
         {
            if (key.equals(hashTable[index].getKey()))
               found = true; // Key found
            else             // Follow probe sequence
            {
               index = (index + increment) % hashTable.length; // Quadratic probing
               increment = increment + 2;                      // Odd values for quadratic probing
            } 
         }
         else 
         {
            // Save index of first location in removed state
            if (availableIndex == -1)
               availableIndex = index;
            index = (index + increment) % hashTable.length;    // Quadratic probing
            increment = increment + 2;                         // Odd values for quadratic probing
         } 
      } 
        // Assertion: Either key or null is found at hashTable[index]
      
      if (found || (availableIndex == -1) )
         return index;                                      // Index of either key or null
      else
         return availableIndex;                          // Index of an available location
   }
   
   
   /*
	 * We have completed this method for you, and you do not 
	 * have to modify it. Increases the size of the hash table 
	 * to a prime >= twice its old size. In doing so, this 
	 * method must rehash the table entries. Precondition: 
	 * checkIntegrity has been called. The method enlargeHashTable
	 * must be called inside the add method somewhere.  
	 */
	private void enlargeHashTable()
	{
      Entry<K, V>[] oldTable = hashTable;
      int oldSize = hashTable.length;
      int newSize = getNextPrime(oldSize + oldSize);
      checkSize(newSize); // Check that the prime size is not too large

      // The cast is safe because the new array contains null entries
      @SuppressWarnings("unchecked")
      Entry<K, V>[] tempTable = (Entry<K, V>[])new Entry[newSize]; // Increase size of array
      hashTable = tempTable;
      numberOfEntries = 0; // Reset number of dictionary entries, since
                           // it will be incremented by add during rehash

      // Rehash dictionary entries from old array to the new and bigger array;
      // skip both null locations and removed entries
      for (int index = 0; index < oldSize; index++)
      {
         if ( (oldTable[index] != null) && (oldTable[index] != AVAILABLE) )
            add(oldTable[index].getKey(), oldTable[index].getValue());
      } 
	} 

	
	 
	/*
		 * We have completed this method for you, and you do not 
		 * have to modify it. Returns true 
		 * if lambda > MAX_LOAD_FACTOR for hash table; otherwise returns false.
		 * This method must be called inside add method somewhere. 
		 */
    
   private boolean isHashTableTooFull()
   {
      return numberOfEntries > MAX_LOAD_FACTOR * hashTable.length;
   } 

   
   /*
	 * We have completed this method for you, and you do not 
	 * have to modify it. Returns a prime integer 
	 * that is >= the given integer, but <= MAX_SIZE. 
	 */
	private int getNextPrime(int anInteger)
	{
		// if even, add 1 to make odd
   	if (anInteger % 2 == 0)
      {
        anInteger++;
		} 
		
		// test odd integers
      while (!isPrime(anInteger))
      {
         anInteger = anInteger + 2;
      } 
      
		return anInteger;
	} 
	
	 /*
		 * We have completed this method for you, and you do not 
		 * have to modify it. Returns true if the given integer 
		 * is prime. 
		 */
		
	private boolean isPrime(int anInteger)
	{
		boolean result;
		boolean done = false;
		
		// 1 and even numbers are not prime
		if ( (anInteger == 1) || (anInteger % 2 == 0) )
	  {
			result = false; 
		}
		
		// 2 and 3 are prime
		else if ( (anInteger == 2) || (anInteger == 3) )
		{
			result = true;
		}
		
		else // anInteger is odd and >= 5
		{
			assert (anInteger % 2 != 0) && (anInteger >= 5);
			
			// a prime is odd and not divisible by every odd integer up to its square root
			result = true; // assume prime
			for (int divisor = 3; !done && (divisor * divisor <= anInteger); divisor = divisor + 2)
			{
		   	if (anInteger % divisor == 0)
	      {
					result = false; // divisible; not prime
					done = true;
				} 
			} 
		} 
	   	
		return result;
	} 

	
	
   // Throws an exception if this object is not initialized.
   private void checkIntegrity()
   {
      if (!integrityOK)
         throw new SecurityException ("HashedDictionary object is corrupt.");
   } 
   
   
   
   /*
	 * We have completed this method for you, and you do not 
	 * have to modify it. Returns true if the given integer 
	 * is prime. Ensures that the client requests a capacity
	 * that is not too small or too large. This method must be called
	 * from the constructor of the hash table. 
	 */

   
   private int checkCapacity(int capacity)
   {
      if (capacity < DEFAULT_CAPACITY)
         capacity = DEFAULT_CAPACITY;
      else if (capacity > MAX_CAPACITY)
         throw new IllegalStateException("Attempt to create a dictionary " +
                                         "whose capacity is larger than " +
                                         MAX_CAPACITY);
      return capacity;
   } 
   
   // Throws an exception if the hash table becomes too large.
   private void checkSize(int size)
   {
      if (size > MAX_SIZE)
         throw new IllegalStateException("Dictionary has become too large.");
   } 
   
	
   /*
    * We have completed the Entry class for you. 
    * No change is required to the Entry class. 
    */
	private class Entry<K, V>
	{
		private K key;
		private V value;
     
		private Entry(K searchKey, V dataValue)
		{
         key = searchKey;
         value = dataValue;
		} 
		
		private K getKey()
		{
			return key;
		} 
		
		private V getValue()
		{
			return value;
		} 
		
		private void setValue(V newValue)
		{
			value = newValue;
		} 
	} 
	
	private class keyIterator implements Iterator<K>
	{
		private int currentIndex;
		private int numberLeft;
		
		private keyIterator()
		{
			currentIndex = 0;
			numberLeft = numberOfEntries;
		}
		public boolean hasNext()
		{
			return numberLeft>0;
		}
		public K next()
		{
			K result = null;
			if(hasNext())
			{
				while(hashTable[currentIndex]==null)
				{
					currentIndex++;
				}
				result = (K) hashTable[currentIndex].getKey();
				numberLeft--;
				currentIndex++;
			}
			else
				throw new NoSuchElementException();
			return result;
		}
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private class valueIterator implements Iterator<V>
	{
		private int currentIndex;
		private int numberLeft;
		
		private valueIterator()
		{
			currentIndex = 0;
			numberLeft = numberOfEntries;
		}
		
		public boolean hasNext()
		{
			return numberLeft>0;
		}
		
		public V next()
		{
			V result = null;
			if(hasNext())
			{
				while(hashTable[currentIndex]==null)
				{
					currentIndex++;
				}
				result = hashTable[currentIndex].getValue();
				numberLeft--;
				currentIndex++;
			}
			else
				throw new NoSuchElementException();
			return result;
		}
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
		
		
	}
} 


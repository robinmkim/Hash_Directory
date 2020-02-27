package hw7;

public class Name implements Comparable<Name>
{
	private String first; // First name
	private String last;  // Last name

public Name(String firstName)                 // For testing ***************
{
   setName(firstName, "");
} // end constructor

public Name()
	{
		this("", "");
	} // end default constructor

	public Name(String firstName, String lastName)
	{
		first = firstName;
		last = lastName;
	} // end constructor

public int hashCode()                         // For testing--causes collisions ***************
{
   int h = 0;
   
   for (int i = 0; i < first.length(); i++)
   {
      h = h + first.charAt(i);
   } // end for
   
   return h;                                  // This hash code causes collisions
   
   //    return first.hashCode() + last.hashCode(); // This would be a reasonable hash code
} // end hashCode

public void setName(String firstName, String lastName)
	{
		setFirst(firstName);
		setLast(lastName);
	} // end setName

	public String getName()
	{
		return toString();
	} // end getName

	public void setFirst(String firstName)
	{
		first = firstName; 
	} // end setFirst

	public String getFirst()
	{
		return first;
	} // end getFirst

	public void setLast(String lastName)
	{
		last = lastName;
	} // end setLast

	public String getLast()
	{
		return last;
	} // end getLast

	public void giveLastNameTo(Name aName)
	{
		aName.setLast(last);
	} // end giveLastNameTo

	public String toString()
	{
		return first + " " + last;
	} // end toString

public int compareTo(Name other)  
{
   int result = last.compareTo(other.last);
   
   // If last names are equal, check first names
   if (result == 0)
      result = first.compareTo(other.first);
      
   return result;
} // end compareTo

public boolean equals(Object other)
{
   boolean result;
   
   if ((other == null) || (getClass() != other.getClass()))
      result = false;
   else
   {
      Name otherName = (Name)other;
      result = first.equals(otherName.first) &&
      last.equals(otherName.last);
   } // end if
   
   return result;
} // end equals
} // end Name


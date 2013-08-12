package spellchecker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Basic framework for knowledge based approach (Edit Distance Approach)
 * 
 * 1)Read parallel corpus and store list of CORRECT words
 * 2)calculate edit distance of input word with all correct words
 * 3)find min edit distance and all candidate words with min edit distance
 * 4)break ties with some additional knowledge
 * */

public class Framework3 {

	ArrayList<String> correctWords = new ArrayList<String>() ;
	public void readFile() throws FileNotFoundException,IOException
	{
		//open the parallel corpus file
		BufferedReader br = new BufferedReader(new FileReader("words.txt"));
		
		System.out.println("Parallel Corpus being Parsed");
		
		//read the parallel corpus file
		String line;
		while((line = br.readLine()) != null)
		{
			String parts[] = line.split("->");
			//parts[0] has wrong word
			//parts[1] has correct word
			
			/*
			 *TODO Store correct and incorrect words in appropriate data structure 
			 */
			
			//the following code stores all correct words in a list(no repetitions)
			String words[] = parts[1].split(", ");
			for(int i = 0; i< words.length ; i++)
			{
				if (!correctWords.contains(words[i]))
					correctWords.add(words[i]);
			}
		}
		//at this point
		//correctWords contains a non repeating complete list of correct words
		/*
		 //prints all correct words list
		for (int i = 0; i< correctWords.size() ; i++)
		{
			System.out.println(i+" : " + correctWords.get(i));
		}
		*/
	}
	
	public void calculateDistance(String wrong)
	{
		String correct;  
		int distance=0;
	    System.out.println("Calculating distances of wrong word from all possible words...");
		int min = 9999;
		Set<String> candidates = new HashSet<String>();
		
	    for (int i = 0 ; i < correctWords.size() ; i++)
		{   
			correct = correctWords.get(i);
		    //calculate distance between correctWords[i] and wrong
			//add to appropriate data structure
			distance = editDistance(wrong, correct);
			if(min > distance) //min changes
			{
				min = distance;
				candidates.clear(); //flush set
				candidates.add(correct); // add current word to set
			}
			else if (min == distance) //a word with edit dist = min found hence it is a candidate
			{
				candidates.add(correct);
			}
			else //word with edit dist > min 
			{
				//do nothing
			}
			//System.out.println("The edit distance between \"" + wrong + "\" and \"" + correct + "\" is " + distance);
			
		}
	    System.out.println("min distance: "+min);
	    System.out.println("-----------------\nCandidates:\n------------------");
	    Iterator<String> iterator = candidates.iterator();
	    while(iterator.hasNext())
	    {
	    	String word = iterator.next();
	    	System.out.println(word);
	    }
	    System.out.println("-----------------");
	}
	
	//given two strings calculate edit distance between them
	public int editDistance(String s1, String s2)
	{
		// int distance = 0;
		
		int substituteDist;   // Edit distance if first char. match or do a replace
	    int insertDist;  // Edit distance if insert first char of s1 in front of s2.
	    int deleteDist;  // Edit distance if delete first char of s2.
	    int transposeDist;    // edit distance for twiddle (first 2 char. must swap).
	    
	    if(s1.length() == 0)
	      return s2.length();   // Insert the remainder of s2
	    else  if (s2.length()== 0)
	      return s1.length();   // Delete the remainer of s1
	    else {
	      substituteDist = editDistance(s1.substring(1), s2.substring(1));
	      if(s1.charAt(0) != s2.charAt(0))
	        substituteDist+=2;  // If first 2 char. don't match must replace

	      insertDist = editDistance(s1.substring(1), s2) + 1;
	      deleteDist = editDistance(s1, s2.substring(1)) + 1;

	      if(s1.length() > 1 && s2.length() > 1 && 
	          s1.charAt(0) == s2.charAt(1) && s1.charAt(1) == s2.charAt(0)) 
	        transposeDist = editDistance(s1.substring(2), s2.substring(2)) + 2;
	      else
	        transposeDist = Integer.MAX_VALUE;  // Can't swap if first 2 char. don't match

	      return Math.min(substituteDist, Math.min(insertDist, Math.min(deleteDist, transposeDist)));
	    }
	}
	  	
	
		
	public void calculateMin()
	{
		//given the data str from above calculate min and generate list of candidate words
	}
	
	public void breakTies()
	{
		//given the list of candidates find the ONE best word
	}
	public static void main(String[] args) throws FileNotFoundException, IOException{
		Framework3 obj = new Framework3();
		obj.readFile();
		//Scanner input = new Scanner(System.in);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String wrong;    // The strings to find the edit distance between
		System.out.println("Enter wrong string");
		wrong = br.readLine();
		obj.calculateDistance(wrong);
		
		    
	}

}

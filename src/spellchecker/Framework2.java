package spellchecker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Basic framework for knowledge based approach (Edit Distance Approach)
 * 
 * 1)Read parallel corpus and store list of CORRECT words
 * 2)calculate edit distance of input word with all correct words
 * 3)find min edit distance and all candidate words with min edit distance
 * 4)break ties with some additional knowledge
 * */

public class Framework2 {

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
		
		for (int i = 0; i< correctWords.size() ; i++)
		{
			System.out.println(i+" : " + correctWords.get(i));
		}
	}
	
	public void calculateDistance(String wrong)
	{
		for (int i = 0 ; i < correctWords.size() ; i++)
		{
			//calculate distance between correctWords[i] and wrong
			//add to appropriate data structure
		}
	}
	
	//given two strings calculate edit distance between them
	public int distanceBetween(String first, String second)
	{
		int distance = 0;
		
		
		return distance;
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
		new Framework2().readFile();
	}

}

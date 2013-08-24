package spellchecker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.SortedSet;

import spellchecker.Bigrams;
import spellchecker.Utilities;


public class BigWordCounts {
	
	HashMap<String,Integer> wordCounts = new HashMap<String, Integer>();
	int totalWordCount = 0;
	
	public void readCorpus() throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader("big.txt"));
		System.out.println("Reading big.txt corpus for word probabilities...");
		String line;
		
		while((line = br.readLine()) != null) //read line by line
		{
			String words[] = line.split(" ");
			for (int i = 0 ; i < words.length ; i++) //process word by word
			{
				if(Utilities.isValid(words[i]))
				{
					totalWordCount ++;
					//System.out.println(words[i]);
					if(wordCounts.containsKey(words[i])) //update count
						wordCounts.put(words[i], wordCounts.get( words[i] ) + 1);
					else  //initialize count
						wordCounts.put(words[i], 1);
				}
			}
		}
		
		//now wordCounts has counts of all words
		
	}
	
	
	public int getCount(String word) 
	{
		if(wordCounts.containsKey(word))
			return wordCounts.get(word);
		else
			return 0;
	}
	
	public double getProbability(String word)
	{
		return (double) getCount(word) / (totalWordCount); 
			
		//return (double) (getCount(word)+1) / (totalWordCount + wordCounts.size()); 
	}
	
	public int getTotalWordCount()
	{
		return totalWordCount;
	}

	public static void main(String args[]) throws IOException
	{
		BigWordCounts b = new BigWordCounts();
		b.readCorpus();
		System.out.println("love: "+ b.getCount("love"));
		System.out.println(b.getTotalWordCount());
		System.out.println(b.getProbability("love"));
	}
}

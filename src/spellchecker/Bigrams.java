package spellchecker;

import java.util.HashMap;

/**
	Class Bigrams - Contains functions for using bigram probabilities to compute and use for tie-breaking
	
	CONTENTS:
	1: getCounts() - Returns the matrix containing bigram occurence counts
	2: getProbabilities() - Returns the matrix containing bigram probabilities
	3: update() - Accepts a correct word and accordingly updates bigram counts
	4: getProbability(String) - Accepts a word and calculates its bigram probabilities
*/

public class Bigrams
{

	/*
		bigramCounts is a matrix that will calculate counts of the form (a|null), (x|y), etc.
		First index is used for preceeding character and second index for succeeding character.
		Thus, count(b|a) is count of string "ab", represented in bigramCounts[1][2].
		The 0 index is used for null characters.
	*/
	
	int[][] bigramCounts = new int[27][27];
	
	//	bigramProbabilities is the same as bigramCounts, except that it computes probabilities.
	double[][] bigramProbabilities = new double[27][27];
	
	//	charIndex assigns numbers for a to z
	HashMap<Character, Integer> charIndex = new HashMap<Character, Integer>();
	
	public Bigrams()
	{
		// Initialize the charIndex HashMap using ASCII codes
		for(int i=1; i<27; i++)
			charIndex.put((char) (i+96), i);
	}
	
	public int[][] getCounts()
	{	return bigramCounts;	}
	
	public double[][] getProbabilities()
	{
		computeProbabilities();	// See below
		return bigramProbabilities;
	}
	
	/*
		update() accepts a String representing a correct word from the corpus and updates the bigram counts accordingly.
	*/
	public void update(String word)
	{
		//System.out.println(word);
		// For first letter, update bigramCounts[0][charIndex] as it is null followed by char
		bigramCounts[0][charIndex.get(word.charAt(0))]++;
	
		for(int i=1; i<word.length(); i++)	// Loop over all letters in word except for first and last
		{
			int i1 = charIndex.get(word.charAt(i-1));
			int i2 = charIndex.get(word.charAt(i));
			bigramCounts[i1][i2]++;
		}
		
		// For last letter, update bigramCounts[charIndex][0] as it is char followed by null
		bigramCounts[charIndex.get(word.charAt(word.length()-1))][0]++;
	}
	
	/*
		getProbability() accepts a string and returns its bigram probability
	*/
	public double getProbability(String word)
	{
		computeProbabilities();
		
		// Initialize probability to 1
		double prob = 1.0;
		
		// First letter is preceeded by null, multiply prob accordingly
		prob *= bigramProbabilities[0][charIndex.get(word.charAt(0))];
		if(prob == 0.0)
			return prob;
		
		for(int i=1; i<word.length(); i++)	// All other letters
		{
			int i1 = charIndex.get(word.charAt(i-1));
			int i2 = charIndex.get(word.charAt(i));
			prob *= bigramProbabilities[i1][i2];
			
			if(prob == 0.0)
				return prob;
		}
		
		prob *= bigramProbabilities[charIndex.get(word.charAt(word.length()-1))][0];
		
		return prob;
	}
	
	
	/*
		computeProbabilities() takes bigramCounts into account and computes bigramProbabilities accordingly
	*/
	void computeProbabilities()
	{
		int[] sums = new int[27];
	
		// Summing up all occurences of each individual character for denominator
		for(int i=0; i<27; i++)
			for(int j=0; j<27; j++)
				sums[i] += bigramCounts[i][j];
		
		// Finding individual probabilities as per definition
		// For example - P(x|a) = (No of occurences of a) / (No of occurences of ax)
		for(int i=0; i<27; i++)
			if(sums[i] != 0)
				for(int j=0; j<27; j++)
					bigramProbabilities[i][j] = bigramCounts[i][j] / (double) sums[i];
	}
	
	public void printProbabilities()
	{
		
		
		System.out.println("---- BIGRAM PROBABILITIES ----");
		System.out.print("   ");
		for(int i = 'a' ; i <= 'z' ; i++)
			System.out.print("      "+(char)i);
		System.out.println();
		for(int i=0; i<27; i++)
		{
			System.out.print((char)(i+96)+" ");
			for(int j=0; j<27; j++)
				System.out.printf("%.3f  ",bigramProbabilities[i][j]);
			System.out.println();
		}
	}
	// main function for testing purposes
	public static void main(String[] ar)
	{
		Bigrams ob = new Bigrams();
		ob.update("apple");
		ob.update("simple");
		
		int[][] c = ob.getCounts();
		double[][] d = ob.getProbabilities();
		
		System.out.println("---- COUNTS ----");
		for(int i=0; i<27; i++)
		{
			for(int j=0; j<27; j++)
				System.out.print(c[i][j] + " ");
			System.out.println();
		}
		
		System.out.println("---- PROBABILITIES ----");
		for(int i=0; i<27; i++)
		{
			for(int j=0; j<27; j++)
				System.out.print(d[i][j] + " ");
			System.out.println();
		}
		
		System.out.println(ob.getProbability("ab"));
	}
}
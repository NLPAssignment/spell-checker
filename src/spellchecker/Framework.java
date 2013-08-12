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
 * 4)break ties using bigram probabilities
 * */

public class Framework {

	ArrayList<String> correctWords = new ArrayList<String>() ;	// Why not HashSet here too? - Sagar
	
	Bigrams bigrams = new Bigrams();
	ErrorMatrices errorMatrices = new ErrorMatrices();
	
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
			
			// 1) for knowledge based static approach
			//the following code stores all correct words in a list(no repetitions)
			String words[] = parts[1].split("[,|'| |-]"); //not the right solution-patch for time being -Lekha
			
			for(int i = 0; i< words.length ; i++)
			{
				String currentWord = words[i].toLowerCase(); //for things like America
				if (!currentWord.isEmpty() && !correctWords.contains(currentWord) && currentWord.matches("[a-z]*"))
				{
					correctWords.add(currentWord);
					//System.out.println("-"+currentWord+"-");
					bigrams.update(currentWord); //updates bigram counts					
				}
			}
			
			// 2) For generative technique using Jurafsky formulation 
			//update counts for S,I,D,X matrices
			
			if(parts[0].matches("[a-z]*") && parts[1].matches("[a-z]*"))
				errorMatrices.updateMatrices(parts[0],parts[1]);
			
		}
		
		double probabilities [][] = bigrams.getProbabilities();
		bigrams.printProbabilities();
		
		errorMatrices.printDMatrix();
		errorMatrices.printIMatrix();
		errorMatrices.printSMatrix();
		errorMatrices.printXMatrix();
		//at this point
		//correctWords contains a non repeating complete list of correct words
		
		 //prints all correct words list
		/*
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
		int min = Integer.MAX_VALUE;	// Maximum possible value, instead of "9999" - Sagar
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
	    double candidateProbability = 0.0;
	    double maxProbability = 0.0;
	    String best="";
	    while(iterator.hasNext())
	    {
	    	String candidateWord = iterator.next();
	    	System.out.println(candidateWord);
	    	candidateProbability = bigrams.getProbability(candidateWord);
	    	if(maxProbability < candidateProbability)
	    	{
	    		maxProbability = candidateProbability;
	    		best = candidateWord;
	    	}
	    }
	    System.out.println("-----------------");
	    System.out.println("Best Word: "+best);
	}
	
	//given two strings calculate edit distance between them
	public int editDistance(String s1, String s2)
	{	
		int m=s1.length();
    	int n=s2.length();
        int[][]d=new int[m+1][n+1];
        for(int i=0;i<=m;i++){
           d[i][0]=i;
        }
        for(int j=0;j<=n;j++){
           d[0][j]=j;
        }
        for(int j=1;j<=n;j++){
        	for(int i=1;i<=m;i++){
        		if(s1.charAt(i-1)==s2.charAt(j-1)){
        			d[i][j]=d[i-1][j-1];
        		}
        		else{
        			d[i][j]=Math.min((d[i-1][j]+1),Math.min((d[i][j-1]+1),(d[i-1][j-1]+2)));
        		}
        	}
        }
        return(d[m][n]);
	
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
		Framework obj = new Framework();
		obj.readFile();
		//Scanner input = new Scanner(System.in);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String wrong;    // The strings to find the edit distance between
		System.out.println("Enter wrong string");
		wrong = br.readLine();
		obj.calculateDistance(wrong);
		
		    
	}

}

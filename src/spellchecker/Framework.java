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

	BigWordCounts bigWordCounts = new BigWordCounts();
	public Framework() throws IOException
	{
		bigWordCounts.readCorpus();
	}
	/*
		Custom class created to define a data set: Training set or Test set
		Data members: ArrayLists of correct words and corresponding wrong words
		Function: createFold() to create folds of data for cross-validation and return them
	*/
	public class DataSet
	{
		public ArrayList<String> correctWords = new ArrayList<String>();
		public ArrayList<String> wrongWords = new ArrayList<String>();
		
		/*
			Function createFold() which accepts a fold number (zero-based) and the total number of folds
			Returns an array of Datasets as follows:
			DataSet[0] contains training set, DataSet[1] contains test set
		*/
		public DataSet[] createFold(int foldNo, int totalFolds)
		{
			DataSet[] foldSets = new DataSet[2];
			foldSets[0] = new DataSet();
			foldSets[1] = new DataSet();
			
			//For DataSet[1] (testing set), get all words from (len(DataSet)*foldNo / totalFolds) to ((len(DataSet)*(foldNo+1) / totalFolds) - 1)
			int testSetStart = correctWords.size() * foldNo / totalFolds;
			int testSetEnd = correctWords.size() * (foldNo + 1) / totalFolds;
			//foldSets[1].correctWords = (ArrayList<String>) correctWords.subList(testSetStart, testSetEnd);
			//foldSets[1].wrongWords = (ArrayList<String>) wrongWords.subList(testSetStart, testSetEnd);
			
			for(int i=testSetStart; i<testSetEnd; i++)
			{
				foldSets[1].correctWords.add(correctWords.get(i));
				foldSets[1].wrongWords.add(wrongWords.get(i));
			}
			
			//For DataSet[0] (training set), get everything not in test set
			for(int i=0; i<correctWords.size(); i++)
			{
				// If test set does not contain the word, add it, and its corresponding wrong word to train set
				if(!foldSets[1].correctWords.contains(correctWords.get(i)))
				{
					foldSets[0].correctWords.add(correctWords.get(i));
					foldSets[0].wrongWords.add(wrongWords.get(i));
				}
			}
			
			return foldSets;
		}
	}

	ArrayList<String> correctWords = new ArrayList<String>();	// Why not HashSet here too? - Sagar
	ArrayList<String> wrongWords = new ArrayList<String>();
	DataSet dataSet = new DataSet();
	
	Bigrams bigrams = new Bigrams();
	
	ErrorMatrices errorMatrices = new ErrorMatrices();
	
	static final int EDIT_DISTANCE = 0;
	static final int CONFUSION_MATRIX = 1;
	
	static final int NUMBER_OF_FOLDS = 5;
	
	public void readFile() throws FileNotFoundException,IOException
	{
		/* lekha - generating bigrams from big.txt instead of wikipedia corpus
		BufferedReader br2 = new BufferedReader(new FileReader("big.txt"));
		System.out.println("Reading corpus...");
		String line2;
		while((line2 = br2.readLine()) != null)
		{
			String words[] = line2.split(" ");
			for (int i = 0 ; i < words.length ; i++)
			{
				if(Utilities.isValid(words[i]))
					bigrams.update(words[i]);
			}
		}
		*/
		
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
				1) for knowledge based static approach
				the following code stores all correct words in a list(no repetitions)
			*/
			
			String currentCorrectWord = parts[1].toLowerCase(); //for things like America
			String currentWrongWord = parts[0].toLowerCase(); //for things like America
			
			// Using isValid() function from spellchecker.Utilities class to get rid of corner cases
			if (Utilities.isValid(currentCorrectWord) && Utilities.isValid(currentWrongWord) && !correctWords.contains(currentCorrectWord))
			{
				correctWords.add(currentCorrectWord);
				wrongWords.add(currentWrongWord);
				bigrams.update(currentCorrectWord); //updates bigram counts
			}
			
			/*
				2) For generative technique using Jurafsky formulation 
				update counts for S,I,D,X matrices
			*/
			
			if(Utilities.isValid(parts[0]) && Utilities.isValid(parts[1]))
				errorMatrices.updateMatrices(parts[0], parts[1]);
			
		}
		
		bigrams.printProbabilities();
		
		errorMatrices.printDMatrix();
		errorMatrices.printIMatrix();
		errorMatrices.printSMatrix();
		errorMatrices.printXMatrix();
		errorMatrices.printCounts();
		//at this point
		//correctWords contains a non repeating complete list of correct words
		
		 //prints all correct words list
		//System.out.println("\n--CORRECT DICTIONARY WORDS--\n");
		for (int i = 0; i< correctWords.size() ; i++)
		{
			//System.out.println(i+": " + correctWords.get(i));	Temporarily commented out - Sagar
		}
		
		// Sagar - Temporarily creating DataSet here, NEEDS TO BE CHANGED
		dataSet.correctWords = correctWords;
		dataSet.wrongWords = wrongWords;
	}
	
	/*
	 * This function uses edit distance approach for spell checking
	 * it computes edit distances from all possible correct words and finds candidates with min edit distance
	 * ties are broken using bigram probabilities
	 * */
	public String spellCheckEditDistance(String wrong)
	{
		String correct;  
		int distance=0;
	    //System.out.println("Calculating distances of wrong word from all possible words...");
		int min = Integer.MAX_VALUE;
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
	    	candidateProbability = bigrams.getProbability(candidateWord);//bigWordCounts.getProbability(candidateWord); 
	    	if(maxProbability < candidateProbability)
	    	{
	    		maxProbability = candidateProbability;
	    		best = candidateWord;
	    	}
	    }
	    System.out.println("-----------------");
	    System.out.println("Best Word: " + best);
		
		return best;
	}
	
	//given two strings calculate edit distance between them using dynamic programming
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
	
	
	/*
		Implements spell checker using confusion matrices (Kernighan approach).
		Accepts a wrong string and a training set to obtain the error matrices & bigram probabilities
		During cross-validation, testSet is made up of "folds" from the full data
		During actual spellcheck, testSet will be the entire data itself
	*/
	public String spellCheckConfusionMatrices(String wrong, DataSet testSet)
	{
		String correct;  
		double probability = 0.0;
	    //System.out.println("Calculating probabilites with all possible words...");
		double max = 0.0;
		// Set<String> candidates = new HashSet<String>();
		String candidate = "";
		
		//iterate over all correct words in training set ONLY
	    for (int i = 0 ; i < testSet.correctWords.size() ; i++)
		{   
			correct = testSet.correctWords.get(i); //get the ith correct word
			probability  = bigrams.getProbability(correct);//bigWordCounts.getProbability(correct); //find probability of the correct word itself 
				
			int result[] = errorMatrices.findError(wrong, correct); //detect the type of error and the characters involved in the error
			
			//System.out.println("type of error: "+result[0]);
			
			switch(result[0])
			{
			
			case ErrorMatrices.NO_ERROR: 
			
				probability = 1; //p=1 since we have found a dictionary word 
			
			case ErrorMatrices.INSERTION: 
				System.out.println("Candidate Word: "+correct);
				System.out.println("p(c): "+probability);
				probability *= errorMatrices.getIProbability(result[1], result[2]);  //so far probability contained P(C),  now we mult it by P(W/C) wrt insertion
				System.out.println("p(w/c): "+errorMatrices.getIProbability(result[1], result[2]));
				System.out.println("p(c/w): "+probability);
				break;
			
			case ErrorMatrices.DELETION:
				System.out.println("Candidate Word: "+correct);
				System.out.println("p(c): "+probability);
				probability *= errorMatrices.getDProbability(result[1], result[2]); 
				System.out.println("p(w/c): "+errorMatrices.getDProbability(result[1], result[2]));
				System.out.println("p(c/w): "+probability);
				break;
			
			case ErrorMatrices.SUBSTITUTION:
				System.out.println("Candidate Word: "+correct);
				System.out.println("p(c): "+probability);
				probability *= errorMatrices.getSProbability(result[1], result[2]); 
				System.out.println("p(w/c): "+errorMatrices.getSProbability(result[1], result[2]));
				System.out.println("p(c/w): "+probability);
				break;
			
			case ErrorMatrices.TRANSPOSITION:
				System.out.println("Candidate Word: "+correct);
				System.out.println("p(c): "+probability);
				probability *= errorMatrices.getXProbability(result[1], result[2]); 
				System.out.println("p(w/c): "+errorMatrices.getXProbability(result[1], result[2]));
				System.out.println("p(c/w): "+probability);
				break;
			
			case ErrorMatrices.UNKNOWN_ERROR: 
				
				probability = 0; //not a single error hence should not be part of candidate set
				break;
				
			}
			
			//at this point correct is a candidate word and probability is P(C/W)
			
			if ( max < probability ) //word with probability > max found
			{
				max = probability;
				//candidates.clear();
				//candidates.add(correct);
				candidate = correct;
			}
			/* else if ( max == probability )
			{
				if(max != 0.0)
					candidates.add(correct);
			} */
			else //probability < max so can't be a candidate
			{	/*do nothing*/		}
			
		}
	    System.out.println("\nmax probability: "+max);
	    System.out.println("-----------------\nCorrect Word:\n------------------");
	    
	    /* Iterator<String> iterator = candidates.iterator();
	    while(iterator.hasNext())
	    {
	    	String candidateWord = iterator.next();
	    	System.out.println(candidateWord);
	    } */
		
	    System.out.println(candidate);
	    System.out.println("------------------");
	   	
		return candidate;
	}
	
	/*
		Checks accuracy of a method on the test sets in correctWords and wrongWords and returns it
		Working for methid = EDIT_DISTANCE, but not for CONFUSION_MATRIX
		EDIT_DISTANCE requires only testSet as testing happens on entire set itself; so trainSet can be passed as null
		CONFUSION_MATRIX requires cross-validation, so both sets should be appropriately filled
	*/
	public double checkAccuracy(int method, DataSet trainSet, DataSet testSet)
	{
		/* if(method == Framework.CONFUSION_MATRIX)
		{
			System.out.println("Under construction");
			return -1.0;
		} */
	
		int correctCount = 0;
		int wrongCount = 0;
		String predictedCorrect = "";
		
		for(int i=0; i < testSet.correctWords.size(); i++)
		{
			String actualCorrect = testSet.correctWords.get(i);
			String wrong = testSet.wrongWords.get(i);
			
			if(method == Framework.EDIT_DISTANCE)
				predictedCorrect = spellCheckEditDistance(wrong);
				
			else if(method == Framework.CONFUSION_MATRIX)
			{
				predictedCorrect = spellCheckConfusionMatrices(wrong, testSet);
			}
			
			if(predictedCorrect.equals(actualCorrect))
				correctCount++;
			else
				wrongCount++;
		}
		
		double accuracy = (double) (correctCount * 100) / (correctCount + wrongCount);
		return accuracy;
	}
	
	/*
		Computes accuracy using cross-validation
		Not possible for Edit Distance, so returns -1.0
		Accepts full data set so that it can be divided into training and test sets for each iteration of cross-validation
	*/
	public double checkCrossValidateAccuracy(int method, DataSet fullSet)
	{
		double totalAccuracy = 0.0;
	
		if(method == Framework.EDIT_DISTANCE)
		{
			System.err.println("Error: Cross-validation not possible on Edit Distance");
			return -1.0;
		}
		
		else if(method == Framework.CONFUSION_MATRIX)
		{
			// Now performing cross-validation
			for(int i=0; i<Framework.NUMBER_OF_FOLDS; i++)
			{
				DataSet[] foldSets = fullSet.createFold(i, NUMBER_OF_FOLDS);	// Returns training set in [0] and test set in [1]
				
				// Retrain error matrices for current fold
				errorMatrices.sMatrix = new int[27][27];
				errorMatrices.xMatrix = new int[27][27];
				errorMatrices.iMatrix = new int[27][27];
				errorMatrices.dMatrix = new int[27][27];
				errorMatrices.charCount = new int[27];
				
				for(int j=0; j<foldSets[0].correctWords.size(); j++)
					errorMatrices.updateMatrices(foldSets[0].wrongWords.get(j), foldSets[0].correctWords.get(j));
				
				double currentAccuracy = checkAccuracy(method, foldSets[0], foldSets[1]);
				totalAccuracy += currentAccuracy;	// Keeping track of sum of all iterations
				
				System.out.println("Accuracy for fold " + i + " = " + currentAccuracy);
			}
			
			// Cross-validation done, now output results
			return (totalAccuracy / Framework.NUMBER_OF_FOLDS);	// Return average of all folds
		}
	
		System.out.println("Under Construction");
			return -1.0;
	}
	
	public void analyseConfusionMatrices() throws IOException
	{
		BufferedReader br = new BufferedReader((new InputStreamReader(System.in)));
		System.out.println("Confusion Matrix Analysis: \n" +
				"1 Insert\n" +
				"2 Delete\n" +
				"3 Substitute\n" +
				"4 Transpose\n" +
				"Enter error type: ");
		int errorType = Integer.parseInt(br.readLine());
		System.out.println("Enter first letter: ");
		char first = br.readLine().charAt(0);
		System.out.println("Enter second letter: ");
		char second = (char)br.readLine().charAt(0);
		errorMatrices.setAnalysisParameters(first, second, errorType);
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException{
		
		Framework obj = new Framework();
		
		//obj.analyseConfusionMatrices(); //takes user input to find instances of a specific error
		
		obj.readFile();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String wrong;    // The strings to find the edit distance between
		//System.out.println("Enter wrong string");
		//wrong = br.readLine();
		
		System.out.println("The accuracy of Edit Distance Approach is: " + obj.checkAccuracy(Framework.EDIT_DISTANCE, null, obj.dataSet));
		//System.out.println("\nEdit distance: ");
		//obj.spellCheckEditDistance(wrong);
		//System.out.println("\nConfusion Matrices Approach: ");
		//obj.spellCheckConfusionMatrices(wrong, obj.dataSet);
		
		//System.out.println("The cross-validated accuracy of Confusion Matrices approach is: " + obj.checkCrossValidateAccuracy(Framework.CONFUSION_MATRIX, obj.dataSet));
	}

}

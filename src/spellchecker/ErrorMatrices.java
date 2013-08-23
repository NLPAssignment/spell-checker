package spellchecker;

import java.util.HashMap;

/*
 * Class contains 4 matrices one each for substitution, insertion, deletion and transpose errors. 
 * It also contains occurence counts of each letter
 */
public class ErrorMatrices {

	int sMatrix[][] = new int [27][27]; //substitution counts
	int xMatrix[][] = new int [27][27]; //transposition counts
	int iMatrix[][] = new int [27][27]; //insertion counts
	int dMatrix[][] = new int [27][27]; //deletion counts
	int charCount[] = new int [27]; //count of occurence of a given character
	
	static final int NO_ERROR 		= 	0;
	static final int INSERTION 		= 	1;
	static final int DELETION 		= 	2;
	static final int SUBSTITUTION 	= 	3;
	static final int TRANSPOSITION 	= 	4;
	static final int UNKNOWN_ERROR 	= 	5;
	
	char first;
	char second; 
	int errorType=0;
	public void setAnalysisParameters(char f, char s, int e)
	{
		first = f; 
		 second = s; 
		 errorType = e;
	}
	//	charIndex assigns numbers for a to z
	
	HashMap<Character, Integer> charIndex = new HashMap<Character, Integer>();
	
	public ErrorMatrices()
	{
		// Initialize the charIndex HashMap using ASCII codes
		for(int i=1; i<27; i++)
			charIndex.put((char) (i+96), i);
	}
	
	/*
	 * Input: Wrong word and correct word
	 * Output: c[0] contains 1,2,3,4 depending on type of error i,d,s or x 
	 * 		   c[0] contains 0 for no error
	 * 		   c[0] contains 5 for impossibility of single error i.e. there may be more than one transformation etc not a single insert/delete/transpose/substitution 
	 * c[1] contains index of first char
	 * c[2] contains index if second char
	 * eg for insertion c[1] contains (index of) 't' c[2] contains 'e' if 'e' is inserted after 't'
	 * similarly for deletion
	 * for substitution c[1] is replaced by c[2]
	 * for xposition c[1]c[2] was the correct order but it was exchanged in wrong word
	 * for no error or unknown error c[1] and c[2] are invalid
	 * */
	
	public int[] findError(String wrong, String correct)
	{
		int answer[] = new int[3];
		if(wrong.equals(correct)) //no error
			answer[0] = NO_ERROR;
		else if( ( wrong.length() > ( correct.length() + 1 ) ) || ( wrong.length() < ( correct.length() -1  ) ) ) //cannot be a single error
			answer[0] = UNKNOWN_ERROR;
		else //difference in length has to be atmost 1 can be insert/delete/substitute/transpose/ or 'e' impossible
		{
			
			int lengthWrong = wrong.length();
			int lengthCorrect = correct.length();
			
			if(lengthWrong > lengthCorrect)//insert error possible
			{
				//compare chars till they match
				int i;
				for( i= 0 ; i < lengthCorrect ; i++)
				{
					if(wrong.charAt(i) == correct.charAt((i)))
					{	/*Do nothing simply advance pointers */ }
					else
					{	break;	}
				}
				//i now points to index where corresponding chars differ
				if(i == lengthCorrect )//insertion on last char
				{
					answer[0] = INSERTION;
					answer[1] = (i>0) ? charIndex.get( correct.charAt(i-1) ) : 0;
					answer[2] = charIndex.get( wrong.charAt(i) );
				}
				else if( correct.substring( i ).equals( wrong.substring( i + 1 ) ) ) //insertion before last char
				{
					answer[0] = INSERTION;
					answer[1] = (i>0) ? charIndex.get( correct.charAt(i-1) ) : 0;
					answer[2] = charIndex.get( wrong.charAt(i) );
				}
				else //not a single insertion error
				{
					answer[0] = UNKNOWN_ERROR;
				}
				
			}
			else if(lengthWrong < lengthCorrect) //delete error possible
			{
				//compare chars till they match
				int i;
				for( i= 0 ; i < lengthWrong ; i++)
				{
					if(wrong.charAt(i) == correct.charAt(i))
					{	/*do nothing*/	}
					else
					{	break;		}
				}
				//i has index where corresponding chars differ
				if( i == lengthWrong ) //deletion at end
				{
					answer[0] = DELETION;
					answer[1] = (i>0) ? charIndex.get( correct.charAt(i-1) ) : 0;
					answer[2] = charIndex.get( correct.charAt(i) );
				}
				else if ( correct.substring( i + 1 ).equals( wrong.substring( i ) ) ) //deletion before last char
				{
					answer[0] = DELETION;
					answer[1] = (i>0) ? charIndex.get( correct.charAt(i-1) ) : 0;
					answer[2] = charIndex.get( correct.charAt(i) );
				}
				else
				{
					answer[0] = UNKNOWN_ERROR;
				}
				
			}
			else //length equal so substitution or transposition possible
			{
				//match corresponding chars
				int i;
				for ( i = 0 ; i < lengthCorrect ; i++ )
				{
					if(wrong.charAt(i) == correct.charAt(i))
					{ /*do nothing just advance pointer*/	}
					else
					{		break;		}
				}
				//i can never be lengthCorrect(since we've already tested for no error in beginning)
				
				if (i == (lengthCorrect-1))//last letter doesn't match thus it is a substitution
				{
					answer[0] = SUBSTITUTION;
					answer[1] = charIndex.get( correct.charAt(i) );
					answer[2] = charIndex.get( wrong.charAt(i) );
				}
				else//either substitution or transpose
				{
					if( wrong.substring( i + 1 ).equals( correct.substring( i + 1 ) ) )//substitution
					{
						answer[0] = SUBSTITUTION;
						answer[1] = charIndex.get( correct.charAt(i) );
						answer[2] = charIndex.get( wrong.charAt(i) );
					}
					else
					{
						if( correct.charAt(i) == wrong.charAt(i+1) && correct.charAt(i+1) == wrong.charAt(i) )
						{
							if( (i+2) == lengthCorrect ) //transposition at the end
							{
								answer[0] = TRANSPOSITION;
								answer[1] = charIndex.get(correct.charAt(i));
								answer[2] = charIndex.get(correct.charAt(i+1));
							}
							else if ( correct.substring( i + 2 ).equals( wrong.substring( i + 2 ) ) )
							{
								answer[0] = TRANSPOSITION;
								answer[1] = charIndex.get(correct.charAt(i));
								answer[2] = charIndex.get(correct.charAt(i+1));
							}
							else
							{
								answer[0] = UNKNOWN_ERROR;
							}
						}
						else
						{
							answer[0] = UNKNOWN_ERROR;
						}
					}
				}			
			}		
		}
		return answer;
	}
	
	/*
	 * function takes 2 words as input, a wrong word and its corresponding correct word
	 * it then finds type of error, insert, delete, substitution or transpose
	 * it then updates appropriate matrix with error count values*/
	public void updateMatrices(String wrong, String correct)
	{
		
		charCount[0]++;
		for(int i = 0 ; i < correct.length() ; i++)
		{
		//update charCount of ith character
		charCount[charIndex.get(correct.charAt(i))] ++;
		}
		
		int result[] = findError(wrong, correct);
		
		switch(result[0])
		{
		
		case NO_ERROR: 
		
			//do nothing
			break;
		
		case INSERTION:
			if (errorType == 1 && result[1] == charIndex.get(first) && result[2] == charIndex.get(second))
				System.out.println(wrong+" --> "+correct);
			iMatrix[ result[1] ][ result[2] ] ++ ; //update insertion matrix
			break;
		
		case DELETION:
			if (errorType == 2 && result[1] == charIndex.get(first) && result[2] == charIndex.get(second))
				System.out.println(wrong+" --> "+correct);
			dMatrix[ result[1] ][ result[2] ] ++ ; //update deletion matrix
			break;
		
		case SUBSTITUTION:
			if (errorType == 3 && result[1] == charIndex.get(first) && result[2] == charIndex.get(second))
				System.out.println(wrong+" --> "+correct);
			sMatrix[ result[1] ][ result[2] ] ++ ; //update substitution matrix
			break;
		
		case TRANSPOSITION:
			if (errorType == 4 && result[1] == charIndex.get(first) && result[2] == charIndex.get(second))
				System.out.println(wrong+" --> "+correct);
			xMatrix[ result[1] ][ result[2] ] ++ ; //update transposition matrix
			break;
		
		case UNKNOWN_ERROR: 
			
			//do nothing
			break;
			
		}
	}
	
	/*
	 * P(second/first) with respect to insertion 
	 * i.e. probability that 'second' char is inserted after 'first' char
	 * */
	public double getIProbability(int first, int second)
	{
		double probability;
		
		probability = (double)(iMatrix[first][second] +1) / (charCount[first] +27);
		
		return probability;
	}
	
	/*
	 * P(second/first) with respect to deletion 
	 * i.e. probability that 'second' char is deleted after 'first' char
	 * */
	public double getDProbability(int first, int second)
	{
		double probability;
		
		probability = (double)(dMatrix[first][second] +1)/ (charCount[first] +27);
		
		return probability;
	}
	
	/*
	 * P(second/first) with respect to substitution
	 * i.e. probability that 'second' char is substituted for 'first' char
	 * */
	public double getSProbability(int first, int second)
	{
		double probability;
		
		probability = (double)(sMatrix[first][second] + 1) / (charCount[first]+27);
		
		return probability;
	}
	
	/*
	 * P(second/first) with respect to transposition 
	 * i.e. probability that 'second' char is exchanged with 'first' char
	 * */
	public double getXProbability(int first, int second)
	{
		double probability;
		
		probability = (double)(xMatrix[first][second] + 1) /( charCount[first] +27);
		
		return probability;
	}
	
	public void printCounts()
	{
		System.out.println("\n---COUNTS---\n");
		char ch = 96;
		for ( int i = 0 ; i < charCount.length ; i ++ , ch++)
		{
			System.out.println(ch+" : "+charCount[i]);
		}
	}
	
	public void printIMatrix()
	{
		System.out.println("\n---- I matrix ----\n");
		Utilities.printErrorMatrix(iMatrix);
	}
	
	public void printDMatrix()
	{
		System.out.println("\n---- D matrix ----\n");
		Utilities.printErrorMatrix(dMatrix);
	}
	
	public void printSMatrix()
	{
		System.out.println("\n---- S matrix ----\n");
		Utilities.printErrorMatrix(sMatrix);
	}
	
	public void printXMatrix()
	{
		System.out.println("\n---- X matrix ----\n");
		Utilities.printErrorMatrix(xMatrix);
	}
	
	public static void main(String[] args) {
		ErrorMatrices em = new ErrorMatrices();
		
		em.updateMatrices("abc", "bc"); //insertion at start
		em.updateMatrices("abc", "bc"); //insertion at start
		em.updateMatrices("abcd", "abc");//insertion at end
		em.updateMatrices("abdc", "abc"); //insertion in middle
		System.out.println("P(d/c) "+em.getIProbability(3, 4));
		em.printIMatrix();
		
		em.updateMatrices("bc", "abc"); //deletion at start
		em.updateMatrices("abc", "abcd");//deletion at end
		em.updateMatrices("abc", "abdc"); //deletion in middle
		em.printDMatrix();
		
		em.updateMatrices("abc", "acb"); //xposition at end
		em.updateMatrices("abc", "bac"); //xpsn at start
		em.updateMatrices("abcd", "acbd");//xpsn in middle
		em.printXMatrix();
		
		em.updateMatrices("abcd", "abce"); //subs at end
		em.updateMatrices("abcd", "ebcd"); //subs at start
		em.updateMatrices("abcd", "abed"); //subs in middle
		em.printSMatrix();
		
	/*
		int ch[] = em.findError("lekha", "lekha");
		System.out.println("Error type: "+ch[0]);
		System.out.println("Char1: "+ch[1]);
		System.out.println("Char2: "+ch[2]);
		*/
		
	}

}

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
	
	//	charIndex assigns numbers for a to z
	HashMap<Character, Integer> charIndex = new HashMap<Character, Integer>();
	
	public ErrorMatrices()
	{
		// Initialize the charIndex HashMap using ASCII codes
		for(int i=1; i<27; i++)
			charIndex.put((char) (i+96), i);
	}
	
	/*
	 * function takes 2 words as input, a wrong word and its corresponding correct word
	 * it then finds type of error, insert, delete, substitution or transpose
	 * it then updates appropriate matrix with error count values*/
	
	
	public void updateMatrices(String wrong, String correct)
	{
		int lengthWrong = wrong.length();
		int lengthCorrect = correct.length();
		
		//System.out.println("wrong word: "+wrong+"\tlength: "+lengthWrong);
		//System.out.println("correct word: "+correct+"\tlength: "+lengthCorrect);

		charCount[0]++;
		for(int i = 0 ; i < correct.length() ; i++)
		{
			//update charCount of ith character
			charCount[charIndex.get(correct.charAt(i))] ++;
		}
		
		//now we make a single error assumption and first detect which type of error has occured
		//1-insert
		//2-delete
		//3-substitution
		//4-transpose
		
		if(lengthWrong > lengthCorrect)//insert error
		{
			//System.out.println("insert");
			
			//the following loop compares chars of correct and wrong string
			//till corresponding chars are found same, no action is performed
			//the first point of difference gives us the inserted char
			int i;
			for( i= 0 ; i < lengthCorrect ; i++)
			{
				if(wrong.charAt(i) == correct.charAt((i)))
				{
					//Do nothing simply advance pointers
				}
				else
				{
					break;
				}
			}
			int previousCharIndex = (i>0) ? charIndex.get(correct.charAt(i-1)):0;
			int insertedCharIndex = charIndex.get(wrong.charAt(i));
			iMatrix[previousCharIndex][insertedCharIndex] ++; //update insert matrix count
			//System.out.println("inserted "+insertedCharIndex+" after "+previousCharIndex);
			//System.out.println("inserted char: "+wrong.charAt(i));
		}
		else if(lengthWrong < lengthCorrect) //delete error
		{
			//System.out.println("delete");
			
			
			int i;
			for( i= 0 ; i < lengthWrong ; i++)
			{
				if(wrong.charAt(i) == correct.charAt(i))
				{
					//do nothing
				}
				else
				{
					break;
				}
			}
			int previousCharIndex = (i>0) ? charIndex.get(correct.charAt(i-1)):0;
			int deletedCharIndex = charIndex.get(correct.charAt(i));
			dMatrix [previousCharIndex][deletedCharIndex] ++; //update deletion matrix
			//System.out.println("deleted "+deletedCharIndex+" after "+previousCharIndex);
			//System.out.println("char deleted: "+correct.charAt(i));
		}
		else //substitution or transposition
		{
			int i;
			for ( i = 0 ; i < lengthCorrect ; i++ )
			{
				if(wrong.charAt(i) == correct.charAt(i))
				{
					//do nothing
				}
				else
				{
					break;
				}
			}
			
			//System.out.println(i);
			
			if(i == lengthCorrect)//no error found
			{
				//do nothing wrt error matrices
				//System.out.println("No error");
			}
			
			else if (i == (lengthCorrect-1))//last letter doesn't match thus it is a substitution
			{
				int originalCharIndex = charIndex.get(correct.charAt(i));
				int substitutedCharIndex = charIndex.get(wrong.charAt(i));
				sMatrix [originalCharIndex][substitutedCharIndex] ++; //update substitution matrix count
				
				//System.out.println("substitution: "+originalCharIndex+" changed to "+substitutedCharIndex);
			}
			else//either substitution or transpose
			{
				if(wrong.substring(i+1).equals(correct.substring(i+1)))
				{
					int originalCharIndex = charIndex.get(correct.charAt(i));
					int substitutedCharIndex = charIndex.get(wrong.charAt(i));
					sMatrix [originalCharIndex][substitutedCharIndex] ++; //update substitution matrix count
					
					//System.out.println("substitution: "+originalCharIndex+" changed to "+substitutedCharIndex);
				}
				else
				{
					if(correct.charAt(i) == wrong.charAt(i+1) && correct.charAt(i+1) == wrong.charAt(i))
					{
						int firstCharIndex = charIndex.get(correct.charAt(i));
						int secondCharIndex = charIndex.get(correct.charAt(i+1));
						
						xMatrix [firstCharIndex][secondCharIndex] ++;
						//System.out.println("transpose: "+firstCharIndex+" exchanged with "+secondCharIndex);
					}
				}
			}
			
		}
	}
	
	public double getIProbability(int first, int second)
	{
		double probability;
		
		probability = (double)iMatrix[first][second] / charCount[first];
		
		return probability;
	}
	
	public double getDProbability(int first, int second)
	{
		double probability;
		
		probability = (double)dMatrix[first][second] / charCount[first];
		
		return probability;
	}
	
	public double getSProbability(int first, int second)
	{
		double probability;
		
		probability = (double)sMatrix[first][second] / charCount[first];
		
		return probability;
	}
	
	public double getXProbability(int first, int second)
	{
		double probability;
		
		probability = (double)xMatrix[first][second] / charCount[first];
		
		return probability;
	}
	
	public void printCounts()
	{
		System.out.println("\n---COUNTS---\n");
		for ( int i = 0 ; i < charCount.length ; i ++ )
		{
			System.out.println(i+" : "+charCount[i]);
		}
	}
	public void printIMatrix()
	{
		System.out.println("\n---- I matrix ----\n");
		System.out.print("     ");
		for(int i = 'a' ; i <= 'z' ; i++)
			System.out.print("    "+(char)i);
		System.out.println();
		for(int i=0; i<27; i++)
		{
			System.out.print((char)(i+96)+" ");
			for(int j=0; j<27; j++)
				System.out.printf("%4d ",iMatrix[i][j]);
			System.out.println();
			
		}
	}
	
	public void printDMatrix()
	{
		System.out.println("\n---- D matrix ----\n");
		System.out.print("     ");
		for(int i = 'a' ; i <= 'z' ; i++)
			System.out.print("    "+(char)i);
		System.out.println();
		
		for(int i=0; i<27; i++)
		{
			System.out.print((char)(i+96)+" ");
			for(int j=0; j<27; j++)
				System.out.printf("%4d ",dMatrix[i][j]);
			System.out.println();
		}
	}
	
	public void printSMatrix()
	{
		System.out.println("\n---- S matrix ----\n");
		System.out.print("     ");
		for(int i = 'a' ; i <= 'z' ; i++)
			System.out.print("    "+(char)i);
		System.out.println();
		
		for(int i=0; i<27; i++)
		{
			System.out.print((char)(i+96)+" ");
			for(int j=0; j<27; j++)
				System.out.printf("%4d ",sMatrix[i][j]);
			System.out.println();
		}
	}
	
	public void printXMatrix()
	{
		System.out.println("\n---- X matrix ----\n");
		System.out.print("     ");
		for(int i = 'a' ; i <= 'z' ; i++)
			System.out.print("    "+(char)i);
		System.out.println();
		
		for(int i=0; i<27; i++)
		{
			System.out.print((char)(i+96)+" ");
			
			for(int j=0; j<27; j++)
				System.out.printf("%4d ",xMatrix[i][j]);
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		ErrorMatrices em = new ErrorMatrices();
		
		em.updateMatrices("abc", "bc"); //insertion at start
		em.updateMatrices("abc", "bc"); //insertion at start
		em.updateMatrices("abcd", "abc");//insertion at end
		//em.updateMatrices("abdc", "abc"); //insertion in middle
		System.out.println("P(d/c) "+em.getIProbability(3, 4));
		em.printIMatrix();
		
		//em.updateMatrices("bc", "abc"); //deletion at start
		//em.updateMatrices("abc", "abcd");//deletion at end
		//em.updateMatrices("abc", "abdc"); //deletion in middle
		
		//em.printDMatrix();
		
		//em.updateMatrices("abc", "acb"); //xposition at end
		//em.updateMatrices("abc", "bac"); //xpsn at start
		//em.updateMatrices("abcd", "acbd");//xpsn in middle
		
		//em.printXMatrix();
		
		//em.updateMatrices("abcd", "abce"); //subs at end
		//em.updateMatrices("abcd", "ebcd"); //subs at start
		//em.updateMatrices("abcd", "abed"); //subs in middle
		//em.printSMatrix();
		
	}

}

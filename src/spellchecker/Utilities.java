package spellchecker;

public class Utilities
{
	/*
		Takes a word and checks if it:
		1: Is Non-Empty
		2: Has no special symbols
	*/
	public static boolean isValid(String word)
	{
		if(!word.isEmpty() && word.matches("[a-z]*"))
			return true;
		else
			return false;
	}
	
	/*
		Takes one of the four error matrices and prints it; made to get rid of code duplication
	*/
	public static void printErrorMatrix(int[][] matrix)
	{
		System.out.print("     ");
		for(int i = 'a' ; i <= 'z' ; i++)
			System.out.print("    " + (char)i);
		System.out.println();
		for(int i=0; i<27; i++)
		{
			System.out.print((char)(i+96)+" ");
			for(int j=0; j<27; j++)
				System.out.printf("%4d ", matrix[i][j]);
			System.out.println();
		}
	}
}
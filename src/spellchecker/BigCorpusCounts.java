package spellchecker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BigCorpusCounts {

	Bigrams bigrams = new Bigrams();
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		Bigrams bigrams = new Bigrams();
		BufferedReader br = new BufferedReader(new FileReader("big.txt"));
		System.out.println("Reading corpus...");
		String line;
		while((line = br.readLine()) != null)
		{
			String words[] = line.split(" ");
			for (int i = 0 ; i < words.length ; i++)
			{
				if(Utilities.isValid(words[i]))
					bigrams.update(words[i]);
			}
		}
		bigrams.printProbabilities();
	}

}

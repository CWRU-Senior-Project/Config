import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HtmlParser 
{
	/* **************************
	 * Properties
	 * **************************/
	
	/* **************************
	 * Functions
	 * **************************/
	
	/**
	 * Remove brackets from text
	 * @param inputText
	 * @return
	 */
	public static String removeBrackets(final String inputText)
	{
		String lessThan = "&lt";
		String greatherThan = "&gt";
		String outputText = inputText.replaceAll(lessThan, "");
		outputText = outputText.replaceAll(greatherThan, "");
		return outputText.toLowerCase().trim();
	}
	
	/**
	 * Remove white space from text
	 * @param inputText
	 * @return
	 */
	public static String removeBlanks(final String inputText)
	{
		String blank = "&nbsp;";
		String outputText = inputText.replaceAll(blank, "");
		return outputText.toLowerCase().trim();
	}
	
	/**
	 * Remove existing HTML tags from text
	 * @param inputText
	 * @return
	 */
	public static List<String> removeTags(final String inputText)
	{
		return new ArrayList<String>(Arrays.asList(inputText.split("(<[^<]*>)|(</[^<]*>)")));
	}
	
	/**
	 * Extract the textual content from the html formatted text
	 * @param inputText
	 * @return
	 */
	public static List<String> getContentFromLine(final String inputText)
	{
		List<String> content = removeTags(inputText);
		List<String> output = new ArrayList<String>();
		
		for (String text : content)
		{
			String temp = removeBrackets(removeBlanks(text));
			if (0 < temp.length())
			{
				output.add(temp);
			}
		}
		
		return output;
	}
	
	/**
	 * Get the substring following the search text after formatting has been removed
	 * @param inputLine
	 * @param searchText
	 * @return
	 */
	protected String getNextContent(String inputLine, String searchText)
	{
		List<String> content = getContentFromLine(inputLine);
		String output = "";
		
		if (content.contains(searchText))
		{
			int index = content.indexOf(searchText);
			
			if (content.size() > (index + 1))
			{
				output = content.get(index + 1);
			}
		}
		
		return output;
	}

	/**
	 * Retrieve string tag name from input line
	 * @param inputLine
	 * @return
	 */
	public String getTagFromLine(String inputLine)
	{
		List<String> splitLine = getContentFromLine(inputLine);
		String output = "";
		
		if (1 < splitLine.size())
		{
			 output = removeBrackets(removeBlanks(splitLine.get(1)));
		}
		
		return output;
	}
	
	String getDescriptionFromLine(String inputLine)
	{
		return getNextContent(inputLine, "description:");
	}
	
	String getRequiredFromLine(String inputLine)
	{
		return getNextContent(inputLine, "required:");
	}
	
	String getTagTypeFromLine(String inputLine)
	{
		return getNextContent(inputLine, "type:");
	}
	
	/**
	 * Group text so each line in output corresponds to one div
	 * @param inputBlock
	 * @return
	 */
	static List<String> setOneDivPerLine(final List<String> inputBlock)
	{
		List<String> outputList = new ArrayList<String>();
		String nextLine = "";
		String temp;
		
		for (String line : inputBlock)
		{	
			while (0 < line.length())
			{
				//	if contains div, split and create new line
				if (line.contains("<div") && line.contains("</div>"))
				{
					int startIndex = line.indexOf("<div");
					int endIndex = line.indexOf("</div>");
					int endOfDiv = 0;
					
					if (startIndex < endIndex)
					{
						endOfDiv = getEndOfDiv("<div", line);
						nextLine += line.substring(0, startIndex);

						if (0 < nextLine.length())
						{
							outputList.add(nextLine);
							nextLine = "";
						}
						
						temp = line.substring(startIndex, endOfDiv + 1);
						if (0 < temp.length())
						{
							outputList.add(temp);
						}
					}
					else
					{
						endOfDiv = endIndex + 5;
						nextLine += line.substring(0, endIndex);

						if (0 < nextLine.length())
						{
							outputList.add(nextLine);
							nextLine = "";
						}
						
						temp = line.substring(endIndex, endOfDiv + 1);
						if (0 < temp.length())
						{
							outputList.add(temp);
						}
					}
					
					line = line.substring(endOfDiv + 1);
					
					continue;
				}
				
				else if (line.contains("<div"))
				{
					int startIndex = line.indexOf("<div");
					nextLine += line.substring(0, startIndex);
					
					if (0 < nextLine.length())
					{
						outputList.add(nextLine);
						nextLine = "";
					}
					
					int endOfDiv = getEndOfDiv("<div", line);
					
					temp = line.substring(startIndex, endOfDiv + 1);
					if (0 < temp.length())
					{
						outputList.add(temp);
					}
					
					line = line.substring(endOfDiv + 1);
				}
				
				//	if contains /div, split and create new line
				else if (line.contains("</div>"))
				{
					int endIndex = line.indexOf("</div>");
					
					nextLine += line.substring(0, endIndex);
					
					if (0 < nextLine.length())
					{
						outputList.add(nextLine);
						nextLine = "";
					}
					
					int endOfDiv = endIndex + 5;
					
					temp = line.substring(endIndex, endOfDiv + 1);
					if (0 < temp.length())
					{
						outputList.add(temp);
					}
					
					line = line.substring(endOfDiv + 1);
				}
				
				//	if not, concatenate with last line
				else if (!line.contains("div"))
				{
					nextLine += line;
					break;
				}
			}
		}
		
		return outputList;
	}
	
	/**
	 * Identify where a DIV tag ends
	 * @param tag
	 * @param line
	 * @return
	 */
	static int getEndOfDiv(String tag, String line)
	{
		int endOfDiv = 0;
		
		String[] temp = line.split(tag);
		for (String tempLine : temp)
		{
			if ((!tempLine.startsWith("<")) && (tempLine.contains(">")))
			{
				endOfDiv += tempLine.indexOf(">");
				break;
			}
			else
			{
				endOfDiv += tempLine.length() + tag.length();
			}
		}
		return endOfDiv;
	}
	
	/**
	 * Find the ending depth of the block
	 * NOTE: does not report min/max depth
	 * 
	 * @param inputBlock
	 * @return
	 */
	static int getEndDepth(List<String> inputBlock)
	{
		int depth = 0;
		
		for (String line : inputBlock)
		{
			if (line.contains("<div"))
			{
				depth++;
			}
			else if (line.contains("</div>"))
			{
				depth--;
			}
		}
		
		return depth;
	}
	
}

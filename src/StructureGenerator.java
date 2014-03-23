import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class StructureGenerator 
{
	public List<String> readFromFile(String filename)
	{
		List<String> content = new ArrayList<String>();
		
		try
		{
			Path path = FileSystems.getDefault().getPath(filename);
			content = Files.readAllLines(path, Charset.defaultCharset());
		}
		catch (IOException e)
		{
			System.err.println("Error encountered while reading file: " + e.getLocalizedMessage());
		}
		
		return content;
	}
	
	public TagNode generateNodes(List<String> inputBlock)
	{
		TagNode parentNode = new TagNode("parent", null);
		TagNode superParent = parentNode;
		TagNode currentNode;
		String currentAnchor = ""; 
		
		List<String> nodeBlock = new ArrayList<String>();
		
		for (String line : HtmlParser.setOneDivPerLine(inputBlock))
		{
			//	if next anchor found, create node
			if ((line.contains("<a")) && (!currentAnchor.equalsIgnoreCase(line)))
			{
				currentNode = createNode(nodeBlock);
				if (null != currentNode)
				{
					currentNode.addParent(parentNode);
					parentNode.addChild(currentNode);
				}
				parentNode = getAncestor(parentNode, HtmlParser.getEndDepth(nodeBlock));
//				System.out.println(currentNode);
				nodeBlock = new ArrayList<String>();
				currentAnchor = line;
			}
			
			nodeBlock.add(line);
		}
		
		//	create node for last anchor
		currentNode = createNode(nodeBlock);
		if (null != currentNode)
		{
			currentNode.addParent(parentNode);
			parentNode.addChild(currentNode);
			parentNode = getAncestor(parentNode, HtmlParser.getEndDepth(nodeBlock));
		}
//		System.out.println(currentNode);
		
//		System.out.println(superParent.getStructure());
		return superParent;
	}
	
	public TagNode getAncestor(TagNode currentNode, int depth)
	{
		TagNode output = null;
		
		if (0 == depth)
		{
			output = currentNode;
		}
		else if (0 < depth)
		{
			List<TagNode> children = currentNode.children;
			if ((null != children) && (0 < children.size()))
			{
				output = getAncestor(children.get(children.size() - 1), depth - 1);
			}
		}
		else
		{
			List<TagNode> parents = currentNode.parents;
			if ((null != parents) && (0 < parents.size()))
			{
				output = getAncestor(parents.get(parents.size() - 1), depth + 1);
			}
		}
		
		return output;
	}
	
	public TagNode createNode(List<String> inputBlock)
	{
		TagNode node = null;
		List<String> content = new ArrayList<String>();
//		System.out.println("Depth:\t" + HtmlParser.getEndDepth(inputBlock));
		
		for (String line : inputBlock)
		{
			content.addAll(HtmlParser.getContentFromLine(line));
		}
		
		if ((7 <= content.size()) && (0 < content.get(0).trim().length()))
		{
			node = new TagNode("", null);
			
			//	tag name
			node.tagName = content.get(0);
			
			//	description:
			node.description = content.get(2);
			
			//	required:
			String required = content.get(4);
			if ("0" == required)
			{
				node.required = FuzzyBoolean.YES;
			}
			else if ("1" == required)
			{
				node.required = FuzzyBoolean.NO;
			}
			else
			{
				node.required = FuzzyBoolean.MAYBE;
			}
			
			//	type:
			node.tagType = content.get(6);
			
			updateMap(tagTypeMap, node.tagType);
			
			//	default:
			int defaultValueIndex = content.indexOf("default:");
			if (-1 != defaultValueIndex)
			{
				node.defaultValue = content.get(defaultValueIndex);
			}
			
			//	attributes	
			int attributeIndex = content.indexOf("attributes");
			
			if (-1 != attributeIndex)
			{
				/*
				for (int i = attributeIndex; content.size() > i; i++)
				{
					String temp = content.get(i);
					temp = temp.substring(0, Math.min(temp.length(), 10));
					while (10 > temp.length())
					{
						temp += " ";
					}
					System.out.print("\t" + temp);
				}
				System.out.println("");
				*/
				for (int i = attributeIndex + 1; content.size() > (i + 6); i += 7)
				{
					String attribute = content.get(i);
					String description = content.get(i + 2);
					String type = content.get(i + 4);
					String defaultValue = content.get(i + 6);
					node.addAttribute(attribute, type, description, defaultValue);
					
					updateMap(attributeMap, attribute);
					updateMap(typeMap, type);
					updateMap(valueMap, defaultValue);
				}
			}
		}
		
		return node;
	}
	
	public void updateMap(Map<String, Integer> map, String key)
	{
		if (map.containsKey(key))
		{
			map.put(key, map.get(key) + 1);
		}
		else
		{
			map.put(key, 1);
		}
	}
	
	Map<String, Integer> typeMap = new HashMap<String, Integer>();
	Map<String, Integer> tagTypeMap = new HashMap<String, Integer>();
	Map<String, Integer> attributeMap = new HashMap<String, Integer>();
	Map<String, Integer> valueMap = new HashMap<String, Integer>();
}
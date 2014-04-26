import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class StructureGenerator 
{
	/* *****************************
	 * Properties
	 * *****************************/
	Map<String, Integer> typeMap = new HashMap<String, Integer>();
	Map<String, Integer> tagTypeMap = new HashMap<String, Integer>();
	Map<String, Integer> attributeMap = new HashMap<String, Integer>();
	Map<String, Integer> valueMap = new HashMap<String, Integer>();
	
	/* *****************************
	 * Functions
	 * *****************************/
	
	/**
	 * Extract lines from file as list of strings
	 * 
	 * @param filename
	 * @return
	 */
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

	/**
	 * Generate representation of SDF based on known format of web page
	 * @param inputBlock
	 * @return
	 */
	public TagNode generateNodes(List<String> inputBlock)
	{
		TagNode parentNode = new TagNode("parent", null);
		TagNode superParent = parentNode;
		TagNode currentNode = null;
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
				
				parentNode = getAncestorOrDescendent(parentNode, HtmlParser.getEndDepth(nodeBlock));
				nodeBlock = new ArrayList<String>();
				currentAnchor = line;
				
				if (null == parentNode)
				{
					break;
				}
			}
			
			nodeBlock.add(line);
		}
		
		//	create node for last anchor
		currentNode = createNode(nodeBlock);
		if (null != currentNode)
		{
			currentNode.addParent(parentNode);
			parentNode.addChild(currentNode);
			parentNode = getAncestorOrDescendent(parentNode, HtmlParser.getEndDepth(nodeBlock));
		}

		return superParent;
	}

	/**
	 * Retrieve the par
	 * @param currentNode
	 * @param depth
	 * @return
	 */
	public TagNode getAncestorOrDescendent(TagNode currentNode, int depth)
	{
		TagNode output = null;
		
		if (null == currentNode)
		{
			return output;
		}
		
		if (0 == depth)
		{
			output = currentNode;
		}
		
		//	If looking for children
		else if (0 < depth)
		{
			List<TagNode> children = currentNode.children;
			if ((null != children) && (0 < children.size()))
			{
				output = getAncestorOrDescendent(children.get(children.size() - 1), depth - 1);
			}
		}
		
		//	If looking for parents
		else
		{
			List<TagNode> parents = currentNode.parents;
			if ((null != parents) && (0 < parents.size()))
			{
				output = getAncestorOrDescendent(parents.get(parents.size() - 1), depth + 1);
			}
		}
		
		return output;
	}
	
	/**
	 * Generate a new node from line contents
	 * @param inputBlock
	 * @return
	 */
	public TagNode createNode(List<String> inputBlock)
	{
		TagNode node = null;
		List<String> content = new ArrayList<String>();
		
		for (String line : inputBlock)
		{
			content.addAll(HtmlParser.getContentFromLine(line));
		}
		
		// Filter lines based on known structure
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
			
			//	If attributes are present
			if (-1 != attributeIndex)
			{
				//	Add each attribute to the node
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
}
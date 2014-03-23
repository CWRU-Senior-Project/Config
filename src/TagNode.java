import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TagNode 
{
	String tagName;
	List<TagNode> children;
	List<TagNode> parents;
	String description;
	FuzzyBoolean required;
	String tagType;
	String defaultValue;
	Map<String, List<String>> attributeMap;
	List<TagNode> variants;
	List<TagNode> examples;
	
	public TagNode(String tagName, TagNode parent)
	{
		this.tagName = tagName;
		children = new ArrayList<TagNode>();
		parents = new ArrayList<TagNode>();
		required = FuzzyBoolean.MAYBE;
		attributeMap = new HashMap<String, List<String>>();
		variants = new ArrayList<TagNode>();
		examples = new ArrayList<TagNode>();
	}
	
	public void addChild(TagNode child)
	{
		children.add(child);
	}
	
	public void addChildren(List<TagNode> children)
	{
		this.children.addAll(children);
	}
	
	public void addParent(TagNode parent)
	{
		parents.add(parent);
	}
	
	public void addParents(List<TagNode> parents)
	{
		this.parents.addAll(parents);
	}
	
	public void addAttribute(String attribute, String type, String description, String defaultValue)
	{
		List<String> tagAttributeData = new ArrayList<String>();
		tagAttributeData.add(type);
		tagAttributeData.add(description);
		tagAttributeData.add(defaultValue);
		
		attributeMap.put(attribute, tagAttributeData);
	}
	
	@Override
	public String toString()
	{
		String output = "Tag Name:\t" + tagName + "\n";
		output += "Description:\t" + description + "\n";
		output += "Tag Type:\t" + tagType + "\n";
		output += "Default:\t" + defaultValue + "\n";
		output += "Required:\t" + required + "\n";
		output += "Attributes:\t" + attributeMap + "\n";
		output += "Parents:\t";
		
		for (TagNode parent : parents)
		{
			output += parent.tagName + ", ";
		}
		output += "\n";
		output += "Children:\t";
		
		for (TagNode child : children)
		{
			output += child.tagName + ", ";
		}
		output += "\n";
		return output;
	}
	
	public String getStructure()
	{
		return getStructure("");
	}
	
	public String getStructure(String prefix)
	{
		String output = tagName + "\n";
		for (TagNode child : children)
		{
			output += prefix +"|\n" + prefix + "|__" + child.getStructure("\t" + prefix);
		}
		
		return output;
	}
	
	public String getCompleteRecord()
	{
		return getCompleteRecord("");
	}
	
	public String getCompleteRecord(String prefix)
	{
		String output = tagName + "\n\n" + this + "\n";
		for (TagNode child : children)
		{
			output += prefix +"|\n" + prefix + "|__" + child.getCompleteRecord("\t" + prefix);
		}
		
		return output;
	}
}

enum FuzzyBoolean
{
	YES, NO, MAYBE;
}
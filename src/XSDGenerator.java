import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

public class XSDGenerator 
{
	public void writeToFile(String filename, List<String> outputLines)
	{
		try
		{
			Path path = FileSystems.getDefault().getPath(filename);
			java.nio.file.Files.write(path, outputLines, Charset.defaultCharset(), (OpenOption) StandardOpenOption.CREATE);
		} 
		catch (IOException e)
		{
			System.err.println("Error encountered with file writing: " + e.getLocalizedMessage());
		}
	}
	
	public void generateXSDFromTagNode(TagNode node)
	{
		//	if no children and no attributes, simple type
		if ((node.children.isEmpty()) && (node.attributeMap.isEmpty()))
		{
			
		}
		
		//	otherwise complex type
		else
		{
			
		}
	}
	
	public List<String> createComplexTypes()
	{
		List<String> output = new ArrayList<String>();
		
		//	pose
		String pose = "<xs:element name=\"pose\">"
				+ "<xs:simpleType>"
				+    "<xs:restriction base=\"xs:string\">"
				+       "<xs:whiteSpace value=\"replace\" />"
				+       "<xs:pattern value=\"([ ]*(-)?[0-9]+(\\.[0-9]*)?[ ]+){5}((-)?[0-9]+(\\.[0-9]*)?[ ]*)\" />"
				   + "</xs:restriction>"
				+ "</xs:simpleType>"
				+ "</xs:element>";
				
		output.add(pose);
		
		//	time
		String time = "<xs:element name=\"time\">"
				+ "<xs:simpleType>"
				+    "<xs:restriction base=\"xs:string\">"
				+       "<xs:whiteSpace value=\"replace\" />"
				+       "<xs:pattern value=\"[ ]*[0-9]+(\\.[0-9]*)?[ ]+[0-9]+(\\.[0-9]*)?[ ]*\" />"
				   + "</xs:restriction>"
				+ "</xs:simpleType>"
				+ "</xs:element>";
		output.add(time);
		
		//	color
		String color = "<xs:element name=\"color\">"
				+ "<xs:simpleType>"
				+    "<xs:restriction base=\"xs:string\">"
				+       "<xs:whiteSpace value=\"replace\" />"
				+       "<xs:pattern value=\"([ ]*[0-1]+(\\.[0-9]*)?[ ]+){3}([0-1]+(\\.[0-9]*)?[ ]*)\" />"	//	TODO add 0-1 bounds checking
				   + "</xs:restriction>"
				+ "</xs:simpleType>"
				+ "</xs:element>";
		output.add(color);
		
		//	bool
		//	vector2d
		//	vector3
		
		return output;
	}
	
	public String getSimpleType(String type)
	{
		String output = "";
		
		switch (type)
		{
			case "pose":	//	0 0 0  0 0 0
				break;
			case "time":	//	0 0
				break;
			case "color":	//	0 0 0 1
				break;
			case "int":
				break;
			case "string":
				break;
			case "vector2d":	//	1 1
				break;
			case "bool":	//	0 or 1
				break;
			case "unsigned int":
				break;
			case "double":
				break;
			case "vector3":	//	1 1 1
				break;
			case "n/a":		//	no value
				break;
			default:
				break;
		}
		
		return output;
	}
	
	
	public List<String> getAttributeList(Map<String, List<String>> attributeMap)
	{
		List<String> outputList = new ArrayList<String>();
		
		String attribute;
		
		for (String name : attributeMap.keySet())
		{
			List<String> values = attributeMap.get(name);
			String type = values.get(0);
			String description = values.get(1);
			String defaultValue = values.get(2);
			
			switch (type)
			{
				case "int":
					type = "xs:int";
					break;
				case "unsigned int":
					type = "xs:unsignedInt";
					break;
				case "bool":
					type = "xs:boolean";
					break;
				case "string":
				default:
					type = "xs:string";
					break;
			}
			
			attribute = "<xs:attribute"
					+ " name=\"" + name + "\""
					+ " type=\"" + type + "\""
					+ " default=\"" + defaultValue + "\""
	//				+ " fixed=\"" + fixed + "\""
	//				+ " use=\"" + use + "\""
					+ " />"
					+ "   <!-- Description: " + description + " -->";
			
			outputList.add(attribute);
		}
		
		return outputList;
	}
}

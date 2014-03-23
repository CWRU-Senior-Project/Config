import java.util.ArrayList;
import java.util.List;


public class HtmlParser_Test 
{
	public static void main(String[] args)
	{
		HtmlParser parser = new HtmlParser();
		StructureGenerator gen = new StructureGenerator();
		TagNode parent = gen.generateNodes(gen.readFromFile("C:\\Users\\EPHESUS\\Downloads\\SDF_v1.html"));
		System.out.println(parent.getCompleteRecord());
		
	//	System.out.println(gen.typeMap);
	//	System.out.println(gen.attributeMap);
	//	System.out.println(gen.valueMap);
		System.out.println(gen.tagTypeMap);
	}
}

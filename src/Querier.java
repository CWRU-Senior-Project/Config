import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//	TODO: implement single-pass to answer all queries
public class Querier 
{
	//	model or link name contains *wheel*
	boolean modelContainsWheel(TagNode tagNode)
	{
		boolean wheelFound = false;
		
		List<TagNode> foundList = findNodesByTagName("model", tagNode, 6);
		Iterator<TagNode> iter = foundList.iterator();
		
		while ((!wheelFound) && (iter.hasNext()))
		{
			TagNode node = iter.next();
			
			if (containsAttribute(node, "name", "*wheel*"))
			{
				wheelFound = true;
			}
		}
		
		if (!wheelFound)
		{
			foundList = findNodesByTagName("link", tagNode, 7);
			iter = foundList.iterator();
			
			while ((!wheelFound) && (iter.hasNext()))
			{
				TagNode node = iter.next();
				
				if (containsAttribute(node, "name", "*wheel*"))
				{
					wheelFound = true;
				}
			}
		}
		
		return wheelFound;
	}
	
	//	search root and all children for nodes with tagName
	List<TagNode> findNodesByTagName(String tagName, TagNode tagNode, int maxDepth)
	{
		List<TagNode> foundNodes = new ArrayList<TagNode>();
		List<TagNode> searchableNodes = new ArrayList<TagNode>();
		
		for (TagNode child : tagNode.children)
		{
			if (tagName.equalsIgnoreCase(child.tagName))
			{
				foundNodes.add(child);
			}
			
			//	TODO: validate assumption that child nodes cannot contain nodes with the same tagName 
			else
			{
				searchableNodes.add(child);
			}
		}
		
		for (TagNode next : searchableNodes)
		{
			foundNodes.addAll(findNodesByTagName(tagName, next, maxDepth - 1));
		}
		
		return foundNodes;
	}
	
	//	check attribute
	boolean containsAttribute(TagNode tagNode, String attributeName, String regexAttributeValue)
	{
		boolean output = false;
		String value;
		
		//	TODO: rename existing attribute map to definition; use attributeMap for actual stored values
		if (
				(tagNode.attributeMap.containsKey(attributeName))
				&& ((value = tagNode.attributeMap.get(attributeName)).matches(regexAttributeValue))
			)
		{
			output = true;
		}
		
		return output;
	}
	
	//	totalMass = sum of model masses
	//	error if includes a model with no mass
	boolean modelHasComponentMass(TagNode tagNode)
	{
		
		return false;
	}
	
	//	Ex(x.link.collision.geometry = plane, x = model, x.pose, x.static = true)
	boolean worldHasValidTerrain(TagNode tagNode)
	{
		boolean output = false;
		
		for (TagNode child : tagNode.children)
		{
			if (
					("model".equalsIgnoreCase(child.tagName))
					&& (containsPose(child))
					&& (isStaticModel(child))
					&& (hasGeometry("plane", child))
				)
			{
				output = true;
				break;
			}
		}
		
		return output;
	}
	
	boolean hasGeometry(String geometry, TagNode tagNode)
	{
		boolean output = false;
		
		List<String> substructure = new ArrayList<String>();
		substructure.add("link");
		substructure.add("collision");
		substructure.add("geometry");
		substructure.add(geometry);
		
		if (containsSubstructure(substructure, tagNode))
		{
			output = true;
		}
		
		return output;
	}
	
	boolean containsPose(TagNode tagNode)
	{
		boolean output = false;
		
		List<String> substructure = new ArrayList<String>();
		substructure.add("pose");
		
		if (containsSubstructure(substructure, tagNode))
		{
			output = true;
		}
		
		return output;
	}

	//	AxEy(x = model, x.static != true, x.link = y, y = link, y.inertial.mass != 0)
	boolean worldHasMasslessNonStaticObjects(TagNode tagNode)
	{
		boolean output = false;
		
		for (TagNode child : tagNode.children)
		{
			if (
					("model".equalsIgnoreCase(child.tagName)) 
					&& !(isStaticModel(child))
					&& (modelHasZeroMass(child))
				)
			{
				output = true;
				break;
			}
		}
		
		return output;
	}
	
	boolean modelHasZeroMass(TagNode tagNode)
	{
		boolean zeroMass = false;
		
		List<String> substructure = new ArrayList<String>();
//		substructure.add("link");
		substructure.add("inertial");
		substructure.add("mass");
		
		//	if all links have zero mass
		for (TagNode child : tagNode.children)
		{
			if (
					("link".equalsIgnoreCase(child.tagName))
					&& (!containsSubstructure(substructure, child))
					// || contains value != 0
				)
			{
				zeroMass = true;
				break;
			}
		}
		
		return zeroMass;
	}


	boolean worldContainsFrictionlessNonStaticObjects(TagNode tagNode)
	{
		boolean output = false;
		
		//	loop through objects in world
		for (TagNode child : tagNode.children)
		{
			//	TODO add filtering for static on value rather than just tag
			if (
					("model".equalsIgnoreCase(child.tagName)) 
					&& !(isStaticModel(child))
					&& !(modelHasFriction(child))
				)
			{
				output = true;
				break;
			}
		}
		//	modelHasFriction()
		return output;
	}

	//	AxEy(x = model, x.link = y, y = link, y.collision.surface.friction = z,	(z.ode.mu != 0) || (z.bullet.friction != 0))
	boolean modelHasFriction(TagNode tagNode)
	{
		boolean hasFriction = false;
		
		List<String> substructure = new ArrayList<String>();
//		substructure.add("link");
		substructure.add("collision");
		substructure.add("surface");
		substructure.add("friction");
		
//		List<List<String>> variants = new ArrayList<List<String>>();
		
		List<String> firstVariant = new ArrayList<String>(substructure);
		firstVariant.add("ode");
		firstVariant.add("mu");
//		variants.add(firstVariant);
		
		List<String> secondVariant = new ArrayList<String>(substructure);
		secondVariant.add("bullet");
		secondVariant.add("friction");
//		variants.add(secondVariant);
		
		for (TagNode child : tagNode.children)
		{
			if (
					("link".equalsIgnoreCase(child.tagName))
					&& (
							(containsSubstructure(firstVariant, tagNode))
							|| (containsSubstructure(secondVariant, tagNode))
						)
				)
			{
				//	TODO: get friction value
				hasFriction = true;				
			}
		}
		
		return hasFriction;
	}
	
	//	TODO: breaks on first found, add continuation
	boolean containsSubstructure(List<String> tagList, TagNode tagNode)
	{
		boolean tagFound = false;
		TagNode currentNode = tagNode;
		
		for (String tagName : tagList)
		{
			Iterator<TagNode> iter = currentNode.children.iterator();
//			List<TagNode> nextChildren = new ArrayList<TagNode>();
			tagFound = false;
			
			while (iter.hasNext())
			{
				TagNode nextChild = iter.next();
				if (tagName.equalsIgnoreCase(nextChild.tagName))
				{
					tagFound = true;
//					nextChildren.add(nextChild);
					currentNode = nextChild;
					break;
				}
			}
			
			if (!tagFound)
			{
				break;
			}
		}
		
		return tagFound;
	}
	
	boolean isStaticModel(TagNode model)
	{
		List<String> rejectList = new ArrayList<String>();
		rejectList.add("static");
		return containsSubstructure(rejectList, model);
	}
}

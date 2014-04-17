import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//	TODO: implement single-pass to answer all queries
public class Querier 
{
	
	boolean validateWorldFile(TagNode referenceRoot, TagNode worldRoot)
	{
		//	9a	terrain element location + orientation
		boolean terrainReqMet = isTerrainReqMet(referenceRoot, worldRoot);
		
		//	9b	location, orientation, mass of obstacles
		boolean obstacleReqMet = isObstacleReqMet(referenceRoot, worldRoot);
		
		//	9c	material has frictional value
		boolean materialReqMet = isMaterialReqMet(referenceRoot, worldRoot);
		
		//	10a	1+ wheel with material
		boolean wheelReqMet = isWheelReqMet(referenceRoot, worldRoot);
		
		//	10b	mass = sum of components
		boolean componentMassReqMet = isComponentMassReqMet(referenceRoot, worldRoot);
		
		return terrainReqMet & obstacleReqMet & materialReqMet & wheelReqMet & componentMassReqMet;
	}
	
	
	boolean isTerrainReqMet(TagNode referenceRoot, TagNode worldRoot)
	{
		boolean terrainReqMet = true;
		List<TagNode> terrainList = getTerrainList(worldRoot);
		for (TagNode terrain : terrainList)
		{
			if ( 
					(!locationSpecified(terrain))
					|| (!orientationSpecified(terrain))
				)
			{
				terrainReqMet = false;
				break;
			}
		}
		
		return terrainReqMet;
	}
	
	boolean isObstacleReqMet(TagNode referenceRoot, TagNode worldRoot)
	{
		boolean obstacleReqMet = true;
		List<TagNode> obstacleList = getObstacleList(worldRoot);
		for (TagNode obstacle : obstacleList)
		{
			if (
					(!locationSpecified(obstacle))
					|| (!orientationSpecified(obstacle))
					|| (massSpecified(obstacle))
				)
			{
				obstacleReqMet = false;
				break;
			}
		}
		
		return obstacleReqMet;
	}
	
	boolean isMaterialReqMet(TagNode referenceRoot, TagNode worldRoot)
	{
		boolean materialReqMet = true;
		List<TagNode> materialList = getMaterialList(worldRoot);
		for (TagNode material : materialList)
		{
			if (
					(frictionSpecified(material))
				)
			{
				materialReqMet = false;
				break;
			}
		}
		
		return materialReqMet;
	}
	
	boolean isWheelReqMet(TagNode referenceRoot, TagNode worldRoot)
	{
		boolean wheelReqMet = true;
		List<TagNode> vehicleList = new ArrayList<TagNode>();	//	TODO: replace with get method
		for (TagNode vehicle : vehicleList)
		{
			List<TagNode> wheelList = new ArrayList<TagNode>();	//	TODO: replace with get method
			
			if ((wheelReqMet) && (0 < wheelList.size()))
			{
				for (TagNode wheel : wheelList)
				{
					if (
							(materialSpecified(wheel))
						)
					{
						wheelReqMet = false;
						break;
					}
				}
			}
			else
			{
				wheelReqMet = false;
				break;
			}
		}
		
		return wheelReqMet;
	}
	
	boolean isComponentMassReqMet(TagNode referenceRoot, TagNode worldRoot)
	{
		boolean componentMassReqMet = true;
		List<TagNode> objectList = new ArrayList<TagNode>();	//	TODO: replace with get method
		for (TagNode object : objectList)
		{
		/*	if (hasMultipleComponents(object))
			{
				
			}
			*/
		}
		
		return componentMassReqMet;
	}
	
	boolean locationSpecified(TagNode object)
	{
		boolean specified = false;
		List<TagNode> poseNodes = findNodesByTagName("pose", object, 1);
		if ((null != poseNodes) && (0 < poseNodes.size()))
		{
			specified = true;
		}
		return specified;
	}
	
	boolean orientationSpecified(TagNode object)
	{
		boolean specified = false;
		List<TagNode> poseNodes = findNodesByTagName("pose", object, 1);
		if ((null != poseNodes) && (0 < poseNodes.size()))
		{
			specified = true;
		}
		return specified;
	}
	
	boolean massSpecified(TagNode object)
	{
		boolean specified = false;
		List<TagNode> poseNodes = findNodesByTagName("mass", object, 5);
		if ((null != poseNodes) && (0 < poseNodes.size()))
		{
			specified = true;
		}
		return specified;
	}
	
	boolean frictionSpecified(TagNode object)
	{
		boolean specified = false;
		List<TagNode> poseNodes = findNodesByTagName("friction", object, 5);
		if ((null != poseNodes) && (0 < poseNodes.size()))
		{
			specified = true;
		}
		return specified;
	}
	
	boolean materialSpecified(TagNode object)
	{
		boolean specified = false;
		List<TagNode> poseNodes = findNodesByTagName("material", object, 5);
		if ((null != poseNodes) && (0 < poseNodes.size()))
		{
			specified = true;
		}
		return specified;
	}
	
	List<TagNode> getMaterialList(TagNode root)
	{
		return findNodesByTagName("material", root, 50);
	}
	
	
	
	/*
	 * Incomplete methods
	 */
	
	List<TagNode> getTerrainList(TagNode root)
	{
		return null;
	}
	
	List<TagNode> getObstacleList(TagNode root)
	{
		return null;
	}
	
	
	
	List<TagNode> getVehicleList(TagNode root)
	{
		return null;
	}
	
	List<TagNode> getWheelList(TagNode root)
	{
		return null;
	}
	
	List<TagNode> getObjectList(TagNode root)
	{
		return null;
	}
	
	
	
	
	
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
		
		if (0 < maxDepth)
		{
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
		}
		return foundNodes;
	}
	
	//	check attribute
	boolean containsAttribute(TagNode tagNode, String attributeName, String regexAttributeValue)
	{
		boolean output = false;
//		String value;
		
		//	TODO: rename existing attribute map to definition; use attributeMap for actual stored values
		if (
				(tagNode.attributeMap.containsKey(attributeName))
			)
		{
			for (String value : tagNode.attributeMap.get(attributeName))
			{
				if (value.matches(regexAttributeValue))
				{
					output = true;
					break;
				}
			}
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

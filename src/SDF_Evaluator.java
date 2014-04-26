import java.util.List;

/**
 * Evaluates existing SDF world or model file
 */
public class SDF_Evaluator 
{
   /* ************************
    * Properties
    * ************************/
   Querier querier;
   StructureGenerator builder;
   TagNode docRoot;
   
   /* ************************
    * Functions
    * ************************/
   
   public SDF_Evaluator()
   {
	   querier = new Querier();
   }
   
   public static void main(String[] args)
   {
	   SDF_Evaluator eval = new SDF_Evaluator();
	   
	   String filename = "";
	   
	   if ((null != args) && (1 <= args.length) && (null != args[0]))
	   {
		   filename = args[0];
		   List<String> fileLines = eval.builder.readFromFile(filename);
		   eval.docRoot = eval.builder.generateNodes(fileLines);
		   
		   boolean validFile = eval.querier.validateWorldFile(eval.docRoot);
		   
		   if (validFile)
		   {
			   System.out.println("File passed all tests.");
		   }
		   else
		   {
			   System.out.println("File failed at least one requirement.");
		   }
	   }
      else
	  {
		  System.out.println("No argument properly specified. Program accepts"
				  + " path to model or world SDF file for evaluation.");
	  }
   }
}


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;


/**
 * Generates representation of SDF and outputs to file
 * NOTE: Intermediate step to XSD Generation
 *
 */
public class SDF_Generator 
{
   /* ************************
    * Properties
    * ************************/
   StructureGenerator builder;
   TagNode docRoot;
   
   
   /* ************************
    * Functions
    * ************************/
	
   public SDF_Generator()
   {
	   builder = new StructureGenerator();
	   docRoot = null;
   }
   
   /**
    * Primary program entrance
    * Accepts SDF URL or filepath
    * 
    * @param args
    */
   public static void main(String[] args)
   {
      SDF_Generator generator = new SDF_Generator();
      
      String filename = "";
      
      // Get URL string from user input
      if ((null != args) && (1 <= args.length) && (null != args[0]))
      {
    	  String userInput = args[0];
    	  
    	  if (userInput.startsWith("--URL="))
    	  {
    		  // http://gazebosim.org/sdf/1.4/html
    		  filename = generator.downloadPage(userInput.substring(userInput.indexOf("=") + 1));
    	  }
    	  else if (userInput.startsWith("--file="))
    	  {
    		  filename = userInput.substring(userInput.indexOf("=") + 1);
    	  }
      }
      else
	  {
		  filename = "SDF_v2.html";
		  System.out.println("No argument properly specified. Program accepts"
				  + " --URL=<address> or --file=<filepath>.\n"
				  + "Default path of " + filename +" used.");
	  }
      
      
      List<String> fileLines = generator.builder.readFromFile(filename);
      generator.docRoot = generator.builder.generateNodes(fileLines);
      
      List<String> sdfStructure = generator.docRoot.getCompleteRecordAsList();
      generator.writeToFile(sdfStructure, "SDF_Structure.txt");
   }
   
   /**
    * Write Content to specified location
    * @param content
    * @param outputFileName
    */
   void writeToFile(List<String> outputLines, String filename)
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
   
   /**
    * Download web page at input URL
    * @param siteAddressText
    * @return output file name
    */
   String downloadPage(String siteAddressText)
   {
      ReadableByteChannel byteChannel = null;
      FileOutputStream outputStream = null;
      String outputFileName = "DownloadedSDF.html";
		
      try
      {
          //	download website as HTML file to folder
          URL siteAddress = new URL(siteAddressText);
          byteChannel = Channels.newChannel(siteAddress.openStream());
			
			
          File outputFile = new File(outputFileName);
          outputStream = new FileOutputStream(outputFile);
			
          //	Transfer content
          outputStream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
      }
      catch (Exception e)
      {
         System.err.println("Error attempting to download webpage at (" + siteAddressText + "):\t" + e.getLocalizedMessage());
         //	Close streams and files
         try
         {
            if (null != outputStream)
            {
               outputStream.close();
            }
            if (null != byteChannel)
            {
               byteChannel.close();
            }
         }
         catch (Exception closingErrors)
         {
				
         }
      }
      
      return outputFileName;
   }
}

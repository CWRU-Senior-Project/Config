Problem encountered while attempting to make Config v1:

SDF xml format was assumed to be governed by an XSD, but instead each module in gazebo uses their own variant of the SDF which is generated and checked programatically. A full list of the hierarchy is available online which amounts to about 40 pages of text.

An attempt to generate a basic XSD will be made in the future.

Running:
1. compile using:
      ./compileScript.bat
      cd bin

2. run using one of the following:
      java SDF_Generator
      java SDF_Generator --file=<filename>
      java SDF_Generator --URL=<url>

      java SDF_Evaluator <filename>

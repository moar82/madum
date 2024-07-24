/* (c) Copyright 2011 and following years, Aminata SABANÉ,
 * ÉCole Polytechnique de Montréal.
 * 
 * @author: Aminata SABANÉ
 *  
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.creator;

import java.util.Arrays;
import padl.creator.javafile.astVisitors.CompleteVisitor;
import padl.creator.javafile.astVisitors.VisitorFirstParsing;
import padl.creator.javafile.visitor.PadlPrinterVisitor;
import padl.kernel.ICodeLevelModel;
import padl.kernel.exception.CreationException;
import padl.kernel.impl.Factory;
import parser.input.SourceInputsHolder;
import parser.input.impl.FileSystemJavaProject;
import parser.wrapper.JavaParser;
import util.io.Output;

public class Main {
	// TODO: This class should not exist, should be a test case.
	public static void main(final String[] args) throws ClassNotFoundException {
		//the folder of the source code to analyse well organized like a project
		//final String sourcePathEntry = "./rsc/src/";
		final String sourcePathEntry = "./../PADL/src/";
		System.out.println("Main test complete visitor");
		//using librairies?
		final String classPathEntry = "";
		final String[] sourcePathEntries = new String[] { sourcePathEntry };

		//using librairies?

		final String[] classpathEntries = new String[] { classPathEntry };
		
		try {
			SourceInputsHolder javaProject = new FileSystemJavaProject(
				Arrays.asList(classpathEntries),
				Arrays.asList(sourcePathEntries));
			
			final JavaParser eclipseSourceCodeParser =
			new JavaParser(javaProject);
			
			final CompleteVisitor completeVisitor =
			new CompleteVisitor();
		eclipseSourceCodeParser.parse(completeVisitor);

			
		}
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
//		final ICodeLevelModel padlModelFromJavaFiles =
//			Factory.getInstance().createCodeLevelModel("");
//		try {
//			padlModelFromJavaFiles.create(new CompleteJavaFileCreator(
//				sourcePathEntry,
//				classPathEntry));
//		}
//		catch (final CreationException e) {
//			e.printStackTrace(Output.getInstance().errorOutput());
//		}
//		padlModelFromJavaFiles.walk(new PadlPrinterVisitor(false));
	}
}

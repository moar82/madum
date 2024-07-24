/* (c) Copyright 2009 and following years, Aminata SABANE,
 * Ecole Polytechnique de Montr̩al.
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
package madum.generator.visitor;

import java.util.Arrays;
import java.util.List;
import madum.generator.MadumClass;
import padl.kernel.ICodeLevelModel;
import parser.input.SourceInputsHolder;
import parser.input.impl.FileSystemJavaProject;
import parser.wrapper.JavaParser;

public class MadumGeneratorVisitorCaller {

	private ICodeLevelModel model;
	private SourceInputsHolder javaProject;

	/**
	 * Create the javaproject from source code; this javaproject is required for the parsing fo java files
	 * @param aModel
	 * @param aSourcePath
	 * @param aClassPath
	 */
	public MadumGeneratorVisitorCaller(
		ICodeLevelModel aModel,
		String aSourcePath,
		String aClassPath) {
		super();
		this.model = aModel;
		final String[] sourcePathEntries = new String[] { aSourcePath };

		//using librairies?

		final String[] classpathEntries = new String[] { aClassPath };

		try {
			 this.javaProject =
				new FileSystemJavaProject(
					Arrays.asList(classpathEntries),
					Arrays.asList(sourcePathEntries));

			final JavaParser eclipseSourceCodeParser =
				new JavaParser(javaProject);
		}
		catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param aSystemMadumMap
	 */
	public void callDirectFieldAccessVisitor(List<MadumClass> aListOfMadums) {
		final JavaParser eclipseSourceCodeParser = new JavaParser(javaProject);
		final DirectFieldAccessVisitor directFieldAccessVisitor =
			new DirectFieldAccessVisitor(this.model, aListOfMadums);
		eclipseSourceCodeParser.parse(directFieldAccessVisitor);

	}

}

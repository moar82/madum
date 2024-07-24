/* (c) Copyright 2011 and following years, Aminata SABAN�,
 * �Cole Polytechnique de Montr�al.
 * 
 * @author: Aminata SABAN�
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
import padl.creator.javafile.astVisitors.ConditionalModelAnnotator;
import padl.creator.javafile.astVisitors.LOCModelAnnotator;
import padl.creator.javafile.astVisitors.VisitorFirstParsing;
import padl.creator.javafile.astVisitors.VisitorSecondParsing;
import padl.kernel.ICodeLevelModel;
import padl.kernel.ICodeLevelModelCreator;
import padl.kernel.exception.CreationException;
import parser.input.SourceInputsHolder;
import parser.input.impl.FileSystemJavaProject;
import parser.input.impl.FilesAndDirectoriesJavaProject;
import parser.wrapper.JavaParser;
import util.io.Output;

public class LightJavaFileCreator implements ICodeLevelModelCreator {

	private ICodeLevelModel codeLevelModel;
	private SourceInputsHolder javaProject;

	/**
	 * Constructor for parsing all the source code
	 * @param aSourcePathEntry
	 * @param aClasspathEntry
	 */
	public LightJavaFileCreator(
		final String aSourcePathEntry,
		final String aClasspathEntry) {

		//the folder of the source code to analyse well organized like a project
		//final String sourcePathEntry = "./rsc/src/";

		final String[] sourcePathEntries = new String[] { aSourcePathEntry };

		//using librairies?

		final String[] classpathEntries = new String[] { aClasspathEntry };

		try {
			this.javaProject =
				new FileSystemJavaProject(
					Arrays.asList(classpathEntries),
					Arrays.asList(sourcePathEntries));

		}
		catch (final Exception e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}

		this.codeLevelModel = null;

	}

	/**
	 *  Constructor for parsing some java file
	 * @param aSourcePathEntry
	 * @param aClasspathEntry
	 * @param aPathFilesList
	 */
	public LightJavaFileCreator(
		final String aSourcePathEntry,
		final String aClasspathEntry,
		final String[] aPathFilesList) {

		//the folder of the source code to analyse well organized like a project

		final String[] sourcePathEntries = new String[] { aSourcePathEntry };

		//using librairies?

		final String[] classpathEntries = new String[] { aClasspathEntry };

		try {

			this.javaProject =
				new FilesAndDirectoriesJavaProject(
					Arrays.asList(classpathEntries),
					Arrays.asList(sourcePathEntries),
					Arrays.asList(aPathFilesList));

		}
		catch (final Exception e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}

		this.codeLevelModel = null;
	}

	@Override
	public void create(final ICodeLevelModel aCodeLevelModel)
			throws CreationException {

		this.createModelFormSource(aCodeLevelModel, this.javaProject);

	}

	/**
	 * Creation of the model from a source code
	 * @param aCodeLevelModel
	 * @param aSourcePathEntry
	 * @param aClassPathEntry
	 * @return
	 */
	private void createModelFormSource(
		final ICodeLevelModel aCodeLevelModel,
		final SourceInputsHolder javaProject) {

		final JavaParser eclipseSourceCodeParser =
			new JavaParser(this.javaProject);

		final VisitorFirstParsing firstParseVisitor =
			new VisitorFirstParsing(aCodeLevelModel);

		eclipseSourceCodeParser.parse(firstParseVisitor);

		final VisitorSecondParsing secondParseVisitor =
			new VisitorSecondParsing(aCodeLevelModel);

		eclipseSourceCodeParser.parse(secondParseVisitor);

		final LOCModelAnnotator locAnnotator =
			new LOCModelAnnotator(aCodeLevelModel);

		eclipseSourceCodeParser.parse(locAnnotator);

		final ConditionalModelAnnotator conditionalAnnotator =
			new ConditionalModelAnnotator(aCodeLevelModel);

		eclipseSourceCodeParser.parse(conditionalAnnotator);

		this.codeLevelModel = aCodeLevelModel;

	}

	public ICodeLevelModel getCodeLevelModel() {
		return this.codeLevelModel;
	}
}

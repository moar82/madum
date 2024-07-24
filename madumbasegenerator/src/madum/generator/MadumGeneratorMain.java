/* (c) Copyright 2009 and following years, Aminata SABANE,
 * Ecole Polytechnique de Montr��al.
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
package madum.generator;

import java.io.File;
import java.util.Iterator;

import madum.generator.utils.CSVUtils;
import padl.creator.CompleteJavaFileCreator;
import padl.event.IModelListener;
import padl.kernel.ICodeLevelModel;
import padl.kernel.exception.CreationException;
import padl.kernel.impl.Factory;
import padl.util.ModelStatistics;
import util.io.Output;

public class MadumGeneratorMain {

	// for each system. create its code level and call the generation of the
	// madum via MadumGenerator

	public static void main(String args[]) {

		
		String systemName = "containers";
		//sourcePath should contain a well organized source code
		String sourcePath = "./rsc/containers/";
		String classpath = "";
		//Folder in which the results will be stored
		String outputPath = "./rsc/results/containers/";
		
		
		//String systemName = args[0];
		//sourcePath should contain a well organized source code
		//String sourcePath = args[1];
		//String classpath = "";
		//Folder in which the results will be stored
		//String outputPath = args[2];

		// create output folder
		new File(outputPath).mkdirs();

		// build the padl model
		final ICodeLevelModel model = Factory.getInstance()
				.createCodeLevelModel("javaModel");
		final ModelStatistics statisticModelListener = new ModelStatistics();
		model.addModelListener(statisticModelListener);

		try {
			model.create(new CompleteJavaFileCreator(sourcePath, classpath));
		} catch (final CreationException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}

		// print the output of the listener
		Iterator iter = model.getIteratorOnModelListeners();
		IModelListener listener = null;
		while (iter.hasNext() && !(listener instanceof ModelStatistics)) {
			listener = (IModelListener) iter.next();

		}
		System.out.println(listener.toString());
		CSVUtils.generateListOfClassesOfPadlModel(model, outputPath, systemName);
		// generate the madum
		MadumGenerator madumGenerator = new MadumGenerator(model, sourcePath,
				classpath);
		madumGenerator.generateLightMadums(outputPath, systemName);

		System.out
				.println("@@@@@@@@@@@ Process done for the system @@@@@@@@@@@"
						+ systemName);

	}
}

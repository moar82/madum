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

import padl.creator.javafile.visitor.PadlPrinterVisitor;
import padl.kernel.ICodeLevelModel;
import padl.kernel.exception.CreationException;
import padl.kernel.impl.Factory;
import util.io.Output;

public class Main {
	public static void main(final String[] args) throws ClassNotFoundException {
		//the folder of the source code to analyse well organized like a project
		//final String sourcePathEntry = "./rsc/src/";
		final String sourcePathEntry = "./../Java Parser/src/";

		//using librairies?
		final String classPathEntry = "";

		final ICodeLevelModel padlModelFromJavaFiles =
			Factory.getInstance().createCodeLevelModel("");
		try {
			padlModelFromJavaFiles.create(new CompleteJavaFileCreator(
				sourcePathEntry,
				classPathEntry));
		}
		catch (final CreationException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
		padlModelFromJavaFiles.walk(new PadlPrinterVisitor(false));
	}
}

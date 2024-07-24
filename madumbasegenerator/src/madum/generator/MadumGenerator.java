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
package madum.generator;

import java.util.ArrayList;
import java.util.List;
import madum.generator.utils.CSVUtils;
import madum.generator.visitor.MadumGeneratorVisitorCaller;
import padl.kernel.ICodeLevelModel;

public class MadumGenerator {

	private ICodeLevelModel model;
	private MadumGeneratorVisitorCaller visitorsCaller;
	private List<MadumClass> listOfMadums;

	public MadumGenerator(
		ICodeLevelModel aModel,
		String aSourcePath,
		String aClassPath) {
		this.model = aModel;
		this.listOfMadums = new ArrayList<MadumClass>();
		this.visitorsCaller =
			new MadumGeneratorVisitorCaller(this.model, aSourcePath, aClassPath);
	}

	public void generateLightMadums(String outpathBase, String systemName) {
		this.visitorsCaller.callDirectFieldAccessVisitor(this.listOfMadums);
		for (final MadumClass madumClass : this.listOfMadums) {
			madumClass.lightProcess();
			CSVUtils.printMadum(madumClass, outpathBase, systemName);

		}
		CSVUtils.saveLightMadum(outpathBase, systemName, this.listOfMadums);

	}

	public void generateCompleteMadums() {

	}

	public List<MadumClass> geListOfMadums() {
		return this.listOfMadums;
	}

}

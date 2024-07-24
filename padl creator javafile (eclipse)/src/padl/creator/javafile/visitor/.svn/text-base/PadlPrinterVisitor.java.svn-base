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

package padl.creator.javafile.visitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IAggregation;
import padl.kernel.IAssociation;
import padl.kernel.IClass;
import padl.kernel.IComposition;
import padl.kernel.IConstituent;
import padl.kernel.IConstructor;
import padl.kernel.IContainerAggregation;
import padl.kernel.IContainerComposition;
import padl.kernel.ICreation;
import padl.kernel.IDelegatingMethod;
import padl.kernel.IDesignMotifModel;
import padl.kernel.IField;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IGetter;
import padl.kernel.IGhost;
import padl.kernel.IInterface;
import padl.kernel.IMemberClass;
import padl.kernel.IMemberGhost;
import padl.kernel.IMemberInterface;
import padl.kernel.IMethod;
import padl.kernel.IMethodInvocation;
import padl.kernel.IPackage;
import padl.kernel.IParameter;
import padl.kernel.ISetter;
import padl.kernel.IUseRelationship;
import padl.kernel.IWalker;
import util.io.Output;

public class PadlPrinterVisitor implements IWalker {

	String resultRep = "./result1/";
	String currentPackage = "./result1/";
	String currentEntity;
	PrintStream writer;
	boolean inFile;

	/**
	 * 
	 */
	public PadlPrinterVisitor() {
		this.inFile = false;
	}

	/**
	 * 
	 * @param _inFile
	 */
	public PadlPrinterVisitor(final boolean _inFile) {
		this.inFile = _inFile;
	}

	@Override
	public void close(final IAbstractLevelModel anAbstractLevelModel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close(final IClass aClass) {
		// TODO Auto-generated method stub

		this.printTopEntityClose(aClass);
	}
	@Override
	public void close(final IConstructor aConstructor) {
		// TODO Auto-generated method stub

		this.writer.println();
		this.writer.println("		Fin Constructor "
				+ aConstructor.getDisplayName());

	}

	@Override
	public void close(final IDelegatingMethod aDelegatingMethod) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close(final IDesignMotifModel aPatternModel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close(final IGetter aGetter) {
		// TODO Auto-generated method stub
		this.writer.println();
		this.writer.println("		Fin getter " + aGetter.getDisplayName());
	}

	@Override
	public void close(final IGhost aGhost) {
		// TODO Auto-generated method stub
		this.printTopEntityClose(aGhost);
	}

	@Override
	public void close(final IInterface anInterface) {
		// TODO Auto-generated method stub
		this.printTopEntityClose(anInterface);
	}

	@Override
	public void close(final IMemberClass aMemberClass) {
		// TODO Auto-generated method stub
		this.printTopEntityClose(aMemberClass);
	}

	@Override
	public void close(final IMemberGhost aMemberGhost) {
		// TODO Auto-generated method stub
		this.printTopEntityClose(aMemberGhost);
	}

	@Override
	public void close(final IMemberInterface aMemberInterface) {
		// TODO Auto-generated method stub
		this.printTopEntityClose(aMemberInterface);

	}

	@Override
	public void close(final IMethod aMethod) {
		this.writer.println();
		this.writer.println("		Fin methode " + aMethod.getDisplayName());
	}

	@Override
	public void close(final IPackage aPackage) {
	}

	@Override
	public void close(final ISetter aSetter) {
		this.writer.println();
		this.writer.println("		Fin setter " + aSetter.getDisplayName());
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public void open(final IAbstractLevelModel anAbstractLevelModel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void open(final IClass aClass) {
		// TODO Auto-generated method stub

		this.printTopEntityOpen(aClass);

	}

	@Override
	public void open(final IConstructor aConstructor) {
		// TODO Auto-generated method stub

		this.writer.println();
		this.writer.println("		Début Constructor " + aConstructor.toString());

	}

	@Override
	public void open(final IDelegatingMethod aDelegatingMethod) {
		// TODO Auto-generated method stub

	}

	@Override
	public void open(final IDesignMotifModel aPatternModel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void open(final IGetter aGetter) {
		// TODO Auto-generated method stub
		this.writer.println();
		this.writer.println("		Debut getter " + aGetter.toString());

	}

	@Override
	public void open(final IGhost aGhost) {
		// TODO Auto-generated method stub

		this.printTopEntityOpen(aGhost);

	}

	@Override
	public void open(final IInterface anInterface) {
		// TODO Auto-generated method stub

		this.printTopEntityOpen(anInterface);

	}

	@Override
	public void open(final IMemberClass aMemberClass) {
		// TODO Auto-generated method stub
		this.printTopEntityOpen(aMemberClass);

	}

	@Override
	public void open(final IMemberGhost aMemberGhost) {
		// TODO Auto-generated method stub
		this.printTopEntityOpen(aMemberGhost);

	}

	@Override
	public void open(final IMemberInterface aMemberInterface) {
		// TODO Auto-generated method stub
		this.printTopEntityOpen(aMemberInterface);

	}

	@Override
	public void open(final IMethod aMethod) {
		// TODO Auto-generated method stub

		this.writer.println();
		this.writer.println("		Debut Method " + aMethod.getDisplayID());

	}

	@Override
	public void open(final IPackage aPackage) {
		this.currentPackage = this.resultRep + aPackage.getDisplayName() + "/";

		final File f = new File(this.currentPackage);
		if (!f.exists()) {
			f.mkdir();
		}
	}

	@Override
	public void open(final ISetter aSetter) {
		// TODO Auto-generated method stub
		this.writer.println();
		this.writer.println("		Debut setter " + aSetter.toString());

	}

	void printTopEntityClose(final IFirstClassEntity entity) {

		this.writer.println();
		this.writer.println("	fin type " + entity.getClass().toString() + " "
				+ entity.getDisplayName());
		if (this.inFile) {
			if (!entity.getDisplayID().contains("$")) {
				this.writer.close();
			}
		}
	}

	void printTopEntityOpen(final IFirstClassEntity entity) {
		if (this.inFile) {
			if (!entity.getDisplayID().contains("$")) {
				final String name = entity.getDisplayName();
				final int lastIndex = name.lastIndexOf('.');

				this.currentEntity = this.currentPackage + name;

				this.currentEntity = this.currentEntity.replace('>', '+');
				this.currentEntity = this.currentEntity.replace('<', '-');
				this.currentEntity = this.currentEntity.replace('[', '-');
				this.currentEntity = this.currentEntity.replace('[', '+');
				try {
					this.writer = new PrintStream(new File(this.currentEntity));
				}
				catch (final IOException e) {
					e.printStackTrace(Output.getInstance().errorOutput());
				}
			}
		}
		else {
			// TODO: Remove, unnecessary with Output.getInstance().normalOutput()
			this.writer = System.out;
		}

		this.writer.println();
		this.writer.println("	Debut type " + entity.getClass().toString()
				+ " entity.toString()" + entity.toString());
		this.writer.println(" 		ID " + entity.getDisplayID());
		final Iterator iter = entity.getIteratorOnInheritedEntities();

		this.writer.println();
		this.writer.println("		Super classes et interfaces");
		while (iter.hasNext()) {
			final IFirstClassEntity superEntity =
				(IFirstClassEntity) iter.next();

			this.writer.println("			" + superEntity.toString());
		}

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unknownConstituentHandler(
		final String aCalledMethodName,
		final IConstituent aConstituent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(final IAggregation anAggregation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(final IAssociation anAssociation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(final IComposition aComposition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(final IContainerAggregation aContainerAggregation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(final IContainerComposition aContainerComposition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(final ICreation aCreation) {
		// TODO Auto-generated method stub

		this.writer.println();
		this.writer.println(" ICREATION :" + aCreation.toString());

	}

	@Override
	public void visit(final IField aField) {
		// TODO Auto-generated method stub

		this.writer.println();
		this.writer.println("		Field :" + aField.toString() + "cardinality "
				+ aField.getCardinality());

	}

	@Override
	public void visit(final IMethodInvocation aMethodInvocation) {
		// TODO Auto-generated method stub
		this.writer.println();
		this.writer.println("MethodINVOCATIIIIIIIIIIIIIIIIIIION");
		this.writer.println(aMethodInvocation.toString());
		this.writer.println("Type " + aMethodInvocation.getType());
		/*this.writer.println("MethodINVOCATIIIIIIIIIIIIIIIIIIION cardinality = "
				+ aMethodInvocation.getCardinality()+" ID= "+aMethodInvocation.getDisplayID()+" name= "+" comment :"+aMethodInvocation.getComment());
		if (aMethodInvocation.getFieldDeclaringEntity() != null) {
			this.writer.println(" declaring entity    "
					+ aMethodInvocation.getFieldDeclaringEntity().toString());
		}
		final IField field = aMethodInvocation.getFirstCallingField();
		if (field != null) {
			this.writer.println(" invocationField " + field.toString());
			//this.writer.println(aMethodInvocation.toString());
			//when I add the field, I have pb with the toString
		}*/
	}

	@Override
	public void visit(final IParameter aParameter) {
		// TODO Auto-generated method stub

		this.writer.println();
		this.writer.println("			Parameter :" + aParameter.toString()
				+ " cardinality" + aParameter.getCardinality());

	}

	@Override
	public void visit(final IUseRelationship aUse) {
		// TODO Auto-generated method stub

	}

}

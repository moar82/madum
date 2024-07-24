/*
 * (c) Copyright 2001-2004 Yann-Ga�l Gu�h�neuc,
 * University of Montr�al.
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
package padl.visitor;

import java.util.Iterator;
import java.util.Stack;
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
import padl.kernel.IFirstClassEntity;
import padl.kernel.IField;
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
import padl.kernel.IRelationship;
import padl.kernel.ISetter;
import padl.kernel.IUseRelationship;
import padl.kernel.IWalker;
import padl.util.Util;

/**
 * @author Yann-Ga�l Gu�h�neuc
 * @since  2007/02/01
 */
public class TripletDomainCompleteGenerator implements IWalker {
	private IAbstractLevelModel model;
	private final StringBuffer output;
	private Stack stackOfEnclosingEntities;

	public TripletDomainCompleteGenerator() {
		this.output = new StringBuffer();
		this.stackOfEnclosingEntities = new Stack();
	}
	public void close(final IAbstractLevelModel anAbstractLevelModel) {
	}
	public void close(final IClass aClass) {
		this.close((IFirstClassEntity) aClass);
	}
	public void close(final IConstructor aConstructor) {
	}
	public void close(final IDelegatingMethod aDelegatingMethod) {
	}
	public void close(final IDesignMotifModel aPatternModel) {
	}
	private void close(final IFirstClassEntity anEntity) {
		this.stackOfEnclosingEntities.pop();
	}
	public void close(final IGetter aGetter) {
	}
	public void close(final IGhost aGhost) {
		this.close((IFirstClassEntity) aGhost);
	}
	public void close(final IInterface anInterface) {
		this.close((IFirstClassEntity) anInterface);
	}
	public void close(final IMemberClass aMemberClass) {
		this.close((IFirstClassEntity) aMemberClass);
	}
	public void close(final IMemberGhost aMemberGhost) {
		this.close((IFirstClassEntity) aMemberGhost);
	}
	public void close(final IMemberInterface aMemberInterface) {
		this.close((IFirstClassEntity) aMemberInterface);
	}
	public void close(final IMethod aMethod) {
	}
	public void close(final IPackage aPackage) {
	}
	public void close(final ISetter aSetter) {
	}
	private String getCurrentEntityName() {
		final StringBuffer buffer = new StringBuffer();
		final Iterator iterator = this.stackOfEnclosingEntities.iterator();
		while (iterator.hasNext()) {
			final IFirstClassEntity firstClassEntity = (IFirstClassEntity) iterator.next();
			buffer.append(firstClassEntity.getID());
			if (iterator.hasNext()) {
				buffer.append('.');
			}
		}
		return buffer.toString();
	}
	public String getName() {
		return "Complete Triplet Domain";
	}
	public Object getResult() {
		return this.output.toString();
	}
	public void open(final IAbstractLevelModel anAbstractLevelModel) {
		this.model = anAbstractLevelModel;
	}
	public void open(final IClass aClass) {
		this.stackOfEnclosingEntities.push(aClass);

		final Iterator iteratorOnInheritedEntities =
			aClass.getIteratorOnInheritedEntities();
		while (iteratorOnInheritedEntities.hasNext()) {
			final IFirstClassEntity firstClassEntity = (IFirstClassEntity) iteratorOnInheritedEntities.next();
			this.output.append(this.getCurrentEntityName());
			this.output.append(" inherits ");
			this.output.append(Util.computeFullyQualifiedName(
				this.model,
				firstClassEntity));
			this.output.append('\n');
		}

		final Iterator iteratorOnImplementedEntities =
			aClass.getIteratorOnImplementedInterfaces();
		while (iteratorOnImplementedEntities.hasNext()) {
			final IFirstClassEntity firstClassEntity =
				(IFirstClassEntity) iteratorOnImplementedEntities.next();
			this.output.append(this.getCurrentEntityName());
			this.output.append(" implements ");
			this.output.append(Util.computeFullyQualifiedName(
				this.model,
				firstClassEntity));
			this.output.append('\n');
		}
	}
	public void open(final IConstructor aConstructor) {
	}
	public void open(final IDelegatingMethod aDelegatingMethod) {
	}
	public void open(final IDesignMotifModel aPatternModel) {
	}
	public void open(final IGetter aGetter) {
	}
	public void open(final IGhost aGhost) {
		this.stackOfEnclosingEntities.push(aGhost);
	}
	public void open(final IInterface anInterface) {
		this.stackOfEnclosingEntities.push(anInterface);

		final Iterator iteratorOnInheritedEntities =
			anInterface.getIteratorOnInheritedEntities();
		while (iteratorOnInheritedEntities.hasNext()) {
			final IFirstClassEntity firstClassEntity = (IFirstClassEntity) iteratorOnInheritedEntities.next();
			this.output.append(this.getCurrentEntityName());
			this.output.append(" inherits ");
			this.output.append(Util.computeFullyQualifiedName(
				this.model,
				firstClassEntity));
			this.output.append('\n');
		}
	}
	public void open(final IMemberClass aMemberClass) {
		this.open((IClass) aMemberClass);
	}
	public void open(final IMemberGhost aMemberGhost) {
		this.open((IGhost) aMemberGhost);
	}
	public void open(final IMemberInterface aMemberInterface) {
		this.open((IInterface) aMemberInterface);
	}
	public void open(final IMethod aMethod) {
	}
	public void open(final IPackage aPackage) {
	}
	public void open(final ISetter aSetter) {
	}
	public void reset() {
		this.output.setLength(0);
	}
	public void visit(final IAggregation anAggregation) {
		this.visit((IRelationship) anAggregation, " aggregates ");
	}
	public void visit(final IAssociation anAssociation) {
		this.visit((IRelationship) anAssociation, " associates ");
	}
	public void visit(final IComposition aComposition) {
		this.visit((IRelationship) aComposition, " contains ");
	}
	public void visit(final IContainerAggregation aContainerAggregation) {
		this.visit(
			(IRelationship) aContainerAggregation,
			" containerAggregates ");
	}
	public void visit(final IContainerComposition aContainerComposition) {
		this
			.visit((IRelationship) aContainerComposition, " containerContains ");
	}
	public void visit(final ICreation aCreation) {
		this.visit((IRelationship) aCreation, " instantiates ");
	}
	public void visit(final IField aField) {
	}
	public void visit(final IMethodInvocation aMethodInvocation) {
	}
	public void visit(final IParameter aParameter) {
	}
	private void visit(
		final IRelationship aRelationship,
		final String aRelationName) {

		if (!Util.computeFullyQualifiedName(
			this.model,
			aRelationship.getTargetEntity()).equals("")) {

			this.output.append(this.getCurrentEntityName());
			this.output.append(aRelationName);
			this.output.append(Util.computeFullyQualifiedName(
				this.model,
				aRelationship.getTargetEntity()));
			this.output.append('\n');
		}
		else {
			// TODO: How is it possible that the entity be ""???
		}
	}
	public void visit(final IUseRelationship aUse) {
		this.visit((IRelationship) aUse, " uses ");
	}
	public void unknownConstituentHandler(
		final String calledMethodName,
		final IConstituent constituent) {
	}
}

/*
 * (c) Copyright 2001, 2002 Herv� Albin-Amiot and Yann-Ga�l Gu�h�neuc,
 * Ecole des Mines de Nantes
 * Object Technology International, Inc.
 * Soft-Maint S.A.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the authors, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHORS ARE ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.kernel.impl;

import java.util.Iterator;
import padl.kernel.Constants;
import padl.kernel.IConstituent;
import padl.kernel.IConstituentOfEntity;
import padl.kernel.IContainerAggregation;
import padl.kernel.IElementMarker;
import padl.kernel.IFilter;
import padl.kernel.IFirstClassEntity;
import padl.kernel.exception.ModelDeclarationException;
import padl.util.Util;
import util.io.Output;
import util.lang.Modifier;
import util.multilingual.MultilingualManager;

class ContainerAggregation extends Association implements IElementMarker,
		IContainerAggregation {

	private static final long serialVersionUID = -6973745337657683093L;

	// Only used if concrete.
	// Yann 2009/04/28: Container.
	// I promoted this class as a full-fledge container.
	// No need to keep the associated elements aside :-)
	//	private List associationElements;

	private GenericContainerOfConstituents container =
		new GenericContainerOfNaturallyOrderedConstituents(this);
	// Yann 2002/07/31: Start small...
	// For the moment, an aggregation only knows about its field
	// and the getter and setter methods:
	//     "get" / "set" OR "remove" / "add".
	// We leave for later to manage a "get" method in case of cardinality
	// greater than one...
	// However, for updating purpose (cloning...), I must record if
	// this is an aggregation created from void or not.
	// Yann 2010/10/10: DB4O
	// Used to be:
	//	private final boolean isFromVoid;
	// I removed the final to make DB4O works...
	// TODO: Understand how to keep it final with DB4O!
	private boolean isFromVoid;
	private Field originField;
	private Method originGetterMethod;
	private Method originSetterMethod;

	public ContainerAggregation(final Association pAssociation)
			throws ModelDeclarationException {

		this(pAssociation.getID(), pAssociation.getTargetEntity(), pAssociation
			.getCardinality());
	}

	// Yann 2003/12/15: Herv�!
	// This is legacy code from Herv�! Pay it most respect!
	// Meawhile, study if we can safely remove it... :-)
	//	public ContainerAggregation(
	//		final String anID,
	//		final IEntity aTargetEntity,
	//		final int cardinality,
	//		final Field originField,
	//		final Method originGetterMethod,
	//		final Method originSetterMethod)
	//		throws ModelDeclarationException {
	//	
	//		super(anID, aTargetEntity, cardinality);
	//	
	//		this.isFromVoid = false;
	//	
	//		if (this.originField == null
	//			|| this.originGetterMethod == null
	//			|| this.originSetterMethod == null) {
	//	
	//			throw new ModelDeclarationException("An instance of Aggregation must have origin field and methods.");
	//		}
	//		this.originField = originField;
	//		this.originGetterMethod = originGetterMethod;
	//		this.originSetterMethod = originSetterMethod;
	//	
	//		if (!this.isAbstract()) {
	//			this.associationElements.add(originField);
	//		}
	//		this.associationElements.add(originGetterMethod);
	//		this.associationElements.add(originSetterMethod);
	//	}

	public ContainerAggregation(
		final char[] anID,
		final IFirstClassEntity aTargetEntity,
		final int cardinality) throws ModelDeclarationException {

		super(anID, aTargetEntity, cardinality);

		this.isFromVoid = true;

		this.updateAssociation();
	}
	public void addConstituent(final IConstituentOfEntity aConstituent)
			throws ModelDeclarationException {

		throw new ModelDeclarationException(MultilingualManager.getString(
			"ELEMS_ATTACH",
			ContainerAggregation.class));
	}
	public boolean doesContainConstituentWithID(final char[] anID) {
		return this.container.doesContainConstituentWithID(anID);
	}
	public boolean doesContainConstituentWithName(final char[] aName) {
		return this.container.doesContainConstituentWithName(aName);
	}
	//	public boolean doesContainConstituentWithName(final String aName) {
	//		return this.associationElements.contains(this
	//			.getConstituentFromName(aName));
	//	}
	public void endCloneSession() {
		final ContainerAggregation clonedAggregation =
			(ContainerAggregation) this.getClone();
		if (this.isFromVoid) {
			clonedAggregation.updateAssociation();
		}
		else {
			clonedAggregation.originField = (Field) this.originField.getClone();
			clonedAggregation.originGetterMethod =
				(Method) this.originGetterMethod.getClone();
			clonedAggregation.originSetterMethod =
				(Method) this.originSetterMethod.getClone();

			//	clonedAggregation.associationElements.clear();
			//	if (!clonedAggregation.isAbstract()) {
			//		clonedAggregation.associationElements.add(this.originField);
			//	}
			//	clonedAggregation.associationElements.add(this.originGetterMethod);
			//	clonedAggregation.associationElements.add(this.originSetterMethod);
			try {
				if (!clonedAggregation.isAbstract()) {
					clonedAggregation.container
						.addConstituent(this.originField);
				}
				clonedAggregation.container
					.addConstituent(this.originGetterMethod);
				clonedAggregation.container
					.addConstituent(this.originSetterMethod);
			}
			catch (final ModelDeclarationException e) {
				e.printStackTrace(Output.getInstance().errorOutput());
			}
		}
	}
	public Iterator getConcurrentIteratorOnConstituents() {
		return this.container.getConcurrentIteratorOnConstituents();
	}
	public Iterator getConcurrentIteratorOnConstituents(final IFilter filter) {
		return this.container.getConcurrentIteratorOnConstituents();
	}
	public Iterator getConcurrentIteratorOnConstituents(
		final java.lang.Class aConstituentType) {

		return this.container
			.getConcurrentIteratorOnConstituents(aConstituentType);
	}
	public IConstituent getConstituentFromID(final char[] anID) {
		return this.container.getConstituentFromID(anID);
	}
	public IConstituent getConstituentFromID(final String anID) {
		return this.getConstituentFromID(anID.toCharArray());
	}
	public IConstituent getConstituentFromName(final char[] aName) {
		return this.container.getConstituentFromName(aName);
	}
	public IConstituent getConstituentFromName(final String aName) {
		return this.getConstituentFromName(aName.toCharArray());
	}
	//	public IConstituent getConstituentFromName(final String aName) {
	//		final Iterator iterator = this.associationElements.iterator();
	//
	//		while (iterator.hasNext()) {
	//			IConstituent currentElement = (IConstituent) iterator.next();
	//			if (currentElement.getID().equals(aName)) {
	//				return currentElement;
	//			}
	//		}
	//
	//		return null;
	//	}
	public char[] getFieldType() {
		return this.originField.getType();
	}
	public Iterator getIteratorOnConstituents() {
		return this.container.getIteratorOnConstituents();
	}
	public Iterator getIteratorOnConstituents(final IFilter aFilter) {
		return this.container.getIteratorOnConstituents(aFilter);
	}
	public Iterator getIteratorOnConstituents(
		final java.lang.Class aConstituentType) {
		return this.container.getIteratorOnConstituents(aConstituentType);
	}
	public int getNumberOfConstituents() {
		return this.container.getNumberOfConstituents();
	}
	public int getNumberOfConstituents(final java.lang.Class aConstituentType) {
		return this.container.getNumberOfConstituents(aConstituentType);
	}
	public void removeAllConstituent() {
		this.container.removeAllConstituent();
	}
	public void removeConstituentFromID(final char[] anID) {
		this.container.removeConstituentFromID(anID);
	}
	/**
	 * This method returns a list of all the
	 * actors (instances of Association) added to
	 * this container aggregation.
	 */
	// This method is not necessary as of 2005/10/12?
	//	public List listOfConstituents() {
	//		return this.associationElements;
	//	}
	//	public void removeConstituentFromID(final String anID) {
	//		this.associationElements.remove(this.getConstituentFromID(anID));
	//	}
	//	public void removeAllConstituent() {
	//		this.associationElements.clear();
	//	}
	public void setCardinality(int cardinality)
			throws ModelDeclarationException {

		super.setCardinality(cardinality);
		if (this.isFromVoid) {
			this.updateAssociation();
		}
	}
	public void setName(final char[] aName) {
		super.setName(aName);
		if (this.isFromVoid) {
			this.updateAssociation();
		}
	}
	public void setTargetEntity(final IFirstClassEntity anEntity) {
		super.setTargetEntity(anEntity);

		// Yann 2001/07/31: Robustness?
		// This could happen when cloning a
		// partial subset of the model.
		if (anEntity != null) {
			if (this.isFromVoid) {
				this.updateAssociation();
			}
		}
	}
	public void setVisibility(int visibility) throws ModelDeclarationException {

		super.setVisibility(visibility);
		this.updateAssociation();
	}
	public void startCloneSession() {
		super.startCloneSession();

		final ContainerAggregation clone =
			(ContainerAggregation) this.getClone();

		// New association elements.
		// Yann 2010/10/03: Objects!
		// The "container" is now an instance of a class
		// and must be assigned a new instance independently.
		//	((ContainerAggregation) this.getClone()).container
		//		.resetListOfConstituents();
		((ContainerAggregation) this.getClone()).container =
			new GenericContainerOfNaturallyOrderedConstituents(
				((ContainerAggregation) this.getClone()));

		// New Field("~originField").
		this.originField.startCloneSession();
		clone.originField = (Field) this.originField.getClone();

		// New Method("~originGetterMethod").
		this.originGetterMethod.startCloneSession();
		clone.originGetterMethod = (Method) this.originGetterMethod.getClone();

		// New Method("~originSetterMethod").
		this.originSetterMethod.startCloneSession();
		clone.originSetterMethod = (Method) this.originSetterMethod.getClone();

		// Yann: I am not sure the following lines are needed?
		// clone.originGetterMethod.removeParameter(oneParameter);
		// clone.originSetterMethod.removeParameter(oneParameter);
		// clone.oneParameter = new Parameter(null);
		// clone.originSetterMethod.addParameter(clone.oneParameter);

		// Yann 2002/08/05: Target entity
		// I must update the target correctly.
		// clone.setTargetEntity((Entity) this.getTargetEntity().getClone());
		// However, this is done in the performCloneSession() method from the
		// superclass Use.
	}
	public String toString(final int tab) {
		final StringBuffer codeEq = new StringBuffer();
		codeEq.append(super.toString(tab));

		Util.addTabs(tab, codeEq);
		final Iterator iterator = this.container.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			codeEq.append(((Constituent) iterator.next()).toString(tab));
			if (iterator.hasNext()) {
				codeEq.append('\n');
			}
		}

		return codeEq.toString();
	}
	private void updateAssociation() {
		try {
			// Yann 2002/07/31: Update.
			// When an aggregation relationship is built from void,
			// we must create "fake" field and accessor methods.
			this.originField = new Field(IContainerAggregation.ID1);
			this.originField.setName(this.getName());
			this.originField.setVisibility(this.getVisibility());
			this.originField.setPrivate(true);
			this.originField.resetCodeLines();

			this.originGetterMethod = new Method(IContainerAggregation.ID2);
			this.originGetterMethod.setVisibility(this.getVisibility());
			// Yann 2004/05/24: Abstractness.
			// The getter method (respectively setter method) might
			// be abstract if the aggregation has been inferred from
			// the methods parameters (in an interface, for example).
			if (!Modifier.isAbstract(this.getVisibility())) {
				this.originGetterMethod.resetCodeLines();
			}

			this.originSetterMethod = new Method(IContainerAggregation.ID3);
			this.originSetterMethod.setVisibility(this.getVisibility());
			if (!Modifier.isAbstract(this.getVisibility())) {
				this.originSetterMethod.resetCodeLines();
			}

			final Parameter parameter =
				new Parameter(this.getTargetEntity(), Constants.CARDINALITY_ONE);

			if (this.getCardinality() > 1) {
				this.originField.setType(Constants.DEFAULT_LIST_INTERFACE);

				this.originGetterMethod.setName(("remove" + this
					.getTargetEntity()
					.getDisplayName()).toCharArray());
				this.originGetterMethod.addConstituent(parameter);
				this.originGetterMethod.setReturnType(Method.VOID);

				this.originSetterMethod.setName(("add" + this
					.getTargetEntity()
					.getDisplayName()).toCharArray());
				this.originSetterMethod.addConstituent(parameter);
				this.originSetterMethod.setReturnType(Method.VOID);

				if (!this.isAbstract()) {
					this.originField.setCodeLines("new java.util.ArrayList()");

					this.originGetterMethod.setCodeLines(this.getDisplayName()
							+ ".add(" + parameter.getDisplayName() + ");");
					this.originGetterMethod.setReturnType(Method.VOID);

					this.originSetterMethod.setCodeLines(this.getDisplayName()
							+ ".remove(" + parameter.getDisplayName() + ");");
				}
			}
			else {
				this.originField.setType(this.getTargetEntity().getID());

				this.originGetterMethod.setName(("get" + this
					.getTargetEntity()
					.getDisplayName()).toCharArray());
				this.originGetterMethod.setReturnType(this
					.getTargetEntity()
					.getID());

				this.originSetterMethod.setName(("set" + this
					.getTargetEntity()
					.getDisplayName()).toCharArray());
				this.originSetterMethod.setReturnType(Method.VOID);
				this.originSetterMethod.addConstituent(parameter);

				if (!this.isAbstract()) {
					this.originGetterMethod.setCodeLines("return "
							+ this.getDisplayName() + ";");

					this.originSetterMethod.setCodeLines(this.getDisplayName()
							+ " = " + this.getTargetEntity().getDisplayName()
							+ ';');
				}
			}

			if (this.container == null) {
				this.container =
					new GenericContainerOfNaturallyOrderedConstituents(this);
			}
			else {
				this.container.removeAllConstituent();
			}
			if (!this.isAbstract()) {
				this.container.addConstituent(this.originField);
			}
			this.container.addConstituent(this.originGetterMethod);
			this.container.addConstituent(this.originSetterMethod);
		}
		catch (final ModelDeclarationException pde) {
			pde.printStackTrace(Output.getInstance().errorOutput());
		}
		catch (final NullPointerException npe) {
			npe.printStackTrace(Output.getInstance().errorOutput());
		}
	}
}
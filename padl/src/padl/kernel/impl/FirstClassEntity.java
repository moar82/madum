/*
 * (c) Copyright 2001, 2002 Herv� Albin-Amiot and Yann-Ga�l Gu�h�neuc, Ecole
 * des Mines de Nantes Object Technology International, Inc. Soft-Maint S.A.
 * 
 * Use and copying of this software and preparation of derivative works based
 * upon this software are permitted. Any copy of this software or of any
 * derivative work must include the above copyright notice of the authors, this
 * paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND NOT
 * WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY LIABILITY FOR DAMAGES
 * RESULTING FROM THE SOFTWARE OR ITS USE IS EXPRESSLY DISCLAIMED, WHETHER
 * ARISING IN CONTRACT, TORT (INCLUDING NEGLIGENCE) OR STRICT LIABILITY, EVEN
 * IF THE AUTHORS ARE ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.kernel.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import padl.event.IEvent;
import padl.event.IModelListener;
import padl.kernel.IConstituent;
import padl.kernel.IConstituentOfEntity;
import padl.kernel.IElement;
import padl.kernel.IFilter;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IRelationship;
import padl.kernel.IVisitor;
import padl.kernel.exception.ModelDeclarationException;
import padl.path.IConstants;
import padl.util.Util;
import util.io.Output;
import util.multilingual.MultilingualManager;

abstract class FirstClassEntity extends Constituent implements
		IFirstClassEntity {

	// Yann: 2002/07/29: Final!
	// The following fields cannot be final! Because I need to set them
	// up to different values in the performCloneSession() method.
	// (See below...)
	private static final long serialVersionUID = -1526334305973495427L;
	// Yann 2009/04/29: Test!
	// I must leave this field protected so that I can test
	// the protected methods defined in GenericContainer.
	protected GenericContainerOfConstituents container =
		new GenericContainerOfNaturallyOrderedConstituents(
			this,
			GenericContainerConstants.INITIAL_SIZE_ENTITIES);
	private List listOfInheritedEntities = new ArrayList();
	private List listOfInheritingEntities = new ArrayList();

	private String purpose;
	public FirstClassEntity(final char[] actorID) {
		super(actorID);
	}
	public void accept(final IVisitor visitor) {
		this.accept(visitor, "open");
		final Iterator iterator = this.getConcurrentIteratorOnConstituents();
		while (iterator.hasNext()) {
			final IConstituent constituent = (IConstituent) iterator.next();
			// System.out.println(constituent.toString());
			constituent.accept(visitor);
		}
		this.accept(visitor, "close");
	}
	//	public void addConstituent(final IConstituentOfEntity aConstituent)
	//			throws ModelDeclarationException {
	//
	//		if (aConstituent instanceof IElement) {
	//			this.container.addConstituent((IElement) aConstituent);
	//		}
	//		else {
	//			throw new ModelDeclarationException(MultilingualManager.getString(
	//				"ELEM_ADD_ENT",
	//				FirstClassEntity.class));
	//		}
	//	}
	public void addConstituent(final IConstituentOfEntity anElement)
			throws ModelDeclarationException {

		// Yann 2004/05/21: Abstractness.
		// Any method added to an abstract class must be abstract too.
		// This is NOT true! An abstract class may contain concrete
		// methods... Even constructor!?
		// anElement.setAbstract(this.isAbstract());
		if (anElement instanceof IRelationship) {
			this.addConstituent((IRelationship) anElement);
		}
		else {
			this.container.addConstituent(anElement);
		}
	}
	public void addConstituent(final IRelationship aRelationship)
			throws ModelDeclarationException {

		this.container.directlyAddConstituentWithUniqueID(aRelationship);
	}
	public void addInheritedEntity(final IFirstClassEntity anEntity)
			throws ModelDeclarationException {
		// Yann 2002/08/01: Ghost!
		// I added a new kind of entity, Ghost, to represent unavailable
		// classes and interfaces. Thus, the following test is not valid
		// anymore. Moreover, I wonder if this test is in the right place...
		//	if (aPEntity.getClass() != this.getClass()) {
		//		throw new ModelDeclarationException(
		//			this.getClass()
		//				+ " can only inherit from a "
		//				+ this.getClass());
		//	}
		if (anEntity == this) {
			throw new ModelDeclarationException(MultilingualManager.getString(
				"ENT_INHERIT_ITSELF",
				IFirstClassEntity.class));
		}
		if (this.listOfInheritedEntities.contains(anEntity)) {
			throw new ModelDeclarationException(MultilingualManager.getString(
				"ALREADY_INHERITED",
				FirstClassEntity.class,
				new Object[] { anEntity.getDisplayID(), this.getDisplayID() }));
		}
		this.listOfInheritedEntities.add(anEntity);
		//	this.addConstituent(
		//		new Specialisation("S" + anEntity.getConstituentID(), anEntity));

		((FirstClassEntity) anEntity).addInheritingEntity(this);
		//	anEntity.addConstituent(
		//		new Generalisation("G" + this.getConstituentID(), this));
	}
	/**
	 * This method add a new entity to the list of entities
	 * inheriting from this entity.
	 * 
	 * @param anEntity
	 * @throws ModelDeclarationException
	 */
	private void addInheritingEntity(final IFirstClassEntity anEntity)
			throws ModelDeclarationException {
		if (anEntity == this) {
			throw new ModelDeclarationException(MultilingualManager.getString(
				"ENT_INHERIT_ITSELF",
				IFirstClassEntity.class));
		}
		if (this.listOfInheritingEntities.contains(anEntity)) {
			// Yann 2010/04/30: Unnecessary exception!
			// I do not throw an exception anymore because
			// this method can only be called by legetimate
			// code, for example for the RootHierarchyObject
			// that is shared across models.
			//	throw new ModelDeclarationException(MultilingualManager.getString(
			//		"ALREADY_INHERITED",
			//		FirstClassEntity.class,
			//		new Object[] { anEntity.getDisplayID(), this.getDisplayID() }));
		}
		else {
			this.listOfInheritingEntities.add(anEntity);
		}
	}
	public void addModelListener(final IModelListener aModelListener) {
		this.container.addModelListener(aModelListener);
	}
	public void addModelListeners(final List someModelListeners) {
		this.container.addModelListeners(someModelListeners);
	}
	public void removeModelListeners(final List aListOfModelListeners) {
		this.container.removeModelListeners(aListOfModelListeners);
	}
	public boolean doesContainConstituentWithID(final char[] anID) {
		return this.container.doesContainConstituentWithID(anID);
	}
	public boolean doesContainConstituentWithName(final char[] aName) {
		return this.container.doesContainConstituentWithName(aName);
	}
	public void endCloneSession() {
		// I finish the clone session of the elements.
		final Iterator iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			((IConstituent) iterator.next()).endCloneSession();
		}
		super.endCloneSession();
	}
	public boolean equals(final Object anObject) {
		//	if (anObject instanceof IMemberClass
		//			|| anObject instanceof IMemberInterface
		//			|| anObject instanceof IMemberGhost) {
		//		return this.equalsMember((IFirstClassEntity) anObject);
		//	}
		return super.equals(anObject);
	}
	//	public boolean equalsMember(final IFirstClassEntity memberEntity) {
	// Yann 2006/02/21: Member entities...
	// Two member entities may have identical names,
	// as well as identical other attributes but for
	// their JVM-based object id.
	//
	// Stephane 2006/03/23: Naming of Member entities
	// is incorrect by spec: 
	// http://java.sun.com/docs/books/jls/second_edition/html/classes.doc.html#246026
	// this is a hack before member entity ids are changed
	// a lesser hack could include creating a MemberEntity type
	//
	// Yann 2010/10/10: New test!
	// Stephane added the test:
	//	return System.identityHashCode(this) == System
	//		.identityHashCode(memberEntity);
	// that can fail when a member class is stored and restored in a database.
	// Therefore, I revert to using one and only Constituent.equals().
	//	}
	public void fireModelChange(final String anEventType, final IEvent anEvent) {
		this.container.fireModelChange(anEventType, anEvent);
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

	public IFirstClassEntity getInheritedEntity(final char[] anEntityName) {
		final Iterator iterator = this.listOfInheritedEntities.iterator();
		while (iterator.hasNext()) {
			final IFirstClassEntity inheritedEntity =
				(IFirstClassEntity) iterator.next();
			if (Arrays.equals(inheritedEntity.getName(), anEntityName)) {
				return inheritedEntity;
			}
		}
		return null;
	}
	public Iterator getIteratorOnConstituents() {
		// Yann 2009/05/01: Space optimisation...
		// I assume that when I start iterating over
		// the elements in this entity, I must have 
		// finished adding elements and, thus, can
		// trim the size of its container.
		//	this.container.pack();

		return this.container.getIteratorOnConstituents();
	}
	public Iterator getIteratorOnConstituents(final IFilter aFilter) {
		return this.container.getIteratorOnConstituents(aFilter);
	}
	public Iterator getIteratorOnConstituents(
		final java.lang.Class aConstituentType) {
		// Yann 2009/05/01: Space optimisation...
		// I assume that when I start iterating over
		// the elements in this entity, I must have 
		// finished adding elements and, thus, can
		// trim the size of its container.
		//	this.container.pack();

		return this.container.getIteratorOnConstituents(aConstituentType);
	}
	public Iterator getIteratorOnInheritedEntities() {
		return this.listOfInheritedEntities.iterator();
	}
	/**
	 * This method returns the list of all entities inheriting from this
	 * entity.
	 * 
	 * @return list of inheriting actors
	 */
	public Iterator getIteratorOnInheritingEntities() {
		return this.listOfInheritingEntities.iterator();
	}
	public Iterator getIteratorOnModelListeners() {
		return this.container.getIteratorOnModelListeners();
	}
	public int getNumberOfConstituents() {
		// Yann 2009/05/01: Space optimisation...
		// I assume that when I start iterating over
		// the elements in this entity, I must have 
		// finished adding elements and, thus, can
		// trim the size of its container.
		//	this.container.pack();

		return this.container.getNumberOfConstituents();
	}
	public int getNumberOfConstituents(final java.lang.Class aConstituentType) {
		// Yann 2009/05/01: Space optimisation...
		// I assume that when I start iterating over
		// the elements in this entity, I must have 
		// finished adding elements and, thus, can
		// trim the size of its container.
		//	this.container.pack();

		return this.container.getNumberOfConstituents(aConstituentType);
	}
	public int getNumberOfInheritedEntities() {
		return this.listOfInheritedEntities.size();
	}
	public int getNumberOfInheritingEntities() {
		return this.listOfInheritingEntities.size();
	}
	protected char getPathSymbol() {
		return IConstants.ENTITY_SYMBOL;
	}
	public String getPurpose() {
		return this.purpose;
	}
	public boolean isAboveInHierarchy(final IFirstClassEntity anEntity) {
		if (this.equals(anEntity)) {
			return true;
		}
		else {
			final Iterator iterator = anEntity.getIteratorOnInheritedEntities();
			while (iterator.hasNext()) {
				final IFirstClassEntity s = (IFirstClassEntity) iterator.next();
				if (this.equals(s)) {
					return true;
				}
				else {
					return this.isAboveInHierarchy(s);
				}
			}
		}
		return false;
	}
	public void performCloneSession() {
		super.performCloneSession();

		final FirstClassEntity clonedEntity =
			(FirstClassEntity) this.getClone();
		Iterator iterator;

		// Duplicate the hierarchies.
		clonedEntity.listOfInheritedEntities = new ArrayList();
		iterator = this.listOfInheritedEntities.iterator();
		while (iterator.hasNext()) {
			final FirstClassEntity currentFirstClassEntity =
				(FirstClassEntity) iterator.next();
			// Yann: The following lines are not needed anymore?
			// if (currentPEntity.isCloned()) {
			// clonedEntity.removeInherits(currentPEntity);
			// Yann 2001/07/31: Hack!
			// The following test is only needed when cloning
			// a subset of the model.
			// A better and *cleaner* algorithm must be
			// implemented eventually.
			if (currentFirstClassEntity.getClone() != null) {
				clonedEntity.listOfInheritedEntities
					.add(currentFirstClassEntity.getClone());
			}
		}

		// Yann: Start clone session of the entity's elements.
		// The new clone protocol makes explicit the needed shift
		// between the beginning of the cloning of the Entities
		// and the beginning of the cloning of the Elements.
		iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			final IElement element = (IElement) iterator.next();
			if (!(element instanceof IFirstClassEntity)) {
				element.startCloneSession();
			}
		}

		// I perform the clone session of the elements.
		iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			final IElement element = (IElement) iterator.next();
			element.performCloneSession();
			// Yann 2004/12/17: Clone!
			// I must use the addConstituent() method to add the cloned
			// element to the cloned entity or I might have problems
			// (in particular with the cache implemented in class
			// AbstractContainer).
			// Yann 2006/08/09: Clone and member entities...
			// Once upon a time, there was a gug that prevented elements
			// of member entities to be create. I fixed that bug but it
			// undercovered another bug in the cloning process: I was
			// checking if an element was also en entity (case of a 
			// member entity) and put the test *before* calling
			// "performCloneSession()" on this member entity, thus 
			// preventing the elements of the member entity to be cloned!
			// I moved the test just after "performCloneSession()" just
			// to make sure I am not adding a member entity already added
			// (during "startCloneSession()"). Thanks to Saliha for
			// revealing this bug!
			if (!(element instanceof IFirstClassEntity)) {
				try {
					clonedEntity.addConstituent((IElement) element.getClone());
				}
				catch (final ModelDeclarationException e) {
					e.printStackTrace(Output.getInstance().errorOutput());
				}
			}
		}
	}
	public void removeAllConstituent() {
		this.container.removeAllConstituent();
	}
	public void removeConstituentFromID(final char[] anID) {
		this.container.removeConstituentFromID(anID);
	}
	public void removeInheritedEntity(final IFirstClassEntity anEntity) {
		this.listOfInheritedEntities.remove(anEntity);
	}
	public void removeModelListener(final IModelListener aModelListener) {
		this.container.removeModelListener(aModelListener);
	}
	public void setPurpose(final String purpose) {
		this.purpose = purpose;
	}
	public void startCloneSession() {
		// The shallow copy must include a shallow copy of 
		// member entities, because the clones of methods,
		// fields, and so on, depend on it!
		super.startCloneSession();

		// Yann 2010/10/03: Objects!
		// The "container" is now an instance of a class
		// and must be assigned a new instance independently.
		//	((FirstClassEntity) this.getClone()).container
		//		.resetListOfConstituents();
		((FirstClassEntity) this.getClone()).container =
			new GenericContainerOfNaturallyOrderedConstituents(
				((FirstClassEntity) this.getClone()));

		final Iterator iterator =
			this.getIteratorOnConstituents(IFirstClassEntity.class);
		while (iterator.hasNext()) {
			final IFirstClassEntity firstClassEntity =
				(IFirstClassEntity) iterator.next();
			firstClassEntity.startCloneSession();
			try {
				((IFirstClassEntity) this.getClone())
					.addConstituent((IConstituentOfEntity) firstClassEntity
						.getClone());
			}
			catch (final ModelDeclarationException pde) {
				pde.printStackTrace(Output.getInstance().errorOutput());
			}
		}
	}
	public String toString() {
		return this.toString(0);
	}
	public String toString(final int tab) {
		final StringBuffer codeEq = new StringBuffer();
		if (getPurpose() != null) {
			Util.addTabs(tab, codeEq);
			codeEq.append("/* ");
			codeEq.append(getID());
			codeEq.append(" : Purpose\n");
			Util.addTabs(tab, codeEq);
			codeEq.append(getPurpose());
			Util.addTabs(tab, codeEq);
			codeEq.append("\n*/\n");
		}
		codeEq.append(super.toString(tab));
		return codeEq.toString();
	}
}
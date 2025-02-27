/*
 * (c) Copyright 2000-2002 Yann-Gal Guéhéneuc,
 * Ecole des Mines de Nantes and Object Technology International, Inc.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * Yann-Gal Guhneuc, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF YANN-GAEL GUEHENEUC IS ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.kernel.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import padl.kernel.Constants;
import padl.kernel.IField;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IMethodInvocation;
import padl.kernel.IOperation;
import padl.kernel.exception.ModelDeclarationException;
import padl.path.IConstants;
import util.io.Output;
import util.lang.Modifier;

/**
 * @author 	Yann-Gal Guhneuc
 * @since	2002/08/19
 */
class MethodInvocation extends Element implements IMethodInvocation {
	private static final long serialVersionUID = 2646185582780463310L;

	private IOperation calledMethod;
	// Farouk Zaidi 2004/02/20: Fields added
	// Farouk Zaidi 2004/04/02: Modification.
	// "callingField" becomes an array of fields.
	private IField[] callingFields = new IField[0];
	// Yann 2010/10/10: DB4O
	// Used to be:
	//	private final int cardinality;
	// I removed the final to make DB4O works...
	// TODO: Understand how to keep it final with DB4O!
	private int cardinality;
	// Yann 2009/03/20: Serialisation!
	// I must declare transient the "entityDeclaringField" 
	// and "targetEntity" to avoid a stack overflow.
	// Yann 2010/04/10: Still true?
	// I am testing with DB4O, so I remove the transient...
	private IFirstClassEntity entityDeclaringField;
	private int indexInPrimeNumbersArray;
	private int size;
	private IFirstClassEntity targetEntity;
	// Yann 2010/10/10: DB4O
	// Used to be:
	//	private final int type;
	// I removed the final to make DB4O works...
	// TODO: Understand how to keep it final with DB4O!
	private int type;
	// Farouk 2004/03/23: Constructor.
	// Constructor for my needs, i.e., no cardinality and no type
	//
	// Question: How could the target entity be null?
	//	public MethodInvocation(
	//		final IEntity originEntity,
	//		final IEntity targetEntity,
	//		final IEntity entityDeclaringField) {
	//
	//		super(originEntity.getName() + COUNTER++ +":");
	//		this.originEntity = originEntity;
	//		this.targetEntity = targetEntity;
	//		this.entityDeclaringField = entityDeclaringField;
	//		this.type = -1;
	//		this.cardinality = 1;
	//	}
	// Yann 2004/08/02: Cleaning.
	// I modified the constructors to make them consistent
	// and to add the enclosing method (the enclosing method
	// and entity should be removed later, there should not
	// appear at all!)
	public MethodInvocation(
		final int type,
		final int cardinality,
		final int visibility,
		final IFirstClassEntity targetEntity) {

		this(type, cardinality, visibility, targetEntity, null);
	}
	public MethodInvocation(
		final int type,
		final int cardinality,
		final int visibility,
		final IFirstClassEntity targetEntity,
		final IFirstClassEntity entityDeclaringField) {

		super(Constants.DEFAULT_METHODINVOCATION_ID);

		this.type = type;
		this.cardinality = cardinality;
		this.targetEntity = targetEntity;
		this.entityDeclaringField = entityDeclaringField;
		try {
			this.setVisibility(visibility);
		}
		catch (ModelDeclarationException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
	}
	public void addCallingField(final IField aField) {
		final int minCapacity = this.size + 1;
		final int oldCapacity = this.callingFields.length;
		if (minCapacity > oldCapacity) {
			final IField[] oldData = this.callingFields;
			this.indexInPrimeNumbersArray++;
			int newCapacity =
				GenericContainerConstants.PRIME_NUMBERS[this.indexInPrimeNumbersArray];
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			this.callingFields = new IField[newCapacity];
			System.arraycopy(oldData, 0, this.callingFields, 0, this.size);
		}
		this.callingFields[this.size] = aField;
		this.size++;
	}
	public boolean equals(final IMethodInvocation anotherMethodInvocation) {
		// Yann and Aminata 2012/02/02: Java parser!
		// We add much more thorough equality tests to test
		// the differences between the MI created by the Java
		// parser and the class-file parser.
		boolean areEqual = true;
		if (this.getCalledMethod() != null) {
			if (anotherMethodInvocation.getCalledMethod() == null) {
				areEqual = false;
			}
			else {
				areEqual =
					areEqual
							&& this.getCalledMethod().equals(
								anotherMethodInvocation.getCalledMethod());
			}
		}
		if (this.getFieldDeclaringEntity() != null) {
			if (anotherMethodInvocation.getFieldDeclaringEntity() == null) {
				areEqual = false;
			}
			else {
				areEqual =
					areEqual
							&& this.getFieldDeclaringEntity().equals(
								anotherMethodInvocation
									.getFieldDeclaringEntity());
			}
		}
		if (this.getFirstCallingField() != null) {
			if (anotherMethodInvocation.getFirstCallingField() == null) {
				areEqual = false;
			}
			else {
				areEqual =
					areEqual
							&& this.getFirstCallingField().equals(
								anotherMethodInvocation.getFirstCallingField());
			}
		}

		return areEqual
				&& this.getTargetEntity().equals(
					anotherMethodInvocation.getTargetEntity())
				&& this.getCardinality() == anotherMethodInvocation
					.getCardinality()
				&& this.getVisibility() == anotherMethodInvocation
					.getVisibility()
				&& this.getType() == anotherMethodInvocation.getType();
	}
	public boolean equals(final Object object) {
		if (object instanceof IMethodInvocation) {
			return this.equals((IMethodInvocation) object);
		}
		return super.equals(object);
	}
	public IOperation getCalledMethod() {
		return this.calledMethod;
	}
	public int getCardinality() {
		return this.cardinality;
	}
	public IFirstClassEntity getFieldDeclaringEntity() {
		return this.entityDeclaringField;
	}
	/*
	 * Alban Tiberghien : 2008/08/19
	 * FIX : raise exception if 'callingFields' is empty
	 */
	public IField getFirstCallingField() {
		//OLD : return (IField) callingFields.get(0);
		return (this.callingFields == null || this.callingFields.length == 0) ? null
				: (IField) this.callingFields[0];
	}
	public Iterator getIteratorOnCallingFields() {
		final List list = new ArrayList(this.callingFields.length);
		for (int i = 0; i < this.callingFields.length; i++) {
			final IField field = this.callingFields[i];
			list.add(field);
		}
		return list.iterator();
	}

	public char[] getName() {
		final String fieldName;
		final char[] entityDeclaringFieldName;
		final char[] invokedMethodName;
		final char[] targetEntityName;

		if (this.callingFields != null) {
			if (this.callingFields.length > 0) {

				final StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < this.callingFields.length; i++) {
					final IField element = this.callingFields[i];
					buffer.append(element.getName());
					if (i < this.callingFields.length - 1) {
						buffer.append(".");
					}
				}
				fieldName = buffer.toString();
			}
			else {
				fieldName = Constants.DEFAULT_METHODINVOCATION_NO_FIELD;
			}
		}
		else {
			fieldName = Constants.DEFAULT_METHODINVOCATION_NO_FIELD;
		}
		if (this.entityDeclaringField == null) {
			entityDeclaringFieldName =
				Constants.DEFAULT_METHODINVOCATION_NO_FIELD_ENTITY;
		}
		else {
			entityDeclaringFieldName = this.entityDeclaringField.getID();
		}
		if (this.calledMethod == null) {
			invokedMethodName = Constants.DEFAULT_METHODINVOCATION_NO_METHOD;
		}
		else {
			invokedMethodName = this.calledMethod.getName();
		}
		if (this.targetEntity == null) {
			targetEntityName = Constants.DEFAULT_METHODINVOCATION_NO_TARGET;
		}
		else {
			targetEntityName = this.targetEntity.getID();
		}

		final StringBuffer buffer = new StringBuffer();
		buffer.append(fieldName);
		buffer.append(':');
		buffer.append(entityDeclaringFieldName);
		buffer.append(':');
		buffer.append(invokedMethodName);
		buffer.append(':');
		buffer.append(targetEntityName);
		return buffer.toString().toCharArray();
	}

	protected char getPathSymbol() {
		// Yann 2009/04/28: Unification with PADL Statement
		// This method should not be here but in Statement.
		// This class should be a subclass of Statement too!
		return IConstants.STATEMENT_SYMBOL;
	}
	public IFirstClassEntity getTargetEntity() {
		return this.targetEntity;
	}
	public int getType() {
		return this.type;
	}

	public static String getTypeMeaning(final int type) {
		switch (type) {
			case IMethodInvocation.CLASS_CLASS :
				return "CLASS_CLASS";
			case IMethodInvocation.CLASS_CLASS_FROM_FIELD :
				return "CLASS_CLASS_FROM_FIELD";
			case IMethodInvocation.CLASS_INSTANCE :
				return "CLASS_INSTANCE";
			case IMethodInvocation.CLASS_INSTANCE_FROM_FIELD :
				return "CLASS_INSTANCE_FROM_FIELD";
			case IMethodInvocation.INSTANCE_CLASS :
				return "INSTANCE_CLASS";
			case IMethodInvocation.INSTANCE_CLASS_FROM_FIELD :
				return "INSTANCE_CLASS_FROM_FIELD";
			case IMethodInvocation.INSTANCE_CREATION :
				return "INSTANCE_CREATION";
			case IMethodInvocation.INSTANCE_INSTANCE :
				return "INSTANCE_INSTANCE";
			case IMethodInvocation.INSTANCE_INSTANCE_FROM_FIELD :
				return "INSTANCE_INSTANCE_FROM_FIELD";
			case IMethodInvocation.OTHER :
				return "OTHER";
			default :
				return " This type does not exist";
		}
	}
	// Stéphane 2005/12/06: Equals!	
	/**
	 * Method to calculate a correct hashCode to work with equals(Object)
	 * @return hashCode based on targetEntity and type of invocation
	 * @see padl.kernel.impl.Constituent#hashCode()
	 */
	public int hashCode() {
		int hashCode = this.getType();

		final IFirstClassEntity targetEntity = this.getTargetEntity();
		// If target is not null, we can use it to better hash the instances
		if (targetEntity != null) {
			hashCode += 31 * targetEntity.hashCode();
		}
		return hashCode;
	}
	/**
	 * This methods is used by the clone protocol.
	 */
	public void performCloneSession() {
		final MethodInvocation clonedMethodInvocation =
			(MethodInvocation) this.getClone();

		// Yann 2005/08/05: Method invocation!
		// The called method of a method invocation can be null!?
		if (this.calledMethod != null) {
			// Yann 2007/11/13: Cloning...
			// Should this test be there?
			if (this.calledMethod.getClone() == null) {
				this.calledMethod.startCloneSession();
				this.calledMethod.performCloneSession();
			}
			clonedMethodInvocation.calledMethod =
				(IOperation) this.calledMethod.getClone();
		}

		// Yann 2007/11/13: Same reference!
		// I cannot use the clear() method here because
		// both the original and the clone point on the
		// same list!!!
		//	clonedMethodInvocation.callingFields.clear();
		clonedMethodInvocation.callingFields =
			new IField[this.callingFields.length];
		for (int i = 0; i < this.callingFields.length; i++) {
			final IField field = this.callingFields[i];

			// Yann 2007/11/13: Cloning...
			// Should this test be there?
			if (field.getClone() == null) {
				field.startCloneSession();
				field.performCloneSession();
			}
			((MethodInvocation) this.getClone()).callingFields[i] =
				(IField) field.getClone();
		}

		if (this.entityDeclaringField != null) {
			clonedMethodInvocation.entityDeclaringField =
				(IFirstClassEntity) this.entityDeclaringField.getClone();
		}

		if (this.targetEntity != null) {
			clonedMethodInvocation.targetEntity =
				(IFirstClassEntity) this.targetEntity.getClone();
		}
	}
	public void setCalledMethod(final IOperation calledMethod) {
		this.calledMethod = calledMethod;
	}
	public void setCallingField(final List callingFields) {
		this.callingFields = new IField[callingFields.size()];
		final Iterator iterator = callingFields.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			final IField field = (IField) iterator.next();
			this.callingFields[i++] = field;
		}
	}
	public String toString() {
		final String fieldName;
		final char[] entityDeclaringFieldName;
		final char[] invokedMethodName;
		final char[] targetEntityName;

		if (this.callingFields != null) {
			if (this.callingFields.length > 0) {

				final StringBuffer buffer = new StringBuffer();

				//Rickard Eilert 2011/12/12 fixed bug where null
				//pointer exception was thrown because
				//this.callingFields.length was used instead of
				//this.size. The former is the array dimension,
				//and the latter is the actual elements in the array.
				for (int i = 0; i < this.size; i++) {
					final IField field = this.callingFields[i];
					buffer.append(field.getName());
					if (i < this.callingFields.length - 1) {
						buffer.append(".");
					}
				}
				fieldName = buffer.toString();
			}
			else {
				fieldName = Constants.DEFAULT_METHODINVOCATION_NO_FIELD;
			}
		}
		else {
			fieldName = Constants.DEFAULT_METHODINVOCATION_NO_FIELD;
		}
		if (this.entityDeclaringField == null) {
			entityDeclaringFieldName =
				Constants.DEFAULT_METHODINVOCATION_NO_DECLARING_ENTITY;
		}
		else {
			entityDeclaringFieldName = this.entityDeclaringField.getName();
		}
		if (this.calledMethod == null) {
			invokedMethodName =
				Constants.DEFAULT_METHODINVOCATION_NO_INVOKED_METHOD;
		}
		else {
			invokedMethodName = this.calledMethod.getName();
		}
		if (this.targetEntity == null) {
			targetEntityName = Constants.DEFAULT_METHODINVOCATION_NO_TARGET;
		}
		else {
			targetEntityName = this.targetEntity.getName();
		}

		final StringBuffer buffer = new StringBuffer();
		buffer.append("\nField name            : ");
		buffer.append(fieldName);
		buffer.append("\nEntity declaring field: ");
		buffer.append(entityDeclaringFieldName);
		buffer.append("\nMethod invoked        : ");
		buffer.append(invokedMethodName);
		buffer.append("\nTarget entity         : ");
		buffer.append(targetEntityName);
		buffer.append("\nVisibility            : ");
		buffer.append(Modifier.toString(this.getVisibility()));
		buffer.append("\nCadinality            : ");
		buffer.append(this.getCardinality());
		//Aminata : 2011/08/17
		//Add the method invocation type in its toString
		buffer.append("\nType      : ");
		buffer.append(MethodInvocation.getTypeMeaning(this.getType()));
		buffer.append('\n');
		return buffer.toString();
	}
}

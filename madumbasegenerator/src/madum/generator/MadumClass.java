/* (c) Copyright 2009 and following years, Aminata SABANE,
 * Ecole Polytechnique de MontrÃ©al.
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import madum.generator.utils.SetterOrGetterFinder;
import madum.generator.utils.UsefulMethods;
import padl.kernel.IClass;
import padl.kernel.IField;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IMethod;
import padl.kernel.IMethodInvocation;
import padl.kernel.IOperation;

public class MadumClass {

	IClass clazz;
	private long numberOfTestCases = 0;
	private int numberOfFields = -1;
	private int numberOfConstants = 0;
	private int numberOfMethods = -1;
	private int numberOfStaticMethods = 0;
	private List<String> listOfSetters = new ArrayList<String>();
	private List<String> listOfGetters = new ArrayList<String>();
	private List<String> listOfAllFields = new ArrayList<String>();
	private List<String> listOfAllMethods = new ArrayList<String>();
	private List<String> listOfConstructors = new ArrayList<String>();
	private List<String> listOfMadumFields = new ArrayList<String>();
	private List<String> listOfMadumMethods = new ArrayList<String>();
	private Set<String> setOfMadumSetters = new HashSet<String>();
	private Set<String> setOfMadumGetters = new HashSet<String>();
	private Map<String, Set<String>> mapOfAllDirectFieldAccess;
	private Map<String, Set<String>> mapOfTransformersDirectFieldAccess;
	//	private Map<String, Set<QualifiedMethod>> mapOfDirectQualifiedMethods;
	private Map<String, Set<String>> mapOfMethodsCallers =
		new HashMap<String, Set<String>>();//the key is a caller method and the value is the list of methods that are called by this method
	private Map<String, Set<String>> mapOfCalledMethods =
		new HashMap<String, Set<String>>();//the key is a method called and the value is the list of methods that call this method
	private int[][] finalMadum;
	Map<String, List<String>> mapOfMethodsWithAccess =
		new HashMap<String, List<String>>();
	
	
	
	private Set<String> setOfMadumOthers = new HashSet<String>();
	private Set<String> setOfMethodsWithoutAccess = new HashSet<String>();
	private Set<String> setOfAllTransformers = new HashSet<String>();
	private Set<String> setOfFieldsNotUsed = new HashSet<String>();

	private Map<String, Set<String>> mapOfAllDirectSuperFieldAccess;
	private Map<String, Set<String>> mapOfTransformersDirectSuperFieldAccess;

	private Set<String> setOfOverridenMethods = new HashSet<String>();
	private Set<String> setOfSelfCall = new HashSet<String>();
	private Set<String> setOfSuperMethodInvoked = new HashSet<String>();

	//derived madum 
	int[][][] unifiedMadum;
	int[][][] finalUnifiedMadum;
	private List<String> listOfFinalMadumFields = new ArrayList<String>();
	private List<String> listOfFinalMadumMethods = new ArrayList<String>();

	/**
	 * 
	 * @param aClass
	 */
	public MadumClass(IClass aClass) {
		this.clazz = aClass;
	}

	private void initializeMadum() {

		try {
			Iterator<IOperation> operationIterator =
				this.clazz.getConcurrentIteratorOnConstituents(Class
					.forName("padl.kernel.impl.Constructor"));
			while (operationIterator.hasNext()) {
				IOperation operation = operationIterator.next();
				String operationName = operation.getDisplayID();
				int nbArguments =
					operation.getNumberOfConstituents(Class
						.forName("padl.kernel.impl.Parameter"));
				this.listOfAllMethods.add(operation.getDisplayID());
				if (operation.isStatic()) {
					this.numberOfStaticMethods++;
				}
				else {
					this.listOfMadumMethods.add(operation.getDisplayID());

					if (operation instanceof IMethod) {

						if (SetterOrGetterFinder.isGetters(
							operationName,
							nbArguments)) {
							this.listOfGetters.add(operation.getDisplayID());

						}
						else if (SetterOrGetterFinder.isSetters(
							operationName,
							nbArguments)) {
							this.listOfSetters.add(operation.getDisplayID());
						}

					}
					else {

						this.listOfConstructors.add(operation.getDisplayID());
					}

				}
			}

			Iterator<IField> fieldsIterator =
				this.clazz.getConcurrentIteratorOnConstituents(Class
					.forName("padl.kernel.impl.Field"));
			while (fieldsIterator.hasNext()) {
				IField field = fieldsIterator.next();
				String fieldName = field.getDisplayName();
				this.listOfAllFields.add(fieldName);
				if (!(field.isStatic() && field.isFinal())) {
					this.listOfMadumFields.add(fieldName);
				}
				else {
					this.numberOfConstants++;
				}
			}

			//initialize the madum with no access if they are some fields or methods
			this.finalMadum = this.getInitializedMadum();

		}
		catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param aNumberOfFields
	 */
	public void setNumberOfFields(int aNumberOfFields) {
		this.numberOfFields = aNumberOfFields;

	}

	/**
	 * 
	 * @param aNumberOfMethods
	 */
	public void setNumberOfMethods(int aNumberOfMethods) {
		this.numberOfMethods = aNumberOfMethods;
	}

	public void generateMadumOfCadran4() {

	}

	public void generateBaseMadum() {

		Map<String, Set<QualifiedMethod>> mapOfQualifiedMethods =
			this.qualifyDirectMethods(
				this.mapOfAllDirectFieldAccess,
				this.mapOfTransformersDirectFieldAccess);
		this.unifiedMadum =
			this.generateAllMadums(
				this.mapOfAllDirectFieldAccess,
				mapOfQualifiedMethods,
				this.listOfMadumFields,
				this.listOfMadumMethods);
		this.finalMadum =
			this.minimizeMadum(
				this.unifiedMadum,
				this.listOfMadumFields,
				this.listOfMadumMethods);
		this.updateConstructorUsage(
			this.listOfMadumFields,
			this.listOfMadumMethods,
			this.listOfConstructors,
			this.finalMadum);
		this.generateMadumResults();
		this.computeNumberOfTestCases();
		
		

	}

	

	/**
	 * 
	 * @param mapOfAllDirectFieldAccess
	 */
	public void setMapOfAllDirectFieldAccess(
		Map<String, Set<String>> aMapOfAllDirectFieldAccess) {
		this.mapOfAllDirectFieldAccess = aMapOfAllDirectFieldAccess;
	}

	/**
	 * 
	 * @param mapOfTransformersDirectFieldAccess
	 */
	public void setMapOfTransformersDirectFieldAccess(
		Map<String, Set<String>> aMapOfTransformersDirectFieldAccess) {
		this.mapOfTransformersDirectFieldAccess =
			aMapOfTransformersDirectFieldAccess;
	}

	/**
	 * 
	 * @return
	 */
	public IClass getClazz() {
		return this.clazz;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getListOfAllFields() {
		return this.listOfAllFields;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getListOfMadumFields() {
		return this.listOfMadumFields;
	}

	/**
	 * 
	 * @return
	 */
	public int getNumberOfConstants() {
		return this.numberOfConstants;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getListOfAllMethods() {
		return this.listOfAllMethods;

	}

	/**
	 * 
	 * @return
	 */
	public List<String> getListOfMadumMethods() {
		return this.listOfMadumMethods;

	}

	/**
	 * 
	 * @return
	 */
	public int getNbOfMadumGetters() {
		return this.setOfMadumGetters.size();
	}

	/**
	 * 
	 * @return
	 */
	public int getNbOfMadumSetters() {
		return this.setOfMadumSetters.size();
	}

	/**
	 * 
	 * @return
	 */
	public int getNumberOfStaticMethods() {
		return this.numberOfStaticMethods;

	}

	/**
	 * 
	 * @return
	 */
	public long getNumberOfTestCases() {
		return this.numberOfTestCases;

	}

	/**
	 * the number of slices correspond to the number of non constants fields to which methods of the class access directly
	 * @return
	 */
	public int getNbSlices() {
		return this.mapOfAllDirectFieldAccess.size();
	}

	/**
	 * @return Returns the listOfConstructors.
	 */
	public List<String> getListOfConstructors() {
		return this.listOfConstructors;
	}
	/**
	 * @return Returns the setOfMadumSetters.
	 */
	public Set<String> getSetOfMadumSetters() {
		return this.setOfMadumSetters;
	}
	/**
	 * @return Returns the setOfMadumGetters.
	 */
	public Set<String> getSetOfMadumGetters() {
		return this.setOfMadumGetters;
	}
	/**
	 * @return Returns the setOfMadumOthers.
	 */
	public Set<String> getSetOfMadumOthers() {
		return this.setOfMadumOthers;
	}
	/**
	 * @return Returns the setOfMethodsWithoutAccess.
	 */
	public Set<String> getSetOfMethodsWithoutAccess() {
		return this.setOfMethodsWithoutAccess;
	}
	/**
	 * @return Returns the setOfAllTransformers.
	 */
	public Set<String> getSetOfAllTransformers() {
		return this.setOfAllTransformers;
	}
	/**
	 * @return Returns the setOfFieldsNotUsed.
	 */
	public Set<String> getSetOfFieldsNotUsed() {
		return this.setOfFieldsNotUsed;
	}
	/**
	 * 
	 * @return
	 */
	public double getMeanMethodsPerSlice() {
		double sumOfMethods = 0;
		if (this.listOfMadumFields.isEmpty()
				|| this.listOfMadumMethods.isEmpty()
				|| this.mapOfAllDirectFieldAccess.isEmpty()) {
			return 0;
		}
		for (String field : this.mapOfAllDirectFieldAccess.keySet()) {

			sumOfMethods = sumOfMethods + this.getNbMethodsInASlice(field);

		}

		return sumOfMethods / this.getNbSlices();
	}

	/**
	 * Number of all methods that access directly or indirectly to a field without being redundant
	 * @param field
	 * @return
	 */
	public int getNbMethodsInASlice(String field) {
		int nbMethods = 0;
		if (this.listOfMadumFields.isEmpty()
				|| this.listOfMadumMethods.isEmpty()
				|| this.mapOfAllDirectFieldAccess.isEmpty()) {
			return nbMethods;
		}
		int indexOfField = this.listOfMadumFields.indexOf(field);
		for (int j = 0; j < this.listOfMadumMethods.size(); j++) {
			if (this.finalMadum[indexOfField][j] > 0) {
				nbMethods++;
			}

		}
		return nbMethods;
	}

	/**
	 * 
	 * @return
	 */
	public int[][] getFinalMadum() {

		return this.finalMadum;
	}

	/**
	 * 
	 */
	public void lightProcess() {
		
		this.initializeMadum();
		this.buildMethodDependeciesMap();
		if (!this.mapOfAllDirectFieldAccess.isEmpty()) {
			this.generateBaseMadum();
		}
		else {
			for (String method : this.listOfMadumMethods) {
				if (!this.listOfConstructors.contains(method)) {
					this.setOfMethodsWithoutAccess.add(method);
				}
				this.numberOfTestCases = this.listOfMadumMethods.size();
			}
			
		}

	}
	/**
	 * 
	 */
	private Map<String, Set<QualifiedMethod>> qualifyDirectMethods(
		Map<String, Set<String>> aMapOfAllDirectFieldAccess,
		Map<String, Set<String>> aMapOfTransformersDirectFieldAccess) {

		Map<String, Set<QualifiedMethod>> mapOfDirectQualifiedMethods =
			new HashMap<String, Set<QualifiedMethod>>();
		//take the map of all direct fields access and for each method, qualified it (checks if it is a transformer, a setter, a getter or other

		//for (Entry<String, Set<String>> entry : this.mapOfAllDirectFieldAccess

		for (Entry<String, Set<String>> entry : aMapOfAllDirectFieldAccess
			.entrySet()) {
			String field = entry.getKey();
			Set<QualifiedMethod> setOfQualifiedMethods =
				new HashSet<QualifiedMethod>();
			Set<String> setOfMethods = entry.getValue();
			for (String currentMethod : setOfMethods) {
				if (this.listOfSetters.contains(currentMethod)) {
					//it is a setter
					setOfQualifiedMethods.add(new QualifiedMethod(
						currentMethod,
						MethodAccessType.SETTER));
				}

				else if (aMapOfTransformersDirectFieldAccess.containsKey(field)
						&& aMapOfTransformersDirectFieldAccess
							.get(field)
							.contains(currentMethod)) {
					//this method is a transformer
					setOfQualifiedMethods.add(new QualifiedMethod(
						currentMethod,
						MethodAccessType.TRANSFORMER));

				}

				else if (this.listOfGetters.contains(currentMethod)) {
					//it is a getter
					setOfQualifiedMethods.add(new QualifiedMethod(
						currentMethod,
						MethodAccessType.GETTER));

				}
				else {
					//it is an other method 
					setOfQualifiedMethods.add(new QualifiedMethod(
						currentMethod,
						MethodAccessType.OTHER));
				}
			}
			mapOfDirectQualifiedMethods.put(field, setOfQualifiedMethods);

		}

		

		return mapOfDirectQualifiedMethods;

	}
	/**
	 * 
	 * @return
	 */
	private int[][] getInitializedMadum() {
		int[][] madum =
			new int[this.listOfMadumFields.size()][this.listOfMadumMethods
				.size()];
		for (int i = 0; i < this.listOfMadumFields.size(); i++) {
			for (int j = 0; j < this.listOfMadumMethods.size(); j++) {
				madum[i][j] = MethodAccessType.NO_ACCESS;
			}
		}

		return madum;

	}

	/**
	 * contains for each field and each method the number of usages per type of access
	 * @return
	 */
	private int[][][] getInitializedUnifiedMadum() {
		int numberOfTypesOfAccess = 4;
		//0==>transformer, 1==>setter, 2=="getter, 3==>other
		int[][][] madum =
			new int[this.listOfMadumFields.size()][this.listOfMadumMethods
				.size()][numberOfTypesOfAccess];
		for (int i = 0; i < this.listOfMadumFields.size(); i++) {
			for (int j = 0; j < this.listOfMadumMethods.size(); j++) {
				for (int k = 0; k < 4; k++)
					madum[i][j][k] = 0;
			}
		}

		return madum;

	}

	

	/**
	 * 
	 */
	private void buildMethodDependeciesMap() {

		try {
			Iterator<IOperation> operationIterator =
				this.clazz.getConcurrentIteratorOnConstituents(Class
					.forName("padl.kernel.impl.Constructor"));
			while (operationIterator.hasNext()) {
				// for each  method m, list all its method invocations
				final IOperation operation = operationIterator.next();
				if (!operation.isStatic()) {
					Iterator<IMethodInvocation> methodInvocationsIterator =
						operation.getIteratorOnConstituents(Class
							.forName("padl.kernel.impl.MethodInvocation"));
					while (methodInvocationsIterator.hasNext()) {
						final IMethodInvocation methodInvocation =
							methodInvocationsIterator.next();

						final IOperation methodInvoked =
							methodInvocation.getCalledMethod();
						//to be checked --- methodInvoked instanceof IMethod
						//do we have to take into account a constructor called by another method
						if (methodInvoked != null
						//&& methodInvoked instanceof IMethod
								&& !methodInvoked.isStatic()) {

							final IFirstClassEntity targetEntity =
								methodInvocation.getTargetEntity();
							if (targetEntity != null) {
								if (targetEntity.getDisplayID().equals(
									this.clazz.getDisplayID())) {
									// self-call (only this case?)
									//if the call is done by a field...
									if (methodInvocation.getFirstCallingField() != null) {
										continue;
									}
									//comment savoir si la méthode appelée n'est pas celle d'une super classe ou d'un autre objet ayec la même signature
									//operation call methodInvoked
									//how to deal with cycles...
									//for now I will remove recursive methods from the entries
									if (!methodInvoked.getDisplayID().equals(
										operation.getDisplayID())
											&& this.clazz
												.getConstituentFromID(methodInvoked
													.getDisplayID()) != null) {

										if (methodInvocation.getType() != IMethodInvocation.INSTANCE_INSTANCE) {
//											System.out
//												.println(" Not INSTANCE_INSTANCE");
										}
										if (!this.mapOfCalledMethods
											.containsKey(methodInvoked
												.getDisplayID())) {
											this.mapOfCalledMethods.put(
												methodInvoked.getDisplayID(),
												new HashSet<String>());
										}
										this.mapOfCalledMethods.get(
											methodInvoked.getDisplayID()).add(
											operation.getDisplayID());

										if (!this.mapOfMethodsCallers
											.containsKey(operation
												.getDisplayID())) {
											this.mapOfMethodsCallers.put(
												operation.getDisplayID(),
												new HashSet<String>());
										}
										this.mapOfMethodsCallers.get(
											operation.getDisplayID()).add(
											methodInvoked.getDisplayID());
									}
								}
							}
						}
					}
				}
			}

			

		}
		catch (ClassNotFoundException e) {

			e.printStackTrace();
		}

	}
	/**
	* 
	*/
	private int[][][] generateAllMadums(
		Map<String, Set<String>> aMapOfAllDirectFieldAccess,
		Map<String, Set<QualifiedMethod>> aMapOfDirectQualifiedMethods,
		List<String> aListOfMadumFields,
		List<String> aListOfMadumMethods) {

		//generate map of methods with direct access and set of all methods with direct access
		//for (Entry<String, Set<String>> entry : this.mapOfAllDirectFieldAccess
		Map<String, List<String>> mapOfMethodsWithAccess =
			new HashMap<String, List<String>>();
		Set<String> setOfMethodsWithDirectAccessToFields =
			new HashSet<String>();
		for (Entry<String, Set<String>> entry : aMapOfAllDirectFieldAccess
			.entrySet()) {
			
			for (String method : entry.getValue()) {
				
				if (!mapOfMethodsWithAccess.containsKey(method)) {
					mapOfMethodsWithAccess.put(method, new ArrayList<String>());
				}

			}
			
			setOfMethodsWithDirectAccessToFields.addAll(entry.getValue());
		}

		

		//for each method in the set of methods, build all its branches to methods that have direct access to field 
		//and if the branch does not contain any cycle, add this method in the map after the method with direct access
		for (String method : aListOfMadumMethods) {
			Set<String> listOfMethodCalled =
				this.mapOfMethodsCallers.get(method);
			if (listOfMethodCalled != null) {
				Stack<String> stack = new Stack<String>();
				stack.push(method);
				for (String methodCalled : listOfMethodCalled) {
					this.createBranch(
						mapOfMethodsWithAccess,
						methodCalled,
						stack);
				}
			}
		}
		
		//after that build the madum by connecting mapOfMethodsWithAccess and mapOfDirectQualifiedMethods

		Map<String, List<QualifiedMethod>> unifiedMap =
			new HashMap<String, List<QualifiedMethod>>();
		for (Entry<String, Set<QualifiedMethod>> entry : aMapOfDirectQualifiedMethods
			.entrySet()) {
			//for each method that directly access to this field add in the list all the methods that call directly or not this method
			List<QualifiedMethod> listOfQualifiedMethods =
				new ArrayList<QualifiedMethod>();
			for (QualifiedMethod currentQualifiedMethod : entry.getValue()) {
				int methodAccessType =
					currentQualifiedMethod.getMethodAccessType();
				//for Max this is correct if the method does smthg else? how to check whether the method does smthg else? this is for later
				if (methodAccessType == MethodAccessType.SETTER) {
					methodAccessType = MethodAccessType.TRANSFORMER;
				}
				listOfQualifiedMethods.add(currentQualifiedMethod);
				List<String> currentListOfCallers =
					mapOfMethodsWithAccess.get(currentQualifiedMethod
						.getMethodId());
				for (String currentCaller : currentListOfCallers) {
					listOfQualifiedMethods.add(new QualifiedMethod(
						currentCaller,
						methodAccessType));
				}
			}
			unifiedMap.put(entry.getKey(), listOfQualifiedMethods);
		}

		

		//Generate the unified madum that sums the number of usages per type of access
		int[][][] unifiedMadum =
			new int[aListOfMadumFields.size()][aListOfMadumMethods.size()][4];
		//InitializedUnifiedMadum();
		for (int i = 0; i < aListOfMadumFields.size(); i++) {
			for (int j = 0; j < aListOfMadumMethods.size(); j++) {
				for (int k = 0; k < 4; k++)
					unifiedMadum[i][j][k] = 0;
			}
		}
		for (Entry<String, List<QualifiedMethod>> entry : unifiedMap.entrySet()) {
			int indexOfField = aListOfMadumFields.indexOf(entry.getKey());
			List<QualifiedMethod> listOfQualifiedMethodsWithAccess =
				entry.getValue();
			Set<String> setOfAlreadyVisitedMethods = new HashSet<String>();
			for (QualifiedMethod currentQualifiedMethod : listOfQualifiedMethodsWithAccess) {
				if (!setOfAlreadyVisitedMethods.contains(currentQualifiedMethod
					.getMethodId())) {
					int indexOfMethod =
						aListOfMadumMethods.indexOf(currentQualifiedMethod
							.getMethodId());
					int[] tabOfUsages =
						this.generateTabOfUsages(
							currentQualifiedMethod.getMethodId(),
							listOfQualifiedMethodsWithAccess);
					unifiedMadum[indexOfField][indexOfMethod] = tabOfUsages;
					setOfAlreadyVisitedMethods.add(currentQualifiedMethod
						.getMethodId());
				}
			}

		}
		return unifiedMadum;

	}

	/**
	 * 
	 * @param anUnifiedMadum
	 * @param aListOfMadumFields
	 * @param aListOfMadumMethods
	 * @return
	 */
	private int[][] minimizeMadum(
		int[][][] anUnifiedMadum,
		List<String> aListOfMadumFields,
		List<String> aListOfMadumMethods) {
		//Initialize final madum
		int[][] finalMadum =
			new int[aListOfMadumFields.size()][aListOfMadumMethods.size()];
		for (int i = 0; i < aListOfMadumFields.size(); i++) {
			for (int j = 0; j < aListOfMadumMethods.size(); j++) {
				//transformer
				if (anUnifiedMadum[i][j][0] > 0) {
					finalMadum[i][j] = MethodAccessType.TRANSFORMER;
				}//setter 
				else if (anUnifiedMadum[i][j][1] > 0) {
					finalMadum[i][j] = MethodAccessType.SETTER;

				}//getter
				else if (anUnifiedMadum[i][j][2] > 0) {
					finalMadum[i][j] = MethodAccessType.GETTER;

				}
				else if (anUnifiedMadum[i][j][3] > 0) {
					finalMadum[i][j] = MethodAccessType.OTHER;
				}

			}
		}
		
		// minimize the madum
		//minimize the final madum using the unified madum 
		//go for each field from its first method to the root. For each method, compare it with its immediat parent, if their number of usages is same, discard the parent from the madum 

		for (Entry<String, Set<String>> entry : this.mapOfAllDirectFieldAccess
			.entrySet()) {
			
			String fieldName = entry.getKey();
			int indexCurrentField = aListOfMadumFields.indexOf(fieldName);
			Set<String> methodsAccessingToField = entry.getValue();
			for (String currentMethod : methodsAccessingToField) {
				//check the current branch till the end of the branch
				List<String> listOfMethodsInTheBranch = new ArrayList<String>();
				listOfMethodsInTheBranch.add(currentMethod);
				checkBranch(
					indexCurrentField,
					currentMethod,
					anUnifiedMadum,
					finalMadum,
					aListOfMadumMethods,
					listOfMethodsInTheBranch);
			}

		}
		return finalMadum;
	}

	/**
	 * 
	 * @param aListOfMadumFields
	 * @param aListOfMadumMethods
	 * @param aListOfConstructors
	 * @param aFinalMadum
	 */
	private void updateConstructorUsage(
		List<String> aListOfMadumFields,
		List<String> aListOfMadumMethods,
		List<String> aListOfConstructors,
		int[][] aFinalMadum) {
		//Change the data usage of each constructor as a constructor
		for (String constructor : aListOfConstructors) {
			int indexConstructor = aListOfMadumMethods.indexOf(constructor);
			for (int i = 0; i < aListOfMadumFields.size(); i++) {
				if (aFinalMadum[i][indexConstructor] > 0) {
					aFinalMadum[i][indexConstructor] =
						MethodAccessType.CONSTRUCTOR;
				}
			}

		}

	}

	/**
	 * 
	 * @param method
	 * @param listOfQualifiedMethods
	 * @return
	 */
	private int[] generateTabOfUsages(
		String method,
		List<QualifiedMethod> listOfQualifiedMethods) {
		int[] tabOfUsages = new int[4];

		for (QualifiedMethod currentMethod : listOfQualifiedMethods) {
			if (method.equals(currentMethod.getMethodId())) {
				//transformer
				if (currentMethod.getMethodAccessType() == MethodAccessType.TRANSFORMER) {
					tabOfUsages[0]++;
				}//setter 
				else if (currentMethod.getMethodAccessType() == MethodAccessType.SETTER) {
					tabOfUsages[1]++;

				}//getter
				else if (currentMethod.getMethodAccessType() == MethodAccessType.GETTER) {
					tabOfUsages[2]++;

				}
				else if (currentMethod.getMethodAccessType() == MethodAccessType.OTHER) {
					tabOfUsages[3]++;
				}

			}
		}

		return tabOfUsages;
	}

	/**
	 * 
	 * @param method
	 * @param stack
	 */
	private void createBranch(
		Map<String, List<String>> aMapOfMethodsWithAccess,
		String method,
		Stack<String> stack) {
		if (!stack.contains(method)) {
			stack.push(method);
			if (aMapOfMethodsWithAccess.containsKey(method)) {
				//end of the current branch and the method has access to a field
				aMapOfMethodsWithAccess.get(method).add(stack.get(0));
			}
			else if (this.mapOfMethodsCallers.containsKey(method)) {
				//not at the end of the branch
				for (String methodCalled : this.mapOfMethodsCallers.get(method)) {
					this.createBranch(aMapOfMethodsWithAccess, method, stack);
				}

			}//else==>end of the branch without access to a field

		}//else==>cycle in this branch, abandon it

	}

	private void generateMadumResults() {
		//from the set below, we can compute the number of test cases for a class

		//generate set of setters of the class
		this.setOfMadumSetters =
			this.generateSpecificSet(MethodAccessType.SETTER);

		//generate set of transformers of the class
		this.setOfAllTransformers =
			this.generateSpecificSet(MethodAccessType.TRANSFORMER);

		//generate set of getters of the class
		this.setOfMadumGetters =
			this.generateSpecificSet(MethodAccessType.GETTER);

		//generate set of others of the class
		this.setOfMadumOthers =
			this.generateSpecificSet(MethodAccessType.OTHER);

		//generate set of no_access of the class
		this.setOfMethodsWithoutAccess =
			this.generateSetOfMethodsWhithoutAccess();

		//generate set of fields which are not used
		this.setOfFieldsNotUsed = this.generateSetOfFieldsNotUsed();

	}

	/**
	 * 
	 * @return
	 */
	private Set<String> generateSetOfFieldsNotUsed() {
		Set<String> resultSet = new HashSet<String>();
		for (int i = 0; i < this.listOfMadumFields.size(); i++) {
			String field = this.listOfMadumFields.get(i);
			boolean notAccess = true;
			int j = 0;
			while (notAccess && j < this.listOfMadumMethods.size()) {
				if (this.finalMadum[i][j] > 0) {
					notAccess = false;
				}
				j++;

			}
			if (notAccess) {
				resultSet.add(field);
			}

		}
		return resultSet;
	}
	/**
	 * 
	 * @param methodAccessType
	 * @return
	 */
	private Set<String> generateSpecificSet(int methodAccessType) {
		Set<String> resultSet = new HashSet<String>();
		for (int j = 0; j < this.listOfMadumMethods.size(); j++) {
			String method = this.listOfMadumMethods.get(j);
			boolean find = false;
			int i = 0;
			while (!find && i < this.listOfMadumFields.size()) {
				if (this.finalMadum[i][j] == methodAccessType) {
					resultSet.add(method);
					find = true;
				}
				i++;

			}

		}
		return resultSet;
	}

	

	/**
	 * 
	 * @param methodAccessType
	 * @return
	 */
	private Set<String> generateSetOfMethodsWhithoutAccess() {
		Set<String> resultSet = new HashSet<String>();
		for (int i = 0; i < this.listOfMadumMethods.size(); i++) {
			String method = this.listOfMadumMethods.get(i);
			boolean notAccess = true;
			int j = 0;
			while (notAccess && j < this.listOfMadumFields.size()) {
				if (this.finalMadum[j][i] > 0) {
					notAccess = false;
				}
				j++;

			}
			if (notAccess) {
				resultSet.add(method);
			}

		}
		return resultSet;
	}

	/**
	 * 
	 * @return
	 */
	private void computeNumberOfTestCases() {

		//Total number of test cases for a class= |C| + |getters| + |setters| + |others| + sum ( |Cj|*|Tdi|!)
		int sumTestCasesForSliceSequences = 0;
		for (String field : this.mapOfAllDirectFieldAccess.keySet()) {
			sumTestCasesForSliceSequences =
				(this.listOfConstructors.size() * UsefulMethods.factorial(this
					.computeNumberOfTransformers(field)))
						+ sumTestCasesForSliceSequences;
		}
		this.numberOfTestCases =
			this.listOfConstructors.size() + this.setOfMadumGetters.size()
					+ this.setOfMadumSetters.size()
					+ this.setOfMadumOthers.size()
					+ this.setOfMethodsWithoutAccess.size()
					+ sumTestCasesForSliceSequences;
	}

	/**
	 * 
	 * @param field
	 * @return
	 */
	private int computeNumberOfTransformers(String field) {
		int nbOfTansformers = 0;
		int indexOfField = this.listOfMadumFields.indexOf(field);
		for (int j = 0; j < this.listOfMadumMethods.size(); j++) {
			if (this.finalMadum[indexOfField][j] == MethodAccessType.TRANSFORMER) {
				nbOfTansformers++;
			}

		}
		return nbOfTansformers;
	}
	/**
	 * 
	 * @param indexCurrentField
	 * @param aCurrentmethod
	 * @param unifiedMadum
	 */
	private void checkBranch(
		int indexCurrentField,
		String aCurrentmethod,
		int[][][] unifiedMadum,
		int[][] aFinalMadum,
		List<String> aListOfMadumMethods,
		List<String> listOfMethodsInTheBranch) {
		if (this.mapOfCalledMethods.containsKey(aCurrentmethod)) {
			//we are not at the end of the branch
			int indexCurrentMethod =
				aListOfMadumMethods.indexOf(aCurrentmethod);
			Set<String> setOfParentMethods =
				this.mapOfCalledMethods.get(aCurrentmethod);
			for (String currentParentMethod : setOfParentMethods) {
				if (!listOfMethodsInTheBranch.contains(currentParentMethod)) {
					int indexOfCurrentParent =
						aListOfMadumMethods.indexOf(currentParentMethod);
					//Should I wait after building the valid branch or it is fine?
					//compare the madum of the method to the madum of its parent, if their madums are same,remove the entry of the parent
					if (Arrays.equals(
						unifiedMadum[indexCurrentField][indexCurrentMethod],
						unifiedMadum[indexCurrentField][indexOfCurrentParent])) {
						aFinalMadum[indexCurrentField][indexOfCurrentParent] =
							MethodAccessType.NO_ACCESS;
					}
					else {
						if (aFinalMadum[indexCurrentField][indexOfCurrentParent] == MethodAccessType.TRANSFORMER) {
							if (unifiedMadum[indexCurrentField][indexCurrentMethod][0] == unifiedMadum[indexCurrentField][indexOfCurrentParent][0]) {
								aFinalMadum[indexCurrentField][indexOfCurrentParent] =
									MethodAccessType.NO_ACCESS;
							}
						}
						else if (aFinalMadum[indexCurrentField][indexOfCurrentParent] == MethodAccessType.SETTER) {
							if (unifiedMadum[indexCurrentField][indexCurrentMethod][1] == unifiedMadum[indexCurrentField][indexOfCurrentParent][1]) {
								aFinalMadum[indexCurrentField][indexOfCurrentParent] =
									MethodAccessType.NO_ACCESS;
							}
						}
						else if (aFinalMadum[indexCurrentField][indexOfCurrentParent] == MethodAccessType.GETTER) {
							if (unifiedMadum[indexCurrentField][indexCurrentMethod][2] == unifiedMadum[indexCurrentField][indexOfCurrentParent][2]) {
								aFinalMadum[indexCurrentField][indexOfCurrentParent] =
									MethodAccessType.NO_ACCESS;
							}
						}
						else if (aFinalMadum[indexCurrentField][indexOfCurrentParent] == MethodAccessType.OTHER) {
							if (unifiedMadum[indexCurrentField][indexCurrentMethod][3] == unifiedMadum[indexCurrentField][indexOfCurrentParent][3]) {
								aFinalMadum[indexCurrentField][indexOfCurrentParent] =
									MethodAccessType.NO_ACCESS;
							}
						}
					}
					listOfMethodsInTheBranch.add(currentParentMethod);
					this.checkBranch(
						indexCurrentField,
						currentParentMethod,
						unifiedMadum,
						aFinalMadum,
						aListOfMadumMethods,
						listOfMethodsInTheBranch);
				}//else==>we are in the cycle... it changes something in the minimisation (let check later), 
					//means if the parent has been added from another method and not from the current method???

			}

		}//end of the branch; we check and remove all the redundancies in this branch

	}
	@Override
	public int hashCode() {

		int result = 1;
		result = ((this.clazz == null) ? 0 : this.clazz.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MadumClass other = (MadumClass) obj;
		if (this.clazz == null) {
			if (other.clazz != null)
				return false;
		}
		else if (!this.clazz.equals(other.clazz))
			return false;
		return true;
	}
	public void setMapOfAllDirectSuperFieldAccess(
		Map<String, Set<String>> aMapOfAllDirectSuperFieldAccess) {
		this.mapOfAllDirectSuperFieldAccess = aMapOfAllDirectSuperFieldAccess;

	}
	public void setMapOfTransformersDirectSuperFieldAccess(
		Map<String, Set<String>> aMapOfTransformersDirectSuperFieldAccess) {
		this.mapOfTransformersDirectSuperFieldAccess =
			aMapOfTransformersDirectSuperFieldAccess;

	}

	public Map<String, Set<String>> getMapOfAllDirectSuperFieldAccess() {
		return this.mapOfAllDirectSuperFieldAccess;

	}
	/**
	 * @return Returns the setOfOverridenMethods.
	 */
	public Set<String> getSetOfOverridenMethods() {
		return this.setOfOverridenMethods;
	}
	/**
	 * @param setOfOverridenMethods The setOfOverridenMethods to set.
	 */
	public void setSetOfOverridenMethods(Set<String> setOfOverridenMethods) {
		this.setOfOverridenMethods = setOfOverridenMethods;
	}
	/**
	 * @return Returns the setOfSelfCall.
	 */
	public Set<String> getSetOfSelfCall() {
		return this.setOfSelfCall;
	}
	/**
	 * @param setOfSelfCall The setOfSelfCall to set.
	 */
	public void setSetOfSelfCall(Set<String> setOfSelfCall) {
		this.setOfSelfCall = setOfSelfCall;
	}
	/**
	 * @return Returns the setOfSuperMethodInvoked.
	 */
	public Set<String> getSetOfSuperMethodInvoked() {
		return this.setOfSuperMethodInvoked;
	}
	/**
	 * @param setOfSuperMethodInvoked The setOfSuperMethodInvoked to set.
	 */
	public void setSetOfSuperMethodInvoked(Set<String> setOfSuperMethodInvoked) {
		this.setOfSuperMethodInvoked = setOfSuperMethodInvoked;
	}
	/**
	 * @return Returns the mapOfMethodsCallers.
	 */
	public Map<String, Set<String>> getMapOfMethodsCallers() {
		return this.mapOfMethodsCallers;
	}
	/**
	 * @param mapOfMethodsCallers The mapOfMethodsCallers to set.
	 */
	public void setMapOfMethodsCallers(
		Map<String, Set<String>> mapOfMethodsCallers) {
		this.mapOfMethodsCallers = mapOfMethodsCallers;
	}
	/**
	 * @return Returns the mapOfCalledMethods.
	 */
	public Map<String, Set<String>> getMapOfCalledMethods() {
		return this.mapOfCalledMethods;
	}
	/**
	 * @param mapOfCalledMethods The mapOfCalledMethods to set.
	 */
	public void setMapOfCalledMethods(
		Map<String, Set<String>> mapOfCalledMethods) {
		this.mapOfCalledMethods = mapOfCalledMethods;
	}
	/**
	 * @return Returns the finalUnifiedMadum.
	 */
	public int[][][] getFinalUnifiedMadum() {
		return this.finalUnifiedMadum;
	}
	/**
	 * @param finalUnifiedMadum The finalUnifiedMadum to set.
	 */
	public void setFinalUnifiedMadum(int[][][] finalUnifiedMadum) {
		this.finalUnifiedMadum = finalUnifiedMadum;
	}
	/**
	 * @return Returns the listOfFinalMadumFields.
	 */
	public List<String> getListOfFinalMadumFields() {
		return this.listOfFinalMadumFields;
	}
	/**
	 * @param listOfFinalMadumFields The listOfFinalMadumFields to set.
	 */
	public void setListOfFinalMadumFields(List<String> listOfFinalMadumFields) {
		this.listOfFinalMadumFields = listOfFinalMadumFields;
	}
	/**
	 * @return Returns the listOfFinalMadumMethods.
	 */
	public List<String> getListOfFinalMadumMethods() {
		return this.listOfFinalMadumMethods;
	}
	/**
	 * @param listOfFinalMadumMethods The listOfFinalMadumMethods to set.
	 */
	public void setListOfFinalMadumMethods(List<String> listOfFinalMadumMethods) {
		this.listOfFinalMadumMethods = listOfFinalMadumMethods;
	}
	/**
	 * @param finalMadum The finalMadum to set.
	 */
	public void setFinalMadum(int[][] finalMadum) {
		this.finalMadum = finalMadum;
	}
	/**
	 * @return Returns the unifiedMadum.
	 */
	public int[][][] getUnifiedMadum() {
		return this.unifiedMadum;
	}
	/**
	 * @param unifiedMadum The unifiedMadum to set.
	 */
	public void setUnifiedMadum(int[][][] unifiedMadum) {
		this.unifiedMadum = unifiedMadum;
	}

}

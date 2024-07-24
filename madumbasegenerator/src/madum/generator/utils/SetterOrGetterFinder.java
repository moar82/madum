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
package madum.generator.utils;

import padl.kernel.Constants;
import padl.util.Util;

public class SetterOrGetterFinder {

	public static boolean isGettersMany(

	final String currentName, final int nbArguments) {

		// Yann 2005/08/07: Getters and Setters!
		// I return whether or not the current method is a getter or
		// a setter so that I can replace the actual IMethod by an
		// appropriate instances of ISetter or IGetter.
		for (int i = 0; i < Constants.GETTERS_CARDINALITY_MANY.length; i++) {
			if (currentName.startsWith(Constants.GETTERS_CARDINALITY_MANY[i])
					&& nbArguments == Constants.GETTERS_MAX_ARGS_CARDINALITY_MANY[i]) {

				// Yann 2003/11/28: Collection.
				// I now handle the case of:
				//		remove(int)

				return true;
			}
		}
		return false;
	}
	public static boolean isGettersOne(
		final String currentName,
		final int nbArguments) {

		// Yann 2003/11/28: Generalization!
		// Names of the getters and setters are now in the
		// Constants interface.
		// Yann 2005/08/07: Getters and Setters!
		// I return whether or not the current method is a getter or
		// a setter so that I can replace the actual IMethod by an
		// appropriate instances of ISetter or IGetter.
		for (int i = 0; i < Constants.GETTERS_CARDINALITY_ONE.length; i++) {
			if (currentName.startsWith(Constants.GETTERS_CARDINALITY_ONE[i])
					&& nbArguments == Constants.GETTERS_MAX_ARGS_CARDINALITY_ONE[i]) {

				return true;
			}
		}

		return false;
	}
	public static boolean isSettersMany(

	final String currentName, final int nbArguments) {

		// Yann 2005/08/07: Getters and Setters!
		// I return whether or not the current method is a getter or
		// a setter so that I can replace the actual IMethod by an
		// appropriate instances of ISetter or IGetter.
		for (int i = 0; i < Constants.SETTERS_CARDINALITY_MANY.length; i++) {
			// Yann 2002/08/21: Collection.
			// I now handle the case of:
			//		add(int, <Type>)
			// Yann 2003/11/28: Hashtable.
			// I now handle the case of:
			//		put(<Object key>, <Object value>)
			if (currentName.startsWith(Constants.SETTERS_CARDINALITY_MANY[i])
					&& nbArguments == Constants.SETTERS_MAX_ARGS_CARDINALITY_MANY[i]) {

				return true;
			}
		}

		return false;
	}
	public static boolean isSettersOne(
		final String currentName,
		final int nbArguments) {

		// Yann 2003/11/28: Generalization!
		// Names of the getters and setters are now in the
		// Constants interface.
		// Yann 2005/08/07: Getters and Setters!
		// I return whether or not the current method is a getter or
		// a setter so that I can replace the actual IMethod by an
		// appropriate instances of ISetter or IGetter.
		for (int i = 0; i < Constants.SETTERS_CARDINALITY_ONE.length; i++) {
			if (currentName.startsWith(Constants.SETTERS_CARDINALITY_ONE[i])
					&& nbArguments == Constants.SETTERS_MAX_ARGS_CARDINALITY_ONE[i]) {
				// Yann 2006/02/23: Primitive types.
				// Why bother checking if it is a primitive type?
				//	&& !Util.isPrimtiveType(arguments[0])) {

				return true;
			}
		}

		return false;
	}
	
	/**
	 * 
	 * @param methodName
	 * @param nbArguments
	 * @return
	 */
	public static boolean isGetters(String methodName, int nbArguments) {
		
		return SetterOrGetterFinder.isGettersMany(methodName, nbArguments) || SetterOrGetterFinder.isGettersOne(methodName, nbArguments);
		
	}
	
	/**
	 * 
	 * @param methodName
	 * @param nbArguments
	 * @return
	 */
	public static boolean isSetters(String methodName, int nbArguments) {
		
		return SetterOrGetterFinder.isSettersMany(methodName, nbArguments) || SetterOrGetterFinder.isSettersOne(methodName, nbArguments);
	}
}

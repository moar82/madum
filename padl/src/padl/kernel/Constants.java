/*
 * (c) Copyright 2001-2003 Yann-Ga�l Gu�h�neuc,
 * �cole des Mines de Nantes and Object Technology International, Inc.
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
package padl.kernel;

public interface Constants {
	boolean DEBUG = false;

	// Priority constants.
	int HIGHEST_RECOGNITION_PRIORITY = 100;
	int HIGH_RECOGNITION_PRIORITY = 75;
	int NORMAL_RECOGNITION_PRIORITY = 50;
	int LOW_RECOGNITION_PRIORITY = 25;
	int LOWEST_RECOGNITION_PRIORITY = 0;

	// Weight constants.
	int MANDATORY = 100;
	int STRONGLY_REQUIRED = 75;
	int REQUIRED = 50;
	int WEAKLY_REQUIRED = 25;
	int OPTIONAL = 1;

	// Cardinalities.
	int CARDINALITY_ONE = 1;
	int CARDINALITY_MANY = 2;

	// Prefix of getters and setters.
	// Yann 2006/02/23: New names.
	// I add "get" and "set" to MANY because of class 
	// org.argouml.application.configuration.ConfigurationProperties
	// which defines getValue(String, String) and setValue(String, String)
	// Yann 2007/10/31: Law and order!
	// When several names start with the same letters,
	// I must put the longer one first. Also, the one
	// with the larger number of possible arguments.
	String[] GETTERS_CARDINALITY_MANY =
		new String[] { "removeFrom", "removeFrom", "remove", "get", "get",
				"has", "elementAt", "contains", "elements", "size", "indexOf",
				"subList", "remove" };
	int[] GETTERS_MAX_ARGS_CARDINALITY_MANY =
		new int[] { 2, 1, 1, 1, 2, 1, 1, 1, 0, 0, 1, 2, 3 };

	String[] SETTERS_CARDINALITY_MANY =
		new String[] { "addTo", "addTo", "add", "put", "clear", "reset",
				"init", "set", "add", "put" };
	int[] SETTERS_MAX_ARGS_CARDINALITY_MANY =
		new int[] { 2, 1, 1, 2, 0, 0, 0, 2, 2, 3 };

	String[] GETTERS_CARDINALITY_ONE = new String[] { "get", "is", "is" };
	int[] GETTERS_MAX_ARGS_CARDINALITY_ONE = new int[] { 0, 0, 1 };

	String[] SETTERS_CARDINALITY_ONE = new String[] { "set", "set" };
	int[] SETTERS_MAX_ARGS_CARDINALITY_ONE = new int[] { 1, 1 };

	char[] DEFAULT_PACKAGE_ID = "DEFAULT_PACKAGE_ID".toCharArray();
	char[] DEFAULT_CODELEVELMODEL_ID = "CodeLevelModel".toCharArray();
	char[] DEFAULT_LIST_INTERFACE = "java.util.List".toCharArray();
	char[] DEFAULT_DESIGNLEVELMODEL_ID = "DesignLevelModel".toCharArray();
	char[] DEFAULT_HIERARCHY_ROOT_ID = "java.lang.Object".toCharArray();
	char[] DEFAULT_HIERARCHY_ROOT_NAME = "Object".toCharArray();
	char[] FORBIDDEN_ID = "FORBIDDEN_ID".toCharArray();
	char[] DEFAULT_IDIOMLEVELMODEL_ID = "IdiomLevelModel".toCharArray();
	char[] DEFAULT_METHODINVOCATION_ID = "Method Invocation".toCharArray();
	char[] DEFAULT_METHODINVOCATION_NO_DECLARING_ENTITY =
		"NoDeclaringEntity".toCharArray();
	String DEFAULT_METHODINVOCATION_NO_FIELD = "NoField";
	char[] DEFAULT_METHODINVOCATION_NO_FIELD_ENTITY =
		"NoFieldEntity".toCharArray();
	char[] DEFAULT_METHODINVOCATION_NO_INVOKED_METHOD =
		"NoInvokedMethod".toCharArray();
	char[] DEFAULT_METHODINVOCATION_NO_METHOD = "NoMethod".toCharArray();
	char[] DEFAULT_METHODINVOCATION_NO_TARGET = "NoTarget".toCharArray();

	char[] NONAME = "NONAME".toCharArray();
}

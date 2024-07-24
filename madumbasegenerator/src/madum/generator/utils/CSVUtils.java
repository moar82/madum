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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import madum.generator.MadumClass;
import madum.generator.MethodAccessType;
import padl.kernel.IClass;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IFirstClassEntity;

public class CSVUtils {

	public static void generateListOfClassesOfPadlModel(
		ICodeLevelModel aModel,
		String outputPath,
		String systemName) {
		//put the list of padl model classes in a csv file; 
		Iterator<IFirstClassEntity> iterator =
			aModel.getIteratorOnTopLevelEntities();
		List<String> classesList = new ArrayList<String>();
		while (iterator.hasNext()) {
			IFirstClassEntity currentEntity = iterator.next();
			if (currentEntity instanceof IClass) {
				classesList.add(currentEntity.getDisplayID());
			}
		}
		//write the list in a csv file
		System.out.print("Model contains ");
		System.out.print(classesList.size());
		System.out.println(" classes");
		String finalOutputPath =
			new StringBuffer()
				.append(outputPath)
				.append("classesList_")
				.append(systemName)
				.append(".csv")
				.toString();
		CSVUtils.writeListInCSV(classesList, "classId", finalOutputPath);

	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	public static List<String> readIntoList(String path) {
		List<String> list = new ArrayList<String>();
		try {
			BufferedReader bufferedReader =
				new BufferedReader(new FileReader(path));
			String line;
			try {
				// read title
				line = bufferedReader.readLine();
				// read first line of data
				line = bufferedReader.readLine();

				while (line != null) {
					list.add(line);
					line = bufferedReader.readLine();

				}
			}
			catch (IOException e) {

				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 
	 * @param list
	 * @param title
	 * @param aResultFilePath
	 */
	public static void writeListInCSV(
		List<String> list,
		String title,
		String aResultFilePath) {

		PrintStream writer;
		try {
			writer = new PrintStream(new File(aResultFilePath));
			writer.println(title);
			for (int i = 0; i < list.size(); i++) {
				writer.println(list.get(i));
			}
			writer.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void printMadum(
		MadumClass aMadumClass,
		String pathRoot,
		String systemName) {
		PrintStream writer;
		try {
			String finalPath =
				new StringBuffer()
					.append(pathRoot)
					.append("Madum_")
					.append(systemName)
					.append("_")
					.append(aMadumClass.getClazz().getDisplayID())
					.append(".csv")
					.toString();
			int[][] finalMadum = aMadumClass.getFinalMadum();
			writer = new PrintStream(new File(finalPath));
			for (String method : aMadumClass.getListOfMadumMethods()) {
				writer.print(";");
				writer.print(method);
			}
			writer.println("");
			for (int i = 0; i < aMadumClass.getListOfMadumFields().size(); i++) {
				writer.print(aMadumClass.getListOfMadumFields().get(i));
				for (int j = 0; j < aMadumClass.getListOfMadumMethods().size(); j++) {
					String label="";
					if(finalMadum[i][j]==MethodAccessType.CONSTRUCTOR){
						label="c";
					}else if(finalMadum[i][j]==MethodAccessType.SETTER){
						label="s";
					}else if(finalMadum[i][j]==MethodAccessType.GETTER){
						label="g";
					}else if(finalMadum[i][j]==MethodAccessType.OTHER){
						label="o";
					}else if(finalMadum[i][j]==MethodAccessType.TRANSFORMER){
						label="t";
					}else{
						label="";
					}
					writer.print(";" + label);
				}
				writer.println("");
			}

			writer.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param pathRoot
	 * @param systemName
	 * @param aListOfMadums
	 */
	public static void saveLightMadum(
		String pathRoot,
		String systemName,
		List<MadumClass> aListOfMadums) {

		String detailsPath =
			new StringBuffer()
				.append(pathRoot)
				.append("DetailsMadums_")
				.append(systemName)
				.append(".csv")
				.toString();

		try {

			PrintStream detailsWriter = new PrintStream(new File(detailsPath));

			String detailTitle =
				"classId;AllFields;ConstantFields;FieldsNotUsed;AllMethods;StaticMethods;Constructors;Getters;Setters;Transformers;Others;Slices;MeanMethodsPerSlice;TestCases";

			detailsWriter.println(detailTitle);
			for (MadumClass madumClass : aListOfMadums) {

				//details
				detailsWriter.print(madumClass.getClazz().getDisplayID() + ";");
				detailsWriter.print(madumClass.getListOfAllFields().size()
						+ ";");
				detailsWriter.print(madumClass.getNumberOfConstants() + ";");
				detailsWriter.print(madumClass.getSetOfFieldsNotUsed().size()
						+ ";");

				detailsWriter.print(madumClass.getListOfAllMethods().size()
						+ ";");
				detailsWriter
					.print(madumClass.getNumberOfStaticMethods() + ";");
				detailsWriter.print(madumClass.getListOfConstructors().size()
						+ ";");
				detailsWriter.print(madumClass.getSetOfMadumGetters().size()
						+ ";");
				detailsWriter.print(madumClass.getSetOfMadumSetters().size()
						+ ";");
				detailsWriter.print(madumClass.getSetOfAllTransformers().size()
						+ ";");
				detailsWriter
					.print((madumClass.getSetOfMadumOthers().size() + madumClass
						.getSetOfMethodsWithoutAccess()
						.size()) + ";");

				detailsWriter.print(madumClass.getNbSlices() + ";");
				DecimalFormat df = new DecimalFormat("########.00");
				detailsWriter.print(df.format(madumClass
					.getMeanMethodsPerSlice()) + ";");
				detailsWriter.print(madumClass.getNumberOfTestCases());

				detailsWriter.println("");
				

			}

			detailsWriter.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param pathRoot
	 * @param aMadumClass
	 */
	public static void saveCompleteMadum(String pathRoot, MadumClass aMadumClass) {

	}

	/**
	 * 
	 * @param dataPath
	 * @return
	 */
	public static Map<String, String[]> readIntoMap(String dataPath) {
		Map<String, String[]> map = new HashMap<String, String[]>();

		try {
			BufferedReader bufferedReader =
				new BufferedReader(new FileReader(dataPath));
			String line;
			try {
				// read title
				line = bufferedReader.readLine();
				// read first line of data
				line = bufferedReader.readLine();

				while (line != null) {
					String[] elts = line.split(";");
					String[] tab = new String[elts.length - 1];
					for (int i = 0; i < tab.length; i++) {
						tab[i] = elts[i + 1];
					}
					map.put(elts[0], tab);
					line = bufferedReader.readLine();

				}
				bufferedReader.close();
			}
			catch (IOException e) {

				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 
	 * @param map
	 * @param path
	 * @param title
	 */
	public static void writeMapInCSV(
		Map<String, String[]> map,
		String path,
		String title) {
		PrintStream writer;
		try {
			writer = new PrintStream(new File(path));
			writer.println(title);
			for (Entry<String, String[]> entry : map.entrySet()) {
				StringBuffer buffer = new StringBuffer();
				buffer.append(entry.getKey());
				String tab[] = entry.getValue();
				for (String s : tab) {
					buffer.append(";");
					buffer.append(s);
				}
				writer.println(buffer.toString());
			}
			writer.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}

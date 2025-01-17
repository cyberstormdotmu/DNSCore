/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package de.uzk.hki.da.convert;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.format.KnownFormatCmdLineErrors;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Event;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.CommandLineConnector;
import de.uzk.hki.da.utils.IOTimeoutException;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.SimplifiedCommandLineConnector;
import de.uzk.hki.da.utils.StringUtilities;


/**
 * The Class CLIConversionStrategy.
 */
public class CLIConversionStrategy implements ConversionStrategy{

	/** The logger. */
	@SuppressWarnings("unused")
	private static Logger logger = 
			LoggerFactory.getLogger(CLIConversionStrategy.class);
		
	/** The command line. */
	protected String commandLine;
	
	/** The pkg. */
	protected Package pkg;
	
	/** The object. */
	protected Object object;

	
	/** The cli connector. */
	protected SimplifiedCommandLineConnector cliConnector;
	
	public static long timeout = 86400123;
	
	private String toAbsolutePath(Path dataPath,DAFile f) {
		return dataPath.toString()+"/"+f.getRep_name()+"/"+f.getRelative_path();
	}
	
	/**
	 * Convert file.
	 *
	 * @param ci the ci
	 * @return the list
	 * @throws FileNotFoundException the file not found exception
	 */
	@Override
	public List<Event> convertFile(
			WorkArea wa,
			ConversionInstruction ci
			) 
					throws FileNotFoundException {
		if (pkg==null) throw new IllegalStateException("Package not set");
		Path.make(wa.dataPath(),object.getNameOfLatestBRep(),ci.getTarget_folder()).toFile().mkdirs();
		
		String[] commandAsArray = assemble(wa,ci, object.getNameOfLatestBRep());
		String targetName = this.targetFileName(wa, ci);
		String targetSuffix= ci.getConversion_routine().getTarget_suffix();
		if (targetSuffix.equals("*")) targetSuffix= FilenameUtils.getExtension(toAbsolutePath(wa.dataPath(),ci.getSource_file()));

		try {
			if (!cliConnector.execute(commandAsArray, CLIConversionStrategy.timeout)) {
				throw new RuntimeException("convert did not succeed:"+cliConnector.getErrorMessages());
			}
		} catch (Exception xtc) {
			String targetDirName = wa.dataPath() + "/" + object.getNameOfLatestBRep(); 
			Path path = Path.make(targetDirName, targetName + "." + targetSuffix);
			File targetFile = path.toFile();
			boolean delly = targetFile.delete();
			logger.debug("Not convertet: delete: " + path + ": " + delly);
			if (xtc.getCause() instanceof IOTimeoutException) {
				throw new UserException(UserExceptionId.CONVERSION_TIMEOUT, "Zeitüberschreitung bei der Konversion");
			}
			
			throw xtc;
		}
		
		DAFile result = new DAFile(object.getNameOfLatestBRep(),
				ci.getTarget_folder()+"/"+FilenameUtils.removeExtension(Matcher.quoteReplacement(
				FilenameUtils.getName(toAbsolutePath(wa.dataPath(),ci.getSource_file())))) + "." + targetSuffix);
		
		Event e = new Event();
		e.setType("CONVERT");
		e.setDetail(StringUtilities.createString(commandAsArray));
		e.setSource_file(ci.getSource_file());
		e.setTarget_file(result);
		e.setDate(new Date());
		
		List<Event> results = new ArrayList<Event>(); results.add(e);
		return results;
	}

	public String targetFileName(WorkArea wa, ConversionInstruction ci) {
		String ret = ci.getTarget_folder() + "/" + FilenameUtils.removeExtension(
				Matcher.quoteReplacement(FilenameUtils.getName(toAbsolutePath(wa.dataPath(), ci.getSource_file()))));
		return ret;
	}	
	
	/**
	 * Tokenizes commandLine and replaces certain strings.
	 * "input" and "output" get replaced by paths of source and destination file.
	 * strings beginning with "{" and ending with "}" get replaced by the contents of additionalParams of the ConversionInstruction.
	 * Each of the {}-surrounded string gets replaced by exactly one token of additional params.
	 *
	 * @param ci the ci
	 * @param repName the rep name
	 * @return The processed command as list of tokens. The tokenized string has the right format
	 * for a call in Runtime.getRuntime().exec(commandToExecute). This holds especially true
	 * for filenames (which replace the input/output parameters) that are separated by
	 * whitespaces. "file 2.jpg" is represented as one token only.
	 */
	protected String[] assemble( 
			WorkArea wa,
			ConversionInstruction ci, String repName){
				
		String commandLine_=commandLine;
		
		// replace additional params
		List<String> ap= tokenize( ci.getAdditional_params(), ",");
		for (String s:ap){
			
			Pattern pattern= Pattern.compile("\\{.*?\\}");
			Matcher matcher= pattern.matcher(commandLine_);
			commandLine_= matcher.replaceFirst(s);
		}
	
		// tokenize before replacement to group original tokens together
		// (to prevent wrong tokenization like two tokens for "file" "2.jpg"
		//  which can result from replacement)
		String[] tokenizedCmd = tokenize(commandLine_);
		
		String targetSuffix= ci.getConversion_routine().getTarget_suffix();
		if (targetSuffix.equals("*")) targetSuffix= FilenameUtils.getExtension(wa.toFile(ci.getSource_file()).getAbsolutePath());
		StringUtilities.replace(tokenizedCmd, "input", wa.toFile(ci.getSource_file()).getAbsolutePath());
		StringUtilities.replace(tokenizedCmd, "output", wa.dataPath()+"/"+repName+"/"+StringUtilities.slashize(ci.getTarget_folder())+
				FilenameUtils.removeExtension(Matcher.quoteReplacement(
				FilenameUtils.getName(wa.toFile(ci.getSource_file()).getAbsolutePath()))) + "." + targetSuffix);
		
		return tokenizedCmd;
	}

	
	
	
	/**
	 * Tokenize.
	 *
	 * @param what the what
	 * @param delim the delim
	 * @return the list
	 */
	private List<String> tokenize(String what,String delim){
		if (what==null) what="";
		
		List<String> l= new ArrayList<String>();
		
		StringTokenizer tokenizer= new StringTokenizer( what, delim );
		while (tokenizer.hasMoreTokens()){			
			l.add( tokenizer.nextToken() );
		}
		return l;
	}

	/**
	 * Tokenize.
	 *
	 * @param what the what
	 * @return the string[]
	 */
	private String[] tokenize(String what){
		List<String> l = tokenize(what," ");
		return l.toArray(new String[l.size()]);
	}
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setParam(java.lang.String)
	 */
	@Override
	public
	void setParam(String param) {
		this.commandLine=param;
	}
	
	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setCLIConnector(de.uzk.hki.da.convert.CLIConnector)
	 */
	@Override
	public void setCLIConnector(CommandLineConnector cliConnector) {
		this.cliConnector = new SimplifiedCommandLineConnector();
	}

	/* (non-Javadoc)
	 * @see de.uzk.hki.da.convert.ConversionStrategy#setObject(de.uzk.hki.da.model.Object)
	 */
	@Override
	public void setObject(Object obj) {
		this.object = obj;
		this.pkg = obj.getLatestPackage();
	}

	@Override
	public void setPruneErrorOrWarnings(boolean prune) {
		
	}

	@Override
	public void setKnownFormatCommandLineErrors(
			KnownFormatCmdLineErrors knownErrors) {
	}
}

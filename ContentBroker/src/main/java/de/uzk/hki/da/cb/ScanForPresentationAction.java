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

package de.uzk.hki.da.cb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.core.SubsystemNotAvailableException;
import de.uzk.hki.da.format.FileFormatException;
import de.uzk.hki.da.format.FileFormatFacade;
import de.uzk.hki.da.format.FileWithFileFormat;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.metadata.EadMetsMetadataStructure;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.ConversionInstructionBuilder;
import de.uzk.hki.da.model.ConversionPolicy;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Package;
import de.uzk.hki.da.util.ConfigurationException;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FriendlyFilesUtils;
import de.uzk.hki.da.utils.StringUtilities;


/**
 * @author Daniel M. de Oliveira
 * @author Sebastian Cuy
 */
public class ScanForPresentationAction extends AbstractAction{
	
	private static final String PREMIS_XML = "premis.xml";
	private FileFormatFacade fileFormatFacade;
	private final ConversionInstructionBuilder ciB = new ConversionInstructionBuilder();
	private DistributedConversionAdapter distributedConversionAdapter;
	
	public ScanForPresentationAction(){}
	
	@Override
	public void checkConfiguration() {
		if (distributedConversionAdapter==null) throw new ConfigurationException("distributedConversionAdapter");
		if (fileFormatFacade==null) throw new ConfigurationException("fileFormatFacade");
	}
	

	@Override
	public void checkPreconditions() {
	}

	@Override
	public boolean implementation() throws SubsystemNotAvailableException{
		
		if (!StringUtilities.isSet(preservationSystem.getPresServer())){
			return true;
		}

		if (Boolean.TRUE.equals(o.getContractor().isUsePublicMets())){
			return true;
		}
		
		List<? extends FileWithFileFormat> fffl=null;
		try {
			fffl = fileFormatFacade.identify(wa.dataPath(),o.getNewestFilesFromAllRepresentations(o.getFriendlyFileExtensions()),o.getLatestPackage().isPruneExceptions());
		} catch (FileFormatException e) {
			throw new RuntimeException(C.ERROR_MSG_DURING_FILE_FORMAT_IDENTIFICATION,e);
		} catch (IOException e) {
			throw new SubsystemNotAvailableException(e);
		}
		
		@SuppressWarnings("unchecked")
		List<ConversionInstruction> cisPres = generateConversionInstructionsForPresentation(
			o.getLatestPackage(),
			(List<DAFile>) fffl);
		
		
		if (cisPres.size() == 0) logger.trace("no Conversion instructions for Presentation found!");				
		for (ConversionInstruction ci:cisPres) logger.info("Built conversionInstructionForPresentation: "+ci.toString());
		
		j.getConversion_instructions().addAll(cisPres);
		
		return true;
	}
	
	

	@Override
	public void rollback() {
		
		j.getConversion_instructions().clear();
		for (ConversionInstruction ci: j.getConversion_instructions()){
			logger.warn("still exists: "+ci);
		}
	}

	/**
	 * Every file in the files list gets tested with respect to if a ConversionPolicies of contractor PRESENTER will apply to it.
	 * If that is the case a ConversionInstruction gets generated for that file. Based on that information
	 * a format conversion process will later be executed for that file. 
	 * 
	 * @author Sebastian Cuy, Daniel de Oliveira
	 * @param pathToRepresentation physical path to source representation
	 * @param files
	 */
	public List<ConversionInstruction> generateConversionInstructionsForPresentation( 
			Package pkg, List<DAFile> files ){
		
		List<ConversionInstruction> cis = new ArrayList<ConversionInstruction>();

		TreeSet<String> neverConverted = this.neverConverted();
		for (DAFile file : files) {

			// get cps for fileanduser. do with cps: assemble

			String relPath = file.getRelative_path();
			logger.debug("File: " + relPath);

			if (neverConverted.contains(relPath)) {
				logger.debug("Skipping file: " + relPath);
			} else {
				logger.trace("Generating ConversionInstructions for PRESENTER: " + relPath);
				List<ConversionPolicy> policies = preservationSystem.getApplicablePolicies(file, true);
				if (o.grantsRight("PUBLICATION") && !wa.toFile(file).getName().toLowerCase().endsWith(".xml") && !wa.toFile(file).getName().toLowerCase().endsWith(".rdf")
						&& !wa.toFile(file).getName().toLowerCase().endsWith(".xmp") && (policies == null || policies.isEmpty())) {
					throw new RuntimeException("No policy found for file " + wa.toFile(file).getAbsolutePath() + "(" + file.getFormatPUID()
							+ ")! Package can not be published because it would be incomplete.");
				} else {
					if (FriendlyFilesUtils.isFriendlyFile(relPath, o.getFriendlyFileExtensions())) {
						logger.debug("Friendly file: " + relPath);
					} else {
						for (ConversionPolicy p : policies) {
							logger.info("Found applicable Policy for FileFormat " + p.getSource_format() + " -> " + p.getConversion_routine().getName() + "("
									+ file.getRelative_path() + ")");
							ConversionInstruction ci = ciB.assembleConversionInstruction(wa, file, p);
							ci.setTarget_folder(ci.getTarget_folder());
							ci.setSource_file(file);

							cis.add(ci);
						}
					}
				}
			}
		}
		return cis;
	}
	
	protected TreeSet<String> neverConverted()
	{
		TreeSet<String> ret = new TreeSet<String>();
		ret.add(PREMIS_XML);
		ret.add(C.PUBLIC_METS);

		if (o.getMetadata_file() != null) {
			ret.add(o.getMetadata_file());
			String packageType = o.getPackage_type();

			if ("EAD".equals(packageType)) {
				String mfPathSrc = o.getLatest(o.getMetadata_file()).getPath().toString();
				EadMetsMetadataStructure emms = null;
				try {
					emms = new EadMetsMetadataStructure(wa.dataPath(), new File(mfPathSrc), o.getDocuments());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (emms != null) {
					List<String> metse = emms.getMetsRefsInEad();
					for (int mmm = 0; mmm < metse.size(); mmm++) {
						String mets = metse.get(mmm);
						String normMets = FilenameUtils.normalize(mets);
						if (normMets != null){
							mets = normMets; 
						}
						ret.add(mets);
					}
				}
			}
		}
		
		return ret;
	}
	
	
	boolean isSemanticsPackage(File packageContent, String origName) {
		if (!new File(packageContent.getAbsolutePath()+"/"+origName).exists()) return false;
		return true;
	}

	boolean isStandardPackage(File packageContent){
		
		boolean is=true;
		if (!new File(packageContent.getAbsolutePath()+"/data").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/bagit.txt").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/bag-info.txt").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/manifest-md5.txt").exists()) is=false;
		if (!new File(packageContent.getAbsolutePath()+"/tagmanifest-md5.txt").exists()) is=false;
		
		return is;
	}
			
	public FileFormatFacade getFileFormatFacade() {
		return fileFormatFacade;
	}

	public void setFileFormatFacade(FileFormatFacade fileFormatFacade) {
		this.fileFormatFacade = fileFormatFacade;
	}
	
	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}

	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}

	
}

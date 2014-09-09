/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2014 LVRInfoKom
  Landschaftsverband Rheinland

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.UserException;
import de.uzk.hki.da.core.UserException.UserExceptionId;
import de.uzk.hki.da.metadata.MetadataStructure;
import de.uzk.hki.da.metadata.MetadataStructureFactory;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.repository.RepositoryException;
import de.uzk.hki.da.utils.C;

/**
 * Detects the package type of an object and validates the metadata structure.
 * 
 * The package type can be of the four types
 * <ul>
 * <li>METS
 * <li>EAD
 * <li>XMP
 * <li>LIDO
 * </ul>
 * 
 * When package type XMP is detected, and no XMP.rdf has been found, a XMP.rdf
 * manifest gets generated an put to the temp representation.
 * 
 * @author Daniel M. de Oliveira
 */
public class ValidateMetadataAction extends AbstractAction {
	
	private static final String PACKAGE_TYPE_FOR_OBJECT_DETERMINDED = "Package type for object determinded: ";
	private String detectedPackageType = "NONE";
	private DAFile detectedMetadataFile;
	private boolean packageTypeInObjectWasSetBeforeRunningAction=false;
	MetadataStructureFactory msf = new MetadataStructureFactory();
	
	
	@Override
	void checkActionSpecificConfiguration() throws ConfigurationException {
		// Auto-generated method stub
		
	}

	@Override
	void checkSystemStatePreconditions() throws IllegalStateException {
		// Auto-generated method stub
		
	}

	@Override
	boolean implementation() throws FileNotFoundException, IOException,
			UserException, RepositoryException {
		
		detect();
		throwExceptionIfPackageTypeCollision();
		logger.info(PACKAGE_TYPE_FOR_OBJECT_DETERMINDED+detectedPackageType);
		if (detectedPackageType.equals("NONE")){
			return true;
		}
		
		MetadataStructure ms = createMetadataStructure();
		if (!ms.isValid()){
			throw new UserException(UserExceptionId.INCONSISTENT_PACKAGE, 
					"Package of type "+detectedPackageType+" is not consistent");
		}
		
		object.setPackage_type(detectedPackageType);
		object.setMetadata_file(detectedMetadataFile.getRelative_path());
		return true;
	}
	
	
	private MetadataStructure createMetadataStructure() {
		MetadataStructure ms=null;
		try {
			File d = detectedMetadataFile.toRegularFile();
			ms = msf.create(detectedPackageType, d);
		} catch (Exception e){
			throw new RuntimeException("problem occured during creation of metadata structure",e);
		}
		return ms;
	}

	
	
	
	
	
	
	
	/**
	 * if something else has been detected in a previous SIP.
	 */
	private void throwExceptionIfPackageTypeCollision() {
		if (!(object.getPackage_type()==null||object.getPackage_type().isEmpty())){
			packageTypeInObjectWasSetBeforeRunningAction=true;
			if ((!detectedPackageType.equals(object.getPackage_type()))
					||(!detectedMetadataFile.equals(object.getMetadata_file()))){
				throw new RuntimeException("COLLISION");
			}
		}
	}
	

	@Override
	void rollback() throws Exception {
		if (!packageTypeInObjectWasSetBeforeRunningAction){
			object.setMetadata_file(null);
			object.setPackage_type(null);
		}
	}

	/**
	 * @throws UserException If more than one metadata file was found.
	 * @author Daniel M. de Oliveira 
	 */
	private void detect(){
		
		if (getFilesOfMetadataType(C.EAD).size()>=2){
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"duplicate EAD");
		}
		if (getFilesOfMetadataType(C.LIDO).size()>1){
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"duplicate LIDO");
		}

		int ptypeCount=0;
		
		if (getFilesOfMetadataType(C.EAD).size()==1){
			detectedMetadataFile=getFilesOfMetadataType(C.EAD).get(0);
			detectedPackageType=C.EAD;
			ptypeCount++;
		}
		
		if ((getFilesOfMetadataType(C.EAD).size()!=1)&&
				getFilesOfMetadataType(C.METS).size()>1){
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"duplicate METS");
		}  
				
		if (getFilesOfMetadataType(C.METS).size()==1){
			detectedMetadataFile=getFilesOfMetadataType(C.METS).get(0);
			detectedPackageType=C.METS;
			ptypeCount++;
		}
		
		if ((getFilesOfMetadataType(C.XMP)).size()>=1){
			detectedMetadataFile=new DAFile(object.getLatestPackage(),
					object.getNameOfNewestARep().replace("+a", "+b"),C.XMP_METADATA_FILE);
			detectedPackageType=C.XMP;
			ptypeCount++;
		}
		
		if ((getFilesOfMetadataType(C.LIDO)).size()==1){
			detectedMetadataFile=getFilesOfMetadataType(C.LIDO).get(0);
			detectedPackageType=C.LIDO;
			ptypeCount++;
		}
		
		if (ptypeCount>1)
			throw new UserException(UserExceptionId.DUPLICATE_METADATA_FILE,"duplicate METADATA");
	}
	
	
	private List<DAFile> getFilesOfMetadataType(String metadataFormatIdentifier){
		List<DAFile> result = new ArrayList<DAFile>();
		
		for (DAFile f:object.getLatestPackage().getFiles()){
			if (metadataFormatIdentifier.equals(f.getFormatSecondaryAttribute())) {
				result.add(f);
			}
		}
		return result;
	}
	
	
	public void setMsf(MetadataStructureFactory msf) {
		this.msf = msf;
	}
}

package de.uzk.hki.da.at;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uzk.hki.da.utils.FolderUtils;

/**
 * 
 * @author Polina Gubaidullina
 *
 */

public class ATSipBuilderCliXmp {
	
	private static File targetDir = new File("target/atTargetDir/");
	private static File sourceDir = new File("src/test/resources/at/");
	private static String singleSip = "ATBuildSingleXmpSip.tgz";
	private static Process p;
	
	@Before
	public void setUp() throws IOException{	
		FolderUtils.deleteDirectorySafe(targetDir);
	}
	
	@After
	public void tearDown() throws IOException{
		FolderUtils.deleteDirectorySafe(targetDir);
		p.destroy();
	}
	
	@Test
	public void testBuildSingleSipCorrectReferences() throws IOException {
		
		File source = new File(sourceDir, "ATBuildSingleXmpSip");
		
		String cmd = "./SipBuilder-Unix.sh -rights=\""+ATWorkingDirectory.CONTRACT_RIGHT_LICENSED.getAbsolutePath()+"\" -source=\""+source.getAbsolutePath()+"/\" -destination=\""+targetDir.getAbsolutePath()+"/\" -single -alwaysOverwrite";
		
		p=Runtime.getRuntime().exec(cmd,
		        null, new File("target/installation"));
		
		BufferedReader stdInput = new BufferedReader(new
        InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new
        InputStreamReader(p.getErrorStream()));
		 
		boolean identifiedMetadataType = false;
		String s = "";
		// read the output from the command
	    System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null) {
	         System.out.println(s);
	         if(s.contains("Im Verzeichnis ATBuildSingleXmpSip wurde keine Metadatendatei gefunden")) {
	        	 identifiedMetadataType = true;
	         }
	    }
	    
	    // read any errors from the attempted command
	    System.out.println("Here is the standard error of the command (if any):\n");
	    while ((s = stdError.readLine()) != null) {
	        System.out.println(s);
	    }
	    
	    assertFalse(new File("target/atTargetDir/"+singleSip).exists());
	    assertTrue(identifiedMetadataType);
	}
}

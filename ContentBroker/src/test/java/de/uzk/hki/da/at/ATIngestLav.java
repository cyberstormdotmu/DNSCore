package de.uzk.hki.da.at;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uzk.hki.da.metadata.MetsParser;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.utils.C;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.XMLUtils;

public class ATIngestLav extends AcceptanceTest {
	
	private static final String urn1 = "urn+nbn+de+danrw+de2189-48c69c71-b98e-4229-a1c1-69a5930d44103";
	private static final String urn2 = "urn+nbn+de+danrw+de2189-89532c28-d082-4c38-8783-21b9019225988";
	private static final String urn3 = "urn+nbn+de+danrw+de2189-0c6ab310-f2f6-4f66-80e2-a138bd4db6938";
	
	private static Object object1;
	private static Object object2;
	private static Object object3;
	
	private  String PORTAL_CI_TEST =getTestIndex();
	
	
	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		ath.putSIPtoIngestArea(urn1, "tgz", urn1);
		ath.awaitObjectState(urn1,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(urn1);
		
		ath.putSIPtoIngestArea(urn2, "tgz", urn2);
		ath.awaitObjectState(urn2,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(urn2);
		
		ath.putSIPtoIngestArea(urn3, "tgz", urn3);
		ath.awaitObjectState(urn3,Object.ObjectStatus.ArchivedAndValidAndNotInWorkflow);
		ath.waitForDefinedPublishedState(urn3);
	
		object1=ath.getObject(urn1);
		object2=ath.getObject(urn2);
		object3=ath.getObject(urn3);
	
	}
	
	@Test
	public void testDDBExclusion() throws IOException, JDOMException {
		assertTrue("ddbExcluded have to be false",!ath.getObject(urn1).ddbExcluded());
		assertTrue("ddbExcluded have to be false",!ath.getObject(urn2).ddbExcluded());
		assertTrue("ddbExcluded have to be false",!ath.getObject(urn3).ddbExcluded());
	}
	
	@Test
	public void testAIPSize() throws IOException, JDOMException {
		System.out.println(urn1 + " filesize: "+ath.getObject(urn1).getAip_size());
		System.out.println(urn2 + " filesize: "+ath.getObject(urn2).getAip_size());
		System.out.println(urn3 + " filesize: "+ath.getObject(urn3).getAip_size());
		
		double diff1=(ath.getObject(urn1).getAip_size()-188416.0)/(ath.getObject(urn1).getAip_size()+1); // Before XML Validation by Jhove 188416, after validation 218112
		double diff2=(ath.getObject(urn2).getAip_size()-235008.0)/(ath.getObject(urn2).getAip_size()+1); // Before XML Validation by Jhove 235008, after validation 264704
		double diff3=(ath.getObject(urn3).getAip_size()-256512.0)/(ath.getObject(urn3).getAip_size()+1); // Before XML Validation by Jhove 256512, after validation 287744
		
		// 4% difference will be tolerated
		System.out.println("RelDifferenzes: "+diff1+"  "+diff2+"  "+diff3);
		assertTrue("Wrong File Size d1: "+diff1,Math.abs(diff1)<0.03);
		assertTrue("Wrong File Size d2: "+diff2,Math.abs(diff2)<0.03);
		assertTrue("Wrong File Size d3: "+diff3,Math.abs(diff3)<0.03);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws IOException{
	}
	
	@Test
	public void testPIPMets() throws IOException, JDOMException {

		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		File metsFile1 = ath.loadFileFromPip(object1.getIdentifier(), "METS.xml");
		Document mets1 = builder.build
				(new FileReader(metsFile1));
		assertTrue(metsFile1.exists());
		
		File metsFile2 = ath.loadFileFromPip(object2.getIdentifier(), "METS.xml");
		Document mets2 = builder.build
				(new FileReader(metsFile2));
		
		File metsFile3 = ath.loadFileFromPip(object3.getIdentifier(), "METS.xml");
		Document mets3 = builder.build
				(new FileReader(metsFile3));		
	
		
		MetsParser mp = new MetsParser(mets1);
		List<String> references = mp.getReferences();
		assertTrue(references.size()==8);
		for(String r : references) {
			if(r.endsWith(".jpg")) {
				assertTrue(r.startsWith("http://"));
			}
		}
		
		mp = new MetsParser(mets2);
		references = mp.getReferences();
		assertTrue(references.size()==10);
		for(String r : references) {
			if(r.endsWith(".jpg")) {
				assertTrue(r.startsWith("http://"));
			}
		}
		
		mp = new MetsParser(mets3);
		references = mp.getReferences();
		assertTrue(references.size()==11);
		for(String r : references) {
			if(r.endsWith(".jpg")) {
				assertTrue(r.startsWith("http://"));
			}
		}
	}
	
	@Test
	public void testPIPEdm1() throws IOException, JDOMException {

		SAXBuilder builder = XMLUtils.createValidatingSaxBuilder();
		File edmFile1 = ath.loadFileFromPip(object1.getIdentifier(), "EDM.xml");
		Document edmDoc1 = builder.build
				(new FileReader(edmFile1));
		for(int i=0;i<30 && !edmFile1.exists();i++){
			FolderUtils.waitToCompleteNFSAwareFileOperation();
			System.out.println("Target("+edmFile1+") file is not created yet, wait: "+i);
		}
		assertTrue(edmFile1.exists());
		
//		File edmFile2 = Path.make(contractorsPipsPublic, object2.getIdentifier(), "EDM.xml").toFile();
//		Document edmDoc2 = builder.build
//				(new FileReader(edmFile2));
//		
//		File edmFile3 = Path.make(contractorsPipsPublic, object3.getIdentifier(), "EDM.xml").toFile();
//		Document edmDoc3 = builder.build
//				(new FileReader(edmFile3));		
	
		assertTrue(edmFile1.exists());
		@SuppressWarnings("unchecked")
		List<Element> providetCho = edmDoc1.getRootElement().getChildren("ProvidedCHO", C.EDM_NS);
		for(Element pcho : providetCho) {
			assertTrue(pcho.getChild("hasType", C.EDM_NS).getValue().equals("is root element"));
			assertTrue(pcho.getChild("title", C.DC_NS).getValue().equals("Nr. 44985"));
			
			@SuppressWarnings("unchecked")
			List<Element> identifier = pcho.getChildren("identifier", C.DC_NS);
			assertTrue(identifier.size()==4);
			
			boolean vzIdExists = false;
			boolean objectIdExists = false;
			boolean urnIdExists = false;
			for(Element id : identifier) {
				if(id.getValue().equals("Vz      48c69c71-b98e-4229-a1c1-69a5930d4410")) {
					vzIdExists = true;
				} else if(id.getValue().equals("urn:nbn:de:danrw:de2189-48c69c71-b98e-4229-a1c1-69a5930d44103")) {
					urnIdExists = true;
				} else if(id.getValue().equals(object1.getIdentifier())) {
					objectIdExists = true;
				}
			}
			assertTrue(vzIdExists&&objectIdExists&&urnIdExists);	
		}
		
		assertTrue(edmDoc1.getRootElement().getChild("Aggregation", C.ORE_NS).getChild("dataProvider", C.EDM_NS).getValue().contains("Landesarchiv NRW"));
		
////		testIndex
		String ident=metadataIndex.getIndexedMetadata(PORTAL_CI_TEST, object1.getIdentifier()+"-dmd00016");
		assertTrue("Identifier Test failed: "+ident,ident.contains("Nr. 44985"));
	}
}

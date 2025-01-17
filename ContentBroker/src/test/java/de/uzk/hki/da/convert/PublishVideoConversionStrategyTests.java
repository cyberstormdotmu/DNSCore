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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import de.uzk.hki.da.metadata.XPathUtils;
import de.uzk.hki.da.model.ConversionInstruction;
import de.uzk.hki.da.model.DAFile;
import de.uzk.hki.da.model.Node;
import de.uzk.hki.da.model.Object;
import de.uzk.hki.da.model.PublicationRight;
import de.uzk.hki.da.model.PublicationRight.Audience;
import de.uzk.hki.da.model.VideoRestriction;
import de.uzk.hki.da.model.WorkArea;
import de.uzk.hki.da.test.TC;
import de.uzk.hki.da.test.TESTHelper;
import de.uzk.hki.da.utils.FolderUtils;
import de.uzk.hki.da.utils.Path;
import de.uzk.hki.da.utils.RelativePath;


/**
 * The Class PublishVideoConversionStrategyTests.
 *
 * @author Daniel M. de Oliveira
 */
public class PublishVideoConversionStrategyTests {

	Path basePath = Path.make(TC.TEST_ROOT_CONVERT,"PublishVideoConversionStrategyTests");
	private Node n;
	
	
	@Before
	public void setUp() {
		
		n = new Node();
		n.setWorkAreaRootPath(basePath);
	}
	
	/**
	 * Tear down.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@After
	public void tearDown() throws IOException {
		
		if (new File(basePath + "dip").exists())
			FolderUtils.deleteDirectorySafe(new File(basePath + "dip"));
		FolderUtils.deleteDirectorySafe(new File(basePath + "/TEST"));
	}
	
	
	
	/**
	 * Test.
	 * @throws IOException 
	 */
	@Test
	public void test() throws IOException {
		
		Document dom = XPathUtils.parseDom(basePath + "premis.xml");
		if (dom==null){
			throw new RuntimeException("Error while parsing premis.xml");
		}
		
		Object o = TESTHelper.setUpObject("1",new RelativePath(basePath));
		PublicationRight right = new PublicationRight();
		right.setAudience(Audience.PUBLIC);
		right.setVideoRestriction(new VideoRestriction());
		right.getVideoRestriction().setHeight("360");
		right.getVideoRestriction().setDuration(180);
		o.getRights().getPublicationRights().add(right);
		
		PublishVideoConversionStrategy s = new PublishVideoConversionStrategy();
		s.setDom(dom);
		
		ConversionInstruction ci = new ConversionInstruction();
		ci.setSource_file(new DAFile("a","filename.avi"));
		ci.setTarget_folder("target/");

		s.setObject(o);
		s.setProcessTimeout(250);
		
		File testFile = new File(basePath + "testfile.txt");
		File pubFile = new File(basePath + "TEST/1/data/dip/public/target/filename.mp4");
		File instFile = new File(basePath + "TEST/1/data/dip/institution/target/filename.mp4");
		
		FileUtils.copyFile(testFile, pubFile);
		FileUtils.copyFile(testFile, instFile);
						
		s.convertFile(new WorkArea(n,o),ci);
	}
}

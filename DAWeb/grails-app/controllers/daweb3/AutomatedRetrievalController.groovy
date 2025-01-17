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

package daweb3
import grails.converters.*


/**
 * Create Retrieval Requests from JSON requests
 * @Author Jens Peters
 */
class AutomatedRetrievalController {
	
	
	def springSecurityService
	
	def index() {
		def admin = 0;
		User user = springSecurityService.currentUser
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		[user: user, admin:admin]
	}
    
	
	def queueForRetrievalJSON () {
		User user = springSecurityService.currentUser
		def QueueUtils qu = new QueueUtils(); 
		def result = [success:false]
//		CbNode cbn = CbNode.get(grailsApplication.config.localNode.id)
		CbNode cbn = CbNode.get(grailsApplication.config.getProperty('localNode.id'))
		def jsonObject = request.JSON
		
		def instance = Object.findByIdentifier(jsonObject['identifier'])
		try {
		if (instance!=null) {
			if (instance.user.shortName != user.getShortName()) {
				result.msg = "Sie haben nicht die nötigen Berechtigungen, um das Objekt " + jsonObject['identifier'] + " anzufordern!"
				render result as JSON
				return
			}
			qu.createJob( instance ,"900", cbn.getName())
			result = [success:true]
			result.msg = "Erfolgreich Arbeitsauftrag erstellt für "  + jsonObject['identifier']
			render result as JSON
			return
		}
		instance = Object.findByUrn(jsonObject['urn'])
		if (instance!=null) {
			if (instance.user.shortName != user.getShortName()) {
				result.msg = "Sie haben nicht die nötigen Berechtigungen, um das Objekt "+ jsonObject['urn'] + " anzufordern!"
				render result as JSON
				return
			}
			qu.createJob( instance ,"900", cbn.getName())
			result = [success:true]
			result.msg = "Erfolgreich Arbeitsauftrag erstellt für "  + jsonObject['urn']
			render result as JSON
			return
		}
		instance = Object.findByOrigName(jsonObject['origName'])
		if (instance!=null) {
			if (instance.user.shortName != user.getShortName()) {
				result.msg = "Sie haben nicht die nötigen Berechtigungen, um das Objekt "+ jsonObject['origName'] + " anzufordern!"
				render result as JSON
				return
			}
			qu.createJob( instance ,"900", cbn.getName())
			result = [success:true]
			result.msg = "Erfolgreich Arbeitsauftrag erstellt für "  + jsonObject['origName']
			render result as JSON
			return
		}
		} catch (Exception e) { }
		result.msg = "Fehler bei Erstellung eines Arbeitsauftrages"
		render result as JSON
		
	}
}

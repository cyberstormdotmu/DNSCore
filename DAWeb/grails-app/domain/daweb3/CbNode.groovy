package daweb3
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
/** 
 * The ContentBroker Node of DNS 
@Author Andres Quast, Jens Peters

*/
class CbNode {

    static constraints = {
    }
	
	static mapping = {
		table 'nodes'
		version false
		contractors joinTable: [name: "nodes_contractors", key: 'node_id', column: 'contractor_user_id'] 
    }
	static hasMany = [contractors: User]
	int id
	String name
	int urn_index 
	String toString() {
		return "$name"
	}
}

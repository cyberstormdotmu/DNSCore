package daweb3

import java.util.Date;

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
 * The 1:n 
 * @Author Sebastian Cuy
 * @Author Jens Peters
 */
class Package {

    static constraints = {
    }
	
	static mapping = {
		table 'packages'
		version false
	}
	
	int id
	Integer delta
	Integer repair 
	Date last_checked
	String checksum
	String status
	String container_name
	
	String toString() {
		return "Paket Nr. " + delta + " (ID: "+id+" : " + container_name + ")" ;
	}
}

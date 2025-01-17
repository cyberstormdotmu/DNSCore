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

package de.uzk.hki.da.model;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import de.uzk.hki.da.utils.Path;
import java.lang.Object;


/**
 * The Class Node.
 */
@Entity
@Table(name="nodes")
public class Node{
	
	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@OneToOne
	@JoinColumn(name="admin_id",unique=true)
	private User admin;
	
	@ManyToMany
    @JoinTable(name="nodes_contractors", 
                joinColumns={@JoinColumn(name="node_id")}, 
                inverseJoinColumns={@JoinColumn(name="contractor_user_id")})
    private Set<User> contractors = new HashSet<User>();
	
	private String identifier;
	
	/** The name. */
	@Column(columnDefinition="varchar(50)")
	private String name;

	/** The urn_index. */
	private int urn_index=-1;
	
	/** The remain time before retrieval is deleted after creation*/
	@Column(name="retrieval_remain_time",nullable=false, columnDefinition="INTEGER DEFAULT 2")
	private int retrieval_remain_time;

	/** The repl_destinations. */
	@Transient private String repl_destinations;
	
	/** The work area root path. */
	@Transient private Path workArea;
	
	/** The user area root path. */
	@Transient private Path userAreaRoot;
	
	/** The ingest area root path. */
	@Transient private Path ingestAreaRoot;
	
	/** The ingest area noBagit root path. */
	@Transient private Path ingestAreaNoBagitRoot;
	
	/** The grid cache area root path. */
	@Transient private Path gridCacheArea;
	
	/** The working resource. */
	@Transient private String workingResource;
	
	/** The dip resource. */
	@Transient private String dipResource;
	
	/** The log area. */
	@Transient private String logFolder;

	@ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="cooperating_nodes", 
                joinColumns={@JoinColumn(name="node_id")}, 
                inverseJoinColumns={@JoinColumn(name="cooperating_node_id")})
    private Set<Node> cooperatingNodes = new HashSet<Node>();
	
	/** The copies. */
	@OneToMany(orphanRemoval=true,fetch=FetchType.LAZY)
	@JoinColumn(name="node_id")
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.DELETE})
	private List<Copy> copies = new ArrayList<Copy>();

	@Transient private Copy copyToSave;
	
	
	/**
	 * Instantiates a new node.
	 */
	public Node(){}
	
	/**
	 * Instantiates a new node.
	 *
	 * @param name the name
	 * @param urn_index the urn_index
	 */
	public Node(String name,int urn_index){
		this.urn_index=urn_index;
		this.name=name;
	}
	
	/**
	 * Only for Testing purposes.
	 *
	 * @param id the id
	 * @param name the name
	 */
	public Node(int id,String name){
		this.id=id; this.name=name;
	}
	
	
	/**
	 * Instantiates a new node.
	 *
	 * @param name the name
	 * @param workingResource the working resource
	 */
	public Node(String name,String workingResource){
		this.name=name;
		this.workingResource=workingResource;
	}
	
	
	/**
	 * Instantiates a new node.
	 *
	 * @param name the name
	 */
	public Node(String name){
		this.name=name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the log folder.
	 *
	 * @param the log folder
	 */
	public void setLogFolder(String logFolder) {
		this.logFolder = logFolder;
	}
	
	
	/**
	 * Gets the log folder.
	 *
	 * @return the log folder
	 */
	public String getLogFolder() {
		return logFolder;
	}
	
	
	/**
	 * Sets the working resource.
	 *
	 * @param working_resource the new working resource
	 */
	public void setWorkingResource(String working_resource) {
		this.workingResource = working_resource;
	}
	
	/**
	 * Gets the working resource.
	 *
	 * @return the working resource
	 */
	public String getWorkingResource() {
		return workingResource;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "Node["+name+","+workingResource+"]";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override 
	public boolean equals(Object o){
		if (o == this) return true;

		if (o instanceof String){
			if (((String) o).equals(this.name)) return true;
			return false;
		}
		
		if (!(o instanceof Node)) return false;
		
		Node no= (Node) o;
		
		if (no.getName().equals(this.getName())) return true;
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override 
	public int hashCode(){
		return id; // Hacked with full consciousness.
	}

	/**
	 * Gets the urn_index.
	 *
	 * @return the urn_index
	 */
	public int getUrn_index() {
		return urn_index;
	}

	/**
	 * Sets the urn_index.
	 *
	 * @param urn_index the new urn_index
	 */
	public void setUrn_index(int urn_index) {
		this.urn_index = urn_index;
	}

	/**
	 * Sets the repl_destinations.
	 *
	 * @param replicas the new repl_destinations
	 */
	public void setReplDestinations(String replicas) {
		this.repl_destinations = replicas;
	}


	/**
	 * Gets the repl_destinations.
	 *
	 * @return the replica destinations
	 */
	public String getReplDestinations() {
		return repl_destinations;
	}

	/**
	 * Gets the work area root path.
	 *
	 * @return the work area root path
	 */
	public Path getWorkAreaRootPath() {
		return workArea;
	}


	/**
	 * Sets the work area root path.
	 *
	 * @param workAreaRootPath the new work area root path
	 */
	public void setWorkAreaRootPath(Path workAreaRootPath) {
		this.workArea = workAreaRootPath;
	}


	/**
	 * Gets the user area root path.
	 *
	 * @return the user area root path
	 */
	public Path getUserAreaRootPath() {
		return userAreaRoot;
	}


	/**
	 * Sets the user area root path.
	 *
	 * @param transferAreaRoot the new user area root path
	 */
	public void setUserAreaRootPath(Path transferAreaRoot) {
		this.userAreaRoot = transferAreaRoot;
	}


	/**
	 * Gets the ingest area root path.
	 *
	 * @return the ingest area root path
	 */
	public Path getIngestAreaRootPath() {
		return ingestAreaRoot;
	}


	/**
	 * Sets the ingest area root path.
	 *
	 * @param ingestAreaRoot the new ingest area root path
	 */
	public void setIngestAreaRootPath(Path ingestAreaRoot) {
		this.ingestAreaRoot = ingestAreaRoot;
	}
	
	
	/**
	 * Gets ingestNoBagit area root path
	 * 
	 * @return ingestNoBagit area root path
	 */
	public Path getIngestAreaNoBagitRootPath() {
		return ingestAreaNoBagitRoot;
	}

	/**
	 * Sets the ingestNoBagit area root path
	 * 
	 * @param ingestAreaNoBagitRoot the new ingestNoBagit area root path
	 */
	public void setIngestAreaNoBagitRootPath(Path ingestAreaNoBagitRoot) {
		this.ingestAreaNoBagitRoot = ingestAreaNoBagitRoot;
	}

	/**
	 * Sets the grid cache area root path.
	 *
	 * @param gridCacheAreaRootPath the gridCacheAreaRootPath to set
	 */
	public void setGridCacheAreaRootPath(Path gridCacheAreaRootPath) {
		this.gridCacheArea = gridCacheAreaRootPath;
	}


	/**
	 * Gets the grid cache area root path.
	 *
	 * @return the gridCacheAreaRootPath
	 */
	public Path getGridCacheAreaRootPath() {
		return gridCacheArea;
	}


	/**
	 * Gets the dip resource.
	 *
	 * @return the dip resource
	 */
	public String getDipResource() {
		return dipResource;
	}


	/**
	 * Sets the dip resource.
	 *
	 * @param dipResource the new dip resource
	 */
	public void setDipResource(String dipResource) {
		this.dipResource = dipResource;
	}

	public User getAdmin() {
		return admin;
	}

	public void setAdmin(User admin) {
		this.admin = admin;
	}

	public Set<User> getContractors() {
		return contractors;
	}

	public void setContractors(Set<User> contractors) {
		this.contractors = contractors;
	}

	public Set<Node> getCooperatingNodes() {
		return cooperatingNodes;
	}

	public void setCooperatingNodes(Set<Node> cooperatingNodes) {
		this.cooperatingNodes = cooperatingNodes;
	}

	public List<Copy> getCopies() {
		return copies;
	}

	public void setCopies(List<Copy> copies) {
		this.copies = copies;
	}

	
	public Copy getCopyToSave() {
		return copyToSave;
	}
	
	public void setCopyToSave(Copy copyToSave) {
		this.copyToSave = copyToSave;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public int getRetrieval_remain_time() {
		return retrieval_remain_time;
	}

	public void setRetrieval_remain_time(int retrieval_remain_time) {
		this.retrieval_remain_time = retrieval_remain_time;
	}
}

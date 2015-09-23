package daweb3

import org.springframework.web.multipart.MultipartHttpServletRequest;

class ReportController {
	def springSecurityService
	
	def decider = {
		def user = springSecurityService.currentUser
		if (params.get("answer").equals("start")) {
			redirect(action: 'start');
			return
		}  
		if (params.get("answer").equals("delete")) {
			log.debug("deletion")
			redirect(action: 'delete', params: [currentFiles: params.list("currentFiles")]);
			return
		}  
		if (params.get("answer").equals("retrieval")) {
			log.debug("retrieve")
			redirect(action: 'retrieval');
			return
		}
		redirect(action: 'index');
		return
	}
	
	def index = {
		def user = springSecurityService.currentUser
		
		def relativeDir = user.getShortName() + "/incoming"
		def baseFolder = grailsApplication.config.localNode.userAreaRootPath + "/" + relativeDir
		def httpurl = grailsApplication.config.transferNode.downloadLinkPrefix +"/"+   user.getShortName()  + "/incoming"
		
		def msgN = ""
		def baseDir;
		def filelist = []
		try {
			baseDir = new File(baseFolder)
			if (!baseDir.exists()) {
				msgN = "Benutzerordner nicht gefunden"
				log.error(msgN);
			}
		
		
		baseDir.eachFileMatch(~/^(?!\.).*?\.csv/) { file -> filelist.add(file)}
		if (filelist.empty) msgN ="Keine Dateien im Eingangsordner gefunden";
	 
		} catch (e) {
		msgN = "Benutzerordner " + baseFolder+ " existiert nicht!"
		log.error(msgN);
	
		}
		def msg = null;
		msg = params.get("msg");
		params.remove("msg");
		if (msg!=null) {
		msg = msg + " " + msgN
		} else msg = msgN
			[filelist:filelist,
			 msg:msg,httpurl:httpurl]
	}
	
	def save() {
		
		if(request instanceof MultipartHttpServletRequest) {
		def user = springSecurityService.currentUser
		
		def relativeDir = user.getShortName() + "/incoming"
		def baseFolder = grailsApplication.config.localNode.userAreaRootPath + "/" + relativeDir
		
		def uploadedfile = request.getFile("file");
		if (!uploadedfile.isEmpty()) {
			uploadedfile.transferTo(new File (baseFolder + "/"+ uploadedfile.getOriginalFilename()))
			return redirect(action: "index", params: [msg :uploadedfile.getOriginalFilename() + " wurde hochgeladen" ]);
			} 
		} 
		return redirect(action: "index", params: [msg: "keine Datei hochgeladen!"])
		
	}
	
	def start(){
			def user = springSecurityService.currentUser
			CbNode node = CbNode.findById(grailsApplication.config.localNode.id);
			SystemEvent se = new SystemEvent();
			se.setType("CreateStatusReportEvent");
			se.setUser(user);
			se.setNode(node);
			se.save();
			return redirect(action: "index", params: [msg: "Auftrag zur Erstellung eines Statusreports erfolgreich erstellt!"])
	}
	
	def retrieval(){
		def user = springSecurityService.currentUser
		CbNode node = CbNode.findById(grailsApplication.config.localNode.id);
		SystemEvent se = new SystemEvent();
		se.setType("CreateRetrievalRequestsEvent");
		se.setUser(user);
		se.setNode(node);
		se.save();
		return redirect(action: "index", params: [msg: "Auftrag zum massenhaften Retrieval erfolgreich erstellt!"])
		
	} 
	
	def delete = {
		def user = springSecurityService.currentUser
		def relativeDir = user.getShortName() + "/incoming"
		def baseFolder = grailsApplication.config.localNode.userAreaRootPath + "/" + relativeDir
		
		def files = params.list("currentFiles")

		def msg = ""
		files.each {
			 log.info "Datei ${it}"
			 try {
				 if (new File (baseFolder+ "/" + it).exists()) new File (baseFolder+ "/" + it).delete();
			 } catch (Exception e) {
			 }
		}
		[msg:msg]
		redirect(action:"index",params: [msg: files.size() + " Dateien gelöscht!"])
	}
}
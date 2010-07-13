package es.urjc.mctwp.service.commands.researchCmds;

import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.FileDAO;
import es.urjc.mctwp.modelo.File;
import es.urjc.mctwp.modelo.Protocolable;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class LoadProtocolableAttachments extends ResultCommand<List<File>> {
	private Protocolable source = null;
	private FileDAO dao = null;
	
	public LoadProtocolableAttachments(BeanFactory bf) {
		super(bf);
		dao = (FileDAO) bf.getBean(BeanNames.FILE_DAO);
		setAction(ActionNames.ADD_FILE_PRT);
	}

	public void setSource(Protocolable source) {
		this.source = source;
	}

	public Protocolable getSource() {
		return source;
	}
	
	@Override
	public boolean isValidCommand(){
		return super.isValidCommand() &&
			source != null &&
			dao != null;
	}

	@Override
	public ResultCommand<List<File>> runCommand() throws Exception {
		this.setResult(dao.findFilesBySource(source));
		return this;
	}
}

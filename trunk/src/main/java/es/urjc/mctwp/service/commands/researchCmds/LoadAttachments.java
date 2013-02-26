package es.urjc.mctwp.service.commands.researchCmds;

import java.util.List;

import org.springframework.beans.factory.BeanFactory;

import es.urjc.mctwp.dao.FileDAO;
import es.urjc.mctwp.modelo.File;
import es.urjc.mctwp.modelo.ImageContainer;
import es.urjc.mctwp.service.ActionNames;
import es.urjc.mctwp.service.BeanNames;
import es.urjc.mctwp.service.ResultCommand;

public class LoadAttachments extends ResultCommand<List<File>> {
	private ImageContainer source = null;
	private FileDAO dao = null;
	
	public LoadAttachments(BeanFactory bf) {
		super(bf);
		dao = (FileDAO) bf.getBean(BeanNames.FILE_DAO);
		setActionName(ActionNames.ADD_FILE_PRT);
	}

	public void setSource(ImageContainer source) {
		this.source = source;
	}

	public ImageContainer getSource() {
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

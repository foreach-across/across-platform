package com.foreach.across.modules.platform.application.controllers;

import com.foreach.common.filemanager.business.FileDescriptor;
import com.foreach.common.filemanager.services.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Marc Vanbrabant
 */
@RestController
public class FileManagerController
{
	@Autowired
	private FileManager fileManager;

	@RequestMapping("/fileManager/create")
	public FileDescriptor create() {
		return fileManager.createFile();
	}
}

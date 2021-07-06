/*
 * cron4j - A pure Java cron-like scheduler
 * 
 * Copyright (C) 2007-2010 Carlo Pelliccia (www.sauronsoftware.it)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.jspxnet.cron4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * A {@link TaskCollector} implementation, reading the task list from a group of
 * files.
 * </p>
 * 
 * @author Carlo Pelliccia
 * @since 2.0
 */
class FileTaskCollector implements TaskCollector {

	/**
	 * File list.
	 */
	private List<File> FILES = new ArrayList<>();

	/**
	 * Adds a file.
	 * 
	 * @param file
	 *            The file.
	 */
	public synchronized void addFile(File file) {
		FILES.add(file);
	}

	/**
	 * Removes a file.
	 * 
	 * @param file
	 *            The file.
	 */
	public synchronized void removeFile(File file) {
		FILES.remove(file);
	}

	/**
	 * Returns the file list.
	 * 
	 * @return The file list.
	 */
	public synchronized File[] getFiles() {
		int size = FILES.size();
		File[] ret = new File[size];
		for (int i = 0; i < size; i++) {
			ret[i] = FILES.get(i);
		}
		return ret;
	}

	/**
	 * Implements {@link TaskCollector#getTasks()}.
	 */
	@Override
	public synchronized TaskTable getTasks() {
		TaskTable ret = new TaskTable();
		for (File f : FILES) {
			TaskTable aux = null;
			try {
				aux = CronParser.parse(f);
			} catch (IOException e) {
				Exception e1 = new Exception("Cannot parse cron file: "
						+ f.getAbsolutePath(), e);
				e1.printStackTrace();
			}
			if (aux != null) {
				int auxSize = aux.size();
				for (int j = 0; j < auxSize; j++) {
					ret.add(aux.getSchedulingPattern(j), aux.getTask(j));
				}
			}
		}
		return ret;
	}

}

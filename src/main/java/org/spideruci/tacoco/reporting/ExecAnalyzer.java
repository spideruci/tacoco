package org.spideruci.tacoco.reporting;
/*******************************************************************************
 * Copyright (c) 2009, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.spideruci.tacoco.reporting.data.SourceFileCoverage.LineCoverageFormat;

/**
 * This example reads given execution data files and dumps their content.
 */
public final class ExecAnalyzer {
  
	/**
	 * Reads all execution data files specified as the arguments and dumps the
	 * content.
	 * 
	 * @param args
	 *            list of execution data files
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
	  ExecAnalyzer execAnalyzer = new ExecAnalyzer();
	  ExecutionDataParser parser = new ExecutionDataParser(new File(args[0]));
	  execAnalyzer.dumpContent(args[1], parser);
	}

	private void dumpContent(final String file,
	    final ExecutionDataParser parser) throws IOException {
		System.out.printf("exec file: %s%n", file);
		
		final FileInputStream in = new FileInputStream(file);
		final ExecutionDataReader reader = new ExecutionDataReader(in);
		
		reader.setSessionInfoVisitor(new ISessionInfoVisitor() {
			public void visitSessionInfo(final SessionInfo info) {
			  String sessionName = String.format("Session \"%s\": %s - %s", 
			      info.getId(),
            new Date(info.getStartTimeStamp()),
            new Date(info.getDumpTimeStamp()));
			  parser.resetExecDataStore();
			  parser.setCoverageTitle(sessionName);
				System.out.println("\n" + sessionName);
			}
		});
		
		reader.setExecutionDataVisitor(parser);
		reader.read();
		
		int count = 0;
		PrintStream out = System.out;
		for(IBundleCoverage coverage : parser.getCoverageBundles()) {
      ICoveragePrintable printer = new CoverageJsonPrinter(coverage, out, false, LineCoverageFormat.DENSE);
      printer.printCoverageTitle();
      printer.printCoverage();
      count += 1;
      System.out.printf("completed printing coverage bundle for %s.%n", coverage.getName());
      System.out.printf("completed printing %d coverage bundle(s).%n%n", count);
    }
		
		in.close();
		System.out.println();
	}
}

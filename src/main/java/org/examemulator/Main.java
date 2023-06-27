package org.examemulator;

import static org.examemulator.util.FileUtil.readQuestionsFiles;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.gui.main.MainController;
import org.examemulator.gui.questionnaire.QuestionnaireController;
import org.examemulator.util.FileUtil;
import org.examemulator.util.database.HsqldbServer;
import org.jboss.weld.exceptions.IllegalArgumentException;

import jakarta.enterprise.inject.se.SeContainerInitializer;

public class Main {

    public static void main(final String... args) {

	final var container = SeContainerInitializer.newInstance().initialize();

	HsqldbServer.start();

//	final var mainController = container.select(QuestionnaireController.class).get();
	final var mainController = container.select(MainController.class).get();
	mainController.show();

//	mainController.loadCertificationFromFolder("/home/fernando/Development/workspaces/eclipse-workspace/exam-emulator/src/test/resources/Food-Certification");
	
	Runtime.getRuntime().addShutdownHook(new Thread(() -> {

	    // Both OpenWebBeans and Weld seem to shutdown on their own.
	    // Weld even prints a stack trace if we close it.
	    if (container.isRunning()) {
		container.close();
	    }

	    HsqldbServer.stop();
	}));
//	test01();
    }
    
}

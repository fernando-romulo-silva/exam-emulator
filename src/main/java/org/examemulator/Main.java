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

//	final var container = SeContainerInitializer.newInstance().initialize();
//
//	HsqldbServer.start();
//
//	final var mainController = container.select(QuestionnaireController.class).get();
////	final var mainController = container.select(MainController.class).get();
//	mainController.show();
//
//	Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//
//	    // Both OpenWebBeans and Weld seem to shutdown on their own.
//	    // Weld even prints a stack trace if we close it.
//	    if (container.isRunning()) {
//		container.close();
//	    }
//
//	    HsqldbServer.stop();
//	}));
	
	test01();
    }
    
    public static void test01() {
	loadCertificationFromFolder("/home/fernando/Development/workspaces/eclipse-workspace/exam-emulator/src/test/resources/Food-Certification");
    }
    
    public static void loadCertificationFromFolder(final String certificationDir) {

	final var certificationPath = Paths.get(certificationDir);
	
	if (Files.notExists(certificationPath)) {
	    throw new IllegalArgumentException(MessageFormat.format("Certification folder ''{0}'' does not exist",certificationPath));
	}
	
	final var questionnairesSetPath = certificationPath.resolve("Questionnaires");
	if (Files.notExists(certificationPath) || !Files.isDirectory(certificationPath)) {
	    throw new IllegalArgumentException("Certification folder 'Questionnaires' does not exist!");
	}
	
	final var questionnaireSetFolders = FileUtil.readQuestionnairesFolder(questionnairesSetPath.toString());
	if (questionnaireSetFolders.isEmpty()) {
	    throw new IllegalArgumentException("The 'Questionnaires' does not have any folders, questionnaires set!");
	}
	
	final var regexQuestionnaireSetName = "\\d{1,2} - [a-zA-Z0-9-_() ]+";
	
	final var regexQuestionnaireName = "\\d{1,2} - Questionnaire \\d{1,2}[a-zA-Z0-9-_() ]+";
	
	for (final var quetionnaireFolder : questionnaireSetFolders) {
	    System.out.println(quetionnaireFolder.matches(regexQuestionnaireSetName));
	}
    }
}

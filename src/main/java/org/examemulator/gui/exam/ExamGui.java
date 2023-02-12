package org.examemulator.gui.exam;

import javax.swing.SwingUtilities;

import org.examemulator.service.ExamService;
import org.slf4j.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class ExamGui {

    @Inject
    private Logger log;

    private ExamView examView;
    
    ExamGui(){
	examView = new ExamView();
    }

    public void showMainWindow() {
	exec("show main window", () -> examView.setVisible(true));
    }

    private void exec(final String name, final Runnable func) {
	log.info("starting swing-invoke: " + name);

	SwingUtilities.invokeLater(() -> {
	    log.info("starting swing-event: " + name);
	    func.run();
	    log.info("finished swing-event: " + name);
	});

	log.info("finished swing-invoke: " + name);
    }

    public void onBoot(@Observes final BootEvent event) {
	log.info("starting cdi-event: boot");
	showMainWindow();
	log.info("finished cdi-event: boot");
    }
    
    public record BootEvent() {}
}

package org.examemulator.gui.exam;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class ExamGui {

    ExamView getView() {
	return new ExamView();
    }

//    public void showMainWindow() {
//	exec("show main window", () -> examView.setVisible(true));
//    }
//
//    private void exec(final String name, final Runnable func) {
//	log.info("starting swing-invoke: " + name);
//
//	SwingUtilities.invokeLater(() -> {
//	    log.info("starting swing-event: " + name);
//	    func.run();
//	    log.info("finished swing-event: " + name);
//	});
//
//	log.info("finished swing-invoke: " + name);
//    }
//
//    public void onBoot(@Observes final BootEvent event) {
//	log.info("starting cdi-event: boot");
//	showMainWindow();
//	log.info("finished cdi-event: boot");
//    }
//    
//    public static interface BootEvent {}
}

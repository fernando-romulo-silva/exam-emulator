package org.examemulator.gui.statitics;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StatiticsGui {

    StatiticsView view;
    
    @PostConstruct
    private void setup() {
	
//        log.info("starting constructing: UI");
        try {
            SwingUtilities.invokeAndWait(() -> {
//                log.info("starting constructing: main window");
                view = new StatiticsView();
                view.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                view.setSize(300, 450);
                
                view.setVisible(true);

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
//        log.info("finished constructing: UI");
    }

}

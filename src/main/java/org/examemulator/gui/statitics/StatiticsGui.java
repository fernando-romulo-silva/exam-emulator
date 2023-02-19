package org.examemulator.gui.statitics;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class StatiticsGui {

    StatiticsView getView() {
	return new StatiticsView();
    }
}

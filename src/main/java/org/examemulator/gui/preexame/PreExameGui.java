package org.examemulator.gui.preexame;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class PreExameGui {

    PreExameView getView() {
	return new PreExameView();
    }
}

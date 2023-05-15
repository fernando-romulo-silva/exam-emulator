package org.examemulator.gui.pretest;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class PretestGui {

    PretestView getView() {
	return new PretestView();
    }
}

package org.examemulator.service;

record ExamStructureFolder(
		String questionnaireName, // 
		String questionnaireDesc,  //
		Integer questionnaireOrder,
		String setName,  //
		String setDesc,  //
		Integer setOrder,
		String certificationName) {
}

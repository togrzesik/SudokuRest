package com.sudoku;

import com.sudoku.controller.SudokuController;
import com.sudoku.model.SudokuField;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import javax.validation.ValidationException;
import java.io.IOException;

@SpringBootTest
class SudokuRestApplicationTests {
	@Autowired
	SudokuController sudokuController;
	private Model model;
	private SudokuField field;

	@Test
	void contextLoads() {
	}
	@Before
	public void init() throws ValidationException, IOException {
		mockMvc = MockMvcBuilders
				.standaloneSetup(sudokuController)
				.build();
	}
	private MockMvc mockMvc;

	@Test
	public void getBoardTest() throws Exception {
		sudokuController.getInitForm(model);
		mockMvc.perform(MockMvcRequestBuilders.get("/fieldinputform"))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}
	@Test
	public void SubmitTest() throws Exception {
		sudokuController.showSolutions(model, field);
		mockMvc.perform(MockMvcRequestBuilders.post("/solutions"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

}

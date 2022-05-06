package test.albo.mx.marvel.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ResponseColaborators {
	
	private LocalDateTime lastSync;
	private List<String> editors;
	private List<String> writers;
	private List<String> colorists; 

}

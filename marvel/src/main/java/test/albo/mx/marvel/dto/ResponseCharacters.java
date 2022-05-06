package test.albo.mx.marvel.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ResponseCharacters {

	private LocalDateTime lastSync;
	private List<CharacterInteracted> characters;

}

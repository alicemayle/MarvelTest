package test.albo.mx.marvel.dto;

import java.util.List;

import lombok.Data;

@Data
public class CharacterInteracted {
	
	private String character;
	private List<String> comics;

}

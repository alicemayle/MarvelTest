package test.albo.mx.marvel.dto.external;

import lombok.Data;

@Data
public class ComicExternal {

	private Integer id;
	private String title;
	private CreatorExternal creators;
	private CharactersComicExternal characters;
	
	private Integer available;
}

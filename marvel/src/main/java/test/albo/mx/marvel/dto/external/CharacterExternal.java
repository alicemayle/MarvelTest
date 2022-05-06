package test.albo.mx.marvel.dto.external;

import lombok.Data;

@Data
public class CharacterExternal {

	private Integer id;
	private String name;
	private ComicExternal comics;
}

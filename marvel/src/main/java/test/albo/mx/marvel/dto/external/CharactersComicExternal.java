package test.albo.mx.marvel.dto.external;

import java.util.List;

import lombok.Data;

@Data
public class CharactersComicExternal {

	private String collectionURI;
	private List<ItemCharacterExternal> items;
}

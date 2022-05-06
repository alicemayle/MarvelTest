package test.albo.mx.marvel.dto.external;

import java.util.List;

import lombok.Data;

@Data
public class CreatorExternal {

	private String collectionURI;
	private List<ItemCreatorExternal> items;
}

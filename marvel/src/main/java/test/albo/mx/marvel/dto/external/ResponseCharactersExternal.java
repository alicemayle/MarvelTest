package test.albo.mx.marvel.dto.external;

import lombok.Data;

@Data
public class ResponseCharactersExternal {
	
	private Integer code;
	private String status;
	private DataExternal data;

}

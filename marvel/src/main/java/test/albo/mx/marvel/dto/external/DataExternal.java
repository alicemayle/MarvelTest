package test.albo.mx.marvel.dto.external;

import java.util.List;

import lombok.Data;

@Data
public class DataExternal {

	private Integer offset;
	private Integer limit;
	private Integer total;
	private Integer count;
	private List<Object> results;
}

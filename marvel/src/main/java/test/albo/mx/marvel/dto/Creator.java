package test.albo.mx.marvel.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "creators")
public class Creator {

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "name") 				private String name;
	@Column(name = "role") 				private String role;
	@Column(name = "description_role") 	private String descriptionRole;
	@Column(name = "hero") 				private String hero;
	@Column(name = "id_character") 		private Integer idCharacter;
	@Column(name = "id_comic")			private Integer idComic;
	@Column(name = "id_creator")		private Integer idCreator;
}

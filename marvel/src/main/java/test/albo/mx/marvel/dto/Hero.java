package test.albo.mx.marvel.dto;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "heroes")
public class Hero {
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "last_sync") 	private LocalDateTime lastSync;
	@Column(name = "id_character") 	private Integer idCharacter;
	@Column(name = "name") 			private String name;
	@Column(name = "hero") 			private String hero;
}

package test.albo.mx.marvel.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import test.albo.mx.marvel.dto.CharacterComic;

@Repository
public interface CharacterComicRepository extends JpaRepository<CharacterComic, Integer> {
	@Query("SELECT c FROM CharacterComic c "
			+ "WHERE (c.idCharacter = ?1)"
			+ "AND (c.idComic = ?2)"
			+ "AND (c.hero = ?3)")
	List<CharacterComic> findByIdCharacterIdComicHero(
			Integer idCharacter, 
			Integer idComic,
			String hero);
	
	@Query("SELECT DISTINCT c.idCharacter FROM CharacterComic c WHERE (c.hero = ?1)")
	List<Integer> findByHero(String hero);

	List<CharacterComic> findByIdCharacter(Integer idCharacter);
}

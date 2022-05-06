package test.albo.mx.marvel.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import test.albo.mx.marvel.dto.Creator;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Integer> {

	@Query("SELECT c FROM Creator c "
			+ "WHERE (c.idCreator = ?1)"
			+ "AND (c.idCharacter = ?2)"
			+ "AND (c.idComic = ?3)")
	List<Creator> findByIdCreatorIdCharacterIdComic(
			Integer idCreator, 
			Integer idCharacter, 
			Integer idComic);
	
	@Query("SELECT DISTINCT c.name FROM Creator c "
			+ "WHERE (c.role = ?1)"
			+ "AND (c.hero = ?2)")
	List<String> findByHeroAndRole(
			String role, 
			String hero);
}

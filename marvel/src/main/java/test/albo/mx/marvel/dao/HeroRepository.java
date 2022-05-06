package test.albo.mx.marvel.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import test.albo.mx.marvel.dto.Hero;

@Repository
public interface HeroRepository extends JpaRepository<Hero, Integer> {

	Hero findByHero(String hero);
}

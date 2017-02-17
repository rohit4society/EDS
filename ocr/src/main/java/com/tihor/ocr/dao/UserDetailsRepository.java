package com.tihor.ocr.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.tihor.ocr.domain.Model;
@Repository
public interface UserDetailsRepository extends GraphRepository<Model> {

		/*  // derived finder
		  Movie findByTitle(String title);

		  @Query("MATCH (m:Movie)<-[rating:RATED]-(user) WHERE id(movie)={movie} return rating")
		  List<Rating> getRatings(@Param("movie") Movie movie);

		  // Co-Actors
		  Set<Person> findByActorsMoviesActorName(String name);

		  @Query("MATCH (movie:Movie)-[:HAS_GENRE]->(genre)<-[:HAS_GENRE]-(similar)
		          WHERE id(movie) = {0} RETURN similar")
		  List<Movie> findSimilarMovies(Movie movie);
		}
}*/
		Model findByRoll(String rollNo);
		
}
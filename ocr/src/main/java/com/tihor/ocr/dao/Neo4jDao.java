/*package com.tihor.ocr.dao;

public class Neo4jDao {
	@Autowired
	MovieRepository movieRepository;

	@Test public void persistedMovieShouldBeRetrievableFromGraphDb() {
	     Movie forrest = new Movie("1", "Forrest Gump");
	     forrest = movieRepository.save(forrest);

	     Movie foundForrest = findMovieByProperty("title", forrest.getTitle()).iterator().next();
	     assertEquals(forrest.getId(), foundForrest.getId());
	     assertEquals(forrest.getTitle(), foundForrest.getTitle());
	}
}
*/
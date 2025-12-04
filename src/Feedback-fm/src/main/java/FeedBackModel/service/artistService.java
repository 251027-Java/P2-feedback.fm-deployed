package FeedBackModel.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feedbackmodel.Artist;

@Service
@Transactional
public class artistService {

    private final artistRepository artistRepo;

    @Autowired
    public artistService(artistRepository artistRepo) {
        this.artistRepo = artistRepo;
    }

    // get artist by artist ID (Spotify ID, which is the primary key)
    public Artist getArtistById(String artistId) {
        return artistRepo.findById(artistId)
        .orElseThrow(() -> new RuntimeException("Artist not found: " + artistId));
    }

    // get artist by name
    public Artist getArtistByName(String name) {
        return artistRepo.findByName(name)
        .orElseThrow(() -> new RuntimeException("Artist not found: " + name));
    }

    // create new artist from spotify profile
    // should be called when processing Spotify API data
    public Artist createArtist(String artistId, String name, String href) {
        // check if artist already exists
        Optional<Artist> existingArtist = artistRepo.findById(artistId);
        if (existingArtist.isPresent()) {
            return updateArtist(artistId, name, href);
        }

        // if artist does not exist
        Artist a = new Artist(artistId, name, href);
        
        return artistRepo.save(a);
    }

    // update artist from spotify profile
    public Artist updateArtist(String artistId, String name, String href) {
        Artist a = getArtistById(artistId);
        if (name != null) {
            a.setName(name);
        }
        if (href != null) {
            a.setHref(href);
        }
        return artistRepo.save(a);
    }

    // delete artist
    public void deleteArtist(String artistId) {
        Artist a = getArtistById(artistId);
        artistRepo.delete(a);
    }

    // checks to see if artist already exists
    public boolean artistExists(String artistId) {
        return artistRepo.findById(artistId).isPresent();
    }

    public boolean artistExistsByName(String name) {
        return artistRepo.findByName(name).isPresent();
    }

    // gets or creates artist
    public Artist getOrCreate(String artistId, String name, String href) {
        return artistRepo.findById(artistId)
            .map(a -> updateArtist(artistId, name, href))
            .orElseGet(() -> createArtist(artistId, name, href));
    }
}

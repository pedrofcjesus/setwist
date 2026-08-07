package com.setwist.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.setwist.backend.model.Band;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.SetlistSong;
import com.setwist.backend.model.Song;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SetlistRepository;
import com.setwist.backend.repository.SongRepository;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(
            BandRepository bandRepository,
            SongRepository songRepository,
            SetlistRepository setlistRepository) {

        return args -> {
            // Apenas popula se a base de dados estiver totalmente vazia
            if (bandRepository.count() == 0) {

                // 1. Criar uma Banda de teste
                Band band = new Band();
                band.setName("Smoodies-Teste");
                band.setDescription("Banda de Teste");
                band = bandRepository.save(band);

                // 2. Criar Músicas para a Banda
                Song song1 = new Song();
                song1.setTitle("Superstition");
                song1.setArtist("Stevie Wonder");
                song1.setSongKey("Em");
                song1.setDurationSeconds(240);
                song1.setBand(band);

                Song song2 = new Song();
                song2.setTitle("Valerie");
                song2.setArtist("Amy Winehouse");
                song2.setSongKey("D#");
                song2.setDurationSeconds(270);
                song2.setBand(band);

                Song song3 = new Song();
                song3.setTitle("Treasure");
                song3.setArtist("Bruno Mars");
                song3.setSongKey("A#m");
                song3.setDurationSeconds(220);
                song3.setBand(band);

                songRepository.saveAll(java.util.List.of(song1, song2, song3));

                // 3. Criar uma Setlist e associar as músicas
                Setlist setlist = new Setlist();
                setlist.setName("Main Setlist");
                setlist.setDescription("Alinhamento de teste");
                setlist.setBand(band);

                SetlistSong item1 = new SetlistSong(setlist, song1, 1);
                SetlistSong item2 = new SetlistSong(setlist, song2, 2);
                SetlistSong item3 = new SetlistSong(setlist, song3, 3);

                setlist.getSetlistSongs().addAll(java.util.List.of(item1, item2, item3));

                setlistRepository.save(setlist);

                System.out.println("✅ [DatabaseSeeder] Dados de teste carregados com sucesso!");
            }
        };
    }
}
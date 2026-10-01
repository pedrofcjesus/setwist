package com.setwist.backend.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.BandRepertoire;
import com.setwist.backend.model.BandRole;
import com.setwist.backend.model.Setlist;
import com.setwist.backend.model.SetlistSong;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandMemberRepository;
import com.setwist.backend.repository.BandRepertoireRepository;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SetlistRepository;
import com.setwist.backend.repository.SongRepository;
import com.setwist.backend.repository.UserRepository;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            BandRepository bandRepository,
            BandMemberRepository bandMemberRepository,
            SongRepository songRepository,
            BandRepertoireRepository bandRepertoireRepository,
            SetlistRepository setlistRepository) {

        return args -> {
            // Apenas popula se a base de dados estiver totalmente vazia
            if (userRepository.count() == 0) {

                // 1. Criar um Utilizador de Teste
                User user = new User();
                user.setEmail("teste@setwist.com");
                user.setPassword("123456"); // Em produção deve ser encriptada com PasswordEncoder
                user.setName("Utilizador Teste");
                user = userRepository.save(user);

                // 2. Criar Banda e definir o utilizador como criador e ADMIN
                Band band = new Band("Smoodies-Teste", "Banda de Teste");
                band.setUser(user);
                band = bandRepository.save(band);

                BandMember member = new BandMember(band, user, BandRole.ADMIN);
                bandMemberRepository.save(member);

                // 3. Criar Músicas na Biblioteca Pessoal do Utilizador
                Song song1 = new Song("Superstition", "Stevie Wonder", user);
                Song song2 = new Song("Valerie", "Amy Winehouse", user);
                Song song3 = new Song("Treasure", "Bruno Mars", user);

                songRepository.saveAll(List.of(song1, song2, song3));

                // 4. Adicionar as Músicas ao Repertório da Banda (com tom, duração e BPM)
                BandRepertoire rep1 = new BandRepertoire(band, song1, "Em", 240, 100);
                BandRepertoire rep2 = new BandRepertoire(band, song2, "D#", 270, 105);
                BandRepertoire rep3 = new BandRepertoire(band, song3, "A#m", 220, 116);

                bandRepertoireRepository.saveAll(List.of(rep1, rep2, rep3));

                // 5. Criar uma Setlist e associar os itens do repertório
                Setlist setlist = new Setlist();
                setlist.setName("Main Setlist");
                setlist.setDescription("Alinhamento de teste");
                setlist.setBand(band);
                setlist.setUser(user);

                SetlistSong item1 = new SetlistSong(setlist, rep1, 1);
                SetlistSong item2 = new SetlistSong(setlist, rep2, 2);
                SetlistSong item3 = new SetlistSong(setlist, rep3, 3);

                setlist.getSetlistSongs().addAll(List.of(item1, item2, item3));

                setlistRepository.save(setlist);

                System.out.println("✅ [DatabaseSeeder] Dados de teste carregados com sucesso!");
            }
        };
    }
}
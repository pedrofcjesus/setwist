package com.setwist.backend.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.setwist.backend.model.Band;
import com.setwist.backend.model.BandMember;
import com.setwist.backend.model.BandRepertoire;
import com.setwist.backend.model.BandRole;
import com.setwist.backend.model.Song;
import com.setwist.backend.model.User;
import com.setwist.backend.repository.BandMemberRepository;
import com.setwist.backend.repository.BandRepertoireRepository;
import com.setwist.backend.repository.BandRepository;
import com.setwist.backend.repository.SongRepository;
import com.setwist.backend.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            BandRepository bandRepository,
            BandMemberRepository bandMemberRepository,
            SongRepository songRepository,
            BandRepertoireRepository bandRepertoireRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.findByEmail("pedro@example.com").isPresent()) {
                return;
            }

            // 1. Utilizador
            User user = new User();
            user.setName("Pedro");
            user.setEmail("pedro@example.com");
            user.setPassword(passwordEncoder.encode("123456"));
            userRepository.save(user);

            // 2. Músicas Globais (Apenas catálogo genérico)
            Song song1 = new Song();
            song1.setTitle("Valerie");
            song1.setArtist("Amy Winehouse");
            song1.setUser(user);

            Song song2 = new Song();
            song2.setTitle("Vejam Bem");
            song2.setArtist("José Afonso");
            song2.setUser(user);

            Song song3 = new Song();
            song3.setTitle("Superstition");
            song3.setArtist("Stevie Wonder");
            song3.setUser(user);

            songRepository.saveAll(List.of(song1, song2, song3));

            // 3. Bandas
            Band smoodies = new Band();
            smoodies.setName("Smoodies");
            smoodies.setDescription("Soul, Funk & Pop");
            smoodies.setUser(user);
            bandRepository.save(smoodies);

            Band coffeeBreak = new Band();
            coffeeBreak.setName("Coffee Break");
            coffeeBreak.setDescription("Duo Acústico");
            coffeeBreak.setUser(user);
            bandRepository.save(coffeeBreak);

            // 4. Membros da Banda (Usando BandRole.ADMIN)
            BandMember m1 = new BandMember(smoodies, user, BandRole.ADMIN);
            BandMember m2 = new BandMember(coffeeBreak, user, BandRole.ADMIN);
            bandMemberRepository.saveAll(List.of(m1, m2));

            // 5. Repertório das Bandas
            BandRepertoire rep1 = new BandRepertoire();
            rep1.setBand(smoodies);
            rep1.setSong(song1);
            rep1.setSongKey("Abm");
            rep1.setBpm(110);
            rep1.setNotes("Tom subido para a vocalista");

            BandRepertoire rep2 = new BandRepertoire();
            rep2.setBand(smoodies);
            rep2.setSong(song3);
            rep2.setSongKey("Ebm");
            rep2.setBpm(100);
            rep2.setNotes("Arrancada com solo de baixo");

            BandRepertoire rep3 = new BandRepertoire();
            rep3.setBand(coffeeBreak);
            rep3.setSong(song2);
            rep3.setSongKey("Am");
            rep3.setBpm(85);
            rep3.setNotes("Arranjo acústico");

            bandRepertoireRepository.saveAll(List.of(rep1, rep2, rep3));

            System.out.println(">>> [SETWIST] Dados iniciais carregados com sucesso! <<<");
        };
    }
}
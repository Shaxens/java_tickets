package fr.arthur.devoir_java.security;

import fr.arthur.devoir_java.dao.UserDao;
import fr.arthur.devoir_java.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    protected UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String pseudo) throws UsernameNotFoundException {

        Optional<User> user = userDao.findByPseudo(pseudo);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Pseudo introuvable : " + pseudo);
        }

        return new AppUserDetails(user.get());
    }
}
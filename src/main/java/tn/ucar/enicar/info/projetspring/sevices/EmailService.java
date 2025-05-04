package tn.ucar.enicar.info.projetspring.sevices;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendCredentials(String toEmail, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Vos identifiants de connexion");
        message.setText("Bonjour,\n\n"
                + "Votre compte manager a été créé avec succès.\n"
                + "Email: " + toEmail + "\n"
                + "Mot de passe: " + password + "\n\n"
                + "Connectez-vous à: http://votre-application.com/login\n\n"
                + "Cordialement,\nL'équipe d'administration");

        mailSender.send(message);
    }
}
